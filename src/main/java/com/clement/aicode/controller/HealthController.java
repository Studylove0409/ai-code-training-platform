package com.clement.aicode.controller;

import com.clement.aicode.common.Result;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "ok");
        data.put("project", "ai-code-training-platform");
        data.put("message", "AI Code Training Platform is running");

        return Result.success(data);
    }

    @GetMapping("/health/db")
    public Result<Map<String, Object>> dbHealth() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        Map<String, Object> data = new HashMap<>();
        data.put("status", "ok");
        data.put("database", "mysql");
        data.put("result", result);
        data.put("message", "MySQL connection is successful");

        return Result.success(data);
    }
}