package com.main.controller;

import javax.validation.Valid;

import com.main.model.OutsourceAllocationAmount;
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
 *  前端控制器 委外历史余额
 * </p>
 *
 * @author jiewai
 * @since 2018-05-22
 */
@Controller
@RequestMapping("/sc/collection/outsourceBalance")
public class OutsourceAllocationAmountController extends BaseController {

    @Autowired
    private IOutsourceAllocationAmountService outsourceAllocationAmountService;

    @GetMapping("/search")
    public String search() {
        return "main/outsourceAmount/outsourceAmountList";
    }

    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(OutsourceAllocationAmount amount) {
        PageInfo pageInfo;
        pageInfo = new PageInfo(amount.getPage(), amount.getRows(), amount.getSort(), amount.getOrder());
        Map<String, Object> amountMap = new HashMap<>();
        // 创建查询参数
        amountMap.put("custId", amount.getCustId());
        amountMap.put("ious", amount.getIous());
        pageInfo.setCondition(amountMap);
        outsourceAllocationAmountService.selectPageInfo(pageInfo);
        return pageInfo;
    }
    
    /**
     * 添加页面
     * @return
     */
    @GetMapping("/addPage")
    public String addPage1() {
        return "admin/outsourceAllocationAmount/outsourceAllocationAmountAdd";
    }
    
    /**
     * 添加
     * @param 
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Object add(@Valid OutsourceAllocationAmount outsourceAllocationAmount) {

        boolean b = outsourceAllocationAmountService.insert(outsourceAllocationAmount);
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
        OutsourceAllocationAmount outsourceAllocationAmount = new OutsourceAllocationAmount();

        boolean b = outsourceAllocationAmountService.updateById(outsourceAllocationAmount);
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
        OutsourceAllocationAmount outsourceAllocationAmount = outsourceAllocationAmountService.selectById(id);
        model.addAttribute("outsourceAllocationAmount", outsourceAllocationAmount);
        return "admin/outsourceAllocationAmount/outsourceAllocationAmountEdit";
    }
    
    /**
     * 编辑
     * @param 
     * @return
     */
    @PostMapping("/edit")
    @ResponseBody
    public Object edit(@Valid OutsourceAllocationAmount outsourceAllocationAmount) {
        //outsourceAllocationAmount.setUpdateTime(new Date());
        boolean b = outsourceAllocationAmountService.updateById(outsourceAllocationAmount);
        if (b) {
            return renderSuccess("编辑成功！");
        } else {
            return renderError("编辑失败！");
        }
    }
}
