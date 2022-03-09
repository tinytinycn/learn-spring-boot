package com.example.usercenter.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "测试", description = "这是一个测试控制类")
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/user/{id}")
    @Parameter(name = "id", description = "用户id", in = ParameterIn.PATH, required = true)
    public Object getUser(@PathVariable Integer id) {
        return "user";
    }
}
