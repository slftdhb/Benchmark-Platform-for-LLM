package com.evalforge.mapper;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;   
import com.evalforge.entity.ModelEndpoint;
import java.util.List;
import com.evalforge.dto.UpdateModelEndpointRequest;
import com.evalforge.dto.CreateModelEndpointRequest;
import com.evalforge.dto.ModelEndpointResponse;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Optional;

//需要注意，mapper层本身只关注对数据库的操作，以及原始返回

@Repository
public class ModelEndpointMapper {

    private final JdbcTemplate jdbcTemplate;

    //创建对象的方法
    public ModelEndpointMapper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //新增一个模型
    public int insert(CreateModelEndpointRequest request){
        String sql="""
                    INSERT INTO model_endpoint
                    (name,provider, base_url, model_name)
                    VALUES (?,?,?,?)
        """;
        return jdbcTemplate.update(sql, request.name(), request.provider(), request.base_url(), request.model_name());
    }

    //公共方法返回查询的对象
    public ModelEndpointResponse mapRow(ResultSet rs,int rowNum) throws SQLException{
            return new ModelEndpointResponse(
                rs.getLong("id"),
                rs.getBoolean("enabled"),
                rs.getString("name"),
                rs.getString("provider"),
                rs.getString("model_name"),
                rs.getString("base_url")
            );
        }

    //根据ID查找模型端点,record是不可变对象不会自动生成set方法
    public Optional<ModelEndpointResponse> findById(long id){
        String sql =""" 
                    SELECT * FROM model_endpoint Where id = ?
        """;

        List <ModelEndpointResponse> results= jdbcTemplate.query(sql,this::mapRow,id);

        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    //根据id修改某个模型端点
    public int update(long id, UpdateModelEndpointRequest request){

        String sql="""
                UPDATE model_endpoint
                SET enabled = ?, name = ?, provider = ?, base_url = ?, model_name = ?
                WHERE id = ?
                """;
        return jdbcTemplate.update(sql, request.enabled(), request.name(), request.provider(), request.base_url(), request.model_name(), id);
    
    
    }

    //查找所有的模型
    public List<ModelEndpointResponse>  findAll(){
        String sql ="""
                    SELECT * FROM model_endpoint
        """;

        return jdbcTemplate.query(sql,this::mapRow);
    }

    //根据ID删除模型端点
    public int delete(long id){
        String sql="""
            DELETE FROM model_endpoint WHERE id = ?
            """;
        return jdbcTemplate.update(sql,id);
    }

}