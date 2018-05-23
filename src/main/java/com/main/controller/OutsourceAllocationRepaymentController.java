package com.main.controller;

import javax.validation.Valid;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.main.model.OutsourceAllocationRepayment;
import com.main.service.OutsourceAllocationRepaymentService;
import com.sys.commons.base.BaseController;
import com.sys.commons.result.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jiewai
 * @since 2018-05-22
 */
@Controller
@RequestMapping("/outsourceAllocationRepayment")
public class OutsourceAllocationRepaymentController extends BaseController {

    @Autowired private OutsourceAllocationRepaymentService outsourceAllocationRepaymentService;
    
    @GetMapping("/manager")
    public String manager() {
        return "admin/outsourceAllocationRepayment/outsourceAllocationRepaymentList";
    }
    
    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(OutsourceAllocationRepayment outsourceAllocationRepayment, Integer page, Integer rows, String sort, String order) {
        PageInfo pageInfo = new PageInfo(page, rows, sort, order);
        EntityWrapper<OutsourceAllocationRepayment> ew = new EntityWrapper<OutsourceAllocationRepayment>(outsourceAllocationRepayment);
        return pageInfo;
    }
    
    /**
     * 添加页面
     * @return
     */
    @GetMapping("/addPage")
    public String addPage() {
        return "admin/outsourceAllocationRepayment/outsourceAllocationRepaymentAdd";
    }
    
    /**
     * 添加
     * @param 
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Object add(@Valid OutsourceAllocationRepayment outsourceAllocationRepayment) {
        boolean b = outsourceAllocationRepaymentService.insert(outsourceAllocationRepayment);
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
        OutsourceAllocationRepayment outsourceAllocationRepayment = new OutsourceAllocationRepayment();
        boolean b = outsourceAllocationRepaymentService.updateById(outsourceAllocationRepayment);
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
        OutsourceAllocationRepayment outsourceAllocationRepayment = outsourceAllocationRepaymentService.selectById(id);
        model.addAttribute("outsourceAllocationRepayment", outsourceAllocationRepayment);
        return "admin/outsourceAllocationRepayment/outsourceAllocationRepaymentEdit";
    }
    
    /**
     * 编辑
     * @param 
     * @return
     */
    @PostMapping("/edit")
    @ResponseBody
    public Object edit(@Valid OutsourceAllocationRepayment outsourceAllocationRepayment) {
        boolean b = outsourceAllocationRepaymentService.updateById(outsourceAllocationRepayment);
        if (b) {
            return renderSuccess("编辑成功！");
        } else {
            return renderError("编辑失败！");
        }
    }
}
