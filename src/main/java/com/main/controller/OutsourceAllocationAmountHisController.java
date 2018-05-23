package com.main.controller;

import javax.validation.Valid;

import com.main.model.OutsourceAllocationAmountHis;
import com.main.service.IOutsourceAllocationAmountService;
import com.main.service.OutsourceAllocationAmountHisService;
import com.sys.commons.base.BaseController;
import com.sys.commons.result.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *  前端控制器  委外历史余额全量备份
 * </p>
 *
 * @author jiewai
 * @since 2018-05-22
 */
@Controller
@RequestMapping("/sc/collection/outsourceBalance/his")
public class OutsourceAllocationAmountHisController extends BaseController {

    @Autowired
    private OutsourceAllocationAmountHisService outsourceAllocationAmountHisService;
    
    @GetMapping("/search")
    public String search() {
        return "main/outsourceAmount/outsourceAmountList";
    }
    
    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(OutsourceAllocationAmountHis amountHis) {
        PageInfo pageInfo;
        pageInfo = new PageInfo(amountHis.getPage(), amountHis.getRows(), amountHis.getSort(), amountHis.getOrder());
        Map<String, Object> amountHisMap = new HashMap<>();
        // 查询参数
        amountHisMap.put("custId", amountHis.getCustId());
        amountHisMap.put("ious", amountHis.getIous());
        pageInfo.setCondition(amountHisMap);
        outsourceAllocationAmountHisService.selectPageInfo(pageInfo);
        return pageInfo;
    }
    
    /**
     * 添加页面
     * @return
     */
    @GetMapping("/addPage")
    public String addPage() {
        return "admin/outsourceAllocationAmountHis/outsourceAllocationAmountHisAdd";
    }
    
    /**
     * 添加
     * @param 
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Object add(@Valid OutsourceAllocationAmountHis outsourceAllocationAmountHis) {

        boolean b = outsourceAllocationAmountHisService.insert(outsourceAllocationAmountHis);
        if (b) {
            return renderSuccess("添加成功！");
        } else {
            return renderError("添加失败！");
        }
    }
    
    /**
     * 删除
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public Object delete(Long id) {
        OutsourceAllocationAmountHis outsourceAllocationAmountHis = new OutsourceAllocationAmountHis();

        boolean b = outsourceAllocationAmountHisService.updateById(outsourceAllocationAmountHis);
        if (b) {
            return renderSuccess("删除成功！");
        } else {
            return renderError("删除失败！");
        }
    }
    
    /**
     * 编辑
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/editPage")
    public String editPage(Model model, Long id) {
        OutsourceAllocationAmountHis outsourceAllocationAmountHis = outsourceAllocationAmountHisService.selectById(id);
        model.addAttribute("outsourceAllocationAmountHis", outsourceAllocationAmountHis);
        return "admin/outsourceAllocationAmountHis/outsourceAllocationAmountHisEdit";
    }
    
    /**
     * 编辑
     * @param 
     * @return
     */
    @PostMapping("/edit")
    @ResponseBody
    public Object edit(@Valid OutsourceAllocationAmountHis outsourceAllocationAmountHis) {
        //outsourceAllocationAmountHis.setUpdateTime(new Date());
        boolean b = outsourceAllocationAmountHisService.updateById(outsourceAllocationAmountHis);
        if (b) {
            return renderSuccess("编辑成功！");
        } else {
            return renderError("编辑失败！");
        }
    }
}
