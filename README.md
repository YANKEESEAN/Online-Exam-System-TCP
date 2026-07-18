# 📚 Online Exam System based on TCP Socket Programming

## 📌 Project Overview
This is a multi-role online examination system built with **Java TCP Socket** programming. The system supports three types of users — **Administrators**, **Students**, and **Graders** — each with distinct functional permissions. It implements a complete exam workflow including question bank management, automated test paper generation, real-time exam taking with auto-save, objective question auto-grading, subjective question manual grading, and score statistics.

This project was developed as a coursework assignment for the **Network Programming** course.

---

## 👥 Roles & Features

| Role | Features |
| :---: | :--- |
| **Administrator** | • Add questions to question bank<br>• Create and publish exam papers (manual or auto-generated)<br>• View and export score statistics |
| **Student** | • Take exams<br>• Auto-save answers during exam<br>• Submit papers<br>• View objective question scores immediately |
| **Grader** | • View pending grading list<br>• Grade subjective questions (with scores and comments)<br>• Auto-calculate total scores |

---

## 🏗️ System Architecture

| Component | Description |
| :---: | :--- |
| **Client-Server Model** | Built on TCP/IP using Java `Socket` and `ServerSocket` |
| **Database** | MySQL for persistent storage of users, questions, papers, and answer records |
| **Design Pattern** | Object-oriented design with layered architecture (UI → Controller → Service → DAO) |

### 📊 UML Diagrams (included in report)
- Use Case Diagrams
- Sequence Diagrams
- Class Diagrams

---

## 📁 Project Structure
```text
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
| :---: | :--- |
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

### Setup Steps

**1. Clone the repository**
```bash
git clone https://github.com/yourusername/Online-Exam-System-TCP.git
```

**2. Import the database**
```bash
mysql -u root -p < exam_user.sql
```

**3. Configure database connection**  
Update JDBC connection settings in `src/util/DBUtil.java`

**4. Run the server**  
Start `com.server.ExamServer.java`

**5. Run the client**  
Start `com.client.ExamClient.java`

### 🔑 Default Accounts

| Role | Username | Password |
| :---: | :---: | :---: |
| Administrator | admin1 | admin123 |
| Student | student1 | 123456 |
| Grader | grader1 | grader123 |

---

## 📸 Sample Workflow

### 1. Admin Login & Question Addition
Login as admin → Select "Add Question" → Enter question details (type, stem, options, answer, knowledge point, difficulty, score) → Question saved to bank.

### 2. Auto-Generate & Publish Paper
Set paper strategy (e.g., 3 single-choice, 2 fill-in, 1 multiple-choice, 1 essay) → System automatically retrieves questions from bank → Preview generated paper → Publish.

### 3. Student Takes Exam
Login as student → Select a published paper → Answer questions sequentially → Answers auto-saved on each switch → Submit → Objective questions graded automatically.

### 4. Grader Grades Subjective Questions
Login as grader → View pending papers → Grade each essay question with score and comment → System auto-calculates total score.

---

## 💻 Core Technologies
- Java SE (`Socket`, `ServerSocket`, I/O streams, multithreading)
- TCP/IP Protocol – reliable connection-oriented communication
- MySQL – data persistence
- JDBC – database connectivity
- Object-Oriented Design – inheritance, polymorphism, encapsulation

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

## 📜 License
This project is for educational purposes only.


# 📚 基于TCP Socket编程的在线考试系统

## 📌 项目概述
本项目是一个基于**Java TCP Socket**编程的多角色在线考试系统。系统支持三种用户角色——**管理员**、**学生**和**阅卷教师**，每种角色拥有不同的功能权限。系统实现了完整的考试流程，包括题库管理、自动组卷、实时考试与自动保存、客观题自动批改、主观题人工批改以及成绩统计等功能。

该项目是**网络编程**课程的课程作业。

---

## 👥 角色与功能

| 角色 | 功能 |
| :---: | :--- |
| **管理员** | • 添加题目到题库<br>• 创建并发布试卷（手动或自动生成）<br>• 查看和导出成绩统计 |
| **学生** | • 参加考试<br>• 考试过程中自动保存答案<br>• 提交试卷<br>• 立即查看客观题得分 |
| **阅卷教师** | • 查看待批改列表<br>• 批改主观题（含评分和评语）<br>• 自动计算总分 |

---

## 🏗️ 系统架构

| 组件 | 说明 |
| :---: | :--- |
| **客户端-服务器模型** | 基于TCP/IP协议，使用Java `Socket` 和 `ServerSocket` 实现 |
| **数据库** | MySQL，用于持久化存储用户、题目、试卷和答题记录 |
| **设计模式** | 面向对象设计，采用分层架构（UI层 → 控制器层 → 服务层 → DAO层） |

### 📊 UML图（包含在报告中）
- 用例图
- 时序图
- 类图

---

## 📁 项目结构
```text
Online-Exam-System-TCP/
├── src/
│   ├── client/            # 客户端界面和控制器
│   ├── server/            # 服务端业务逻辑
│   ├── model/             # 实体类（User、Question、Paper、Answer等）
│   ├── dao/               # 数据库访问层
│   ├── service/           # 业务服务层
│   └── util/              # 工具类
├── lib/                   # 外部依赖（JDBC驱动等）
├── bin/                   # 编译后的class文件
├── exam_user.sql          # 数据库结构和示例数据
├── README.md
└── .vscode/               # VS Code配置
```

---

## 🗄️ 数据库表结构（核心表）

| 表名 | 说明 |
| :---: | :--- |
| `USERS` | 用户账户（学生/管理员/阅卷教师） |
| `QUESTIONS` | 题库，包含题型（单选、多选、填空、简答） |
| `EXAM_PAPERS` | 试卷元数据（课程、时长、总分、状态） |
| `PAPER_QUESTIONS` | 试卷与题目的多对多关系表 |
| `ANSWER_SHEETS` | 学生每次考试的答题记录 |
| `ANSWER_DETAILS` | 答题卷中每道题的详细答案 |

---

## 🚀 快速开始

### 环境要求
- Java 8 或更高版本
- MySQL 5.7 或更高版本
- Eclipse / IntelliJ IDEA（推荐）

### 安装步骤

**1. 克隆仓库**
```bash
git clone https://github.com/yourusername/Online-Exam-System-TCP.git
```

**2. 导入数据库**
```bash
mysql -u root -p < exam_user.sql
```

**3. 配置数据库连接**  
修改 `src/util/DBUtil.java` 中的JDBC连接设置

**4. 启动服务端**  
运行 `com.server.ExamServer.java`

**5. 启动客户端**  
运行 `com.client.ExamClient.java`

### 🔑 默认账号

| 角色 | 用户名 | 密码 |
| :---: | :---: | :---: |
| 管理员 | admin1 | admin123 |
| 学生 | student1 | 123456 |
| 阅卷教师 | grader1 | grader123 |

---

## 📸 操作流程示例

### 1. 管理员登录并添加题目
管理员登录 → 选择"添加题目" → 输入题目信息（题型、题干、选项、答案、知识点、难度、分值）→ 题目保存至题库。

### 2. 自动组卷并发布试卷
设置组卷策略（如：3道单选、2道填空、1道多选、1道简答）→ 系统自动从题库抽取题目 → 预览生成的试卷 → 发布试卷。

### 3. 学生参加考试
学生登录 → 选择已发布的试卷 → 按顺序作答 → 切换题目时自动保存答案 → 提交试卷 → 客观题自动批改并显示得分。

### 4. 阅卷教师批改主观题
阅卷教师登录 → 查看待批改试卷 → 逐题批改简答题并给出分数和评语 → 系统自动计算总分。

---

## 💻 核心技术
- Java SE（`Socket`、`ServerSocket`、I/O流、多线程）
- TCP/IP协议 — 可靠的面向连接通信
- MySQL — 数据持久化存储
- JDBC — 数据库连接
- 面向对象设计 — 继承、多态、封装

---

## 📚 项目收获
- 掌握了Java TCP Socket编程（`Socket`、`ServerSocket`、`InputStream`/`OutputStream`）
- 实践了使用UML进行面向对象分析与设计（用例图、时序图、类图）
- 实现了多角色权限管理和客户端-服务器系统的数据一致性
- 设计了自动保存机制和客观题自动批改逻辑
- 获得了MySQL-JDBC集成和事务管理的实战经验

---

## 📧 联系方式
如有问题或建议，请提交Issue或联系仓库所有者。

---

## 📜 许可证
本项目仅供学习使用。
