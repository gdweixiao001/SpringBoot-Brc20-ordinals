package com.ruoyi.coin.service;

import org.springframework.ui.ModelMap;

/**
 * 首页相关
 */
public interface ICoinIndexService {

    //首页数据
    void indexData(ModelMap mmap,Long userId);

}
