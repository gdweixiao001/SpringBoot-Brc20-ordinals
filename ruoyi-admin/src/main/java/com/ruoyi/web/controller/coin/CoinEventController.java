package com.ruoyi.web.controller.coin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 铭文发展历程
 */
@Controller
@RequestMapping("/coin/event")
public class CoinEventController extends BaseController {
    private String prefix = "coin/event";

    @Autowired
    private ICoinSqlService coinSqlService;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;


    //列表页面
    @GetMapping("/index/{symbolId}")
    public String index(@PathVariable("symbolId") Integer symbolId, ModelMap mmap) {
        mmap.put("symbolId", symbolId);
        return prefix + "/index";
    }


    //列表数据
    @PostMapping("/list/{symbolId}")
    @ResponseBody
    public TableDataInfo list(@PathVariable("symbolId") Integer symbolId) {
        Page page = getPage();
        QueryWrapper wrapper = MjkjUtils.getQueryWrapper();
        wrapper.eq("symbol_id", symbolId);
        IPage<Map<String, Object>> pages = baseSqlService.getDataIPageWrapper("coin_symbol_event_log", page, wrapper);
        return getDataTable(pages);
    }

    //新增页面
    @GetMapping("/add/{symbolId}")
    public String add(@PathVariable("symbolId") Integer symbolId, ModelMap mmap) {
        mmap.put("symbolId",symbolId);
        return prefix + "/add";
    }

    //新增
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(HttpServletRequest request) {
        Map<String, Object> paramMap = MjkjUtils.getParameterMap(request);

        String content = MjkjUtils.getMap2Str(paramMap, "content");
        String symbolId = MjkjUtils.getMap2Str(paramMap, "symbolId");
        if(Func.isEmpty(content) || Func.isEmpty(symbolId)){
            return error("参数为空");
        }
        Long userId = getUserId();
        boolean symbolAdminFlag = coinSqlService.isSymbolAdmin(userId, Func.toInt(symbolId));
        if(!symbolAdminFlag){
            return error("只允许社区管理员操作");
        }

        Map<String,Object> addMap=new HashMap<>();
        addMap.put("create_user",userId);
        addMap.put("create_time",DateUtil.now());
        addMap.put("symbol_id",symbolId);
        addMap.put("content",content);
        baseSqlService.baseInsertData("coin_symbol_event_log",addMap);
        return success();
    }

    //修改页面
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap) {
        Map<String, Object> dataMap = baseSqlService.getTableById("coin_symbol_event_log", id);
        mmap.put("dataMap", dataMap);
        return prefix + "/edit";
    }

    //修改保存
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(HttpServletRequest request) {
        Map<String, Object> parameterMap = MjkjUtils.getParameterMap(request);
        String content = MjkjUtils.getMap2Str(parameterMap, "content");
        String id = MjkjUtils.getMap2Str(parameterMap, "id");
        if(Func.isEmpty(content)){
            return error("参数不允许为空");
        }
        Map<String, Object> eventMap = baseSqlService.getTableById("coin_symbol_event_log", id);
        Integer symbolId = MjkjUtils.getMap2Integer(eventMap, "symbol_id");

        Long userId = getUserId();
        boolean symbolAdminFlag = coinSqlService.isSymbolAdmin(userId,symbolId);
        if(!symbolAdminFlag){
            return error("只允许社区管理员操作");
        }

        Map<String,Object> updateMap=new HashMap<>();
        updateMap.put("content",content);
        baseSqlService.baseUpdateData("coin_symbol_event_log",updateMap,id);
        return success();
    }

    //删除
    @PostMapping("/remove/{symbolId}")
    @ResponseBody
    public AjaxResult remove(@PathVariable("symbolId") Integer symbolId,String ids) {
        Long userId = getUserId();
        boolean symbolAdminFlag = coinSqlService.isSymbolAdmin(userId,symbolId);
        if(!symbolAdminFlag){
            return error("只允许社区管理员操作");
        }
        baseSqlService.baseDeleteSqlStr("coin_symbol_event_log",ids);
        return success();
    }

}