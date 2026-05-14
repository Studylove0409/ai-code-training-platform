# AI 编程学习训练平台

## 项目目标

这是一个面向程序员的 AI 学习训练系统，用于记录学习任务、技术笔记、Bug 复盘，并通过 AI 模拟面试与生成学习反馈。

## 技术栈

Java 17、Spring Boot、MySQL、Redis、JWT、Docker、AI API

## 当前进度

Day 1：项目初始化，完成 /health 接口，完成 MySQL 连接验证。

## 已完成接口

### 健康检查

GET /health

### 数据库连接检查

GET /health/db

## 本地启动

### 1. 创建数据库

```sql
CREATE DATABASE ai_code_training_platform
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;