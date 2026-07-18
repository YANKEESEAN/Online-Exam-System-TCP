package com.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.db.DBUtil;
import com.entity.ExamPaper;
import com.entity.PaperQuestion;
import com.entity.Question;

public class PaperService {
    // 创建试卷并返回自增的paper_id
    public int createPaper(ExamPaper paper) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int paperId = -1;
        try {
            conn = DBUtil.getConnection();

            // 方法1：使用序列直接插入（推荐）
            String sql = "INSERT INTO exam_papers(paper_id, paper_name, course, duration, total_score, creator_id) " +
                    "VALUES(exam_papers_seq.NEXTVAL, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql, new String[] { "paper_id" });

            // 设置参数
            pstmt.setString(1, paper.getPaperName());
            pstmt.setString(2, paper.getCourse());
            pstmt.setInt(3, paper.getDuration());
            pstmt.setInt(4, paper.getTotalScore());
            pstmt.setInt(5, paper.getCreatorId());

            // 执行插入
            pstmt.executeUpdate();

            // 获取生成的paper_id
            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                paperId = rs.getInt(1);
            }

            // 如果方法1不行，使用方法2：查询序列值
            if (paperId == -1) {
                String seqSql = "SELECT exam_papers_seq.CURRVAL FROM DUAL";
                PreparedStatement seqStmt = conn.prepareStatement(seqSql);
                ResultSet seqRs = seqStmt.executeQuery();
                if (seqRs.next()) {
                    paperId = seqRs.getInt(1);
                }
                seqRs.close();
                seqStmt.close();
            }
        } finally {
            // 关闭资源
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            if (conn != null)
                conn.close();
        }
        return paperId;
    }

    // 自动组卷（按策略抽题）
    public void autoGeneratePaper(int paperId, String type, int count, int difficulty, String knowledgePoint, int score)
            throws SQLException {
        Connection conn = DBUtil.getConnection();

        try {
            System.out.println("开始自动组卷...");
            System.out.println("参数: paperId=" + paperId + ", type=" + type +
                    ", count=" + count + ", difficulty=" + difficulty +
                    ", knowledgePoint=" + knowledgePoint);

            // 使用模糊匹配查询
            String sql = "SELECT * FROM questions WHERE type = ? AND difficulty = ? " +
                    "AND (knowledge_point LIKE ? OR knowledge_point LIKE ?) " +
                    "AND ROWNUM <= ? ORDER BY DBMS_RANDOM.VALUE";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, type);
            pstmt.setInt(2, difficulty);
            pstmt.setString(3, "%" + knowledgePoint + "%");
            pstmt.setString(4, knowledgePoint + "%");
            pstmt.setInt(5, count);

            ResultSet rs = pstmt.executeQuery();

            int foundCount = 0;
            int order = 1;
            while (rs.next()) {
                foundCount++;
                int questionId = rs.getInt("id");
                String content = rs.getString("content");
                String actualKnowledgePoint = rs.getString("knowledge_point");
                System.out.println("找到题目: ID=" + questionId + ", 内容=" + content + ", 知识点=" + actualKnowledgePoint);

                // 修改插入语句，包含ID字段并使用序列
                String linkSql = "INSERT INTO paper_question(id, paper_id, question_id, question_order) " +
                        "VALUES(paper_question_seq.NEXTVAL, ?, ?, ?)";
                PreparedStatement linkPstmt = conn.prepareStatement(linkSql);
                linkPstmt.setInt(1, paperId);
                linkPstmt.setInt(2, questionId);
                linkPstmt.setInt(3, order++);
                linkPstmt.executeUpdate();
                linkPstmt.close();

                System.out.println(
                        "已插入题目到 paper_question: id=序列自动生成, paper_id=" + paperId + ", question_id=" + questionId);
            }

            System.out.println("自动组卷完成，共找到 " + foundCount + " 道题目");

            if (foundCount == 0) {
                System.out.println("警告：没有找到符合条件的题目！");
                // 可以在这里添加逻辑：如果没有找到完全匹配的题目，尝试放宽条件
            }

            DBUtil.close(conn, pstmt, rs);
        } catch (Exception e) {
            System.err.println("自动组卷出错: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    // 发布试卷
    public void publishPaper(int paperId) throws SQLException {
        Connection conn = DBUtil.getConnection();
        String sql = "UPDATE exam_papers SET status = 'PUBLISHED' WHERE paper_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, paperId);
        pstmt.executeUpdate();
        DBUtil.close(conn, pstmt, null);
    }

    // 获取已发布试卷列表
    public List<ExamPaper> getPublishedPapers() throws SQLException {
        List<ExamPaper> papers = new ArrayList<>();
        Connection conn = DBUtil.getConnection();
        String sql = "SELECT * FROM exam_papers WHERE status = 'PUBLISHED'";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            ExamPaper paper = new ExamPaper();
            paper.setPaperId(rs.getInt("paper_id"));
            paper.setPaperName(rs.getString("paper_name"));
            paper.setCourse(rs.getString("course"));
            paper.setDuration(rs.getInt("duration"));
            paper.setTotalScore(rs.getInt("total_score"));
            papers.add(paper);
        }
        DBUtil.close(conn, pstmt, rs);
        return papers;
    }

    // 获取试卷题目列表
    public List<PaperQuestion> getPaperQuestions(int paperId) throws SQLException {
        List<PaperQuestion> questions = new ArrayList<>();
        Connection conn = DBUtil.getConnection();
        String sql = "SELECT pq.*, q.id as q_id, q.type, q.content, q.options, q.answer, " +
                "q.knowledge_point, q.difficulty, q.score " + // 明确指定 q.score
                "FROM paper_question pq " +
                "JOIN questions q ON pq.question_id = q.id " +
                "WHERE pq.paper_id = ? ORDER BY pq.question_order";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, paperId);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            PaperQuestion pq = new PaperQuestion();
            pq.setId(rs.getInt("id"));
            pq.setPaperId(rs.getInt("paper_id"));
            pq.setQuestionId(rs.getInt("question_id"));
            pq.setQuestionOrder(rs.getInt("question_order"));

            Question q = new Question();
            q.setId(rs.getInt("question_id"));
            q.setType(rs.getString("type"));
            q.setContent(rs.getString("content"));
            q.setOptions(rs.getString("options") != null ? rs.getString("options").split("\\|") : null);
            q.setAnswer(rs.getString("answer"));
            q.setKnowledgePoint(rs.getString("knowledge_point"));
            q.setDifficulty(rs.getInt("difficulty"));
            q.setScore(rs.getInt("score"));
            pq.setQuestion(q);

            questions.add(pq);
        }
        DBUtil.close(conn, pstmt, rs);
        return questions;
    }
}