package com.ruoyi.web.controller.coin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.coin.http.OkxHttpUtils;
import com.ruoyi.coin.http.model.OkxHttpMarketListResult;
import com.ruoyi.coin.service.ICoinSqlService;
import com.ruoyi.coin.service.IMjkjBaseSqlService;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 布道资料
 */
@Controller
@RequestMapping("/coin/cx")
public class CoinCxController extends BaseController {
    private String prefix = "coin/cx";

    @Autowired
    private ICoinSqlService coinSqlService;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;

    //新增-页面
    @GetMapping("/add/{symbolId}")
    public String add(@PathVariable("symbolId") String symbolId, ModelMap mmap) {
        mmap.put("symbolId", symbolId);
        return prefix + "/add";
    }

    //新增-数据
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(HttpServletRequest request) {
        Map<String, Object> parameterMap = MjkjUtils.getParameterMap(request);
        String symbolId = MjkjUtils.getMap2Str(parameterMap, "symbolId");
        String content = MjkjUtils.getMap2Str(parameterMap, "content");

        //如果不加入某一个币种，是不能参与社区建设
        Long userId = getUserId();
        boolean joinFlag = coinSqlService.isSymbolMember(userId, Func.toInt(symbolId));
        if (!joinFlag) {//未加入
            return error("请先加入社区再建设");
        }

        //保存数据
        Map<String, Object> addMap = new HashMap<>();
        addMap.put("symbol_id", symbolId);
        addMap.put("cx_type", 1);//文本
        addMap.put("content", content);
        addMap.put("create_user", getUserId());
        baseSqlService.baseInsertData("coin_symbol_cx_data", addMap);

        return success();
    }

    //跳转编辑页面
    @GetMapping("/edit/{cxId}")
    public String edit(@PathVariable("cxId") Long cxId, ModelMap mmap) {
        Map<String, Object> cxData = baseSqlService.getTableByIdL("coin_symbol_cx_data", cxId);
        mmap.put("cxData", cxData);
        return prefix + "/edit";
    }

    /**
     * 编辑
     */
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editData(HttpServletRequest request) {
        Map<String, Object> parameterMap = MjkjUtils.getParameterMap(request);
        String cxId = MjkjUtils.getMap2Str(parameterMap, "cxId");
        String content = MjkjUtils.getMap2Str(parameterMap, "content");

        Map<String, Object> cxDataMap = baseSqlService.getTableById("coin_symbol_cx_data", cxId);
        Integer symbolId = MjkjUtils.getMap2Integer(cxDataMap, "symbol_id");

        Long userId = getUserId();

        boolean symbolAdminFlag = coinSqlService.isSymbolAdmin(userId, symbolId);

        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        wrapper.eq("id", cxId);
        wrapper.eq(!symbolAdminFlag, "create_user", userId);///不是社区管理员的话，需要根据用户来查询
        Map<String, Object> dataMap = baseSqlService.getDataOneByWrapper("coin_symbol_cx_data", wrapper);
        if (Func.isEmpty(dataMap)) {
            return toAjax(0);
        }

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("content", content);
        baseSqlService.baseUpdateData("coin_symbol_cx_data", updateMap, cxId);

        return toAjax(1);
    }

    //删除
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(Long ids) {
        Map<String, Object> cxDataMap = baseSqlService.getTableById("coin_symbol_cx_data", Func.toStr(ids));
        Integer symbolId = MjkjUtils.getMap2Integer(cxDataMap, "symbol_id");

        Long userId = getUserId();
        boolean symbolAdminFlag = coinSqlService.isSymbolAdmin(userId, symbolId);

        QueryWrapper<Object> wrapper = new QueryWrapper<>();
        wrapper.eq("id", ids);
        if (!symbolAdminFlag) {//当前不是管理员
            wrapper.eq("create_user", getUserId());
        }
        Map<String, Object> dataMap = baseSqlService.getDataOneByWrapper("coin_symbol_cx_data", wrapper);
        if (Func.isEmpty(dataMap)) {
            return error("数据不存在");
        }
        baseSqlService.baseRealDeleteSql("coin_symbol_cx_data", ids);
        return success();
    }

    //列表
    @PostMapping("/list/{symbolId}")
    @ResponseBody
    public TableDataInfo list(@PathVariable("symbolId") Integer symbolId) {
        Page page = getPage();

        QueryWrapper wrapper = MjkjUtils.getQueryWrapper();
        wrapper.eq("symbol_id", symbolId);
        wrapper.orderByDesc("create_time");
        IPage<Map<String, Object>> pages = baseSqlService.getDataIPageWrapper("coin_symbol_cx_data", page, wrapper);

        Long userId = getUserId();
        boolean sysAdmin = coinSqlService.isSysAdmin(userId);
        boolean symbolAdmin = coinSqlService.isSymbolAdmin(userId, symbolId);

        List<Map<String, Object>> dataMapList = pages.getRecords();
        for(Map<String,Object> dataMap:dataMapList){
            Long createUser = MjkjUtils.getMap2Long(dataMap, "create_user");
            if(sysAdmin || symbolAdmin || Func.equals(userId,createUser)){
                dataMap.put("opFlag",true);
            }else{
                dataMap.put("opFlag",false);
            }

        }

        return getDataTable(pages);
    }


    //同步
    @PostMapping("/template/{symbolId}")
    @ResponseBody
    public AjaxResult template(@PathVariable("symbolId") Integer symbolId) {
        //获取铭文名称
        Map<String, Object> symbolMap = baseSqlService.getDataOneByField("coin_symbol", "symbol_id", symbolId, Func.toStrList("symbol_name"));
        String symbolName = MjkjUtils.getMap2Str(symbolMap, "symbol_name");

        //获取所以模板
        List<Map<String, Object>> templateMapList = baseSqlService.getDataByTable("coin_symbol_cx_template", Func.toStrList("content"));
        for (Map<String, Object> templateMap : templateMapList) {
            String content = MjkjUtils.getMap2Str(templateMap, "content");//内容
            String newContent = content.replaceAll("\\$\\{symbolName\\}", symbolName);//替换后的内容

            //查询是否存在
            QueryWrapper cxWrapper = MjkjUtils.getQueryWrapper();
            cxWrapper.eq("content", newContent);
            cxWrapper.eq("symbol_id", symbolId);
            Map<String, Object> cxDataMap = baseSqlService.getDataOneByWrapper("coin_symbol_cx_data", cxWrapper);
            if (Func.isNotEmpty(cxDataMap)) {
                continue;
            }
            cxDataMap = new HashMap<>();
            cxDataMap.put("symbol_id", symbolId);
            cxDataMap.put("content", newContent);
            cxDataMap.put("create_user", "1");
            baseSqlService.baseInsertData("coin_symbol_cx_data", cxDataMap);
        }


        return success();
    }
}