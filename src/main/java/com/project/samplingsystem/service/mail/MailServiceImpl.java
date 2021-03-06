package com.project.samplingsystem.service.mail;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailUtil;
import com.project.samplingsystem.dao.repository.ParamRepository;
import com.project.samplingsystem.model.constant.NoteBlogV4;
import com.project.samplingsystem.model.entity.NBArticle;
import com.project.samplingsystem.model.entity.permission.NBSysUser;
import com.project.samplingsystem.util.NBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * created by Wuwenbin on 2019-01-08 at 22:27
 *
 * @author wuwenbin
 */
@Service
public class MailServiceImpl implements MailService {
    private final ParamRepository paramRepository;


    @Autowired
    public MailServiceImpl(ParamRepository paramRepository) {
        this.paramRepository = paramRepository;

    }

    @Override
    public void sendNoticeMail(String site, NBArticle article, String comment) {
        String host = paramRepository.findByName(NoteBlogV4.Param.MAIL_SMPT_SERVER_ADDR).getValue();
        String port = paramRepository.findByName(NoteBlogV4.Param.MAIL_SMPT_SERVER_PORT).getValue();
        String from = paramRepository.findByName(NoteBlogV4.Param.MAIL_SERVER_ACCOUNT).getValue();
        String user = paramRepository.findByName(NoteBlogV4.Param.MAIL_SENDER_NAME).getValue();
        String pass = paramRepository.findByName(NoteBlogV4.Param.MAIL_SERVER_PASSWORD).getValue();
        if (StrUtil.isNotEmpty(host)
                && StrUtil.isNotEmpty(port)
                && StrUtil.isNotEmpty(from)
                && StrUtil.isNotEmpty(pass)) {
            MailAccount account = new MailAccount();
            account.setHost(host);
            account.setPort(Integer.valueOf(port));
            account.setAuth(true);
            account.setSslEnable(true);
            account.setFrom(from);
            account.setUser(user);
            account.setPass(pass);
            NBSysUser u = NBUtils.getSessionUser();
            String targetMail = u != null ? u.getEmail() : host;

            String subject = "???????????? - ???{}??? ?????????????????????";
            String content = "<p>??????????????????{}???????????????????????????</p>" +
                    "<p style='font-style:italic;'>{}</p>" +
                    "<p>??????<a href='{}article/{}' target='_blank'>??????</a></p>";
            MailUtil.send(account,
                    CollUtil.newArrayList(targetMail),
                    StrUtil.format(subject, article.getTitle()),
                    StrUtil.format(content, article.getTitle(), comment, site, article.getId()),
                    true);
        }
    }
}
