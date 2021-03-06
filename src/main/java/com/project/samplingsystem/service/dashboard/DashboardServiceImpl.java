package com.project.samplingsystem.service.dashboard;

import com.project.samplingsystem.dao.repository.*;
import com.project.samplingsystem.model.entity.NBArticle;
import com.project.samplingsystem.model.entity.NBComment;
import com.project.samplingsystem.model.pojo.vo.BaseDataStatistics;
import com.project.samplingsystem.model.pojo.vo.LatestComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * created by Wuwenbin on 2019-01-08 at 01:24
 *
 * @author wuwenbin
 */
@Transactional(rollbackOn = Exception.class)
@Service
public class DashboardServiceImpl implements DashboardService {

    private final ArticleRepository articleRepository;
    private final NoteRepository noteRepository;
    private final MessageRepository messageRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final LoggerRepository loggerRepository;

    @Autowired
    public DashboardServiceImpl(ArticleRepository articleRepository,
                                MessageRepository messageRepository, CommentRepository commentRepository,
                                UserRepository userRepository, NoteRepository noteRepository, LoggerRepository loggerRepository) {
        this.articleRepository = articleRepository;
        this.messageRepository = messageRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.loggerRepository = loggerRepository;
    }

    private String addLike(String item) {
        return "%" + item + "%";
    }

    @Override
    public List<BaseDataStatistics> calculateData() {
        long articles = articleRepository.countByDraft(true);
        long notes = noteRepository.count();
        long users = userRepository.count();
        long messages = messageRepository.count();
        long comments = commentRepository.count();
        int ips = loggerRepository.countByIpAddr();
        int todayIps = loggerRepository.countByIpAddrAndTimeLike(addLike(LocalDate.now().toString()));
        int todayComments = commentRepository.countByPostLike(addLike(LocalDate.now().toString()));
        int todayUsers = userRepository.countByCreateLike(addLike(LocalDate.now().toString()));
        BaseDataStatistics d1 = BaseDataStatistics.builder().text("????????????").sum(articles).url("#/article").build();
        BaseDataStatistics d2 = BaseDataStatistics.builder().text("????????????").sum(notes).url("#/note").build();
        BaseDataStatistics d3 = BaseDataStatistics.builder().text("????????????").sum(users).url("#/users").build();
        BaseDataStatistics d4 = BaseDataStatistics.builder().text("????????????").sum(messages).url("#/message").build();
        BaseDataStatistics d5 = BaseDataStatistics.builder().text("??????ip??????").sum(ips).build();
        BaseDataStatistics d6 = BaseDataStatistics.builder().text("????????????ip").sum(todayIps).build();
        BaseDataStatistics d7 = BaseDataStatistics.builder().text("????????????").sum(todayComments).build();
        BaseDataStatistics d8 = BaseDataStatistics.builder().text("??????????????????").sum(todayUsers).build();
        BaseDataStatistics d9 = BaseDataStatistics.builder().text("????????????").sum(comments).url("#/comment").build();
        List<BaseDataStatistics> list = new ArrayList<>(9);
        list.add(d1);
        list.add(d2);
        list.add(d3);
        list.add(d4);
        list.add(d9);
        list.add(d5);
        list.add(d6);
        list.add(d7);
        list.add(d8);
        return list;
    }

    @Override
    public LatestComment findLatestComment() {
        NBComment comment = commentRepository.findLastestComment();
        if (comment != null) {
            Optional<NBArticle> article = articleRepository.findById(comment.getArticleId());
            if (!article.isPresent()) {
                return LatestComment.builder()
                        .articleId(0L)
                        .articleTitle("??????")
                        .articleDate(LocalDateTime.now())
                        .comment(comment).build();
            }
            return LatestComment.builder()
                    .articleId(article.get().getId())
                    .articleTitle(article.get().getTitle())
                    .articleDate(article.get().getPost())
                    .comment(comment).build();
        }
        return LatestComment.builder()
                .articleId(0L)
                .articleTitle("??????")
                .articleDate(LocalDateTime.now())
                .comment(null).build();

    }

    @Override
    public List<Object[]> findTableStatistics() {
        return loggerRepository.findTableData(10);
    }

}
