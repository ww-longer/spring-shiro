package com.main.controller;

import com.main.model.OutsourceAllocationRepayment;
import com.main.model.TurnOverRepayment;
import com.main.service.IOutsourceAllocationAmountService;
import com.main.service.OutsourceAllocationRepaymentService;
import com.main.service.OutsourceCompanyService;
import com.sys.commons.base.BaseController;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.DateUtils;
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
import java.util.*;

/**
 * <p>
 * 外包公司 前端控制器
 * </p>
 *
 * @author jiewai
 * @since 2018-07-04
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
        Date date = new Date();
        List<String> dateList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            Date thisDate = DateUtils.getSomeMouthDay(date, -i, 0);
            dateList.add(DateUtils.dateToString(thisDate, "yyyy-MM"));
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 1; i <= 7; i++) {
            if (i==7) {
                map.put("M" + i + "+", i + "");
            } else {
                map.put("M" + i, i + "");
            }
        }
        model.addAttribute("dateList", dateList);
        model.addAttribute("agecdMap", map);
        model.addAttribute("companies", outsourceCompanyService.loadAllCompany());
        return "sc_main/statement/statisticalInfo";
    }

    /**
     * 查询统计公司下所有账龄的案件总数
     * @return
     */
    @PostMapping("/loadOutCaseCompanyAndAgecdNum")
    @ResponseBody
    public Object loadOutCaseCompanyAndAgecdNum() {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("caseNum", allocationAmountService.loadAllOutCompanyCaseNum());
        objectMap.put("agecdNum", allocationAmountService.loadAllOutCaseAgecdNum());
        objectMap.put("gszlCounts", allocationAmountService.loadAllOutAgecdCaseCount());
        objectMap.put("gszlAmounts", allocationAmountService.loadAllOutAgecdCaseAAmount());
        return renderSuccess(objectMap);
    }

    /**
     * 查询统计所有公司下案件总数据
     * @return
     */
    @PostMapping("/loadRepaymentRateCaseNum")
    @ResponseBody
    public Object loadRepaymentRateCaseNum(String company, String month, Integer handOverAgecd) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("company", company);
        objectMap.put("month", month);
        objectMap.put("agecd", handOverAgecd);
        objectMap.put("repayments", outsourceAllocationRepaymentService.loadRepaymentRateCaseNum(objectMap));
        return renderSuccess(objectMap);
    }

    /**
     *  公司移交还款金额柱状图统计
     * @param handOverAgecd
     * @return
     */
    @PostMapping("/loadCompanyDivideAmountNum")
    @ResponseBody
    public Object loadCompanyDivideAmountNum(Integer handOverAgecd) {
        Map<String, Object> objectMap = new HashMap<>(), maps;
        objectMap.put("agecd", handOverAgecd);
        maps = outsourceAllocationRepaymentService.loadCompanyDivideAmountNum(objectMap);
        objectMap.put("datas", maps.get("datas"));
        objectMap.put("dateList", maps.get("dateList"));
        return renderSuccess(objectMap);
    }

    /**
     * 获取移交还款率
     * @param handOverAgecd
     * @param month
     * @param company
     * @return
     */
    @PostMapping("/loadTurnOverRepaymentRateNum")
    @ResponseBody
    public Object loadTurnOverRepaymentRateNum(Integer handOverAgecd, String month, String company) {
        Map<String, Object> objectMap = new HashMap<>();
        Map<String, List<TurnOverRepayment>> maps;
        if (handOverAgecd == null || handOverAgecd < 7) {
            if (handOverAgecd != null && handOverAgecd != 0) objectMap.put("agecd", handOverAgecd);
        } else {
            objectMap.put("agecds", handOverAgecd);
        }
        if (StringUtils.isNotBlank(month)) {
            objectMap.put("month", month + "%");
        } else {
            Date thisDate = DateUtils.getSomeMouthDay(new Date(), -6, 0);
            objectMap.put("monthStart", DateUtils.dateToString(thisDate, "yyyy-MM"));
        }
        objectMap.put("company", company);
        maps = outsourceAllocationRepaymentService.loadTurnOverRepaymentRateNum(objectMap);
        return renderSuccess(maps);
    }

    /**
     * 下载移交还款率数据
     */
    @GetMapping("/downloadTurnOverRepaymentRate")
    public void downloadTurnOverRepaymentRate(Integer handOverAgecd, String month, String company, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> objectMap = new HashMap<>();
        if (handOverAgecd == null || handOverAgecd < 7) {
            if (handOverAgecd != null && handOverAgecd != 0) objectMap.put("agecd", handOverAgecd);
        } else {
            objectMap.put("agecds", handOverAgecd);
        }
        if (StringUtils.isNotBlank(month)) {
            objectMap.put("month", month + "%");
        } else {
            Date thisDate = DateUtils.getSomeMouthDay(new Date(), -6, 0);
            objectMap.put("monthStart", DateUtils.dateToString(thisDate, "yyyy-MM"));
        }
        objectMap.put("company", company);
        outsourceAllocationRepaymentService.downloadTurnOverRepaymentRate(objectMap, request, response);
    }

}
