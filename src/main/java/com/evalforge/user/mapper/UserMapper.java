package com.evalforge.user.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.evalforge.user.entity.AppUser;

@Mapper 
public interface UserMapper {
            Optional<AppUser> findByUsername(String username);

            Optional<AppUser> findById(Long id);

            int insert(AppUser user);
}
