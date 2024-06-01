package com.ruoyi.web.controller.coin;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.coin.service.ICoinSqlService;
import com.ruoyi.coin.service.ICoinTjService;
import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.common.utils.blade.tool.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 首页、统计、排行榜
 */
@Controller
@RequestMapping("/coin/main")
public class CoinMainController extends BaseController {
    private String prefix = "coin/main";

    @Autowired
    private ICoinSqlService coinSqlService;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;

    @Autowired
    private ICoinTjService tjService;




    //排行榜-页面
    @GetMapping("/rankHtml")
    public String rankHtml(ModelMap mmap) {
        //获取地址数最多的-铭文列表
        IPage<Map<String, Object>> rankByAddressPage = tjService.getRankByAddress(new Page(1, 10));
        //获取成交额最多的-铭文列表
        IPage<Map<String, Object>> rankByVolumePage = tjService.getRankByVolume(new Page(1, 10));
        //获取市值最多的-铭文列表
        IPage<Map<String, Object>> rankByMarketPage = tjService.getRankByMarket(new Page(1, 10));
        //获取交易数最多的-铭文列表
        IPage<Map<String, Object>> rankByTransactionsPage = tjService.getRankByTransactions(new Page(1, 10));


        mmap.put("rankByAddressList",rankByAddressPage.getRecords());
        mmap.put("rankByVolumeList",rankByVolumePage.getRecords());
        mmap.put("rankByMarketList",rankByMarketPage.getRecords());
        mmap.put("rankByTransactionsList",rankByTransactionsPage.getRecords());
        return prefix + "/rank";
    }

    //排行榜-详情页面
    @GetMapping("/rankDetailHtml")
    public String rankDetailHtml(String type,ModelMap mmap) {
        String tohtml="";
        if(Func.equals(type,"dzs")){//地址数
            tohtml="/rankDzs";
        }else if(Func.equals(type,"jye")){//交易额
            tohtml="/rankJye";
        }else if(Func.equals(type,"sz")){//市值
            tohtml="/rankSz";
        }else if(Func.equals(type,"jys")){//交易数
            tohtml="/rankJys";
        }
        return prefix + tohtml;
    }
    //排行榜详情数据
    @PostMapping("/rankDetailPage")
    @ResponseBody
    public TableDataInfo rankDetailPage(String type,String isAsc) {
        String isAscStr=Func.toStr(isAsc,"asc");

        Page page = getPage();
        IPage<Map<String, Object>> pages = tjService.getRankDetailPage(type,isAscStr,page);
        List<Map<String, Object>> records = pages.getRecords();
        if(Func.isNotEmpty(records)){
            for(Map<String,Object> dataMap:records){
                //处理市值
                String market_cap_btc_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_btc",4,"BTC");
                String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt",2,"U");
                dataMap.put("market_cap_btc_str", market_cap_btc_str);
                dataMap.put("market_cap_usdt_str", market_cap_usdt_str);
            }
        }
        return getDataTable(pages);
    }

     /* **********今日数据*************  */

    //今日数据
    @GetMapping("/todayHtml")
    public String todayHtml() {
        return prefix + "/todayHtml";
    }
    //昨日数据
    @GetMapping("/yesterdayHtml")
    public String yesterdayHtml() {
        return prefix + "/yesterdayHtml";
    }

    //统计分析数据
    @GetMapping("/tjfxHtml")
    public String tjfxHtml() {
        return prefix + "/tjfxHtml";
    }


    //今日增长地址数
    @PostMapping("/today/page")
    @ResponseBody
    public TableDataInfo todayPage(String type,String timeType) {
        String timeStr ="";
        if(Func.equals(timeType,"now")){
            timeStr = DateUtil.format(DateUtil.now(), DateUtil.PATTERN_DATE);
        }else{
            Date now = DateUtil.now();
            Date date = DateUtil.plusDays(now, -1);
            timeStr = DateUtil.format(date, DateUtil.PATTERN_DATE);
        }


        Page page = getPage();
        IPage<Map<String, Object>> pages =null;
        if(Func.equals(type,"address")){//今日地址数
            pages = tjService.getCollectAddressPageByTime(page, timeStr);
        }else if(Func.equals(type,"trade")){//今日地址数
            pages = tjService.getCollectTradePageByTime(page, timeStr);
        }else if(Func.equals(type,"volume")){//今日交易额
            pages = tjService.getCollectVolumePageByTime(page, timeStr);
        }else if(Func.equals(type,"market")){//今日市值
            pages = tjService.getCollectMarketPageByTime(page, timeStr);
        }
        return getDataTable(pages);
    }

    //统计分析
    @PostMapping("/tjfx/page")
    @ResponseBody
    public TableDataInfo tjfxPage(String type) {

        if(Func.equals(type,"score")){//评分
            Page page = getPage();
            IPage<Map<String, Object>> pages = tjService.getScoreCoinPage(page);
            List<Map<String, Object>> records = pages.getRecords();
            if(Func.isNotEmpty(records)){
                for(Map<String,Object> dataMap:records){
                    //处理市值
                    String market_cap_btc_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_btc",4,"BTC");
                    String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt",2,"U");
                    dataMap.put("market_cap_btc_str", market_cap_btc_str);
                    dataMap.put("market_cap_usdt_str", market_cap_usdt_str);
                }
            }
            return getDataTable(pages);
        }else if(Func.equals(type,"zjbsmws")){//最近部署铭文数
            Page page = getPage();
            IPage<Map<String, Object>> pages = tjService.getDeployedCoinPage(page);
            List<Map<String, Object>> records = pages.getRecords();
            if(Func.isNotEmpty(records)){
                for(Map<String,Object> dataMap:records){
                    //处理市值
                    String market_cap_btc_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_btc",4,"BTC");
                    String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt",2,"U");
                    dataMap.put("market_cap_btc_str", market_cap_btc_str);
                    dataMap.put("market_cap_usdt_str", market_cap_usdt_str);
                }
            }
            return getDataTable(pages);
        }else if(Func.equals(type,"zjstxs")){//最近上头像数
            Page page = getPage();
            IPage<Map<String, Object>> pages = tjService.getUploadImgCoinPage(page);
            List<Map<String, Object>> records = pages.getRecords();
            if(Func.isNotEmpty(records)){
                for(Map<String,Object> dataMap:records){
                    //处理市值
                    String market_cap_btc_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_btc",4,"BTC");
                    String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt",2,"U");
                    dataMap.put("market_cap_btc_str", market_cap_btc_str);
                    dataMap.put("market_cap_usdt_str", market_cap_usdt_str);
                }
            }
            return getDataTable(pages);
        }else if(Func.equals(type,"zjzzwcs")){
            Page page = getPage();
            IPage<Map<String, Object>> pages = tjService.getMintFinallyCoinPage(page);
            List<Map<String, Object>> records = pages.getRecords();
            if(Func.isNotEmpty(records)){
                for(Map<String,Object> dataMap:records){
                    //处理市值
                    String market_cap_btc_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_btc",4,"BTC");
                    String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt",2,"U");
                    dataMap.put("market_cap_btc_str", market_cap_btc_str);
                    dataMap.put("market_cap_usdt_str", market_cap_usdt_str);
                }
            }
            return getDataTable(pages);
        }

        throw new ServiceException("类型有误");

    }
}