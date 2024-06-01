package com.ruoyi.web.controller.coin;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.coin.service.ICoinUserService;
import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 用户相关
 */
@Controller
@RequestMapping("/coin/open")
public class CoinOpenController extends BaseController {
    private String prefix = "coin/user";

    @Autowired
    private ICoinUserService coinUserService;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;

    //我的邀请列表-数据
    @PostMapping("/getInviteCode")
    @ResponseBody
    public AjaxResult getInviteCode() {
        List<Map<String, Object>> inviteCodeMapList = baseSqlService.getDataByTable("coin_user_info", Func.toStrList("invite_code"));
        String invite_code="";
        if(Func.isEmpty(inviteCodeMapList) || inviteCodeMapList.size()==1){
            invite_code="123456";
        }else{
            int index = RandomUtil.randomInt(0, (inviteCodeMapList.size() - 1));

            Map<String, Object> inviteCodeMap = inviteCodeMapList.get(index);
            invite_code =MjkjUtils.getMap2Str(inviteCodeMap,"invite_code");
        }

        return success(invite_code);
    }

}