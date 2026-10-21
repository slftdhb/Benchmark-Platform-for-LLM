package com.evalforge.benchmark;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.evalforge.demo.RedisDemo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest 
class Redis{

        @Autowired
        private RedisDemo demo;

        @Test
        void shouldReadAndWrite(){
            demo.write();
            assertEquals("Evalforge",demo.read());
            
        }
}