# TCP_Exam_System 网络编程实验系统

## 项目功能
- 用户管理：考生、管理员、阅卷人三角色登录
- 题库管理：添加单选、多选、填空、问答题
- 试卷管理：自动组卷、手动组卷、发布试卷
- 考试流程：考生答题、自动保存、提交判分
- 阅卷流程：主观题批阅、分数统计

## 环境依赖
- JDK 8+
- Oracle 12c/18c/19c
- ojdbc8-19.3.0.0.jar（已放入lib目录）

## 运行步骤
1. 启动服务器：运行 com.server.ExamServer
2. 启动客户端：运行 com.client.ExamClient
3. 按角色登录（测试账号：student1/123456、admin1/admin123、grader1/grader123）