package com.project.samplingsystem.web.management;

import com.project.samplingsystem.config.permission.NBAuth;
import com.project.samplingsystem.dao.repository.CloudFileCateRepository;
import com.project.samplingsystem.dao.repository.CloudFileRepository;
import com.project.samplingsystem.exception.NoteFetchFailedException;
import com.project.samplingsystem.model.entity.NBCloudFile;
import com.project.samplingsystem.model.pojo.framework.LayuiTable;
import com.project.samplingsystem.model.pojo.framework.NBR;
import com.project.samplingsystem.model.pojo.framework.Pagination;
import com.project.samplingsystem.util.NBUtils;
import com.project.samplingsystem.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.project.samplingsystem.config.permission.NBAuth.Group.AJAX;
import static com.project.samplingsystem.config.permission.NBAuth.Group.ROUTER;
import static com.project.samplingsystem.model.entity.permission.NBSysResource.ResType.NAV_LINK;
import static com.project.samplingsystem.model.entity.permission.NBSysResource.ResType.OTHER;

/**
 * created by Wuwenbin on 2018/8/18 at 10:14
 *
 * @author wuwenbin
 */
@RequestMapping("/management/cloudFile")
@Controller
public class CloudFileController extends BaseController {

    private final CloudFileRepository cloudFileRepository;
    private final CloudFileCateRepository cloudFileCateRepository;

    @Autowired
    public CloudFileController(CloudFileRepository cloudFileRepository, CloudFileCateRepository cloudFileCateRepository) {
        this.cloudFileRepository = cloudFileRepository;
        this.cloudFileCateRepository = cloudFileCateRepository;
    }

    @RequestMapping(method = RequestMethod.GET)
    @NBAuth(value = "management:cloudFile:list_page", remark = "?????????????????????", type = NAV_LINK, group = ROUTER)
    public String list() {
        return "management/cloud/cloud_file_list";
    }

    @RequestMapping("/add")
    @NBAuth(value = "management:cloudFile:add_page", remark = "?????????????????????", type = NAV_LINK, group = ROUTER)
    public String add(Model model) {
        model.addAttribute("cateList", cloudFileCateRepository.findAll());
        return "management/cloud/cloud_file_add";
    }

    @RequestMapping("/edit")
    @NBAuth(value = "management:cloudFile:edit_page", remark = "????????????????????????", type = OTHER, group = ROUTER)
    public String edit(Model model, Long id) {
        Optional<NBCloudFile> cloudFile = cloudFileRepository.findById(id);
        model.addAttribute("cateList", cloudFileCateRepository.findAll());
        model.addAttribute("editCloudFile", cloudFile.orElseThrow(NoteFetchFailedException::new));
        return "management/cloud/cloud_file_edit";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @NBAuth(value = "management:cloudFile:list_data", remark = "???????????????????????????????????????", group = AJAX)
    @ResponseBody
    public LayuiTable<NBCloudFile> cloudFileList(Pagination<NBCloudFile> nbCloudFilePagination) {
        Pageable pageable = getPageable(nbCloudFilePagination);
        Page<NBCloudFile> jpaPage = cloudFileRepository.findAll(pageable);
        return layuiTable(jpaPage, pageable);
    }

    @RequestMapping("/create")
    @NBAuth(value = "management:cloudFile:create", remark = "????????????????????????", group = AJAX)
    @ResponseBody
    public NBR projectCreate(@Valid NBCloudFile nbCloudFile, BindingResult result) {
        if (result.getErrorCount() == 0) {
            nbCloudFile.setPost(LocalDateTime.now());
            nbCloudFile.setModify(LocalDateTime.now());
            nbCloudFile.setDescription(NBUtils.stripSqlXSS(nbCloudFile.getDescription()));
            return ajaxDone(
                    () -> cloudFileRepository.save(nbCloudFile) != null
                    , () -> "???????????????"
            );
        } else {
            return ajaxJsr303(result.getFieldErrors());
        }
    }


    @RequestMapping("/update")
    @NBAuth(value = "management:cloudFile:update", remark = "?????????????????????", group = AJAX)
    @ResponseBody
    public NBR projectUpdate(@Valid NBCloudFile nbCloudFile, BindingResult result) {
        if (result.getErrorCount() == 0) {
            nbCloudFile.setModify(LocalDateTime.now());
            return ajaxDone(() -> cloudFileRepository.saveAndFlush(nbCloudFile) != null, () -> "?????????????????????");
        } else {
            return ajaxJsr303(result.getFieldErrors());
        }
    }

    @RequestMapping("/delete/{id}")
    @ResponseBody
    @NBAuth(value = "management:cloudFile:delete", remark = "?????????????????????", group = AJAX)
    public NBR delete(@PathVariable("id") Long id) {
        return ajaxDone(id, cloudFileRepository::deleteById, () -> "????????????");
    }

}
