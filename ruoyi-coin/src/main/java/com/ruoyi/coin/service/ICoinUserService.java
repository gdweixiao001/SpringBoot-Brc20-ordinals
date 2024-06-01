package com.ruoyi.coin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

/**
 * 用户相关相关
 */
public interface ICoinUserService {

    //获取我的邀请
    IPage<Map<String, Object>> getMyInvitePage(IPage page,Long userId);


}
