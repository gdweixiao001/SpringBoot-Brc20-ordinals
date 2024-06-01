package com.ruoyi.coin.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.Map;

/**
 * 用户相关
 */
public interface CoinUserMapper {

    //获取最近部署的铭文列表
    IPage<Map<String, Object>> getMyInvitePage(IPage page,String userInfoId);


}
