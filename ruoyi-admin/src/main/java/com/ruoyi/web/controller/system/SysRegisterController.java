package com.ruoyi.web.controller.system;

import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.shiro.service.SysRegisterService;
import com.ruoyi.system.service.ISysConfigService;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 注册验证
 *
 * @author ruoyi
 */
@Controller
public class SysRegisterController extends BaseController {
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    @ResponseBody
    public AjaxResult ajaxRegister(SysUser user, HttpServletRequest request) {
        Map<String, Object> parameterMap = MjkjUtils.getParameterMap(request);
        String inviteCode = MjkjUtils.getMap2Str(parameterMap, "inviteCode");
        if(Func.isEmpty(inviteCode)){
            return error("请输入邀请码");
        }


        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser")))) {
            return error("当前系统没有开启注册功能！");
        }
        String msg = registerService.register(user,inviteCode);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }
}
