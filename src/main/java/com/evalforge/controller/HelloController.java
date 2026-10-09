package com.evalforge.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(){
        return "evalforge is running";
    }

    @GetMapping("/{id}")
    public long GetByID(@PathVariable long id){
        return id;
    }
    @GetMapping("api/v1/model")
    @ResponseBody
    public String getModelByName(@RequestParam String name){
        return "name:"+name;
    }

}