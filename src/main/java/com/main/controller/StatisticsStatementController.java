package com.main.controller;

import com.main.model.OutsourceAllocationRepayment;
import com.main.service.IOutsourceAllocationAmountService;
import com.main.service.OutsourceAllocationRepaymentService;
import com.main.service.OutsourceCompanyService;
import com.sys.commons.base.BaseController;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jiewai on 2018/7/4.
 */
@Controller
@RequestMapping("/sc/collection/statistic")
public class StatisticsStatementController  extends BaseController {

    @Autowired
    private OutsourceAllocationRepaymentService outsourceAllocationRepaymentService;
    @Autowired
    private OutsourceCompanyService outsourceCompanyService;
    @Autowired
    private IOutsourceAllocationAmountService allocationAmountService;

    /**
     * 还款率报表查询页面
     * @param model
     * @return
     */
    @GetMapping("/repaymentRatePage")
    public String repaymentRatePage(Model model) {
        model.addAttribute("companies", outsourceCompanyService.loadAllCompany());
        return "sc_main/statement/repaymentRateStatement";
    }


    /**
     *  还款率查询
     * @param repayment
     * @return
     */
    @PostMapping("/repaymentRateStatement")
    @ResponseBody
    public PageInfo repaymentRateStatement(OutsourceAllocationRepayment repayment) {
        PageInfo pageInfo;
        pageInfo = new PageInfo(repayment.getPage(), repayment.getRows(), repayment.getSort(), repayment.getOrder());
        Map<String, Object> amountMap = new HashMap<>();
        // 创建查询参数
        amountMap.put("company", repayment.getCompany());
        pageInfo.setCondition(amountMap);
        outsourceAllocationRepaymentService.selectRepaymentRatePageInfo(pageInfo);
        return pageInfo;
    }

    /**
     * 导出还款率
     * @param repayment
     * @param request
     * @param response
     */
    @GetMapping("/downLoadRepaymentReportExp")
    public void downLoadRepaymentReportExp(OutsourceAllocationRepayment repayment, HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        Map<String, Object> objectMap = new HashMap<>();
        // 创建查询参数
        if (StringUtils.isNotBlank(repayment.getCompany())) {
            objectMap.put("company", URLDecoder.decode(repayment.getCompany(),"UTF-8"));
        }
        outsourceAllocationRepaymentService.exportInRepaymentReportByMap(objectMap, request, response);
    }
    /**
     * 导出委外公司排名
     * @param request
     * @param response
     */
    @GetMapping("/companyRankingExp")
    public void companyRankingExp(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        outsourceAllocationRepaymentService.exportCompanyRankingExp(request, response);
    }

    /**
     * 委外数据统计页面
     * @param model model
     * @return
     */
    @GetMapping("/showStatisticPage")
    public String showStatisticPage(Model model) {
        model.addAttribute("companies", outsourceCompanyService.loadAllCompany());
        return "sc_main/statement/statisticalInfo";
    }

    /**
     * 查询统计数据
     * @return
     */
    @PostMapping("/loadAllOutCompanyCaseNum")
    @ResponseBody
    public Object loadAllOutCompanyCaseNum() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("caseNum", allocationAmountService.loadAllOutCompanyCaseNum());
        return renderSuccess(objectMap);
    }

}
