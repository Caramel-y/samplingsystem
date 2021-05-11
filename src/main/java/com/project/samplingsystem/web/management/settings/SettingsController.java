package com.project.samplingsystem.web.management.settings;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.project.samplingsystem.config.application.NBContext;
import com.project.samplingsystem.config.permission.NBAuth;
import com.project.samplingsystem.dao.repository.ParamRepository;
import com.project.samplingsystem.dao.repository.UserRepository;
import com.project.samplingsystem.model.constant.NoteBlogV4;
import com.project.samplingsystem.model.entity.NBParam;
import com.project.samplingsystem.model.entity.permission.NBSysResource;
import com.project.samplingsystem.model.entity.permission.NBSysUser;
import com.project.samplingsystem.model.pojo.framework.NBR;
import com.project.samplingsystem.service.settings.SettingsService;
import com.project.samplingsystem.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * created by Wuwenbin on 2018/8/11 at 16:14
 *
 * @author wuwenbin
 */
@Controller
@RequestMapping("/management")
public class SettingsController extends BaseController {

    private final ParamRepository paramRepository;
    private final SettingsService settingsService;
    private final NBContext context;
    private final UserRepository userRepository;

    @Autowired
    public SettingsController(ParamRepository paramRepository, SettingsService settingsService, NBContext context, UserRepository userRepository) {
        this.paramRepository = paramRepository;
        this.settingsService = settingsService;
        this.context = context;
        this.userRepository = userRepository;
    }

    @RequestMapping("/settings/common")
    @NBAuth(value = "management:settings:common", remark = "网站基本设置界面", group = NBAuth.Group.ROUTER, type = NBSysResource.ResType.NAV_LINK)
    public String settingsCommon(Model model) {
        List<NBParam> params = paramRepository.findAllByLevelGreaterThanEqual(9);
        Map<String, Object> attributeMap = params.stream().collect(Collectors.toMap(NBParam::getName, NBParam::getValue));
        model.addAllAttributes(attributeMap);
        return "management/settings/common";
    }

    @RequestMapping("/settings/theme")
    @NBAuth(value = "management:settings:theme", remark = "网站风格设置界面", group = NBAuth.Group.ROUTER, type = NBSysResource.ResType.NAV_LINK)
    public String settingTheme(Model model) {
        List<NBParam> params = paramRepository.findAllByLevelGreaterThanEqual(9);
        Map<String, Object> attributeMap = params.stream().collect(Collectors.toMap(NBParam::getName, NBParam::getValue));
        model.addAllAttributes(attributeMap);
        return "management/settings/theme";
    }

    @RequestMapping("/settings/mail")
    @NBAuth(value = "management:settings:mail", remark = "网站发送邮件服务器设置", group = NBAuth.Group.ROUTER, type = NBSysResource.ResType.NAV_LINK)
    public String settingsMail(Model model) {
        List<NBParam> params = paramRepository.findAllByLevel(8);
        Map<String, Object> attributeMap = params.stream().collect(Collectors.toMap(NBParam::getName, NBParam::getValue));
        model.addAllAttributes(attributeMap);
        return "management/settings/mail";
    }

    @RequestMapping("/settings/profile")
    @NBAuth(value = "management:settings:profile", remark = "管理员个人信息设置", group = NBAuth.Group.ROUTER, type = NBSysResource.ResType.NAV_LINK)
    public String settingsProfile(Model model, @CookieValue(NoteBlogV4.Session.SESSION_ID_COOKIE) String uuid) {
        model.addAttribute("loginUser", context.getSessionUser(uuid));
        return "management/settings/profile";
    }

    @RequestMapping("/settings/qrcode")
    @NBAuth(value = "management:settings:qrcode", remark = "微信和支付宝二维码图片设置界面", group = NBAuth.Group.ROUTER, type = NBSysResource.ResType.NAV_LINK)
    public String settingsQrcode(Model model) {
        List<NBParam> params = paramRepository.findAllByLevel(11);
        Map<String, Object> attributeMap = params.stream().collect(Collectors.toMap(NBParam::getName, NBParam::getValue));
        model.addAllAttributes(attributeMap);
        return "management/settings/qrcode";
    }

    @RequestMapping("/settings/update")
    @NBAuth(value = "management:settings:update", remark = "网站设置修改操作", group = NBAuth.Group.AJAX)
    @ResponseBody
    public NBR update(String type, String name, String value) {
        final String switches = "switch", text = "text";
        if (StringUtils.isEmpty(type)) {
            return NBR.error("修改参数类型为空！");
        }
        if (switches.equalsIgnoreCase(type)) {
            return settingsService.updateSwitch(name, value);
        } else if (text.equalsIgnoreCase(type)) {
            return settingsService.updateText(name, value);
        } else {
            return NBR.error("未识别修改参数类型！");
        }
    }

    @RequestMapping("/settings/mail/update")
    @NBAuth(value = "management:settings:mail_update", remark = "网站邮件服务器修改操作", group = NBAuth.Group.AJAX)
    @ResponseBody
    public NBR updateMailConfig(HttpServletRequest request) {
        return settingsService.updateMailConfig(WebUtils.getParametersStartingWith(request, ""));
    }

    @RequestMapping(value = "/settings/profile/update", method = RequestMethod.POST)
    @NBAuth(value = "management:settings:profile_update", remark = "网站管理员修改操作", group = NBAuth.Group.AJAX)
    @ResponseBody
    public NBR updateProfile(String nickname, String email, String password1, String password2, @CookieValue(NoteBlogV4.Session.SESSION_ID_COOKIE) String uuid, String avatar) {
        NBSysUser loginUser = context.getSessionUser(uuid);
        if (StrUtil.isNotEmpty(nickname)) {
            userRepository.updateUserNickname(loginUser.getId(), nickname);
        }
        if (!StringUtils.isEmpty(password1)) {
            if (password1.equals(password2)) {
                String dbPass = SecureUtil.md5(password1);
                userRepository.updateUserPass(loginUser.getId(), dbPass);
            } else {
                return ajaxDone(() -> false, () -> "两次输入的密码不一致，更新密码");
            }
        }
        if (!StringUtils.isEmpty(avatar)) {
            userRepository.updateUserAvatar(loginUser.getId(), avatar);
        }
        if (!StringUtils.isEmpty(email)) {
            userRepository.updateUserEmail(loginUser.getId(), email);
        }
        return ajaxDone(() -> true, () -> "重新登录生效，更新信息");
    }

    @RequestMapping("/settings/pay/update")
    @NBAuth(value = "management:settings:pay_update", remark = "支付宝/微信二维码修改操作", group = NBAuth.Group.AJAX)
    @ResponseBody
    public NBR updateQrcode(String value, String name, String msg) {
        paramRepository.updateValueByName(name, value);
        return NBR.ok("修改" + msg + "成功！");
    }
}
