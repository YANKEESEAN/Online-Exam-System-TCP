package com.server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import com.db.DBUtil;
import com.entity.User;
import com.entity.Question;
import com.entity.ExamPaper;
import com.entity.PaperQuestion;
import com.entity.AnswerSheet;
import com.entity.AnswerDetail;
import com.service.PaperService;
import com.service.AnswerService;
import com.service.GradingService;

public class ExamServer {
    private static final int PORT = 8888;
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        new ExamServer().start();
        System.out.println("系统默认编码: " + System.getProperty("file.encoding"));
        System.out.println("系统默认字符集: " + java.nio.charset.Charset.defaultCharset());
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("服务器启动，端口：" + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;
        private User currentUser;
        private PaperService paperService = new PaperService();
        private AnswerService answerService = new AnswerService();
        private GradingService gradingService = new GradingService();

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                // 设置字符编码
                socket.setSoTimeout(0);
                // 创建输入输出流时指定编码
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.flush(); // 重要：先刷新输出流
                ois = new ObjectInputStream(socket.getInputStream());

                // 处理登录
                if (!handleLogin()) {
                    oos.writeObject("登录失败：账号或密码错误");
                    close();
                    return;
                }

                // 按角色分发功能
                if ("ADMIN".equals(currentUser.getRole())) {
                    handleAdmin();
                } else if ("GRADER".equals(currentUser.getRole())) {
                    handleGrader();
                } else if ("STUDENT".equals(currentUser.getRole())) {
                    handleStudent();
                }

                close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 处理登录逻辑，补充发送功能列表
        private boolean handleLogin() throws Exception {
            User user = (User) ois.readObject();
            Connection conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT id, role, total_score, exam_count FROM users WHERE username = ? AND password = ?");
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentUser = new User();
                currentUser.setId(rs.getInt("id"));
                currentUser.setUsername(user.getUsername());
                currentUser.setRole(rs.getString("role"));
                currentUser.setTotalScore(rs.getInt("total_score"));
                currentUser.setExamCount(rs.getInt("exam_count"));
                DBUtil.close(conn, pstmt, rs);

                // 发送登录成功信息
                oos.writeObject("登录成功！当前角色：" + currentUser.getRole());
                oos.writeObject(currentUser.getRole());

                // 根据角色发送功能列表
                if ("ADMIN".equals(currentUser.getRole())) {
                    oos.writeObject("功能列表：1-创建试卷 2-自动组卷 3-发布试卷 4-添加题目 5-退出");
                } else if ("STUDENT".equals(currentUser.getRole())) {
                } else if ("GRADER".equals(currentUser.getRole())) {
                    oos.writeObject("功能列表：1-查看待批阅试卷 2-批阅主观题 3-退出");
                }
                oos.flush(); // 强制刷新，确保客户端收到
                return true;
            }
            DBUtil.close(conn, pstmt, rs);
            return false;
        }

        // 管理员功能处理
        private void handleAdmin() throws Exception {
            while (true) {
                // 读取客户端输入的选择
                String choice = (String) ois.readObject();
                switch (choice) {
                    case "1":
                        createPaper();
                        break;
                    case "2":
                        autoGeneratePaper();
                        break;
                    case "3":
                        publishPaper();
                        break;
                    case "4":
                        addQuestion();
                        break;
                    case "5":
                        oos.writeObject("退出成功");
                        oos.flush();
                        return;
                    default:
                        oos.writeObject("无效选择，请重新输入");
                        oos.flush();
                }
                // 每次操作后重新发送功能列表
                oos.writeObject("功能列表：1-创建试卷 2-自动组卷 3-发布试卷 4-添加题目 5-退出");
                oos.flush();
            }
        }

        // 创建试卷
        private void createPaper() throws Exception {
            // ... (保持不变)
            oos.writeObject("请输入试卷名称：");
            oos.flush();
            String paperName = (String) ois.readObject();

            oos.writeObject("请输入课程名称：");
            oos.flush();
            String course = (String) ois.readObject();

            oos.writeObject("请输入考试时长（分钟）：");
            oos.flush();
            String durationStr = (String) ois.readObject();
            int duration;
            try {
                duration = Integer.parseInt(durationStr);
            } catch (NumberFormatException e) {
                oos.writeObject("考试时长输入格式错误，操作取消。");
                oos.flush();
                return;
            }

            oos.writeObject("请输入试卷总分：");
            oos.flush();
            String totalScoreStr = (String) ois.readObject();
            int totalScore;
            try {
                totalScore = Integer.parseInt(totalScoreStr);
            } catch (NumberFormatException e) {
                oos.writeObject("试卷总分输入格式错误，操作取消。");
                oos.flush();
                return;
            }

            ExamPaper paper = new ExamPaper();
            paper.setPaperName(paperName);
            paper.setCourse(course);
            paper.setDuration(duration);
            paper.setTotalScore(totalScore);
            paper.setCreatorId(currentUser.getId());
            int paperId = paperService.createPaper(paper);
            oos.writeObject("试卷创建成功！ID：" + paperId);
            oos.flush();
        }

        // 自动组卷
        private void autoGeneratePaper() throws Exception {
            oos.writeObject("请输入要自动组卷的试卷ID：");
            oos.flush();
            String paperIdStr = (String) ois.readObject();
            int paperId;
            try {
                paperId = Integer.parseInt(paperIdStr);
            } catch (NumberFormatException e) {
                oos.writeObject("试卷ID输入格式错误，操作取消。");
                oos.flush();
                return;
            }

            // 循环允许用户添加多种题型的策略
            while (true) {
                oos.writeObject("\n请选择操作：1-设置新题型策略 2-完成组卷");
                oos.flush();
                String action = (String) ois.readObject();

                if ("2".equals(action)) {
                    break; // 完成组卷，退出循环
                } else if ("1".equals(action)) {
                    // --- 读取题型策略 ---

                    // 1. 读取题目类型
                    oos.writeObject("请输入题目类型（SINGLE/MULTIPLE/FILL/ESSAY）：");
                    oos.flush();
                    String type = (String) ois.readObject();
                    if (type == null || type.trim().isEmpty()) {
                        oos.writeObject("题目类型不能为空，请重新输入。");
                        oos.flush();
                        continue;
                    }

                    // 2. 读取题目数量 (增加异常处理)
                    oos.writeObject("请输入此类型题目数量：");
                    oos.flush();
                    String countStr = (String) ois.readObject();
                    int count;
                    try {
                        count = Integer.parseInt(countStr);
                    } catch (NumberFormatException e) {
                        oos.writeObject("题目数量输入格式错误，请重新输入。");
                        oos.flush();
                        continue;
                    }

                    // 3. 读取难度 (增加异常处理)
                    oos.writeObject("请输入难度（1-3）：");
                    oos.flush();
                    String difficultyStr = (String) ois.readObject();
                    int difficulty;
                    try {
                        difficulty = Integer.parseInt(difficultyStr);
                        if (difficulty < 1 || difficulty > 3) {
                            oos.writeObject("难度必须在1-3之间，请重新输入。");
                            oos.flush();
                            continue;
                        }
                    } catch (NumberFormatException e) {
                        oos.writeObject("难度输入格式错误，请重新输入。");
                        oos.flush();
                        continue;
                    }

                    // 4. 读取知识点
                    oos.writeObject("请输入知识点：");
                    oos.flush();
                    String knowledgePoint = (String) ois.readObject();

                    // 5. 读取分值 (增加异常处理)
                    oos.writeObject("请输入该类型题目的分值：");
                    oos.flush();
                    String scoreStr = (String) ois.readObject();
                    int score;
                    try {
                        score = Integer.parseInt(scoreStr);
                    } catch (NumberFormatException e) {
                        oos.writeObject("分值输入格式错误，请重新输入。");
                        oos.flush();
                        continue;
                    }

                    // --- 调用服务层进行组卷 ---
                    paperService.autoGeneratePaper(paperId, type, count, difficulty, knowledgePoint, score);
                    oos.writeObject("已成功添加 " + count + " 道 " + type + " 题策略。");
                    oos.flush();

                } else {
                    oos.writeObject("无效选择，请重新输入。");
                    oos.flush();
                }
            }

            // 循环结束后提示组卷成功
            oos.writeObject("自动组卷成功！请记得发布试卷。");
            oos.flush();
        }

        // 发布试卷
        private void publishPaper() throws Exception {
            oos.writeObject("请输入试卷ID：");
            oos.flush();
            String paperIdStr = (String) ois.readObject();
            int paperId;
            try {
                paperId = Integer.parseInt(paperIdStr);
            } catch (NumberFormatException e) {
                oos.writeObject("试卷ID输入格式错误，操作取消。");
                oos.flush();
                return;
            }

            paperService.publishPaper(paperId);
            oos.writeObject("试卷发布成功！");
            oos.flush();
        }

        // 添加题目
        private void addQuestion() throws Exception {
            // ... (为所有 Integer.parseInt 增加 Try-Catch 验证)
            oos.writeObject("请输入题目类型（SINGLE/MULTIPLE/FILL/ESSAY）：");
            oos.flush();
            String type = (String) ois.readObject();

            oos.writeObject("请输入题干：");
            oos.flush();
            String content = (String) ois.readObject();

            String[] options = null;
            if ("SINGLE".equals(type) || "MULTIPLE".equals(type)) {
                oos.writeObject("请输入选项（格式：A.xxx|B.xxx|C.xxx|D.xxx）：");
                oos.flush();
                String optionsStr = (String) ois.readObject();
                options = optionsStr.split("\\|");
            }

            oos.writeObject("请输入答案：");
            oos.flush();
            String answer = (String) ois.readObject();

            oos.writeObject("请输入知识点：");
            oos.flush();
            String knowledgePoint = (String) ois.readObject();

            oos.writeObject("请输入难度（1-3）：");
            oos.flush();
            String difficultyStr = (String) ois.readObject();
            int difficulty;
            try {
                difficulty = Integer.parseInt(difficultyStr);
            } catch (NumberFormatException e) {
                oos.writeObject("难度输入格式错误，操作取消。");
                oos.flush();
                return;
            }

            oos.writeObject("请输入分值：");
            oos.flush();
            String scoreStr = (String) ois.readObject();
            int score;
            try {
                score = Integer.parseInt(scoreStr);
            } catch (NumberFormatException e) {
                oos.writeObject("分值输入格式错误，操作取消。");
                oos.flush();
                return;
            }

            Connection conn = DBUtil.getConnection();
            // 先获取下一个序列值，再插入
            String getSeqSql = "SELECT question_seq.NEXTVAL FROM DUAL";
            PreparedStatement seqStmt = conn.prepareStatement(getSeqSql);
            ResultSet rs = seqStmt.executeQuery();
            int nextId = -1;
            if (rs.next()) {
                nextId = rs.getInt(1);
            }
            rs.close();
            seqStmt.close();

            // 在插入前打印接收到的数据
            System.out.println("接收到的内容: " + content);
            System.out.println("接收到的选项: " + (options != null ? String.join("|", options) : "null"));
            System.out.println("接收到的知识点: " + knowledgePoint);

            // 使用获取到的序列值插入
            String sql = "INSERT INTO questions(id, type, content, options, answer, knowledge_point, difficulty, score) "
                    +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, nextId);
            pstmt.setString(2, type);
            pstmt.setString(3, content);
            pstmt.setString(4, options != null ? String.join("|", options) : null);
            pstmt.setString(5, answer);
            pstmt.setString(6, knowledgePoint);
            pstmt.setInt(7, difficulty);
            pstmt.setInt(8, score);
            pstmt.executeUpdate();
            DBUtil.close(conn, pstmt, null);
            oos.writeObject("题目添加成功！");
            oos.flush();
        }

        // 考生功能处理
        // ExamServer.java - ClientHandler 类的 handleStudent() 方法
        // 请将此方法替换您文件中的 handleStudent() 或作为补充。

        // 假设 PaperService 具有 getAvailablePapers() 和 getPaperQuestions() 方法
        private void handleStudent() throws Exception {
            // 1. 获取并显示试卷列表
            List<ExamPaper> papers = paperService.getPublishedPapers(); // 假设有 getPublishedPapers 方法

            if (papers.isEmpty()) {
                oos.writeObject("目前没有已发布的试卷。");
                oos.flush();
                return;
            }

            // 【修正点 1】：将所有列表内容构建成一个字符串，避免客户端多次读取
            StringBuilder paperListMsg = new StringBuilder();
            paperListMsg.append("\n==================================");
            paperListMsg.append("\n请选择要参加的试卷（输入序号）：\n");

            for (int i = 0; i < papers.size(); i++) {
                ExamPaper paper = papers.get(i);
                paperListMsg.append(String.format("%d. %s (ID %d, 时长 %d 分钟)\n",
                        (i + 1), paper.getPaperName(), paper.getPaperId(), paper.getDuration()));
            }
            paperListMsg.append("==================================");

            // 2. 发送组合后的列表消息和输入提示
            oos.writeObject(paperListMsg.toString());
            oos.writeObject("请输入序号："); // 独立发送输入提示
            oos.flush();

            // 3. 接收学生选择的试卷
            String input = (String) ois.readObject();
            int paperIndex = -1;

            try {
                paperIndex = Integer.parseInt(input) - 1; // 转换为 0-based index
            } catch (NumberFormatException e) {
                oos.writeObject("输入格式错误，考试流程已中止。");
                oos.flush();
                return;
            }

            if (paperIndex < 0 || paperIndex >= papers.size()) {
                oos.writeObject("无效的试卷序号，考试流程已中止。");
                oos.flush();
                return;
            }

            ExamPaper selectedPaper = papers.get(paperIndex);
            int paperId = selectedPaper.getPaperId();

            // 4. 开始考试 (解决找不到 startExam 符号的错误)
            int sheetId = answerService.startExam(currentUser.getId(), paperId);

            oos.writeObject("\n考试开始！时长" + selectedPaper.getDuration() + "分钟，答案自动保存");
            oos.flush();

            // 5. 逐题作答
            List<PaperQuestion> paperQuestions = paperService.getPaperQuestions(paperId);

            if (paperQuestions.isEmpty()) {
                oos.writeObject("试卷中没有题目，考试提前结束。");
                oos.flush();
                return;
            }

            // 【修正点 2】：引入循环计数器 i 来显示正确题号
            for (int i = 0; i < paperQuestions.size(); i++) {
                PaperQuestion pq = paperQuestions.get(i);
                Question q = pq.getQuestion();

                StringBuilder questionDisplay = new StringBuilder();
                // 使用 i + 1 来显示题号
                questionDisplay.append(String.format("\n第%d题（%d分）：%s",
                        i + 1, q.getScore(), q.getContent()));

                if (q.getOptions() != null) {
                    for (String opt : q.getOptions()) {
                        questionDisplay.append("\n").append(opt);
                    }
                }

                oos.writeObject(questionDisplay.toString());
                oos.writeObject("请输入答案："); // 独立发送输入提示
                oos.flush();

                // 6. 读取用户答案并保存 (解决找不到 saveAnswer 符号的错误)
                String userAnswer = (String) ois.readObject();
                answerService.saveAnswer(sheetId, q.getId(), userAnswer);
                oos.writeObject("答案已保存");
                oos.flush();
            }

            // 7. 提交试卷
            oos.writeObject("是否提交试卷？(y/n)");
            oos.flush();
            if ("y".equalsIgnoreCase((String) ois.readObject())) {
                answerService.submitPaper(sheetId);
                // 获取客观题得分
                double objectiveScore = answerService.getObjectiveScore(sheetId);

                oos.writeObject(String.format("试卷提交成功！客观题得分：%.0f，主观题待批阅", objectiveScore));
                oos.flush();
            }
        }
        
        // 阅卷人功能处理
        private void handleGrader() throws Exception {
            while (true) {
                // 读取客户端输入的选择
                String choice = (String) ois.readObject();
                switch (choice) {
                    case "1":
                        List<AnswerSheet> pendingSheets = gradingService.getPendingSheets();

                        // 【修正点 3】：将列表内容合并为一条消息发送
                        StringBuilder sheetListMsg = new StringBuilder();
                        sheetListMsg.append("待批阅试卷共").append(pendingSheets.size()).append("份：\n");
                        for (AnswerSheet sheet : pendingSheets) {
                            sheetListMsg.append("答卷ID：").append(sheet.getSheetId())
                                    .append("，考生ID：").append(sheet.getUserId()).append("\n");
                        }
                        oos.writeObject(sheetListMsg.toString());
                        oos.flush();
                        break;
                    case "2":
                        oos.writeObject("请输入答卷ID：");
                        oos.flush();
                        String sheetIdStr = (String) ois.readObject();
                        int sheetId;
                        try {
                            sheetId = Integer.parseInt(sheetIdStr);
                        } catch (NumberFormatException e) {
                            oos.writeObject("答卷ID输入格式错误，操作取消。");
                            oos.flush();
                            break;
                        }

                        List<AnswerDetail> subjectiveQuestions = gradingService.getSubjectiveQuestions(sheetId);
                        for (AnswerDetail detail : subjectiveQuestions) {
                            Question q = detail.getQuestion();
                            oos.writeObject("\n题目：" + q.getContent());
                            oos.writeObject("标准答案：" + q.getAnswer());
                            oos.writeObject("考生答案：" + detail.getUserAnswer());
                            // 这里的 q.getScore() 现在应该能正确获取到满分
                            oos.writeObject("请输入得分（满分" + q.getScore() + "）：");
                            oos.flush();

                            String scoreStr = (String) ois.readObject();
                            double score;
                            try {
                                score = Double.parseDouble(scoreStr);
                            } catch (NumberFormatException e) {
                                oos.writeObject("得分输入格式错误，该题批阅失败。");
                                oos.flush();
                                continue;
                            }

                            oos.writeObject("请输入评语：");
                            oos.flush();
                            String comment = (String) ois.readObject();

                            String resultMessage = gradingService.gradeSubjective(detail.getDetailId(), score, comment,
                                    currentUser.getId(), sheetId);
                            oos.writeObject(resultMessage);
                            oos.flush();
                        }
                        // 如果还有未批阅的题目，GradingService 会保持状态为 'SUBMITTED'
                        oos.writeObject("该答卷批阅完成！");
                        oos.flush();
                        break;
                    case "3":
                        oos.writeObject("退出成功");
                        oos.flush();
                        return;
                    default:
                        oos.writeObject("无效选择，请重新输入");
                        oos.flush();
                }
                // 每次操作后重新发送功能列表
                oos.writeObject("功能列表：1-查看待批阅试卷 2-批阅主观题 3-退出");
                oos.flush();
            }
        }

        // 关闭资源
        private void close() {
            try {
                if (ois != null)
                    ois.close();
                if (oos != null)
                    oos.close();
                if (socket != null)
                    socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}