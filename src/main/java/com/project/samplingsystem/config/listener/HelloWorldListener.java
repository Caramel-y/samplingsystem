package com.project.samplingsystem.config.listener;

import com.project.samplingsystem.dao.repository.ArticleRepository;
import com.project.samplingsystem.dao.repository.CateRepository;
import com.project.samplingsystem.exception.InitException;
import lombok.extern.slf4j.Slf4j;
import com.project.samplingsystem.model.entity.NBArticle;
import com.project.samplingsystem.model.entity.NBCate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * created by Wuwenbin on 2018/8/2 at 20:35
 *
 * @author wuwenbin
 */
@Slf4j
@Component
@Order(4)
public class HelloWorldListener implements ApplicationListener<ApplicationReadyEvent> {

    private final CateRepository cateRepository;
    private final ArticleRepository articleRepository;

    @Autowired
    public HelloWorldListener(CateRepository cateRepository, ArticleRepository articleRepository) {
        this.cateRepository = cateRepository;
        this.articleRepository = articleRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        if (cateRepository.count() == 0 && articleRepository.count() == 0) {
            log.info("初始化「个人博客」APP 基本内容，请稍后...");
            long cateId = setFirstCategory();
            setHelloWorldArticle(cateId);
            log.info("初始化「个人博客」APP 基本内容完毕");
        }
    }


    /**
     * 设置默认文章（HelloWorld）的分类
     *
     * @return
     */
    private Long setFirstCategory() {
        NBCate defaultCate = NBCate.builder()
                .cnName("默认分类")
                .name("def_cate")
                .fontIcon("fa fa-sliders")
                .build();
        defaultCate = cateRepository.save(defaultCate);
        if (defaultCate.getId() != null) {
            return defaultCate.getId();
        }
        throw new InitException("初始化默认文章分类出错");
    }

    private void setHelloWorldArticle(long cateId) {
        final String title = "欢迎使用个人博客";
        final String content = "<h2 id=\"h2--noteblog-em-v4-em-\"><a name=\"欢迎使用个人博客（NoteBlog <em>v4</em>）\" class=\"reference-link\"></a><span class=\"header-link octicon octicon-link\"></span>欢迎使用个人博客</h2><p>基于springboot+layui编写的一款轻博客系统，可完美搭建您的一款简约博客网站，同时也是非常适用于学习的项目。</p>\n" +
                "<p>感谢！</p>\n" +
                "<p></p>\n";
        final String textContent = "欢迎使用个人博客（NoteBlogv4）个人博客是基于springboot+layui编写的一款轻博客系统，可完美搭建您的一款简约博客网站，同时也是非常适用于学习的项目。";
        final String mdContent = "##欢迎使用个人博客（NoteBlog *v4*）\n\n" +
                "个人博客是基于springboot+layui编写的一款轻博客系统，可完美搭建您的一款简约博客网站，同时也是非常适用于学习的项目。\n\n" +
                "o(*￣︶￣*)o\n" +
                "\n感谢！\n" +
                "\n";
        final String summary = "基于springboot+layui编写的一款轻博客系统，可完美搭建您的一款简约博客网站，同时也是非常适用于学习的项目。";
        NBArticle helloWorldArticle = NBArticle.builder()
                .authorId(1L)
                .cateId(cateId)
                .cover("/static/assets/img/cover.png")
                .content(content)
                .textContent(textContent)
                .cate(cateRepository.getOne(cateId))
                .summary(summary)
                .mdContent(mdContent)
                .post(LocalDateTime.now())
                .draft(false)
                .appreciable(true)
                .title(title).build();
        articleRepository.save(helloWorldArticle);
    }

}
