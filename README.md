```markdown
# Online Exam System based on TCP Socket Programming

## 📄 Project Overview

This is a **multi-role online examination system** built with **Java TCP Socket programming**. The system supports three types of users — **Administrators**, **Students**, and **Graders** — each with distinct functional permissions. It implements a complete exam workflow including question bank management, automated test paper generation, real-time exam taking with auto-save, objective question auto-grading, subjective question manual grading, and score statistics.

This project was developed as a coursework assignment for the **Network Programming** course.

---

## 👥 Roles & Features

| Role | Features |
|------|----------|
| **Administrator** | Add questions to question bank; Create and publish exam papers (manual or auto-generated); View and export score statistics |
| **Student** | Take exams; Auto-save answers during exam; Submit papers; View objective question scores immediately |
| **Grader** | View pending grading list; Grade subjective questions (with scores and comments); Auto-calculate total scores |

---

## 🏗️ System Architecture

- **Client-Server Model**: Built on TCP/IP using Java `Socket` and `ServerSocket`
- **Database**: MySQL for persistent storage of users, questions, papers, and answer records
- **Design Patterns**: Object-oriented design with layered architecture (UI → Controller → Service → DAO)

### Key UML Diagrams (included in report)

- Use Case Diagrams
- Sequence Diagrams
- Class Diagrams

---

## 📁 Project Structure

```
Online-Exam-System-TCP/
├── src/
│   ├── client/            # Client-side UI and controllers
│   ├── server/            # Server-side business logic
│   ├── model/             # Entity classes (User, Question, Paper, Answer, etc.)
│   ├── dao/               # Database access layer
│   ├── service/           # Business service layer
│   └── util/              # Utility classes
├── lib/                   # External dependencies (JDBC driver, etc.)
├── bin/                   # Compiled class files
├── exam_user.sql          # Database schema and sample data
├── README.md
└── .vscode/               # VS Code configuration
```

---

## 🗄️ Database Schema (Key Tables)

| Table | Description |
|-------|-------------|
| `USERS` | User accounts (student/admin/grader) |
| `QUESTIONS` | Question bank with types (single choice, multiple choice, fill-in, essay) |
| `EXAM_PAPERS` | Exam paper metadata (course, duration, total score, status) |
| `PAPER_QUESTIONS` | Many-to-many relationship between papers and questions |
| `ANSWER_SHEETS` | Student answer records for each exam attempt |
| `ANSWER_DETAILS` | Detailed answers for each question in a sheet |

---

## 🚀 Quick Start

### Prerequisites

- Java 8 or higher
- MySQL 5.7 or higher
- Eclipse / IntelliJ IDEA (recommended)

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/Online-Exam-System-TCP.git
   ```

2. **Import the database**
   ```bash
   mysql -u root -p < exam_user.sql
   ```

3. **Configure database connection**
   - Update JDBC connection settings in `src/util/DBUtil.java`

4. **Run the server**
   - Start `com.server.ExamServer.java`

5. **Run the client**
   - Start `com.client.ExamClient.java`
   - Use the following default accounts:
     - Admin: `admin1` / `admin123`
     - Student: `student1` / `123456`
     - Grader: `grader1` / `grader123`

---

## 📸 Sample Workflow

### 1. Admin Login & Question Addition

- Login as admin → Select "Add Question" → Enter question details (type, stem, options, answer, knowledge point, difficulty, score) → Question saved to bank.

### 2. Auto-Generate & Publish Paper

- Set paper strategy (e.g., 3 single-choice, 2 fill-in, 1 multiple-choice, 1 essay) → System automatically retrieves questions from bank → Preview generated paper → Publish.

### 3. Student Takes Exam

- Login as student → Select a published paper → Answer questions sequentially → Answers auto-saved on each switch → Submit → Objective questions graded automatically.

### 4. Grader Grades Subjective Questions

- Login as grader → View pending papers → Grade each essay question with score and comment → System auto-calculates total score.

---

## 🔧 Core Technologies

- **Java SE** (Socket, ServerSocket, I/O streams, multithreading)
- **TCP/IP Protocol** – reliable connection-oriented communication
- **MySQL** – data persistence
- **JDBC** – database connectivity
- **Object-Oriented Design** – inheritance, polymorphism, encapsulation

---

## 📚 What I Learned

- Mastered TCP socket programming with Java (`Socket`, `ServerSocket`, `InputStream`/`OutputStream`)
- Practiced object-oriented analysis and design using UML (use case, sequence, and class diagrams)
- Implemented multi-role permission management and data consistency in a client-server system
- Designed auto-save mechanisms and objective question auto-grading logic
- Gained hands-on experience with MySQL-JDBC integration and transaction management

---

## 📧 Contact

For questions or suggestions, please open an issue or contact the repository owner.

---

## 📝 License

This project is for educational purposes only.
```

---

### **README.md (中文)**

```markdown
# 基于TCP Socket的在线考试系统

## 📄 项目简介

本项目是一个基于 **Java TCP Socket 编程** 的多角色在线考试系统。系统支持三种用户角色 —— **管理员**、**考生**、**阅卷人**，各自拥有不同的功能权限，实现了从题库管理、自动组卷、在线考试（含自动保存）、客观题自动判分、主观题人工批阅到成绩统计的完整考试流程。

本实验为《网络编程》课程的 TCP 编程实验项目。

---

## 👥 角色与功能

| 角色 | 功能 |
|------|------|
| **管理员** | 添加题目到题库；手动/自动组卷并发布试卷；查询与导出成绩统计 |
| **考生** | 参加考试；答题时自动保存答案；提交试卷；查看客观题得分 |
| **阅卷人** | 查看待批阅试卷；批阅主观题（评分+评语）；自动计算总分 |

---

## 🏗️ 系统架构

- **通信模型**：基于 TCP/IP 的 C/S 架构，使用 Java `Socket` 和 `ServerSocket`
- **数据库**：MySQL 存储用户、题目、试卷、答题记录等数据
- **设计模式**：面向对象分层设计（UI → Controller → Service → DAO）

### 包含的 UML 图（详见实验报告）

- 用例图
- 顺序图
- 类图

---

## 📁 项目结构

```
Online-Exam-System-TCP/
├── src/
│   ├── client/            # 客户端界面与控制层
│   ├── server/            # 服务端业务逻辑
│   ├── model/             # 实体类（User、Question、Paper、Answer等）
│   ├── dao/               # 数据访问层
│   ├── service/           # 业务服务层
│   └── util/              # 工具类
├── lib/                   # 外部依赖（JDBC驱动等）
├── bin/                   # 编译输出目录
├── exam_user.sql          # 数据库建表脚本及示例数据
├── README.md
└── .vscode/               # VS Code 配置
```

---

## 🗄️ 数据库表结构（核心表）

| 表名 | 说明 |
|------|------|
| `USERS` | 用户账户（考生/管理员/阅卷人） |
| `QUESTIONS` | 题库（单选/多选/填空/问答） |
| `EXAM_PAPERS` | 试卷信息（课程、时长、总分、状态） |
| `PAPER_QUESTIONS` | 试卷与题目的多对多关联 |
| `ANSWER_SHEETS` | 答卷记录 |
| `ANSWER_DETAILS` | 每道题的详细答题与得分 |

---

## 🚀 快速启动

### 环境要求

- Java 8 或更高版本
- MySQL 5.7 或更高版本
- Eclipse / IntelliJ IDEA（推荐）

### 步骤

1. **克隆项目**
   ```bash
   git clone https://github.com/yourusername/Online-Exam-System-TCP.git
   ```

2. **导入数据库**
   ```bash
   mysql -u root -p < exam_user.sql
   ```

3. **配置数据库连接**
   - 修改 `src/util/DBUtil.java` 中的 JDBC 连接参数

4. **启动服务端**
   - 运行 `com.server.ExamServer.java`

5. **启动客户端**
   - 运行 `com.client.ExamClient.java`
   - 默认测试账号：
     - 管理员：`admin1` / `admin123`
     - 考生：`student1` / `123456`
     - 阅卷人：`grader1` / `grader123`

---

## 📸 操作流程示例

### 1. 管理员登录 → 添加题目

登录管理员账号 → 选择"添加题目" → 输入题型、题干、选项、答案、知识点、难度、分值 → 题目入库。

### 2. 自动组卷与发布

设置组卷策略（如：3道单选 + 2道填空 + 1道多选 + 1道问答） → 系统按策略从题库抽题 → 预览试卷 → 发布。

### 3. 考生参加考试

考生登录 → 选择已发布试卷 → 逐题作答（切换题目时自动保存） → 提交试卷 → 客观题自动判分。

### 4. 阅卷人批阅主观题

阅卷人登录 → 查看待批阅试卷 → 逐题评分并填写评语 → 系统自动计算总分。

---

## 🔧 核心技术

- **Java SE**（Socket、ServerSocket、I/O流、多线程）
- **TCP/IP 协议** —— 面向连接的可靠通信
- **MySQL** —— 数据持久化存储
- **JDBC** —— 数据库连接与操作
- **面向对象设计** —— 继承、多态、封装

---

## 📚 实验收获

- 掌握了 Java TCP Socket 编程的核心范式（Socket、ServerSocket、输入/输出流）
- 实践了面向对象的分析与设计方法，熟练绘制用例图、顺序图、类图
- 实现了多角色权限管理与数据一致性控制
- 设计了自动保存、客观题自动判分等实用功能
- 积累了 MySQL + JDBC 的整合开发与事务处理经验

---

## 📧 联系方式

如有问题或建议，欢迎提交 Issue 或联系仓库所有者。

---

## 📝 许可证

本项目仅供学习交流使用。
```

---

如果您还需要其他内容（如PPT版本的项目介绍等），随时告诉我！
