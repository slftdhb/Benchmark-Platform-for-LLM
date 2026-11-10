package com.evalforge.storage;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.client.EntityExchangeResult;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import com.evalforge.auth.dto.LoginResponse;
import com.evalforge.benchmark.dto.BenchmarkImportResponse;
import com.evalforge.storage.service.StorageService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BenchmarkImportDownloadTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StorageService storageService;

    private RestTestClient client;

    private Long userId;
    private Long benchmarkId;
    private Long versionId;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void importJsonlThenDownloadSource() {
        // 1. 注册 + 登录
        String username = "it_" + UUID.randomUUID().toString()
                .replace("-", "").substring(0, 10);
        String password = "Passw0rd!";

        EntityExchangeResult<Long> registered = client.post()
                .uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("username", username, "password", password))
                .exchange()
                .returnResult(Long.class);

        assertStatus(registered, 200);
        userId = registered.getResponseBody();
        assertNotNull(userId);

        EntityExchangeResult<LoginResponse> login = client.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("username", username, "password", password))
                .exchange()
                .returnResult(LoginResponse.class);

        assertStatus(login, 200);
        String token = login.getResponseBody().accessToken();
        assertNotNull(token);

        // 2. 创建 benchmark（自动创建 version 1 DRAFT）
        String benchmarkName = "it-benchmark-" + UUID.randomUUID();

        EntityExchangeResult<Long> created = client.post()
                .uri("/api/v1/benchmarks")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("Idempotency-Key", UUID.randomUUID().toString())
                .body(Map.of(
                        "name", benchmarkName,
                        "description", "integration test"))
                .exchange()
                .returnResult(Long.class);

        assertStatus(created, 200);
        benchmarkId = created.getResponseBody();
        assertNotNull(benchmarkId);

        // 3. 当前 API 没有直接返回 versionId，测试里查库
        versionId = jdbc.queryForObject(
                "SELECT id FROM benchmark_version WHERE benchmark_id = ? ORDER BY version_no LIMIT 1",
                Long.class, benchmarkId);
        assertNotNull(versionId);

        // 4. 上传 JSONL
        String jsonl = """
                {"sample_key":"s1","input":{"question":"1+1"},"gold":{"answer":"2"}}
                {"sample_key":"s2","input":{"question":"2+2"},"gold":{"answer":"4"}}
                """;
        byte[] content = jsonl.getBytes(StandardCharsets.UTF_8);

        MultipartBodyBuilder parts = new MultipartBodyBuilder();
        parts.part("file", new ByteArrayResource(content))
                .filename("samples.jsonl")
                .contentType(MediaType.APPLICATION_OCTET_STREAM);

        EntityExchangeResult<BenchmarkImportResponse> uploaded = client.post()
                .uri("/api/v1/benchmark-versions/{versionId}/samples/import", versionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(parts.build())
                .exchange()
                .returnResult(BenchmarkImportResponse.class);

        assertStatus(uploaded, 200);
        assertNotNull(uploaded.getResponseBody());
        assertEquals(2, uploaded.getResponseBody().sampleCount());
        assertNotNull(uploaded.getResponseBody().contentHash());

        // 5. 校验数据库
        Map<String, Object> stored = jdbc.queryForMap(
                "SELECT bucket_name, object_key, status, sha256 " +
                        "FROM stored_object " +
                        "WHERE owner_id = ? AND related_resource_id = ? " +
                        "AND object_type = 'BENCHMARK_SOURCE' " +
                        "ORDER BY id DESC LIMIT 1",
                userId, versionId);

        assertEquals("READY", stored.get("status"));
        assertNotNull(stored.get("sha256"));

        Integer sampleCount = jdbc.queryForObject(
                "SELECT sample_count FROM benchmark_version WHERE id = ?",
                Integer.class, versionId);
        assertEquals(2, sampleCount);

        Integer sampleRows = jdbc.queryForObject(
                "SELECT COUNT(*) FROM benchmark_sample WHERE version_id = ?",
                Integer.class, versionId);
        assertEquals(2, sampleRows);

        // 6. 下载并比对内容
        ExchangeResult downloaded = client.get()
                .uri("/api/v1/benchmark-versions/{versionId}/source/download", versionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .returnResult();

        assertStatus(downloaded, 200);
        assertArrayEquals(content, downloaded.getResponseBodyContent());

        String disposition = downloaded.getResponseHeaders()
                .getFirst(HttpHeaders.CONTENT_DISPOSITION);
        assertNotNull(disposition);
        assertTrue(disposition.contains("samples.jsonl"));
    }

    private void assertStatus(ExchangeResult result, int expected) {
        assertEquals(expected, result.getStatus().value(),
                () -> "unexpected status, body="
                        + new String(result.getResponseBodyContent(), StandardCharsets.UTF_8));
    }

    @AfterEach
    void cleanup() {
        try {
            if (userId != null) {
                List<Map<String, Object>> objects = jdbc.queryForList(
                        "SELECT bucket_name, object_key FROM stored_object WHERE owner_id = ?",
                        userId);
                for (Map<String, Object> object : objects) {
                    try {
                        storageService.delete(
                                (String) object.get("bucket_name"),
                                (String) object.get("object_key"));
                    } catch (Exception ignored) {
                    }
                }
            }

            if (versionId != null) {
                jdbc.update("DELETE FROM benchmark_sample WHERE version_id = ?", versionId);
                jdbc.update("DELETE FROM benchmark_version WHERE id = ?", versionId);
            } else if (benchmarkId != null) {
                jdbc.update("DELETE FROM benchmark_version WHERE benchmark_id = ?", benchmarkId);
            }

            if (userId != null) {
                jdbc.update("DELETE FROM stored_object WHERE owner_id = ?", userId);
                jdbc.update("DELETE FROM idempotency_record WHERE user_id = ?", userId);
            }

            if (benchmarkId != null) {
                jdbc.update("DELETE FROM benchmark WHERE id = ?", benchmarkId);
            }

            if (userId != null) {
                jdbc.update("DELETE FROM app_user WHERE id = ?", userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}