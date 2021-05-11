package com.project.samplingsystem.config.listener;

import com.project.samplingsystem.dao.repository.ParamRepository;
import com.project.samplingsystem.exception.UploadPathMissException;
import lombok.extern.slf4j.Slf4j;
import com.project.samplingsystem.model.constant.NoteBlogV4;
import com.project.samplingsystem.model.constant.Upload;
import com.project.samplingsystem.model.entity.NBParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;


/**
 * created by Wuwenbin on 2018/8/3 at 22:46
 * @author wuwenbin
 */
@Slf4j
@Component
@Order(3)
public class UploadFolderListener implements ApplicationListener<ApplicationReadyEvent> {

    private final ParamRepository paramRepository;
    private final Environment env;

    @Autowired
    public UploadFolderListener(ParamRepository paramRepository, Environment env) {
        this.paramRepository = paramRepository;
        this.env = env;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        initUploadFolder();
    }

    /**
     * 初始化上传文件夹
     *
     * @throws Exception
     */
    private void initUploadFolder() {
        NBParam param = paramRepository.findByName(NoteBlogV4.Param.UPLOAD_TYPE);
        String value = param.getValue();
        if (Upload.Method.LOCAL.name().equalsIgnoreCase(value)) {
            String uploadPathKey = "noteblog.upload.path";
            String path = env.getProperty(uploadPathKey);
            if (!StringUtils.isEmpty(path)) {
                log.info("「个人博客」APP 文件上传路径设置为：[{}]", path);
                path = path.replace("file:", "");
                File filePath = new File(path + "file/");
                File imgPath = new File(path + "img/");
                boolean f = false, i = false;
                if (!filePath.exists() && !filePath.isDirectory()) {
                    f = filePath.mkdirs();
                }
                if (!imgPath.exists() && !imgPath.isDirectory()) {
                    i = imgPath.mkdirs();
                }
                if (f && i) {
                    log.info("「个人博客」APP 成功创建上传文件夹目录：[{}] 和 [{}]", path + "file/", path + "img/");
                } else if (f) {
                    log.info("「个人博客」APP 目录 [{}] 已存在上传文件夹或创建失败", path + "img/");
                } else if (i) {
                    log.info("「个人博客」APP 目录 [{}] 已存在上传文件夹或创建失败", path + "file/");
                } else {
                    log.info("「个人博客」APP 已存在上传文件夹！");
                }
            } else {
                log.error("上传路径未正确设置");
                throw new UploadPathMissException("上传路径未正确设置！");
            }
        }
    }
}
