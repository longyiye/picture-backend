package com.yiye.controller;

import com.yiye.common.BaseResponse;
import com.yiye.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class TestController {

    /**
     * 测试接口
     */
    @GetMapping("/test")
    public BaseResponse<String> test() {
        return ResultUtils.success("test success");
    }

}
