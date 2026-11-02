package com.evalforge.user.entity;

import java.time.LocalDateTime;

public class AppUser {
            private Long id;
            private String username;
            private String passwordHash;  
            private String role;
            private boolean enabled;
            private LocalDateTime createdAt;

            public AppUser(){}

            //默认普通用户构造器，role为USER
            public AppUser(String username, String passwordHash) {
                this.username = username;
                this.passwordHash = passwordHash;
                this.role = "USER";
                this.enabled=true;
                this.createdAt =LocalDateTime.now();
            }

            public Long getId() {
                return id;
            }
            public void setId(Long id) {
                this.id = id;
            }
            public String getUsername() {
                return username;
            }
            public void setUsername(String username) {
                this.username = username;
            }
            public String getPasswordHash() {
                return passwordHash;
            }
            public void setPasswordHash(String passwordHash) {
                this.passwordHash = passwordHash;
            }
            public String getRole() {
                return role;
            }
            public void setRole(String role) {
                this.role = role;
            }
            public boolean isEnabled() {
                return enabled;
            }
            public void setEnabled(boolean enabled) {
                this.enabled = enabled;
            }
            public LocalDateTime getCreatedAt() {
                return createdAt;
            }
            public void setCreatedAt(LocalDateTime createdAt) {
                this.createdAt = createdAt;
            }

}
