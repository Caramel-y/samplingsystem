package com.project.samplingsystem.service.upload;

import com.project.samplingsystem.dao.repository.UploadRepository;
import lombok.extern.slf4j.Slf4j;
import com.project.samplingsystem.model.constant.LayUploader;
import com.project.samplingsystem.model.constant.NkUploader;
import com.project.samplingsystem.model.constant.Upload;
import com.project.samplingsystem.model.entity.NBUpload;
import com.project.samplingsystem.model.pojo.framework.NBR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.function.Consumer;

/**
 * created by Wuwenbin on 2018/7/17 at 10:33
 * 本地上传的实现类
 *
 * @author wuwenbin
 */
@Slf4j
@Service("localUpload")
@Transactional(rollbackOn = Exception.class)
public class LocalUploadServiceImpl implements UploadService<Object> {

    private final UploadRepository uploadRepository;

    @Autowired
    public LocalUploadServiceImpl(UploadRepository uploadRepository) {
        this.uploadRepository = uploadRepository;
    }

    @Override
    public Upload.Method getUploadMethod() {
        return Upload.Method.LOCAL;
    }

    @Override
    public <S> Object upload(MultipartFile fileObj, String reqType, Consumer<S> extra, S s) {
        try {
            NBUpload upload = uploadIt(fileObj, extra, s);
            uploadRepository.saveAndFlush(upload);
            if (LAYUI_UPLOADER.equalsIgnoreCase(reqType)) {
                return new LayUploader().ok("上传成功！", upload.getVirtualPath());
            } else if (NKEDITOR_UPLOADER.equalsIgnoreCase(reqType)) {
                return new NkUploader().ok(upload.getVirtualPath());
            } else {
                return NBR.ok("上传成功！", upload.getVirtualPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("上传图片/文件失败，错误信息：{}", e.getMessage());
            if (LAYUI_UPLOADER.equalsIgnoreCase(reqType)) {
                return new LayUploader().err("上传图片/文件失败，错误信息：" + e.getLocalizedMessage());
            } else if (NKEDITOR_UPLOADER.equalsIgnoreCase(reqType)) {
                return new NkUploader().err("上传图片/文件失败，错误信息：" + e.getLocalizedMessage());
            } else {
                return NBR.error("上传图片/文件失败，错误信息：" + e.getLocalizedMessage());
            }
        }
    }
}
