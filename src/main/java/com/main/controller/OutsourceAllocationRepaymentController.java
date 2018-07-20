package com.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.main.model.OutsourceAllocationRepayment;
import com.main.service.OutsourceAllocationRepaymentService;
import com.main.service.OutsourceCompanyService;
import com.sys.commons.base.BaseController;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.ExcelUtils;
import com.sys.commons.utils.FileUtils;
import com.sys.commons.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author jiewai
 * @since 2018-05-22
 */
@Controller
@RequestMapping("/sc/collection/refund")
public class OutsourceAllocationRepaymentController extends BaseController {

    @Autowired
    private OutsourceAllocationRepaymentService outsourceAllocationRepaymentService;
    @Autowired
    private OutsourceCompanyService outsourceCompanyService;

    @GetMapping("/search")
    public String search(Model model) {
        model.addAttribute("companies", outsourceCompanyService.loadAllCompany());
        return "sc_main/refund/outsourceRefundList";
    }

    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(OutsourceAllocationRepayment repayment) {
        PageInfo pageInfo;
        pageInfo = new PageInfo(repayment.getPage(), repayment.getRows(), repayment.getSort(), repayment.getOrder());
        Map<String, Object> amountMap = new HashMap<>();
        // 创建查询参数
        amountMap.put("custId", repayment.getCustId());
        amountMap.put("ious", repayment.getIous());
        amountMap.put("company", repayment.getCompany());
        amountMap.put("startRepaymentDateTime", repayment.getStartRepaymentDateTime());
        amountMap.put("endRepaymentDateTime", repayment.getEndRepaymentDateTime());
        pageInfo.setCondition(amountMap);
        outsourceAllocationRepaymentService.selectPageInfo(pageInfo);
        return pageInfo;
    }

    /**
     * 添加页面
     *
     * @return
     */
    @GetMapping("/uploadRepaymentHisPage")
    public String uploadRepaymentHisPage() {
        return "sc_main/refund/addRepaymentPage";
    }

    /**
     * 导入历史还款记录
     * @param file
     * @return
     */
    @PostMapping("/uploadRepaymentExcel")
    @ResponseBody
    public Object uploadRepaymentExcel(@RequestParam("file") MultipartFile file) {
        if (!file.isEmpty()) {
            // 转换为 File
            try {
                File tempFile = FileUtils.multipartToFile(file);
                // 获取导入文件中的数据
                List<Map<String, Object>> listMap = ExcelUtils.loadAllExcelData(tempFile);
                if (listMap != null && listMap.size() > 0) {
                    outsourceAllocationRepaymentService.importRepaymentHisExcel(listMap);
                    return renderSuccess("文件导入成功！");
                } else {
                    return renderError("表文件为空！");
                }
            } catch (IOException e) {
                e.printStackTrace();
                return renderError("文件读取失败！");
            }
        } else {
            return renderError("文件不存在！");
        }
    }

    /**
     * 导出还款记录
     *
     * @param
     * @return
     */
    @GetMapping("/downLoadRepaymentExp")
    public void downLoadRepaymentExp(OutsourceAllocationRepayment repayment, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> objectMap = new HashMap<>();
        // 创建查询参数
        objectMap.put("company", repayment.getCompany());
        objectMap.put("startRepaymentDateTime", repayment.getStartRepaymentDateTime());
        objectMap.put("endRepaymentDateTime", repayment.getEndRepaymentDateTime());
        outsourceAllocationRepaymentService.exportInAllRepaymentByMap(objectMap, request, response);
    }

    /**
     * 导出所有更新余额和还款
     *
     * @return
     */
    @GetMapping("/downLoadAmountAndRepaymentExp")
    public void downLoadAmountAndRepaymentExp(OutsourceAllocationRepayment repayment, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> objectMap = new HashMap<>();
        // 创建查询参数
        objectMap.put("company", repayment.getCompany());
        objectMap.put("startRepaymentDateTime", repayment.getStartRepaymentDateTime());
        objectMap.put("endRepaymentDateTime", repayment.getEndRepaymentDateTime());
        outsourceAllocationRepaymentService.exportInAmountAndRepaymentByMap(objectMap, request, response);
    }

    /**
     * 编辑页面加载
     *
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/updateRepaymentPage")
    public String updateRepaymentPage(Model model, Integer id) {
        model.addAttribute("repayment", outsourceAllocationRepaymentService.loadRepaymentById(id));
        model.addAttribute("companies", outsourceCompanyService.loadAllCompany());
        return "sc_main/refund/updateRepaymentPage";
    }

    /**
     * 编辑更新
     *
     * @param
     * @return
     */
    @PostMapping("/updateRepaymentById")
    @ResponseBody
    public Object updateRepaymentById(OutsourceAllocationRepayment allocationRepayment) {
        OutsourceAllocationRepayment repayment = outsourceAllocationRepaymentService.loadRepaymentById(allocationRepayment.getId());
        repayment.setRemarks("SYS_金额调整,原本:" + repayment.getCurAmount() + ",现在:" + allocationRepayment.getCurAmount());
        repayment.setIsSumRefund(allocationRepayment.getIsSumRefund());
        repayment.setCompany(allocationRepayment.getCompany());
        repayment.setRepaymentDate(allocationRepayment.getRepaymentDate());
        repayment.setCurAmount(allocationRepayment.getCurAmount());
        boolean b = outsourceAllocationRepaymentService.updateById(repayment);
        if (b) {
            return renderSuccess("编辑成功！");
        } else {
            return renderError("编辑失败！");
        }
    }
}
