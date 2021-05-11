package com.project.samplingsystem.web.management.message;

import com.project.samplingsystem.config.permission.NBAuth;
import com.project.samplingsystem.dao.repository.CommentRepository;
import com.project.samplingsystem.model.entity.NBComment;
import com.project.samplingsystem.model.entity.permission.NBSysResource;
import com.project.samplingsystem.model.pojo.framework.LayuiTable;
import com.project.samplingsystem.model.pojo.framework.NBR;
import com.project.samplingsystem.model.pojo.framework.Pagination;
import com.project.samplingsystem.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * created by Wuwenbin on 2018/8/15 at 16:28
 *
 * @author wuwenbin
 */
@Controller
@RequestMapping("/management/comment")
public class AdminCommentController extends BaseController {

    private final CommentRepository commentRepository;

    @Autowired
    public AdminCommentController(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @RequestMapping
    @NBAuth(value = "management:comment:page", remark = "评论管理页面", group = NBAuth.Group.ROUTER, type = NBSysResource.ResType.NAV_LINK)
    public String tagIndex() {
        return "management/message/comment";
    }

    @RequestMapping("/list")
    @ResponseBody
    @NBAuth(value = "management:comment:list", remark = "评论管理页面分页数据接口", group = NBAuth.Group.AJAX)
    public LayuiTable<NBComment> cateList(Pagination<NBComment> commentPagination, String clearComment) {
        Sort sort = getJpaSort(commentPagination);
        Pageable pageable = PageRequest.of(commentPagination.getPage() - 1, commentPagination.getLimit(), sort);
        if (StringUtils.isEmpty(clearComment)) {
            Page<NBComment> tagPage = commentRepository.findAll(pageable);
            return layuiTable(tagPage, pageable);
        } else {
            Example<NBComment> commentExample = Example.of(
                    NBComment.builder().clearComment(clearComment).build(),
                    ExampleMatcher.matching()
                            .withMatcher("clearComment", ExampleMatcher.GenericPropertyMatcher::contains).withIgnoreCase()
                            .withIgnorePaths("post", "enable")
            );
            Page<NBComment> commentPage = commentRepository.findAll(commentExample, pageable);
            return layuiTable(commentPage, pageable);
        }
    }


    @RequestMapping("/update")
    @ResponseBody
    @NBAuth(value = "management:comment:update", remark = "修改评论状态", group = NBAuth.Group.AJAX)
    public NBR delete(@RequestParam("id") Long id, boolean enable) {
        return ajaxDone(
                () -> commentRepository.updateCommentStatus(id, enable) == 1,
                () -> "修改评论"
        );
    }
}
