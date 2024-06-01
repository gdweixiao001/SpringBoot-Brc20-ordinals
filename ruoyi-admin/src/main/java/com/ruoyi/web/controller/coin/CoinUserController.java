package com.ruoyi.web.controller.coin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.coin.service.ICoinUserService;
import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * 用户相关
 */
@Controller
@RequestMapping("/coin/user")
public class CoinUserController extends BaseController {
    private String prefix = "coin/user";

    @Autowired
    private ICoinUserService coinUserService;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;

    //我的邀请列表-页面
    @GetMapping("/myInviteListHtml")
    public String myInviteListHtml() {
        return prefix + "/myInviteList";
    }

    //我的邀请列表-数据
    @PostMapping("/myInvitePage")
    @ResponseBody
    public TableDataInfo myInvitePage() {
        Page page = getPage();
        IPage<Map<String, Object>> pages = coinUserService.getMyInvitePage(page, getUserId());
        return getDataTable(pages);
    }


}