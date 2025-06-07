package com.wzu.exam.service.impl;

import com.wzu.exam.dto.*;
import com.wzu.exam.model.*;
import com.wzu.exam.repository.*;
import com.wzu.exam.service.StudentExamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentExamServiceImpl implements StudentExamService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final ExamResultRepository examResultRepository;
    private final StudentAnswerRepository studentAnswerRepository;

    @Override
    public Page<ExamDetailDTO> getAvailableExams(Pageable pageable, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        // 获取所有激活的考试
        Page<Exam> examPage = examRepository.findByIsActiveTrue(pageable);

        List<ExamDetailDTO> examDTOs = examPage.getContent().stream()
                .map(exam -> {
                    ExamDetailDTO dto = convertToExamDTO(exam);
                    // 设置考试状态
                    dto.setStatus(determineExamStatus(exam, user));
                    return dto;
                })
                .collect(Collectors.toList());

        return new PageImpl<>(examDTOs, pageable, examPage.getTotalElements());
    }

    @Override
    public ExamDetailDTO getExamById(Long examId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("考试不存在"));

        if (!exam.getIsActive()) {
            throw new IllegalArgumentException("该考试未激活，无法参加");
        }

        return convertToExamDetailDTO(exam);
    }

    @Override
    public boolean hasAttemptedExam(Long examId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new IllegalArgumentException("考试不存在"));

        return studentAnswerRepository.existsByUserAndExam(user, exam);
    }

    @Override
    @Transactional
    public ExamResultDTO submitExam(ExamSubmissionDTO submissionDTO, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        Exam exam = examRepository.findById(submissionDTO.getExamId())
                .orElseThrow(() -> new IllegalArgumentException("考试不存在"));

        if (!exam.getIsActive()) {
            throw new IllegalArgumentException("该考试未激活，无法提交");
        }

        if (hasAttemptedExam(exam.getId(), username)) {
            throw new IllegalArgumentException("您已经参加过此考试，不能重复提交");
        }

        // 获取所有题目
        List<Question> questions = questionRepository.findByExamId(exam.getId());
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, q -> q));

        // 处理学生答案
        List<StudentAnswer> studentAnswers = new ArrayList<>();
        int correctCount = 0;

        for (QuestionAnswerDTO answer : submissionDTO.getAnswers()) {
            Question question = questionMap.get(answer.getQuestionId());
            if (question == null) {
                continue; // 跳过不属于此考试的题目
            }

            StudentAnswer studentAnswer = new StudentAnswer();
            studentAnswer.setUser(user);
            studentAnswer.setQuestion(question);
            studentAnswer.setExam(exam);

            // 使用传入的答案
            String selectedAnswer = answer.getSelectedAnswer();
            if (selectedAnswer == null && answer.getSelectedOption() != null) {
                // 如果没有传入答案字符串，根据选项索引生成
                selectedAnswer = getAnswerByIndex(question, answer.getSelectedOption());
            }

            studentAnswer.setSelectedAnswer(selectedAnswer);
            studentAnswer.setIsCorrect(question.getAnswer().equals(selectedAnswer));

            if (studentAnswer.getIsCorrect()) {
                correctCount++;
            }

            studentAnswers.add(studentAnswer);
        }

        // 保存学生答案
        studentAnswerRepository.saveAll(studentAnswers);

        // 计算得分（按比例计算，满分100分）
        int totalQuestions = questions.size();
        int score = totalQuestions > 0 ? (correctCount * 100) / totalQuestions : 0;

        // 创建考试结果
        ExamResult examResult = new ExamResult();
        examResult.setUser(user);
        examResult.setExam(exam);
        examResult.setScore(score);
        examResult.setDuration(0); // 暂时设置为0，前端可以传递实际时长

        ExamResult savedResult = examResultRepository.save(examResult);

        // 返回结果DTO
        return createExamResultDTO(savedResult, studentAnswers, questions.size());
    }

    @Override
    public Page<ExamResultDTO> getStudentResults(Pageable pageable, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        Page<ExamResult> resultPage = examResultRepository.findByUserOrderByCreatedAtDesc(user, pageable);

        List<ExamResultDTO> resultDTOs = resultPage.getContent().stream()
                .map(result -> {
                    List<StudentAnswer> answers = studentAnswerRepository.findByUserAndExam(user, result.getExam());
                    int totalQuestions = questionRepository.countByExamId(result.getExam().getId());
                    return createExamResultDTO(result, answers, totalQuestions);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(resultDTOs, pageable, resultPage.getTotalElements());
    }

    @Override
    public ExamResultDTO getExamResultById(Long resultId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));

        ExamResult result = examResultRepository.findById(resultId)
                .orElseThrow(() -> new IllegalArgumentException("考试结果不存在"));

        // 验证结果是否属于当前用户
        if (!result.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("无权查看其他用户的考试结果");
        }

        List<StudentAnswer> answers = studentAnswerRepository.findByUserAndExam(user, result.getExam());
        int totalQuestions = questionRepository.countByExamId(result.getExam().getId());

        return createExamResultDTO(result, answers, totalQuestions);
    }

    // 辅助方法 - 根据选项索引获取答案
    private String getAnswerByIndex(Question question, Integer optionIndex) {
        if (optionIndex == null) return "";

        switch (optionIndex) {
            case 0: return question.getOptionA();
            case 1: return question.getOptionB();
            case 2: return question.getOptionC();
            case 3: return question.getOptionD();
            default: return "";
        }
    }

    // 辅助方法 - 确定考试状态
    private String determineExamStatus(Exam exam, User user) {
        // 检查是否已经参加过
        if (studentAnswerRepository.existsByUserAndExam(user, exam)) {
            return "COMPLETED";
        }

        // 简化状态，只有可参加和已完成
        return "IN_PROGRESS";
    }

    // 辅助方法 - 转换为考试DTO
    private ExamDetailDTO convertToExamDTO(Exam exam) {
        ExamDetailDTO dto = new ExamDetailDTO();
        dto.setId(exam.getId());
        dto.setTitle(exam.getTitle());
        dto.setDescription(exam.getDescription());
        dto.setDuration(exam.getDurationMinutes());
        dto.setCreatedAt(exam.getCreatedAt());
        dto.setIsActive(exam.getIsActive());
        // 不包含题目详情，减少数据传输
        return dto;
    }

    // 辅助方法 - 转换为考试详情DTO（包含题目）
    private ExamDetailDTO convertToExamDetailDTO(Exam exam) {
        ExamDetailDTO dto = convertToExamDTO(exam);

        // 添加题目信息，但不包含答案
        List<QuestionDTO> questionDTOs = exam.getQuestions().stream()
                .map(this::convertToQuestionDTO)
                .collect(Collectors.toList());

        dto.setQuestions(questionDTOs);
        return dto;
    }

    // 辅助方法 - 转换为题目DTO（考试进行中，不包含答案）
    private QuestionDTO convertToQuestionDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setContent(question.getContent());

        // 将选项转换为数组格式
        List<String> options = Arrays.asList(
            question.getOptionA(),
            question.getOptionB(),
            question.getOptionC(),
            question.getOptionD()
        );
        dto.setOptions(options);

        // 设置其他字段
        dto.setOptionA(question.getOptionA());
        dto.setOptionB(question.getOptionB());
        dto.setOptionC(question.getOptionC());
        dto.setOptionD(question.getOptionD());
        dto.setScore(question.getScore());
        // 不设置答案，避免作弊
        dto.setExamId(question.getExam().getId());
        return dto;
    }

    // 辅助方法 - 创建考试结果DTO
    private ExamResultDTO createExamResultDTO(ExamResult result, List<StudentAnswer> answers, int totalQuestions) {
        ExamResultDTO dto = new ExamResultDTO();
        dto.setId(result.getId());
        dto.setExamId(result.getExam().getId());
        dto.setExamTitle(result.getExam().getTitle());
        dto.setScore(result.getScore());
        dto.setTotalScore(100); // 满分为100
        dto.setDuration(result.getDuration());
        dto.setSubmittedAt(result.getCreatedAt());

        // 添加学生答案详情
        List<StudentAnswerDTO> answerDTOs = answers.stream()
                .map(this::convertToStudentAnswerDTO)
                .collect(Collectors.toList());

        dto.setAnswers(answerDTOs);
        return dto;
    }

    // 辅助方法 - 转换为学生答案DTO
    private StudentAnswerDTO convertToStudentAnswerDTO(StudentAnswer answer) {
        StudentAnswerDTO dto = new StudentAnswerDTO();
        dto.setQuestionId(answer.getQuestion().getId());
        dto.setQuestion(answer.getQuestion().getContent());
        dto.setOptionA(answer.getQuestion().getOptionA());
        dto.setOptionB(answer.getQuestion().getOptionB());
        dto.setOptionC(answer.getQuestion().getOptionC());
        dto.setOptionD(answer.getQuestion().getOptionD());
        dto.setSelectedAnswer(answer.getSelectedAnswer());
        dto.setCorrectAnswer(answer.getQuestion().getAnswer());
        dto.setIsCorrect(answer.getIsCorrect());
        return dto;
    }
}
//
//    @Override
//    @Transactional
//    public ExamResultDTO submitExam(ExamSubmissionDTO submissionDTO, String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
//
//        Exam exam = examRepository.findById(submissionDTO.getExamId())
//                .orElseThrow(() -> new IllegalArgumentException("考试不存在"));
//
//        if (!exam.getIsActive()) {
//            throw new IllegalArgumentException("该考试未激活，无法提交");
//        }
//
//        if (checkExamAttempt(exam.getId(), username)) {
//            throw new IllegalArgumentException("您已经参加过此考试，不能重复提交");
//        }
//
//        // 获取所有题目
//        List<Question> questions = questionRepository.findByExamId(exam.getId());
//        Map<Long, Question> questionMap = questions.stream()
//                .collect(Collectors.toMap(Question::getId, q -> q));
//
//        // 处理学生答案
//        List<StudentAnswer> studentAnswers = new ArrayList<>();
//        int correctCount = 0;
//
//        for (QuestionAnswerDTO answer : submissionDTO.getAnswers()) {
//            Question question = questionMap.get(answer.getQuestionId());
//            if (question == null) {
//                continue; // 跳过不属于此考试的题目
//            }
//
//            StudentAnswer studentAnswer = new StudentAnswer();
//            studentAnswer.setUser(user);
//            studentAnswer.setQuestion(question);
//            studentAnswer.setExam(exam);
//            studentAnswer.setSelectedAnswer(answer.getSelectedAnswer());
//            studentAnswer.setIsCorrect(question.getAnswer().equals(answer.getSelectedAnswer()));
//
//            if (studentAnswer.getIsCorrect()) {
//                correctCount++;
//            }
//
//            studentAnswers.add(studentAnswer);
//        }
//
//        // 保存学生答案
//        studentAnswerRepository.saveAll(studentAnswers);
//
//        // 计算得分（按比例计算，满分100分）
//        int totalQuestions = questions.size();
//        int score = totalQuestions > 0 ? (correctCount * 100) / totalQuestions : 0;
//
//        // 创建考试结果
//        ExamResult examResult = new ExamResult();
//        examResult.setUser(user);
//        examResult.setExam(exam);
//        examResult.setScore(score);
//        examResult.setDuration(submissionDTO.getDuration());
//
//        ExamResult savedResult = examResultRepository.save(examResult);
//
//        // 返回结果DTO
//        return createExamResultDTO(savedResult, studentAnswers, questions.size());
//    }
//
//    @Override
//    public List<ExamResultDTO> getStudentResults(String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
//
//        List<ExamResult> results = examResultRepository.findByUserOrderByCreatedAtDesc(user);
//
//        return results.stream()
//                .map(result -> {
//                    List<StudentAnswer> answers = studentAnswerRepository.findByUserAndExam(user, result.getExam());
//                    int totalQuestions = questionRepository.countByExamId(result.getExam().getId());
//                    return createExamResultDTO(result, answers, totalQuestions);
//                })
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public ExamResultDTO getExamResultById(Long resultId, String username) {
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
//
//        ExamResult result = examResultRepository.findById(resultId)
//                .orElseThrow(() -> new IllegalArgumentException("考试结果不存在"));
//
//        // 验证结果是否属于当前用户
//        if (!result.getUser().getId().equals(user.getId())) {
//            throw new IllegalArgumentException("无权查看其他用户的考试结果");
//        }
//
//        List<StudentAnswer> answers = studentAnswerRepository.findByUserAndExam(user, result.getExam());
//        int totalQuestions = questionRepository.countByExamId(result.getExam().getId());
//
//        return createExamResultDTO(result, answers, totalQuestions);
//    }
//
//    // 辅助方法 - 转换为考试DTO
//    private ExamDetailDTO convertToExamDTO(Exam exam) {
//        ExamDetailDTO dto = new ExamDetailDTO();
//        dto.setId(exam.getId());
//        dto.setTitle(exam.getTitle());
//        dto.setDescription(exam.getDescription());
//        dto.setDuration(exam.getDuration());
//        dto.setCreatedAt(exam.getCreatedAt());
//        dto.setIsActive(exam.getIsActive());
//        // 不包含题目详情，减少数据传输
//        return dto;
//    }
//
//    // 辅助方法 - 转换为考试详情DTO（包含题目）
//    private ExamDetailDTO convertToExamDetailDTO(Exam exam) {
//        ExamDetailDTO dto = convertToExamDTO(exam);
//
//        // 添加题目信息，但不包含答案
//        List<QuestionDTO> questionDTOs = exam.getQuestions().stream()
//                .map(this::convertToQuestionDTO)
//                .collect(Collectors.toList());
//
//        dto.setQuestions(questionDTOs);
//        return dto;
//    }
//
//    // 辅助方法 - 转换为题目DTO（考试进行中，不包含答案）
//    private QuestionDTO convertToQuestionDTO(Question question) {
//        QuestionDTO dto = new QuestionDTO();
//        dto.setId(question.getId());
//        dto.setContent(question.getContent());
//        dto.setOptionA(question.getOptionA());
//        dto.setOptionB(question.getOptionB());
//        dto.setOptionC(question.getOptionC());
//        dto.setOptionD(question.getOptionD());
//        // 不设置答案，避免作弊
//        dto.setExamId(question.getExam().getId());
//        return dto;
//    }
//
//    // 辅助方法 - 创建考试结果DTO
//    private ExamResultDTO createExamResultDTO(ExamResult result, List<StudentAnswer> answers, int totalQuestions) {
//        ExamResultDTO dto = new ExamResultDTO();
//        dto.setId(result.getId());
//        dto.setExamId(result.getExam().getId());
//        dto.setExamTitle(result.getExam().getTitle());
//        dto.setScore(result.getScore());
//        dto.setTotalScore(100); // 满分为100
//        dto.setDuration(result.getDuration());
//        dto.setSubmittedAt(result.getCreatedAt());
//
//        // 添加学生答案详情
//        List<StudentAnswerDTO> answerDTOs = answers.stream()
//                .map(this::convertToStudentAnswerDTO)
//                .collect(Collectors.toList());
//
//        dto.setAnswers(answerDTOs);
//        return dto;
//    }
//
//    // 辅助方法 - 转换为学生答案DTO
//    private StudentAnswerDTO convertToStudentAnswerDTO(StudentAnswer answer) {
//        StudentAnswerDTO dto = new StudentAnswerDTO();
//        dto.setQuestionId(answer.getQuestion().getId());
//        dto.setQuestion(answer.getQuestion().getContent());
//        dto.setOptionA(answer.getQuestion().getOptionA());
//        dto.setOptionB(answer.getQuestion().getOptionB());
//        dto.setOptionC(answer.getQuestion().getOptionC());
//        dto.setOptionD(answer.getQuestion().getOptionD());
//        dto.setSelectedAnswer(answer.getSelectedAnswer());
//        dto.setCorrectAnswer(answer.getQuestion().getAnswer());
//        dto.setIsCorrect(answer.getIsCorrect());
//        return dto;
//    }
//}
