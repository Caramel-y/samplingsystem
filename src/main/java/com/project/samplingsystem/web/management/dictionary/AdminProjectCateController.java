package com.project.samplingsystem.web.management.dictionary;

import cn.hutool.core.util.StrUtil;
import com.project.samplingsystem.config.permission.NBAuth;
import com.project.samplingsystem.dao.repository.ProjectCateRepository;
import com.project.samplingsystem.dao.repository.ProjectRepository;
import com.project.samplingsystem.model.entity.NBProjectCate;
import com.project.samplingsystem.model.entity.permission.NBSysResource;
import com.project.samplingsystem.model.pojo.framework.LayuiTable;
import com.project.samplingsystem.model.pojo.framework.NBR;
import com.project.samplingsystem.model.pojo.framework.Pagination;
import com.project.samplingsystem.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;

/**
 * created by Wuwenbin on 2018/8/15 at 16:29
 *
 * @author wuwenbin
 */
@Controller
@RequestMapping("/management/dictionary/projectCate")
public class AdminProjectCateController extends BaseController {

    private final ProjectCateRepository cateRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public AdminProjectCateController(ProjectCateRepository cateRepository, ProjectRepository projectRepository) {
        this.cateRepository = cateRepository;
        this.projectRepository = projectRepository;
    }

    @RequestMapping
    @NBAuth(value = "management:projectCate:page", remark = "项目分类管理页面", group = NBAuth.Group.ROUTER, type = NBSysResource.ResType.NAV_LINK)
    public String cate() {
        return "management/dictionary/projectCate";
    }

    @RequestMapping("/list")
    @ResponseBody
    @NBAuth(value = "management:projectCate:list", remark = "项目分类管理分页数据", group = NBAuth.Group.AJAX)
    public LayuiTable<NBProjectCate> cateList(Pagination<NBProjectCate> catePage) {
        //jpa分页是从0开始
        Pageable pageable = PageRequest.of(catePage.getPage() - 1, catePage.getLimit());
        Page<NBProjectCate> page = cateRepository.findAll(pageable);
        return layuiTable(page, pageable);
    }

    @RequestMapping("/create")
    @NBAuth(value = "management:projectCate:create", remark = "添加项目分类操作", group = NBAuth.Group.AJAX)
    @ResponseBody
    public NBR cateCreate(NBProjectCate cate) {
        if (cate != null && StrUtil.isNotEmpty(cate.getName())) {
            return ajaxDone(
                    () -> cateRepository.findCateCount(cate) == 0,
                    () -> ajaxDone(() -> cateRepository.save(cate) != null, () -> "添加项目分类信息"),
                    () -> "已存在此项目分类信息"
            );
        }
        return NBR.error("添加分类值有误！");
    }

    @RequestMapping("/delete")
    @NBAuth(value = "management:projectCate:delete", remark = "删除项目分类操作", group = NBAuth.Group.AJAX)
    @ResponseBody
    public NBR delete(Long cateId) {
        return ajaxDone(
                () -> projectRepository.countByCateId(cateId) == 0,
                () -> ajaxDone(cateId, cateRepository::deleteById, () -> "删除项目分类"),
                () -> "此项目分类下还有项目！"
        );
    }

    @RequestMapping("/update")
    @NBAuth(value = "management:projectCate:update", remark = "修改项目分类操作", group = NBAuth.Group.AJAX)
    @ResponseBody
    public NBR update(@Valid NBProjectCate cate, BindingResult result) {
        if (result.getErrorCount() == 0) {
            return ajaxDone(
                    () -> cateRepository.findCateCount(cate) == 0,
                    () -> ajaxDone(() -> cateRepository.save(cate) != null, () -> "修改项目分类信息"),
                    () -> "已存在此项目分类信息（项目分类名/中文名重复）"
            );
        } else {
            return ajaxJsr303(result.getFieldErrors());
        }
    }
}
