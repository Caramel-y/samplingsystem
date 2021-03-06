package com.project.samplingsystem.service.login;

import cn.hutool.core.codec.Base64;
import com.project.samplingsystem.dao.repository.UserRepository;
import com.project.samplingsystem.config.application.NBContext;
import com.project.samplingsystem.model.constant.NoteBlogV4;
import com.project.samplingsystem.model.entity.permission.NBSysUser;
import com.project.samplingsystem.model.pojo.business.SimpleLoginData;
import com.project.samplingsystem.model.pojo.framework.NBR;
import com.project.samplingsystem.util.CookieUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.project.samplingsystem.model.pojo.framework.NBR.error;
import static com.project.samplingsystem.model.pojo.framework.NBR.ok;

/**
 * 普通登录方法
 * created by Wuwenbin on 2018/7/21 at 18:05
 * @author wuwenbin
 */
@Service("simpleLogin")
@Transactional(rollbackOn = Exception.class)
public class SimpleLoginServiceImpl implements LoginService<SimpleLoginData> {

    private final UserRepository userRepository;
    private final NBContext blogContext;

    @Autowired
    public SimpleLoginServiceImpl(UserRepository userRepository, NBContext blogContext) {
        this.userRepository = userRepository;
        this.blogContext = blogContext;
    }

    @Override
    public NBR doLogin(SimpleLoginData data) {
        NBSysUser findUser = userRepository.findByUsernameAndPasswordAndEnableTrue(data.getBmyName(), data.getBmyPass());
        if (findUser != null) {
            Boolean remember = data.getRemember();
            if (remember != null && remember.equals(Boolean.TRUE)) {
                String cookieValue = Base64.encode(findUser.getUsername())
                        .concat(NoteBlogV4.Session.COOKIE_SPLIT).concat(findUser.getPassword());
                CookieUtils.setCookie(data.getResponse(), NoteBlogV4.Session.REMEMBER_COOKIE_NAME,
                        cookieValue, 15 * 24 * 60 * 60);
            }
            blogContext.setSessionUser(data.getRequest(), data.getResponse(), findUser);
            long masterRoleId = blogContext.getApplicationObj(NoteBlogV4.Session.WEBMASTER_ROLE_ID);
            String redirectUrl =
                    findUser.getDefaultRoleId() == masterRoleId
                            ? NoteBlogV4.Session.MANAGEMENT_INDEX
                            : NoteBlogV4.Session.FRONTEND_INDEX;
            return ok("登陆成功！", redirectUrl);
        } else {
            return error("用户名或密码错误！");
        }

    }


}
