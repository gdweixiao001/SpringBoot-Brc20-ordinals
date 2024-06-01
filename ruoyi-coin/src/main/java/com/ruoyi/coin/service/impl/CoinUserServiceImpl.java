package com.ruoyi.coin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.coin.mapper.CoinUserMapper;
import com.ruoyi.coin.service.ICoinUserService;
import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.common.utils.MjkjUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 用户相关
 */
@Service
public class CoinUserServiceImpl implements ICoinUserService {

    @Autowired
    private CoinUserMapper  userMapper;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;

    //获取我的邀请
    @Override
    public IPage<Map<String, Object>> getMyInvitePage(IPage page,Long userId){
        Map<String, Object> userInfoMap = baseSqlService.getDataOneByField("coin_user_info", "user_id", userId);
        String userInfoId = MjkjUtils.getMap2Str(userInfoMap, "id");

        IPage<Map<String, Object>> pages = userMapper.getMyInvitePage(page,userInfoId);
        return pages;
    }




}
