package com.client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import com.entity.User;
import com.entity.Question;
import com.entity.ExamPaper;
import com.entity.PaperQuestion;
import com.entity.AnswerSheet;
import com.entity.AnswerDetail;

public class ExamClient {
    private static final String SERVER_IP = "localhost";
    private static final int SERVER_PORT = 8888;
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private BufferedReader reader; // 用于正确读取中文

    public static void main(String[] args) {
        new ExamClient().start();
    }

    // 构造方法
    public ExamClient() {
        try {
            // 使用InputStreamReader明确指定UTF-8编码
            this.reader = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
            System.out.println("客户端输入使用UTF-8编码");
        } catch (Exception e) {
            System.err.println("编码设置失败: " + e.getMessage());
            this.reader = new BufferedReader(new InputStreamReader(System.in));
        }
    }

    public void start() {
        try {
            // 建立连接
            socket = new Socket(SERVER_IP, SERVER_PORT);
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            ois = new ObjectInputStream(socket.getInputStream());

            // 执行登录流程
            login();

            // 接收登录结果
            String loginResult = (String) ois.readObject();
            System.out.println(loginResult);
            if (!loginResult.startsWith("登录成功")) {
                close();
                return;
            }

            // 接收角色信息
            String role = (String) ois.readObject();

            // 根据角色进入对应交互流程
            if ("ADMIN".equals(role)) {
                adminInteraction();
            } else if ("GRADER".equals(role)) {
                graderInteraction();
            } else if ("STUDENT".equals(role)) {
                studentInteraction();
            }

            // 结束后关闭资源
            close();
        } catch (Exception e) {
            System.out.println("客户端异常：" + e.getMessage());
            e.printStackTrace();
        }
    }

    private String readInput() {
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.err.println("读取输入时发生I/O错误: " + e.getMessage());
            return "";
        }
    }

    // 登录逻辑：发送用户名和密码
    private void login() throws IOException {
        System.out.print("请输入用户名：");
        String username = readInput().trim();
        System.out.print("请输入密码：");
        String password = readInput().trim();
        oos.writeObject(new User(username, password)); // 发送用户对象
        oos.flush(); // 强制刷新，确保服务器收到
    }

    // 管理员交互流程
    private void adminInteraction() throws Exception {
        System.out.println("\n===== 管理员功能交互 =====");
        while (true) {
            String serverMsg = (String) ois.readObject();
            System.out.println("\n" + serverMsg);

            if (serverMsg.contains("退出成功")) {
                break;
            }

            // 增强输入提示的判断逻辑
            boolean requiresInput = serverMsg.contains("功能列表：") ||
                    serverMsg.trim().endsWith("：") ||
                    serverMsg.trim().endsWith("?") ||
                    serverMsg.contains("请选择"); // 增加对“请输入”关键字的检测，更可靠
            if (requiresInput) {
                System.out.print(">>> ");
                String userInput = readInput().trim();
                oos.writeObject(userInput);
                oos.flush();
            }
        }
    }

    // 考生交互流程（增强版）
    private void studentInteraction() throws Exception {
        System.out.println("\n===== 考生功能交互 =====");
        while (true) {
            String serverMsg;
            try {
                serverMsg = (String) ois.readObject();
            } catch (EOFException | SocketException e) {
                System.out.println("\n[系统提示] 考试流程已结束或连接中断。");
                break;
            }

            System.out.println("\n" + serverMsg);

            // 检查流程是否结束
            if (serverMsg.contains("试卷提交成功") || serverMsg.contains("流程已中止") || serverMsg.contains("目前没有已发布的试卷")) {
                break;
            }

            // 处理答题输入（明确提示用户输入答案）
            boolean requiresInput = (serverMsg.trim().endsWith("：") || serverMsg.trim().endsWith("?")
                    || serverMsg.trim().endsWith(")"))
                    && serverMsg.trim().length() < 50;

            if (requiresInput) {
                System.out.print(">>> ");
                String userInput = readInput().trim();
                // 特殊处理多选题输入提示
                if (serverMsg.contains("多选题") && serverMsg.contains("(例如: A,B,C)")) {
                    // 确保输入格式正确（移除空格）
                    userInput = userInput.replaceAll("\\s+", "");
                }
                oos.writeObject(userInput);
                oos.flush();
            }
        }
    }

    // 阅卷人交互流程
    private void graderInteraction() throws Exception {
        System.out.println("\n===== 阅卷人功能交互 =====");
        while (true) {
            String serverMsg = (String) ois.readObject();
            System.out.println("\n" + serverMsg);

            if (serverMsg.contains("退出成功")) {
                break;
            }

            boolean isFunctionList = serverMsg.contains("功能列表：");
            boolean isShortInputPrompt = serverMsg.trim().endsWith("：") && serverMsg.trim().length() < 50;
            boolean isQuestion = serverMsg.trim().endsWith("?");
            boolean isScorePrompt = serverMsg.startsWith("请输入得分（满分");
            boolean isPendingListHeader = serverMsg.startsWith("待批阅试卷共") && !serverMsg.contains("答卷ID：");

            if ((isFunctionList || isShortInputPrompt || isQuestion || isScorePrompt) && !isPendingListHeader) {
                System.out.print(">>> ");
                String userInput = readInput().trim();
                oos.writeObject(userInput);
                oos.flush();
            }
        }
    }

    // 关闭所有资源
    private void close() {
        try {
            if (reader != null)
                reader.close();
            if (ois != null)
                ois.close();
            if (oos != null)
                oos.close();
            if (socket != null)
                socket.close();
            System.out.println("客户端已断开连接");
        } catch (IOException e) {
            System.out.println("关闭资源异常：" + e.getMessage());
        }
    }
}