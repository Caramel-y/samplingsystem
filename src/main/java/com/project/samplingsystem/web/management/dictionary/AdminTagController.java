package com.project.samplingsystem.web.management.dictionary;

import com.project.samplingsystem.config.permission.NBAuth;
import com.project.samplingsystem.dao.repository.TagReferRepository;
import com.project.samplingsystem.dao.repository.TagRepository;
import com.project.samplingsystem.model.entity.NBTag;
import com.project.samplingsystem.model.entity.permission.NBSysResource;
import com.project.samplingsystem.model.pojo.framework.LayuiTable;
import com.project.samplingsystem.model.pojo.framework.NBR;
import com.project.samplingsystem.model.pojo.framework.Pagination;
import com.project.samplingsystem.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * created by Wuwenbin on 2018/8/15 at 16:28
 *
 * @author wuwenbin
 */
@Controller
@RequestMapping("/management/dictionary/tag")
public class AdminTagController extends BaseController {

    private final TagRepository tagRepository;
    private final TagReferRepository tagReferRepository;

    @Autowired
    public AdminTagController(TagRepository tagRepository, TagReferRepository tagReferRepository) {
        this.tagRepository = tagRepository;
        this.tagReferRepository = tagReferRepository;
    }

    @RequestMapping
    @NBAuth(value = "management:tag:page", remark = "标签管理页面", group = NBAuth.Group.ROUTER, type = NBSysResource.ResType.NAV_LINK)
    public String tagIndex() {
        return "management/dictionary/tag";
    }

    @RequestMapping("/list")
    @ResponseBody
    @NBAuth(value = "management:tag:list", remark = "标签管理页面分页数据接口", group = NBAuth.Group.AJAX)
    public LayuiTable<NBTag> cateList(Pagination<NBTag> tagPagination, String tagName) {
        Sort sort = getJpaSort(tagPagination);
        Pageable pageable = PageRequest.of(tagPagination.getPage() - 1, tagPagination.getLimit(), sort);
        if (StringUtils.isEmpty(tagName)) {
            Page<NBTag> tagPage = tagRepository.findAll(pageable);
            return layuiTable(tagPage, pageable);
        } else {
            Example<NBTag> tagExample = Example.of(
                    NBTag.builder().name(tagName).build(),
                    ExampleMatcher.matching().withMatcher("name", ExampleMatcher.GenericPropertyMatcher::contains).withIgnoreCase()
            );
            Page<NBTag> tagPage = tagRepository.findAll(tagExample, pageable);
            return layuiTable(tagPage, pageable);
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    @NBAuth(value = "management:tag:update", remark = "修改标签数据操作接口", group = NBAuth.Group.AJAX)
    public NBR update(@Valid NBTag tag, BindingResult result) {
        if (result.getErrorCount() == 0) {
            return ajaxDone(() -> tagRepository.save(tag) != null, () -> "修改标签");
        } else {
            return ajaxJsr303(result.getFieldErrors());
        }
    }

    @RequestMapping("/delete")
    @ResponseBody
    @NBAuth(value = "management:tag:delete", remark = "删除标签数据操作接口", group = NBAuth.Group.AJAX)
    public NBR delete(@RequestParam("id") Long cateId) {
        return ajaxDone(
                () -> tagReferRepository.countByTagId(cateId) == 0,
                () -> ajaxDone(cateId, tagRepository::deleteById, () -> "删除标签"),
                () -> "请删除相关使用此标签的内容！"
        );
    }
}
