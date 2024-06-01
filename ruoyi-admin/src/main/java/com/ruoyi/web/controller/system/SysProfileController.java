package com.ruoyi.web.controller.system;

import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.common.utils.blade.tool.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.MimeTypeUtils;
import com.ruoyi.framework.shiro.service.SysPasswordService;
import com.ruoyi.system.service.ISysUserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 个人信息 业务处理
 *
 * @author ruoyi
 */
@Controller
@RequestMapping("/system/user/profile")
public class SysProfileController extends BaseController {
    private static final Logger log = LoggerFactory.getLogger(SysProfileController.class);

    private String prefix = "system/user/profile";

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysPasswordService passwordService;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;

    /**
     * 个人信息
     */
    @GetMapping()
    public String profile(ModelMap mmap) {
        SysUser user = getSysUser();
        mmap.put("user", user);
        mmap.put("roleGroup", userService.selectUserRoleGroup(user.getUserId()));
        mmap.put("postGroup", userService.selectUserPostGroup(user.getUserId()));

        //获取钱包地址
        Map<String, Object> dataMap = baseSqlService.getDataOneByField("coin_user_info", "user_id", user.getUserId());
        String wallet_address = MjkjUtils.getMap2Str(dataMap, "wallet_address");
        Date expireVipTime = MjkjUtils.getMap2DateTime(dataMap, "expire_vip_time");
        String follow_symbol_cou = MjkjUtils.getMap2Str(dataMap, "follow_symbol_cou");

        boolean wallet_address_null = Func.isEmpty(wallet_address);

        String expireVipTimeStr="";
        if(Func.isNotEmpty(expireVipTime)){
            Date now = DateUtil.now();
            if(now.getTime()>expireVipTime.getTime()){
                expireVipTimeStr="已过期";
            }else{
                expireVipTimeStr= DateUtil.format(expireVipTime,DateUtil.PATTERN_DATETIME);
            }
        }

        mmap.put("wallet_address_null", wallet_address_null);
        mmap.put("expireVipTimeStr",expireVipTimeStr);
        mmap.put("follow_symbol_cou",follow_symbol_cou);
        mmap.putAll(dataMap);

        return prefix + "/profile";
    }

    @GetMapping("/checkPassword")
    @ResponseBody
    public boolean checkPassword(String password) {
        SysUser user = getSysUser();
        return passwordService.matches(user, password);
    }

    @GetMapping("/resetPwd")
    public String resetPwd(ModelMap mmap) {
        SysUser user = getSysUser();
        mmap.put("user", userService.selectUserById(user.getUserId()));
        return prefix + "/resetPwd";
    }

    @Log(title = "重置密码", businessType = BusinessType.UPDATE)
    @PostMapping("/resetPwd")
    @ResponseBody
    public AjaxResult resetPwd(String oldPassword, String newPassword) {
        SysUser user = getSysUser();
        if (!passwordService.matches(user, oldPassword)) {
            return error("修改密码失败，旧密码错误");
        }
        if (passwordService.matches(user, newPassword)) {
            return error("新密码不能与旧密码相同");
        }
        user.setSalt(ShiroUtils.randomSalt());
        user.setPassword(passwordService.encryptPassword(user.getLoginName(), newPassword, user.getSalt()));
        user.setPwdUpdateDate(DateUtils.getNowDate());
        if (userService.resetUserPwd(user) > 0) {
            setSysUser(userService.selectUserById(user.getUserId()));
            return success();
        }
        return error("修改密码异常，请联系管理员");
    }

    /**
     * 修改用户
     */
    @GetMapping("/edit")
    public String edit(ModelMap mmap) {
        SysUser user = getSysUser();
        mmap.put("user", userService.selectUserById(user.getUserId()));
        return prefix + "/edit";
    }

    /**
     * 修改头像
     */
    @GetMapping("/avatar")
    public String avatar(ModelMap mmap) {
        SysUser user = getSysUser();
        mmap.put("user", userService.selectUserById(user.getUserId()));
        return prefix + "/avatar";
    }

    /**
     * 修改用户
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/update")
    @ResponseBody
    public AjaxResult update(SysUser user, HttpServletRequest request) {
        Map<String, Object> parameterMap = MjkjUtils.getParameterMap(request);
        String new_wallet_address = MjkjUtils.getMap2Str(parameterMap, "wallet_address");

        Long userId = getUserId();
        Map<String, Object> userInfoMap = baseSqlService.getDataOneByField("coin_user_info", "user_id", userId);
        String wallet_address = MjkjUtils.getMap2Str(userInfoMap, "wallet_address");
        String id = MjkjUtils.getMap2Str(userInfoMap, "id");
        wallet_address=Func.isEmpty(wallet_address)?"":wallet_address;
        if(!Func.equals(new_wallet_address,wallet_address)){
            if(Func.isNotEmpty(wallet_address)){
                return error("该账号已经绑定过钱包地址，不允许更改");
            }

            Map<String, Object> oneUserInfoMap = baseSqlService.getDataOneByField("coin_user_info", "wallet_address", new_wallet_address);
            if(Func.isNotEmpty(oneUserInfoMap)){
                return error("该地址已被其他人绑定，如非本人绑定，可联系客户申诉！");
            }

            //更详细钱包
            Map<String,Object> updateMap= new HashMap<>();
            updateMap.put("wallet_address",new_wallet_address);
            baseSqlService.baseUpdateData("coin_user_info",updateMap,id);
        }


        String userName = user.getUserName();
        if(userName.contains("管理员") || userName.contains("客服")){
            return error("名称含有特殊字符");
        }


        SysUser currentUser = getSysUser();
        currentUser.setUserName(userName);
        if (userService.updateUserInfo(currentUser) > 0) {
            setSysUser(userService.selectUserById(currentUser.getUserId()));
            return success();
        }
        return error();
    }

    /**
     * 保存头像
     */
    @Log(title = "个人信息", businessType = BusinessType.UPDATE)
    @PostMapping("/updateAvatar")
    @ResponseBody
    public AjaxResult updateAvatar(@RequestParam("avatarfile") MultipartFile file) {
        SysUser currentUser = getSysUser();
        try {
            if (!file.isEmpty()) {
                String avatar = FileUploadUtils.upload(RuoYiConfig.getAvatarPath(), file, MimeTypeUtils.IMAGE_EXTENSION);
                currentUser.setAvatar(avatar);
                if (userService.updateUserInfo(currentUser) > 0) {
                    setSysUser(userService.selectUserById(currentUser.getUserId()));
                    return success();
                }
            }
            return error();
        } catch (Exception e) {
            log.error("修改头像失败！", e);
            return error(e.getMessage());
        }
    }
}
