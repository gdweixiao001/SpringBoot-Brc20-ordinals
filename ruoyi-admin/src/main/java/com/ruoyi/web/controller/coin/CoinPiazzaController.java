package com.ruoyi.web.controller.coin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.coin.service.ICoinSqlService;
import com.ruoyi.coin.service.IMjkjBaseSqlService;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.MjkjUtils;
import com.ruoyi.common.utils.blade.Func;
import com.ruoyi.common.utils.blade.tool.DateUtil;
import com.ruoyi.common.utils.redis.RedisUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 铭文-社区
 */
@Controller
@RequestMapping("/coin/piazza")
public class CoinPiazzaController extends BaseController {
    private String prefix = "coin/piazza";

    @Autowired
    private ICoinSqlService coinSqlService;

    @Autowired
    private IMjkjBaseSqlService baseSqlService;


    //列表数据
    @PostMapping("/list/{symbolId}")
    @ResponseBody
    public TableDataInfo list(@PathVariable("symbolId") Integer symbolId, HttpServletRequest request) {
        Map<String, Object> parameterMap = MjkjUtils.getParameterMap(request);
        parameterMap.put("symbolId", symbolId);

        Page page = getPage();
        IPage<Map<String, Object>> pages = coinSqlService.getCoinPiazzaPage(page, parameterMap,getUserId());
        return getDataTable(pages);
    }

    //新增页面
    @GetMapping("/add/{symbolId}")
    public String add(@PathVariable("symbolId") Integer symbolId, ModelMap mmap) {
        mmap.put("symbolId", symbolId);
        return prefix + "/add";
    }

    //新增
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(HttpServletRequest request) {
        Map<String, Object> paramMap = MjkjUtils.getParameterMap(request);

        String content = MjkjUtils.getMap2Str(paramMap, "content");
        String title = MjkjUtils.getMap2Str(paramMap, "title");
        Integer symbolId = MjkjUtils.getMap2Integer(paramMap, "symbolId");
        if (Func.isEmpty(content) || Func.isEmpty(symbolId)) {
            return error("参数为空");
        }
        Long userId = getUserId();
        boolean symbolMemberFlag = coinSqlService.isSymbolMember(userId, symbolId);
        if(!symbolMemberFlag){
            return error("需要加入社区才可以操作");
        }

        Map<String, Object> addMap = new HashMap<>();
        addMap.put("user_id",userId);
        addMap.put("create_time", DateUtil.now());
        addMap.put("symbol_id", symbolId);
        addMap.put("content", content);
        addMap.put("title", title);
        addMap.put("view_cou", 0);
        baseSqlService.baseInsertData("coin_symbol_piazza", addMap);
        return success();
    }

    //修改页面
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") String id, ModelMap mmap) {
        Map<String, Object> dataMap = baseSqlService.getTableById("coin_symbol_piazza", id);
        mmap.put("dataMap", dataMap);
        return prefix + "/edit";
    }

    //修改保存
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(HttpServletRequest request) {
        Map<String, Object> parameterMap = MjkjUtils.getParameterMap(request);
        String content = MjkjUtils.getMap2Str(parameterMap, "content");
        String title = MjkjUtils.getMap2Str(parameterMap, "title");
        String id = MjkjUtils.getMap2Str(parameterMap, "id");

        if (Func.isEmpty(content) || Func.isEmpty(title)) {
            return error("参数不允许为空");
        }
        Map<String, Object> piazzaMap = baseSqlService.getTableById("coin_symbol_piazza", id);
        Integer symbolId = MjkjUtils.getMap2Integer(piazzaMap, "symbol_id");
        Long selectUserId = MjkjUtils.getMap2Long(piazzaMap, "user_id");

        Long userId = getUserId();
        boolean symbolMemberFlag = coinSqlService.isSymbolMember(userId, symbolId);
        if(!symbolMemberFlag){
            return error("需要加入社区才可以操作");
        }
        if(!Func.equals(userId,selectUserId)){//不等的话，需要是管理员才能修改
            boolean symbolAdminFlag = coinSqlService.isSymbolAdmin(userId, symbolId);
            if(!symbolAdminFlag){
                return error("当前不是社区管理员");
            }
        }

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("content", content);
        updateMap.put("title", title);
        baseSqlService.baseUpdateData("coin_symbol_piazza", updateMap, id);
        return success();
    }

    //删除
    @PostMapping("/remove/{symbolId}")
    @ResponseBody
    public AjaxResult remove(@PathVariable("symbolId") Integer symbolId, String ids) {
        Map<String, Object> piazzaMap = baseSqlService.getTableById("coin_symbol_piazza", ids);
        Long selectUserId = MjkjUtils.getMap2Long(piazzaMap, "user_id");

        Long userId = getUserId();
        boolean symbolMemberFlag = coinSqlService.isSymbolMember(userId, symbolId);
        if(!symbolMemberFlag){
            return error("需要加入社区才可以操作");
        }
        if(!Func.equals(userId,selectUserId)){//不等的话，需要是管理员才能修改
            boolean symbolAdminFlag = coinSqlService.isSymbolAdmin(userId, symbolId);
            if(!symbolAdminFlag){
                return error("当前不是社区管理员");
            }
        }
        baseSqlService.baseDeleteSqlStr("coin_symbol_piazza", ids);

        return success();
    }

    /**
     * 查询详细
     */
    @GetMapping("/view/{id}")
    public String view(@PathVariable("id") String id, ModelMap mmap) {
        Map<String, Object> dataMap = baseSqlService.getTableById("coin_symbol_piazza", id);
        Date create_time = MjkjUtils.getMap2DateTime(dataMap, "create_time");
        String create_timeStr = DateUtil.format(create_time, DateUtil.PATTERN_DATETIME);
        dataMap.put("create_timeStr",create_timeStr);

        //修改回复数量
        Integer view_cou = MjkjUtils.getMap2Integer(dataMap, "view_cou");
        if(Func.isEmpty(view_cou)){
            view_cou=0;
        }
        Map<String,Object> updateMap=new HashMap<>();
        updateMap.put("view_cou",++view_cou);
        baseSqlService.baseUpdateData("coin_symbol_piazza",updateMap,id);

        //获取的评论列表
        List<Map<String, Object>> piazzaReplyList = coinSqlService.getCoinPiazzaReplyList(id);

        mmap.put("dataMap", dataMap);
        mmap.put("piazzaReplyList",piazzaReplyList);
        return prefix + "/view";
    }

    //回复
    @PostMapping("/reply")
    @ResponseBody
    public AjaxResult reply(HttpServletRequest request) {
        Map<String, Object> paramMap = MjkjUtils.getParameterMap(request);

        String content = MjkjUtils.getMap2Str(paramMap, "content");
        String piazzaId = MjkjUtils.getMap2Str(paramMap, "piazzaId");
        if (Func.isEmpty(content) || Func.isEmpty(piazzaId)) {
            return error("参数为空");
        }
        Map<String, Object> piazzaMap = baseSqlService.getTableById("coin_symbol_piazza", piazzaId);
        Integer symbolId = MjkjUtils.getMap2Integer(piazzaMap, "symbol_id");

        Long userId = getUserId();
        boolean symbolMemberFlag = coinSqlService.isSymbolMember(userId, symbolId);
        if(!symbolMemberFlag){
            return error("需要加入社区才可以操作");
        }
        
        Map<String, Object> addMap = new HashMap<>();
        addMap.put("id", IdWorker.getIdStr());
        addMap.put("user_id", userId);
        addMap.put("symbol_piazza_id",piazzaId);
        addMap.put("reply_content",content);
        addMap.put("reply_time",DateUtil.now());
        baseSqlService.baseSimpleIntegerSql("coin_symbol_piazza_reply", addMap);
        return success();
    }


}