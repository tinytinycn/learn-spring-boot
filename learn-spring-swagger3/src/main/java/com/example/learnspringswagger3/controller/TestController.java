package com.example.learnspringswagger3.controller;

import com.example.learnspringswagger3.domain.req.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"测试相关"}, hidden = false)
@RestController
@RequestMapping(value = "/路由名")
@Slf4j
public class TestController {

    @ApiOperation(value = "GET请求", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "参数id", required = true, dataTypeClass = Integer.class, paramType = "query"),
    })
    @GetMapping("/getOne")
    public Object getOne(@RequestParam Integer id) {
        return "getOne";
    }

    @ApiOperation(value = "POST请求", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "参数id", required = true, dataTypeClass = Integer.class, paramType = "query"),
    })
    @PostMapping("/postOne")
    public Object postOne(@RequestParam Integer id) {
        return "postOne";
    }


    @ApiOperation(value = "POST请求2", notes = "")
    @PostMapping("/postUser")
    public Object postUser(@RequestBody @Validated User user) {
        System.out.println("user = " + user.toString());
        return "postUser";
    }
}
