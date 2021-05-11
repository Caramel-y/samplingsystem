package com.project.samplingsystem.web.frontend.content;


import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import com.project.samplingsystem.dao.repository.ArticleRepository;
import com.project.samplingsystem.dao.repository.CommentRepository;
import com.project.samplingsystem.dao.repository.KeywordRepository;
import com.project.samplingsystem.dao.repository.ParamRepository;
import com.project.samplingsystem.model.entity.NBComment;
import com.project.samplingsystem.model.entity.NBKeyword;
import com.project.samplingsystem.model.pojo.framework.NBR;
import com.project.samplingsystem.service.mail.MailService;
import com.project.samplingsystem.util.NBUtils;
import com.project.samplingsystem.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static com.project.samplingsystem.model.constant.NoteBlogV4.Param.ALL_COMMENT_OPEN;

/**
 * created by Wuwenbin on 2018/2/8 at 18:54
 *
 * @author wuwenbin
 */
@Controller
@RequestMapping("/token/comment")
public class CommentController extends BaseController {

    private final CommentRepository commentRepository;
    private final KeywordRepository keywordRepository;
    private final ParamRepository paramRepository;
    private final ArticleRepository articleRepository;
    private final MailService mailService;

    @Autowired
    public CommentController(CommentRepository commentRepository, KeywordRepository keywordRepository, ParamRepository paramRepository, ArticleRepository articleRepository, MailService mailService) {
        this.commentRepository = commentRepository;
        this.keywordRepository = keywordRepository;
        this.paramRepository = paramRepository;
        this.articleRepository = articleRepository;
        this.mailService = mailService;
    }

    @RequestMapping(value = "/sub",method = RequestMethod.POST)
    @ResponseBody
    public NBR sub(@Valid NBComment comment, BindingResult bindingResult, HttpServletRequest request) {
        final String initSure = "1";
        return ajaxDone(
                () -> initSure.equals(paramRepository.findByName(ALL_COMMENT_OPEN).getValue()) && articleRepository.getOne(comment.getArticleId()).getCommented(),
                () -> {
                    if (!bindingResult.hasErrors()) {
                        comment.setIpAddr(NBUtils.getRemoteAddress(request));
                        boolean develop = NBUtils.getBean(Environment.class).getProperty("noteblog.develop", Boolean.class, true);
                        if (!develop) {
                            comment.setIpCnAddr(NBUtils.getIpCnInfo(NBUtils.getIpInfo(comment.getIpAddr())));
                        } else {
                            comment.setIpCnAddr("本地/未知");
                        }
                        comment.setUserAgent(request.getHeader("user-agent"));
                        comment.setComment(NBUtils.stripSqlXSS(comment.getComment()));
                        comment.setPost(LocalDateTime.now());
                        comment.setClearComment(HtmlUtil.cleanHtmlTag(comment.getComment()));
                        List<NBKeyword> keywords = keywordRepository.findAll();
                        keywords.forEach(kw -> comment.setComment(comment.getComment().replace(kw.getWords(), StrUtil.repeat("*", kw.getWords().length()))));
                        if (commentRepository.save(comment) != null) {
                            mailService.sendNoticeMail(basePath(request), articleRepository.getOne(comment.getArticleId()), comment.getComment());
                            return NBR.ok("发表评论成功");
                        } else {
                            return NBR.error("发表评论失败");
                        }
                    } else {
                        return ajaxJsr303(bindingResult.getFieldErrors());
                    }
                },
                () -> "未开放评论功能，请勿非法操作！"
        );
    }
}
