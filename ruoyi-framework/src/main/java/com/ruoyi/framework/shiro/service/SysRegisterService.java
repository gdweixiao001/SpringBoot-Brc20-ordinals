package com.ruoyi.framework.shiro.service;

import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.ShiroConstants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.*;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册校验方法
 *
 * @author ruoyi
 */
@Component
public class SysRegisterService {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;


    /**
     * 注册
     */
    public String register(SysUser user, String inviteCode) {
        String msg = "", loginName = user.getLoginName(), password = user.getPassword();

        Map<String, Object> p_userInfoMap = baseSqlService.getDataOneByField("coin_user_info", "invite_code", inviteCode);
        if(Func.isEmpty(p_userInfoMap)){
            return "邀请码不存在";
        }
        String pid = MjkjUtils.getMap2Str(p_userInfoMap, "id");

        if (ShiroConstants.CAPTCHA_ERROR.equals(ServletUtils.getRequest().getAttribute(ShiroConstants.CURRENT_CAPTCHA))) {
            msg = "验证码错误";
        } else if (StringUtils.isEmpty(loginName)) {
            msg = "用户名不能为空";
        } else if (StringUtils.isEmpty(password)) {
            msg = "用户密码不能为空";
        } else if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            msg = "密码长度必须在5到20个字符之间";
        } else if (loginName.length() < UserConstants.USERNAME_MIN_LENGTH
                || loginName.length() > UserConstants.USERNAME_MAX_LENGTH) {
            msg = "账户长度必须在2到20个字符之间";
        } else if (!userService.checkLoginNameUnique(user)) {
            msg = "保存用户'" + loginName + "'失败，注册账号已存在";
        } else {
            user.setPwdUpdateDate(DateUtils.getNowDate());
            user.setUserName(loginName);
            user.setSalt(ShiroUtils.randomSalt());
            user.setPassword(passwordService.encryptPassword(loginName, password, user.getSalt()));
            boolean regFlag = userService.registerUser(user);
            if (!regFlag) {
                msg = "注册失败,请联系系统管理人员";
            } else {
                Long userId = user.getUserId();
                String createInviteCode = baseSqlService.creatInviteCode();
                //存到自己表
                Map<String,Object> userInfoMap=new HashMap<>();
                userInfoMap.put("user_id",userId);
                userInfoMap.put("p_id",pid);
                userInfoMap.put("invite_code",createInviteCode);
                userInfoMap.put("is_vip",0);
                userInfoMap.put("follow_symbol_cou",5);
                baseSqlService.baseInsertData("coin_user_info",userInfoMap);

                //插入到权限表
                Map<String,Object> roleUserMap=new HashMap<>();
                roleUserMap.put("user_id",userId);
                roleUserMap.put("role_id",2);
                baseSqlService.baseSimpleIntegerSql("sys_user_role",roleUserMap);


                AsyncManager.me().execute(AsyncFactory.recordLogininfor(loginName, Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }
}
