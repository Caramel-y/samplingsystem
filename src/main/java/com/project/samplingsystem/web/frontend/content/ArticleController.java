package com.project.samplingsystem.web.frontend.content;

import com.project.samplingsystem.dao.repository.ArticleRepository;
import com.project.samplingsystem.dao.repository.CateRepository;
import com.project.samplingsystem.dao.repository.TagRepository;
import com.project.samplingsystem.dao.repository.UserRepository;
import com.project.samplingsystem.exception.ArticleFetchFailedException;
import com.project.samplingsystem.model.entity.NBArticle;
import com.project.samplingsystem.model.entity.NBComment;
import com.project.samplingsystem.model.pojo.bo.CommentQueryBO;
import com.project.samplingsystem.model.pojo.framework.NBR;
import com.project.samplingsystem.model.pojo.framework.Pagination;
import com.project.samplingsystem.service.content.CommentService;
import com.project.samplingsystem.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * created by Wuwenbin on 2018/9/5 at 下午12:34
 *
 * @author wuwenbin
 */
@Controller("frontArticleController")
@RequestMapping({"/article", "/a"})
public class ArticleController extends BaseController {

    private final ArticleRepository articleRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final CommentService commentService;
    private final CateRepository cateRepository;

    @Autowired
    public ArticleController(ArticleRepository articleRepository,
                             TagRepository tagRepository,
                             UserRepository userRepository, CommentService commentService, CateRepository cateRepository) {
        this.articleRepository = articleRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.commentService = commentService;
        this.cateRepository = cateRepository;
    }

    @RequestMapping("/{aId}")
    public String article(@PathVariable("aId") Long aId, Model model, Pagination<NBComment> pagination, CommentQueryBO commentQueryBO) {
        articleRepository.updateViewsById(aId);
        pagination.setLimit(10);
        Optional<NBArticle> fetchArticle = articleRepository.findById(aId);
        model.addAttribute("article", fetchArticle.orElseThrow(() -> new ArticleFetchFailedException("未找到相关文章！")));
        model.addAttribute("tags", tagRepository.findArticleTags(aId, true));
        model.addAttribute("author", userRepository.getOne(fetchArticle.get().getAuthorId()).getNickname());
        commentQueryBO.setArticleId(aId);
        pagination.setLimit(10);
        model.addAttribute("comments", commentService.findPageInfo(getPageable(pagination), commentQueryBO));
        model.addAttribute("similarArticles", articleRepository.findSimilarArticles(fetchArticle.get().getCateId(), 6));
        //normal型页面
        model.addAttribute("cateList", cateRepository.findAll());
        return "frontend/content/article";
    }

    @RequestMapping("/u/{urlSeq}")
    public String articleByUrl(@PathVariable("urlSeq") String urlSeq, Model model, Pagination<NBComment> pagination, CommentQueryBO commentQueryBO) {
        articleRepository.updateViewsBySeq(urlSeq);
        pagination.setLimit(10);
        Optional<NBArticle> fetchArticle = articleRepository.findNBArticleByUrlSequence(urlSeq);
        NBArticle article = fetchArticle.orElseThrow(() -> new ArticleFetchFailedException("未找到相关文章！"));
        model.addAttribute("article", article);
        model.addAttribute("tags", tagRepository.findArticleTags(article.getId(), true));
        model.addAttribute("author", userRepository.getOne(article.getAuthorId()).getNickname());
        commentQueryBO.setArticleId(article.getId());
        pagination.setLimit(10);
        model.addAttribute("comments", commentService.findPageInfo(getPageable(pagination), commentQueryBO));
        model.addAttribute("similarArticles", articleRepository.findSimilarArticles(article.getCateId(), 6));
        return "frontend/content/article";
    }

    @RequestMapping(value = "/comments", method = RequestMethod.POST)
    @ResponseBody
    public Page<NBComment> comments(Pagination<NBComment> pagination, CommentQueryBO commentQueryBO) {
        return commentService.findPageInfo(getPageable(pagination), commentQueryBO);
    }

    @RequestMapping(value = "/approve", method = RequestMethod.POST)
    @ResponseBody
    public NBR approve(@RequestParam Long articleId) {
        return ajaxDone(() -> articleRepository.updateApproveCntById(articleId) == 1, () -> "点赞");
    }
}
