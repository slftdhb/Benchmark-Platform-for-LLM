package com.evalforge.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.evalforge.auth.dto.RegisterRequest;
import com.evalforge.auth.service.AuthService;
import com.evalforge.benchmark.dto.CreateBenchmarkRequest;
import com.evalforge.benchmark.service.BenchmarkImportOrchestator;
import com.evalforge.benchmark.service.BenchmarkService;
import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import com.evalforge.storage.service.S3StorageService;

@SpringBootTest
class BenchmarkImportFailureTest {

    private static final String VALID_JSONL = """
            {"sample_key":"s1","input":{"question":"1+1"},"gold":{"answer":"2"}}
            """;

    @MockitoSpyBean
    private S3StorageService storageService;

    @Autowired
    private BenchmarkImportOrchestator orchestrator;

    @Autowired
    private AuthService authService;

    @Autowired
    private BenchmarkService benchmarkService;

    @Autowired
    private JdbcTemplate jdbc;

    private Long userId;
    private Long benchmarkId;
    private Long versionId;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        userId = authService.register(new RegisterRequest("fail_" + suffix, "Passw0rd!"));
        benchmarkId = benchmarkService.create(
                userId,
                new CreateBenchmarkRequest("fail-bench-" + suffix, "failure test"));

        versionId = jdbc.queryForObject(
                "SELECT id FROM benchmark_version WHERE benchmark_id = ? ORDER BY version_no LIMIT 1",
                Long.class, benchmarkId);
    }

    @Test
    void uploadFailure_marksFailedAndDoesNotImportSamples() {
        doThrow(new RuntimeException("upload failed"))
                .when(storageService)
                .uploadFile(anyString(), anyString(), any(InputStream.class),
                        anyLong(), anyString());
        doNothing().when(storageService).delete(anyString(), anyString());

        MockMultipartFile file = new MockMultipartFile(
                "file", "samples.jsonl", "application/x-ndjson",
                VALID_JSONL.getBytes(StandardCharsets.UTF_8));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orchestrator.importFile(userId, versionId, file));

        assertEquals(ErrorCode.STORAGE_UPLOAD_FAILED, ex.getErrorCode());
        assertEquals("FAILED", storedObject().get("status"));
        assertEquals(0, sampleCount());
        verify(storageService).delete(anyString(), anyString());
    }

    @Test
    void invalidJsonl_rollsBackSamplesAndDeletesObject() {
        doNothing().when(storageService)
                .uploadFile(anyString(), anyString(), any(InputStream.class),
                        anyLong(), anyString());
        doNothing().when(storageService).delete(anyString(), anyString());

        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.jsonl", "application/x-ndjson",
                "this is not json".getBytes(StandardCharsets.UTF_8));

        assertThrows(BusinessException.class,
                () -> orchestrator.importFile(userId, versionId, file));

        assertEquals("FAILED", storedObject().get("status"));
        assertEquals(0, sampleCount());
        assertEquals(0, versionSampleCount());
        verify(storageService).delete(anyString(), anyString());
    }

    @Test
    void deleteFailure_keepsCleanupPendingForRetry() {
        doNothing().when(storageService)
                .uploadFile(anyString(), anyString(), any(InputStream.class),
                        anyLong(), anyString());
        doThrow(new RuntimeException("delete failed"))
                .when(storageService).delete(anyString(), anyString());

        MockMultipartFile file = new MockMultipartFile(
                "file", "bad.jsonl", "application/x-ndjson",
                "not json".getBytes(StandardCharsets.UTF_8));

        assertThrows(BusinessException.class,
                () -> orchestrator.importFile(userId, versionId, file));

        // 当前 safeDelete 会把这个值写成 FAILED，所以这条测试现在会失败；
        // 修复 safeDelete 后应保持 CLEANUP_PENDING。
        assertEquals("CLEANUP_PENDING", storedObject().get("status"));
        assertEquals(0, sampleCount());
    }

    private Map<String, Object> storedObject() {
        return jdbc.queryForMap(
                "SELECT status, error_message FROM stored_object " +
                        "WHERE owner_id = ? AND related_resource_id = ? " +
                        "AND object_type = 'BENCHMARK_SOURCE' " +
                        "ORDER BY id DESC LIMIT 1",
                userId, versionId);
    }

    private int sampleCount() {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM benchmark_sample WHERE version_id = ?",
                Integer.class, versionId);
    }

    private int versionSampleCount() {
        return jdbc.queryForObject(
                "SELECT sample_count FROM benchmark_version WHERE id = ?",
                Integer.class, versionId);
    }

    @AfterEach
    void cleanup() {
        if (versionId != null) {
            jdbc.update("DELETE FROM benchmark_sample WHERE version_id = ?", versionId);
            jdbc.update("DELETE FROM stored_object WHERE related_resource_id = ? AND object_type = 'BENCHMARK_SOURCE'", versionId);
            jdbc.update("DELETE FROM benchmark_version WHERE id = ?", versionId);
        }
        if (benchmarkId != null) {
            jdbc.update("DELETE FROM benchmark WHERE id = ?", benchmarkId);
        }
        if (userId != null) {
            jdbc.update("DELETE FROM idempotency_record WHERE user_id = ?", userId);
            jdbc.update("DELETE FROM app_user WHERE id = ?", userId);
        }
    }
}