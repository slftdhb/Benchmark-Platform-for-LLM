package com.evalforge.auth.service;

import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.evalforge.auth.dto.LoginRequest;
import com.evalforge.auth.dto.LoginResponse;
import com.evalforge.auth.dto.RegisterRequest;
import com.evalforge.common.error.BusinessException;
import com.evalforge.common.error.ErrorCode;
import org.springframework.dao.DuplicateKeyException;
import com.evalforge.security.TokenService;
import com.evalforge.user.entity.AppUser;
import com.evalforge.user.mapper.UserMapper;

@Service 
public class AuthService {
        private final UserMapper userMapper;
        private final BCryptPasswordEncoder passwordEncoder;
        private final TokenService tokenService;

        //根据Spring注册的Bean进行依赖注入
        public AuthService(UserMapper userMapper, BCryptPasswordEncoder passwordEncoder, TokenService tokenService){
            this.userMapper=userMapper;
            this.passwordEncoder=passwordEncoder;
            this.tokenService=tokenService;
        }

        //*
        //注册用户
        //校验输入
        //编码密码
        //插入用户，并将role固定为USER  */
        @Transactional
        public Long register(RegisterRequest request){
                
                //编码密码
                String hashed = passwordEncoder.encode(request.password());
                
                AppUser user = new AppUser(request.username(),hashed);
                
                //注册的时候不先利用findbyname查找，可能会有并发竞争导致重复插入，利用数据库的唯一键约束来抛出key冲突异常
                try{
                    int affectedRows = userMapper.insert(user);
                    if(affectedRows != 1){
                        throw new BusinessException(ErrorCode.INTERNAL_ERROR,"user insert failed");
                    }
                }catch(DuplicateKeyException e){
                    throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "username already exists");
                }
                //利用mybatis的useGeneratedKeys自动主键回填，如果插入成功就返回生成的ID
                return user.getId();
        }

        //*
        // 用户登录
        // 校验输入
        // 查询存储密码
        // 返回结果 */
        public LoginResponse login(LoginRequest request){
                //没找到就返回null,函数接口返回值是optianal<AppUser>
                AppUser user = userMapper.findByUsername(request.username()).orElse(null);
                //如果没查到或者密码不匹配返回相同的错误，避免用用户名枚举攻击
                if(user == null || ! passwordEncoder.matches(request.password(),user.getPasswordHash())){
                        throw new BusinessException(ErrorCode.AUTHENTICATION_FAILED, "invalid username or password");
                }

                if(!user.isEnabled()){
                    throw new BusinessException(ErrorCode.ACCOUNT_DISABLED, "account is disabled");
                }
                
                //登录成功后就颁发token
                String token = tokenService.issueAccessToken(user);
                return new LoginResponse(token,"Bearer",tokenService.accessTtlSeconds());

        }


}
