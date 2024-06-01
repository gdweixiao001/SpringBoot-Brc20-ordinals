package com.ruoyi.coin.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.coin.http.model.OkxTickerInfo;
import com.ruoyi.coin.mapper.CoinTjMapper;
import com.ruoyi.coin.service.ICoinIndexService;
import com.ruoyi.coin.service.ICoinTjService;
import com.ruoyi.coin.service.IOkxService;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.common.utils.blade.tool.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 首页相关
 */
@Service
public class CoinIndexServiceImpl implements ICoinIndexService {

    @Autowired
    private ICoinTjService coinTjService;

    @Autowired
    private IOkxService okxService;

    @Autowired
    private CoinTjMapper coinTjMapper;

    //首页数据
    @Override
    public void indexData(ModelMap mmap,Long userId) {
 /*       //部署列表
        IPage<Map<String, Object>> deployedCoinPage = coinTjService.getDeployedCoinPage(new Page(1, 10));
        //头像列表
        IPage<Map<String, Object>> uploadImgMapPage = coinTjService.getUploadImgCoinPage(new Page(1, 10));
        //铸造完成列表
        IPage<Map<String, Object>> mintSuccessMapPage = coinTjService.getMintFinallyCoinPage(new Page(1, 10));

        mmap.put("deployCou",deployedCoinPage.getTotal());//部署完成数
        mmap.put("uploadImgCou",uploadImgMapPage.getTotal());//上传头像数
        mmap.put("mintCou",mintSuccessMapPage.getTotal());//铸造完成数*/


        List<Map<String, Object>> mySymbolList = coinTjMapper.getMySymbolList(userId);
        for(Map<String,Object> dataMap:mySymbolList){
            String market_cap_btc_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_btc", 4, "BTC");
            dataMap.put("market_cap_btc_str",market_cap_btc_str);

            BigDecimal zdf = MjkjUtils.getMap2BigD(dataMap, "zdf");
            BigDecimal zdftmp = zdf.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            if(zdftmp.compareTo(BigDecimal.ZERO)!=-1){
                dataMap.put("showUpFlag",true);
                dataMap.put("showDownFlag",false);
                dataMap.put("zdf","+"+zdftmp.stripTrailingZeros().toPlainString()+"%");
            }else{
                dataMap.put("showUpFlag",false);
                dataMap.put("showDownFlag",true);
                dataMap.put("zdf",zdftmp.stripTrailingZeros().toPlainString()+"%");
            }
        }
        mmap.put("mySymbolList",mySymbolList);


        List<OkxTickerInfo> hotTickInfoList =null;
        try{
           hotTickInfoList = okxService.getHotTickInfo();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(Func.isNotEmpty(hotTickInfoList)){
            for (int i = 0; i < 6; i++) {
                OkxTickerInfo okxTickerInfo = hotTickInfoList.get(i);
                String ticker = okxTickerInfo.getTicker();
                String priceChangeRateStr = okxTickerInfo.getPriceChangeRate();
                BigDecimal priceChangeRateBig = new BigDecimal(priceChangeRateStr);
                BigDecimal zdf = priceChangeRateBig.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);

                mmap.put("image"+i,okxTickerInfo.getImage());
                mmap.put("ticker"+i,ticker);
                if(zdf.compareTo(BigDecimal.ZERO)==1){
                    mmap.put("zdf"+i,"+"+zdf.stripTrailingZeros().toPlainString()+"%");
                }else{
                    mmap.put("zdf"+i,zdf.stripTrailingZeros().toPlainString()+"%");
                }
            }
        }



    }

}
