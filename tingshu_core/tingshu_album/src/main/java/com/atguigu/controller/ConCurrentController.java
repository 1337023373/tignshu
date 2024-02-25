package com.atguigu.controller;

import com.atguigu.service.SetNumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "并发管理接口")
@RestController
@RequestMapping("/api/album")
public class ConCurrentController {

    @Autowired
    private SetNumService setNumService;
    @Operation(summary = "例子")
    @GetMapping("/setNum")
    public String setNum() {
        setNumService.setNum();
        return "success";
    }
}
