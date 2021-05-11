package com.project.samplingsystem.web.management.authority;

import com.project.samplingsystem.config.permission.NBAuth;
import com.project.samplingsystem.dao.repository.RoleRepository;
import com.project.samplingsystem.dao.repository.UserRepository;
import com.project.samplingsystem.model.entity.permission.NBSysResource;
import com.project.samplingsystem.model.entity.permission.NBSysRole;
import com.project.samplingsystem.model.entity.permission.NBSysUser;
import com.project.samplingsystem.model.pojo.framework.LayuiTable;
import com.project.samplingsystem.model.pojo.framework.NBR;
import com.project.samplingsystem.model.pojo.framework.Pagination;
import com.project.samplingsystem.service.users.UsersService;
import com.project.samplingsystem.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * created by Wuwenbin on 2018/7/28 at 23:37
 *
 * @author wuwenbin
 */
@Controller
@RequestMapping("/management/users")
public class UserController extends BaseController {

    private final UsersService usersService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Autowired
    public UserController(UsersService us, UserRepository ur, RoleRepository rr) {
        this.usersService = us;
        this.userRepository = ur;
        this.roleRepository = rr;
    }

    @RequestMapping
    @NBAuth(value = "management:user:router", remark = "用户管理界面", type = NBSysResource.ResType.NAV_LINK, group = NBAuth.Group.ROUTER)
    public String usersListRouter() {
        return "management/authority/users";
    }


    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @NBAuth(value = "management:user:table_list", remark = "用户信息分页数据", group = NBAuth.Group.AJAX)
    public LayuiTable<NBSysUser> userList(Pagination<NBSysUser> userPage, NBSysUser user) {
        Sort jpaSort = getJpaSort(userPage);
        Pageable pageable = PageRequest.of(userPage.getPage() - 1, userPage.getPageSize(), jpaSort);
        Page<NBSysUser> pageUser = usersService.findUserPage(pageable, user);
        return layuiTable(pageUser, pageable);
    }


    @RequestMapping(value = "/enable/update", method = RequestMethod.POST)
    @ResponseBody
    @NBAuth(value = "management:user:enable_update", remark = "修改用户状态信息", group = NBAuth.Group.AJAX)
    public NBR enableUpdate(Long id, Boolean enable) {
        return ajaxDone(
                () -> userRepository.updateUserStatus(id, enable) > 0,
                () -> "修改用户状态"
        );
    }

    @RequestMapping("/roles/list")
    @ResponseBody
    @NBAuth(value = "management:user:role_list", remark = "查询用户的角色信息", group = NBAuth.Group.AJAX)
    public List<NBSysRole> roleList(Long userId) {
        if (StringUtils.isEmpty(userId)) {
            return roleRepository.findAll();
        } else {
            return roleRepository.findUserRoleIds(userId);
        }
    }

    @RequestMapping("/roles/id/update")
    @ResponseBody
    @NBAuth(value = "management:user:role_update", remark = "修改用户的角色关联信息", group = NBAuth.Group.AJAX)
    public NBR userRolesIdUpdate(Long userId, String roleIds) {
        if (StringUtils.isEmpty(roleIds)) {
            return NBR.error("角色信息为空，至少选择一个角色信息！");
        } else if (StringUtils.isEmpty(userId)) {
            return NBR.error("用户信息为空，请勾选用户！");
        } else {
            usersService.updateUserRoleIds(userId, roleIds);
            return NBR.ok("更新用户角色信息成功！");
        }
    }

    @RequestMapping("/roles/str/update")
    @ResponseBody
    @NBAuth(value = "management:user:role_update", remark = "修改用户的角色关联信息", group = NBAuth.Group.AJAX)
    public NBR userRolesStrUpdate(Long userId, String roleNames) {
        if (StringUtils.isEmpty(roleNames)) {
            return NBR.error("角色信息为空，至少选择一个角色信息！");
        } else if (StringUtils.isEmpty(userId)) {
            return NBR.error("用户信息为空，请勾选用户！");
        } else {
            usersService.updateUserRolesStr(userId, roleNames);
            return NBR.ok("更新用户角色信息成功！");
        }
    }

    @RequestMapping("/nickname/update")
    @ResponseBody
    @NBAuth(value = "management:user:nickname_update", remark = "修改用户昵称信息", group = NBAuth.Group.AJAX)
    public NBR nicknameUpdate(Long userId, String nickname) {
        if (StringUtils.isEmpty(nickname)) {
            return NBR.error("昵称不能为空！");
        } else {
            return ajaxDone(
                    () -> userRepository.updateUserNickname(userId, nickname) > 0,
                    () -> "修改用户昵称"
            );
        }
    }

}
