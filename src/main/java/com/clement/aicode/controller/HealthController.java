package com.clement.aicode.controller;

import com.clement.aicode.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "ok");
        data.put("project", "ai-code-training-platform");
        data.put("message", "AI Code Training Platform is running");

        return Result.success(data);
    }
}