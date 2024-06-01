package com.ruoyi.web.controller.coin;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.coin.http.OkxHttpUtils;
import com.ruoyi.coin.http.model.OkxHttpMarketListResult;
import com.ruoyi.coin.service.ICoinSqlService;
import com.ruoyi.coin.service.ICoinTradeService;
import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.coin.service.IOkxService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.common.utils.blade.tool.DateUtil;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 铭文详情
 */
@Controller
@RequestMapping("/coin/symbol")
public class CoinSymbolController extends BaseController {
    private String prefix = "coin/symbol";

    @Autowired
    private ICoinSqlService coinSqlService;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;

    @Autowired
    private ICoinTradeService coinTradeService;

    @Autowired
    private IOkxService okxService;

    //列表页面
    @GetMapping("/index")
    public String index() {
        return prefix + "/index";
    }

    //列表数据
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(HttpServletRequest request) {
        Map<String, Object> parameterMap = MjkjUtils.getParameterMap(request);

        Page page = getPage();
        Long userId = getUserId();
        IPage<Map<String, Object>> pages = coinSqlService.getCoinSymbolPage(page, parameterMap, userId);
        return getDataTable(pages);
    }


    /**
     * 详情
     */
    @GetMapping("/view/{symbolId}")
    public String view(@PathVariable("symbolId") String symbolId, ModelMap mmap) {
        QueryWrapper wrapper = MjkjUtils.getQueryWrapper();
        wrapper.eq("symbol_id", symbolId);
        Map<String, Object> dataMap = baseSqlService.getDataOneByWrapper("coin_symbol", wrapper);

        String symbol_name = MjkjUtils.getMap2Str(dataMap, "symbol_name");

        Long userId = getUserId();
        //必须是vip或者加入社区成员才能查看
        boolean vipFlag = coinSqlService.isVip(userId);
        if(!vipFlag){
            boolean isSymbolMemberFlag = coinSqlService.isSymbolMember(userId, Func.toInt(symbolId));
            if(!isSymbolMemberFlag){
                return "/error/404";
            }
        }

        //处理市值
        String market_cap_btc_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_btc",4,"BTC");
        String market_cap_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "market_cap_usdt",2,"U");
        dataMap.put("market_cap_btc_str", market_cap_btc_str);
        dataMap.put("market_cap_usdt_str", market_cap_usdt_str);

        //处理成交量
        String volume_btc_str = MjkjUtils.getMap2BigDfomat(dataMap, "volume_btc",4,"BTC");
        String volume_usdt_str = MjkjUtils.getMap2BigDfomat(dataMap, "volume_usdt",2,"U");
        dataMap.put("volume_btc_str", volume_btc_str);
        dataMap.put("volume_usdt_str", volume_usdt_str);

        //24小时成交额
        String trade24HTotalPrice = MjkjUtils.getMap2BigDfomat(dataMap, "volume_24h", 4, " BTC");
        //String trade24HTotalPrice = coinTradeService.get24HTradeTotalPrice(Func.toInt(symbolId));
        dataMap.put("trade24HTotalPrice", trade24HTotalPrice);
        //String trade24HTotalCou = coinTradeService.get24HTradeTotalCou(Func.toInt(symbolId));
       // dataMap.put("trade24HTotalCou", trade24HTotalCou);

        String signTypeStr = coinTradeService.getSignStr(Func.toInt(symbolId));
        dataMap.put("signTypeStr",signTypeStr);
        //判断当天是否有 签到
        Date now = DateUtil.now();
        String nowStr = DateUtil.format(now, DateUtil.PATTERN_DATE);
        boolean memberFlag = coinSqlService.isSymbolMember(userId, Func.toInt(symbolId));
        boolean signFlag=false;
        if(memberFlag){//成员才能签到
            //判断是否有签到过
            QueryWrapper queryWrapper = MjkjUtils.getQueryWrapper();
            queryWrapper.eq("symbol_id",symbolId);
            queryWrapper.eq("user_id",userId);
            queryWrapper.eq("DATE_FORMAT(sign_time, '%Y-%m-%d')",nowStr);
            Map<String, Object> signMap = baseSqlService.getDataOneByWrapper("coin_symbol_sign_log", queryWrapper);

            if(Func.isEmpty(signMap)){
                signFlag=true;
            }
        }
        dataMap.put("signFlag",signFlag);

        //获取实时 市场详情
        String hostUrl = okxService.getHostUrl();
        OkxHttpMarketListResult.OkxDataModel marketList = OkxHttpUtils.getMarketList(hostUrl,symbol_name, 1, null, 1);

        List<OkxHttpMarketListResult.ItemModel> items = new ArrayList<>();

        String flowPriceStr = "--";
        if (Func.isNotEmpty(marketList)) {
            items = marketList.getItems();
            flowPriceStr = items.get(0).getFloorPrice();
        }
        //地板价
        dataMap.put("flowPriceStr", flowPriceStr);

        //处理部署时间
        String deployed_timeStr = MjkjUtils.getMap2DateTimeStr(dataMap, "deployed_time", DateUtil.PATTERN_DATETIME, "-");
        dataMap.put("deployed_time", deployed_timeStr);

        //处理头像时间
        String img_timeStr = MjkjUtils.getMap2DateTimeStr(dataMap, "img_time", DateUtil.PATTERN_DATETIME, "-");
        dataMap.put("img_time", img_timeStr);


        String confirmed_minted = MjkjUtils.getMap2Str(dataMap, "confirmed_minted", "-");
        dataMap.put("confirmed_minted", confirmed_minted);

        String max_cou_mint = MjkjUtils.getMap2Str(dataMap, "max_cou_mint", "-");
        dataMap.put("max_cou_mint", max_cou_mint);

        String limit_per_mint = MjkjUtils.getMap2Str(dataMap, "limit_per_mint", "-");
        dataMap.put("limit_per_mint", limit_per_mint);


        //获取发展历程
        String initEventContent=deployed_timeStr + "部署成功";
        List<Map<String, Object>> eventMapList = coinSqlService.getEventMapList(Func.toInt(symbolId), Func.toStrList(initEventContent));


        Integer score = MjkjUtils.getMap2Integer(dataMap, "score");
        if(Func.isEmpty(score)){
            score=60;
        }
        //返回结果
        mmap.put("marketItemList", items);
        mmap.put("detailModel", dataMap);
        mmap.put("score", score);
        mmap.put("eventMapList", eventMapList);
        return prefix + "/view";
    }

    //成员列表
    @PostMapping("/member/{symbolId}")
    @ResponseBody
    public TableDataInfo memberList(@PathVariable("symbolId") Integer symbolId) {
        Long userId = getUserId();
        boolean adminFlag=userId==1;

        Page page = getPage();
        IPage<Map<String, Object>> pages = coinSqlService.getCoinMemberPage(page, symbolId);
        List<Map<String, Object>> records = pages.getRecords();
        if(Func.isNotEmpty(records)){
            for(Map<String,Object> dataMap:records ){
                dataMap.put("adminFlag",adminFlag);
            }
        }


        return getDataTable(pages);
    }

    //持币列表数据
    @PostMapping("/address/{symbolId}")
    @ResponseBody
    public TableDataInfo addressList(@PathVariable("symbolId") Integer symbolId) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol_id", symbolId);
        Page page = getPage();
        IPage<Map<String, Object>> pages = coinSqlService.getCoinAddressPage(page, params);
        return getDataTable(pages);
    }

    //交易列表列表数据
    @PostMapping("/trade/{symbolId}")
    @ResponseBody
    public TableDataInfo tradeList(@PathVariable("symbolId") Integer symbolId) {
        Map<String, Object> params = new HashMap<>();
        params.put("symbol_id", symbolId);
        Page page = getPage();
        IPage<Map<String, Object>> pages = coinSqlService.getCoinTxPage(page, params);
        return getDataTable(pages);
    }

    //相同铸造者
    @PostMapping("/sameSymbolCrateUser/{symbolId}")
    @ResponseBody
    public TableDataInfo sameSymbolCrateUserList(@PathVariable("symbolId") Integer symbolId) {
        Map<String, Object> symbolMap = baseSqlService.getDataOneByField("coin_symbol", "symbol_id", symbolId);
        String symbol_create_address = MjkjUtils.getMap2Str(symbolMap, "symbol_create_address", "-1");

        Page page = getPage();
        IPage<Map<String, Object>> pages = coinSqlService.getCoiSameCreateUserPage(page,getUserId(), symbolId,symbol_create_address);
        return getDataTable(pages);
    }



    //新增
    @PostMapping("/join")
    @ResponseBody
    public AjaxResult join(HttpServletRequest request) {
        Map<String, Object> paramMap = MjkjUtils.getParameterMap(request);
        Integer symbolId = MjkjUtils.getMap2Integer(paramMap, "symbolId");
        if (Func.isEmpty(symbolId)) {
            return error("参数为空");
        }
        Long userId = getUserId();
        QueryWrapper queryWrapper = MjkjUtils.getQueryWrapper();
        queryWrapper.eq("symbol_id",symbolId);
        queryWrapper.eq("user_id",userId);
        Map<String, Object> dataMap = baseSqlService.getDataOneByWrapper("coin_symbol_member", queryWrapper);
        if(Func.isNotEmpty(dataMap)){
            return error("已加入，无需再次加入");
        }
        //判断次数
        Map<String, Object> userInfoMap = baseSqlService.getDataOneByField("coin_user_info", "user_id", userId);
        String userInfoId = MjkjUtils.getMap2Str(userInfoMap, "id");
        Integer cou = MjkjUtils.getMap2Integer(userInfoMap, "follow_symbol_cou");
        if(cou<=0){
            return error("剩余关注铭文次数不足，请联系系统管理员");
        }

        Map<String,Object> updateMap=new HashMap<>();
        updateMap.put("follow_symbol_cou",--cou);
        baseSqlService.baseUpdateData("coin_user_info",updateMap,userInfoId);

        //加入社区
        Map<String,Object> joinMap=new HashMap<>();
        joinMap.put("symbol_id",symbolId);
        joinMap.put("user_id",userId);
        joinMap.put("is_admin",0);//普通
        baseSqlService.baseInsertData("coin_symbol_member",joinMap);


        return success();
    }

    //设置为管理员
    @PostMapping("/setAdmin")
    @ResponseBody
    public AjaxResult setAdmin(HttpServletRequest request) {
        Map<String, Object> paramMap = MjkjUtils.getParameterMap(request);
        String memberId = MjkjUtils.getMap2Str(paramMap, "memberId");
        if (Func.isEmpty(memberId)) {
            return error("参数为空");
        }
        boolean sysAdmin = coinSqlService.isSysAdmin(getUserId());
        if(!sysAdmin){
            return error("只允许超级管理员操作");
        }
        //设置为管理员
        Map<String,Object> updateMap=new HashMap<>();
        updateMap.put("is_admin",1);//管理员
        baseSqlService.baseUpdateData("coin_symbol_member",updateMap,memberId);
        return success();
    }

    //签到
    @PostMapping("/sign")
    @ResponseBody
    public AjaxResult sign(HttpServletRequest request) {
        Map<String, Object> paramMap = MjkjUtils.getParameterMap(request);
        String symbolId = MjkjUtils.getMap2Str(paramMap, "symbolId");
        if (Func.isEmpty(symbolId)) {
            return success();
        }
        Long userId = getUserId();
        Date now = DateUtil.now();
        String nowStr = DateUtil.format(now, DateUtil.PATTERN_DATE);
        //判断是否有签到过
        QueryWrapper queryWrapper = MjkjUtils.getQueryWrapper();
        queryWrapper.eq("symbol_id",symbolId);
        queryWrapper.eq("user_id",userId);
        queryWrapper.eq("DATE_FORMAT(sign_time, '%Y-%m-%d')",nowStr);
        Map<String, Object> signMap = baseSqlService.getDataOneByWrapper("coin_symbol_sign_log", queryWrapper);
        if(Func.isNotEmpty(signMap)){
            return success();
        }
        boolean memberFlag = coinSqlService.isSymbolMember(userId, Func.toInt(symbolId));
        if(!memberFlag){
            return success();
        }

        int score=1;
        boolean vip = coinSqlService.isVip(userId);
        if(vip){
            score=5;
        }

        Map<String,Object> addMap=new HashMap<>();
        addMap.put("symbol_id",symbolId);
        addMap.put("user_id",userId);
        addMap.put("sign_time",now);
        addMap.put("score",score);
        baseSqlService.baseInsertData("coin_symbol_sign_log",addMap);

        return success();
    }

}