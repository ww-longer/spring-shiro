package com.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.main.model.OutsourceAllocationAmountHis;
import com.main.model.OutsourceCompany;
import com.main.service.OutsourceAllocationAmountHisService;
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

import java.util.Date;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private OutsourceCompanyService outsourceCompanyService;
    
    @GetMapping("/search")
    public String search() {
        return "sc_main/outsourceAmount/outsourceAmountList";
    }
    
    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(OutsourceAllocationAmountHis amountHis) {
        PageInfo pageInfo;
        pageInfo = new PageInfo(amountHis.getPage(), amountHis.getRows(), amountHis.getSort(), amountHis.getOrder());
        Map<String, Object> amountHisMap = new HashMap<>();
        // 查询参数
        if (StringUtils.isNotBlank(amountHis.getName())) {
            amountHisMap.put("name", "%" + amountHis.getName() + "%");
        }
        amountHisMap.put("custId", amountHis.getCustId());
        amountHisMap.put("ious", amountHis.getIous());
        amountHisMap.put("company", amountHis.getCompany());
        amountHisMap.put("startTransferTime", amountHis.getStartTransferTime());
        amountHisMap.put("endTransfertTime", amountHis.getEndTransferTime());
        amountHisMap.put("startThePushDayTime", amountHis.getStartThePushDayTime());
        amountHisMap.put("endThePushDayTime", amountHis.getEndThePushDayTime());
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
        return "sc_main/outsourceAllocationAmountHis/outsourceAllocationAmountHisAdd";
    }
    
    /**
     * 下载导出退催清单
     * @param 
     * @return
     */
    @GetMapping("/downloadThePushExp")
    @ResponseBody
    public void downloadThePushExp(OutsourceAllocationAmountHis amountHis, HttpServletRequest request, HttpServletResponse response) {
        try {
            Map<String, Object> condition = new HashMap<>();
            // 查询参数
            String startThePushDayTime = amountHis.getStartThePushDayTime();
            String endTransferTime = amountHis.getEndThePushDayTime();
            // 默认导出当前日期之前的数据
            if (StringUtils.isBlank(endTransferTime)) {
                endTransferTime = DateUtils.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss");
            }
            condition.put("startTransferTime", amountHis.getStartTransferTime());
            condition.put("endTransfertTime", amountHis.getEndTransferTime());
            condition.put("startThePushDayTime", startThePushDayTime);
            condition.put("endThePushDayTime", endTransferTime);
            condition.put("nowCollectionAmount", 0);
            outsourceAllocationAmountHisService.loadAmountHisByMaps(condition, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            renderError("导出文件异常!");
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
        return "sc_main/outsourceAllocationAmountHis/outsourceAllocationAmountHisEdit";
    }
    
    /**
     * 编辑
     * @param
     * @return
     */
    @PostMapping("/edit")
    @ResponseBody
    public Object edit(@Valid OutsourceAllocationAmountHis outsourceAllocationAmountHis) {
        boolean b = outsourceAllocationAmountHisService.updateById(outsourceAllocationAmountHis);
        if (b) {
            return renderSuccess("编辑成功！");
        } else {
            return renderError("编辑失败！");
        }
    }


    /**
     * 直接从历史案件中留案,加载历史案件,返回留案页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/updateAmountHisPage")
    public String updateAmountHisPage(int id, Model model) {
        OutsourceAllocationAmountHis amountHis = outsourceAllocationAmountHisService.loadAmountHisById(id);
        if (null != amountHis) {
            List<OutsourceCompany> companies = outsourceCompanyService.loadAllCompany();
            model.addAttribute("amountHis", amountHis);
            model.addAttribute("companies", companies);
            model.addAttribute("transfer", DateUtils.dateToString(amountHis.getTransfer(), "yyyy-MM-dd HH:mm:ss"));
            model.addAttribute("thePushDay", DateUtils.dateToString(amountHis.getThePushDay(), "yyyy-MM-dd HH:mm:ss"));
        }
        return "sc_main/outsourceAmount/updateAmountHisPage";
    }
}
