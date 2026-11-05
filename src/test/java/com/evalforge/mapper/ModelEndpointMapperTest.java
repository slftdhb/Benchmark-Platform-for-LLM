package com.evalforge.mapper;

import com.evalforge.dto.CreateModelEndpointRequest;
import com.evalforge.dto.ModelEndpointResponse;
import com.evalforge.dto.UpdateModelEndpointRequest;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.List;
import javax.sql.DataSource;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

// 真实 MyBatis 解析 XML 并执行 Mapper 代理；仅模拟 JDBC，不连接 MySQL。
class ModelEndpointMapperTest {
    private SqlSessionFactory sessionFactory;
    private PreparedStatement statement;

    @BeforeEach
    void setUp() throws Exception {
        DataSource dataSource = mock(DataSource.class);
        Connection connection = mock(Connection.class);
        statement = mock(PreparedStatement.class);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.getAutoCommit()).thenReturn(true);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.getUpdateCount()).thenReturn(1);

        Configuration configuration = new Configuration(new Environment(
                "test", new JdbcTransactionFactory(), dataSource));
        configuration.setMapUnderscoreToCamelCase(true);
        String resource = "mapper/ModelEndpointMapper.xml";
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(resource)) {
            assertNotNull(input);
            new XMLMapperBuilder(input, configuration, resource, configuration.getSqlFragments()).parse();
        }
        sessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    @Test
    void queriesMapRecordConstructorAndBindId() throws Exception {
        ResultSet results = mockResults();
        when(results.next()).thenReturn(true, false, true, false);
        ModelEndpointResponse expected = new ModelEndpointResponse(
                7L, false, "test-model", "test-provider", "model-v1", "https://example.test");
        try (SqlSession session = sessionFactory.openSession(true)) {
            ModelEndpointMapper mapper = session.getMapper(ModelEndpointMapper.class);
            assertEquals(expected, mapper.findById(7L).orElseThrow());
            assertEquals(List.of(expected), mapper.findAll());
        }
        verify(statement).setLong(1, 7L);
    }

    @Test
    void missingIdReturnsEmptyOptional() throws Exception {
        mockResults();
        try (SqlSession session = sessionFactory.openSession(true)) {
            assertTrue(session.getMapper(ModelEndpointMapper.class).findById(99L).isEmpty());
        }
    }

    @Test
    void insertBindsRequestRecordProperties() throws Exception {
        CreateModelEndpointRequest request = new CreateModelEndpointRequest(
                "test-model", "https://example.test", "test-provider", "model-v1");
        try (SqlSession session = sessionFactory.openSession(true)) {
            assertEquals(1, session.getMapper(ModelEndpointMapper.class).insert(request));
        }
        verify(statement).setString(1, request.name());
        verify(statement).setString(2, request.provider());
        verify(statement).setString(3, request.base_url());
        verify(statement).setString(4, request.model_name());
    }

    @Test
    void updateBindsBothNamedParameters() throws Exception {
        UpdateModelEndpointRequest request = new UpdateModelEndpointRequest(
                false, "updated-model", "provider", "https://example.test", "model-v2");
        try (SqlSession session = sessionFactory.openSession(true)) {
            assertEquals(1, session.getMapper(ModelEndpointMapper.class).update(7L, request));
        }
        verify(statement).setBoolean(1, false);
        verify(statement).setString(2, request.name());
        verify(statement).setString(3, request.provider());
        verify(statement).setString(4, request.base_url());
        verify(statement).setString(5, request.model_name());
        verify(statement).setLong(6, 7L);
    }

    @Test
    void deleteReturnsAffectedRows() throws Exception {
        when(statement.getUpdateCount()).thenReturn(0);
        try (SqlSession session = sessionFactory.openSession(true)) {
            assertEquals(0, session.getMapper(ModelEndpointMapper.class).delete(99L));
        }
        verify(statement).setLong(1, 99L);
    }

    private ResultSet mockResults() throws Exception {
        ResultSet results = mock(ResultSet.class);
        ResultSetMetaData metadata = mock(ResultSetMetaData.class);
        when(statement.execute()).thenReturn(true);
        when(statement.getResultSet()).thenReturn(results);
        when(results.getMetaData()).thenReturn(metadata);
        String[] columns = {"id", "enabled", "name", "provider", "model_name", "base_url"};
        when(metadata.getColumnCount()).thenReturn(columns.length);
        for (int i = 0; i < columns.length; i++) {
            when(metadata.getColumnLabel(i + 1)).thenReturn(columns[i]);
            when(metadata.getColumnType(i + 1)).thenReturn(
                    i == 0 ? Types.BIGINT : i == 1 ? Types.BOOLEAN : Types.VARCHAR);
            when(metadata.getColumnClassName(i + 1)).thenReturn(
                    i == 0 ? Long.class.getName() : i == 1 ? Boolean.class.getName() : String.class.getName());
        }
        when(results.getLong("id")).thenReturn(7L);
        when(results.getString("name")).thenReturn("test-model");
        when(results.getString("provider")).thenReturn("test-provider");
        when(results.getString("model_name")).thenReturn("model-v1");
        when(results.getString("base_url")).thenReturn("https://example.test");
        return results;
    }
}
