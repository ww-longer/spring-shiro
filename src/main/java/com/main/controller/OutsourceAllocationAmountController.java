package com.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.main.model.OutsourceAllocationAmount;
import com.main.model.OutsourceAllocationAmountHis;
import com.main.model.OutsourceCompany;
import com.main.service.IOutsourceAllocationAmountService;
import com.main.service.OutsourceCompanyService;
import com.sys.commons.base.BaseController;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.DateUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 前端控制器 委外历史余额
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

    @Autowired
    private OutsourceCompanyService outsourceCompanyService;

    @GetMapping("/search")
    public String search(Model model) {
        model.addAttribute("companies", outsourceCompanyService.loadAllCompany());
        return "sc_main/outsourceAmount/outsourceAmountList";
    }

    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(OutsourceAllocationAmount amount) {
        PageInfo pageInfo;
        pageInfo = new PageInfo(amount.getPage(), amount.getRows(), amount.getSort(), amount.getOrder());
        Map<String, Object> amountMap = new HashMap<>();
        // 创建查询参数
        if (StringUtils.isNotBlank(amount.getName())) {
            amountMap.put("name", "%" + amount.getName() + "%");
        }
        amountMap.put("custId", amount.getCustId());
        amountMap.put("ious", amount.getIous());
        amountMap.put("company", amount.getCompany());
        amountMap.put("startTransferTime", amount.getStartTransferTime());
        amountMap.put("endTransfertTime", amount.getEndTransferTime());
        amountMap.put("startThePushDayTime", amount.getStartThePushDayTime());
        amountMap.put("endThePushDayTime", amount.getEndThePushDayTime());

        pageInfo.setCondition(amountMap);
        outsourceAllocationAmountService.selectPageInfo(pageInfo);
        return pageInfo;
    }

    /**
     * 文件导入页面加载
     *
     * @return
     */
    @GetMapping("/uploadPage")
    public String uploadPage() {
        return "sc_main/outsourceAmount/importAmountDateUpdate";
    }


    /**
     * 加载更新页面
     */
    @GetMapping("/updateAmountPage")
    public String updateAmountPage(@RequestParam("custId") String custId, String ious, int id, Model model) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("id", id);
        List<OutsourceAllocationAmount> amounts = outsourceAllocationAmountService.loadAmountByMaps(objectMap);
        if (null != amounts && amounts.size() > 0) {
            List<OutsourceCompany> companies = outsourceCompanyService.loadAllCompany();
            model.addAttribute("amount", amounts.get(0));
            model.addAttribute("companies", companies);
            model.addAttribute("ious", ious);
            model.addAttribute("transfer", DateUtils.dateToString(amounts.get(0).getTransfer(), "yyyy-MM-dd HH:mm:ss"));
            model.addAttribute("thePushDay", DateUtils.dateToString(amounts.get(0).getThePushDay(), "yyyy-MM-dd HH:mm:ss"));
        }
        return "sc_main/outsourceAmount/updateAmountPage";
    }



    /**
     * 添加页面加载
     *
     * @return
     */
    @GetMapping("/addAmountPage")
    public String addAmountPage(Model model) {
        model.addAttribute("companies", outsourceCompanyService.loadAllCompany());
        return "sc_main/outsourceAmount/addAmountPage";
    }

    /**
     * 余额表数据导入更新
     *
     * @param file
     * @return
     */
    @PostMapping("/outsourceAmountUpdateExcel")
    @ResponseBody
    public Object outsourceAmountUpdateExcel(@RequestParam("file") MultipartFile file) {
        Long startTime = System.currentTimeMillis();
        if (!file.isEmpty()) {
            // 转换为 File
            File tempFile;
            try {
                tempFile = FileUtils.multipartToFile(file);
                // 获取导入文件中的数据
                List<HashMap<String, Object>> listMap = ExcelUtils.loadAllExcelData(tempFile);
                outsourceAllocationAmountService.importInOutsourceAmountExcel(listMap);
                Long endTime = System.currentTimeMillis();
                System.out.println("余额文件上传更新总耗时:" + (endTime - startTime));
                return renderSuccess("数据更新完成！");
            } catch (IOException e) {
                e.printStackTrace();
                return renderError("文件读取失败！");
            }
        } else {
            return renderError("文件不存在！");
        }
    }


    /**
     * 修改退案日期
     *
     * @param amount
     * @return
     */
    @PostMapping("/updateAmountByCustId")
    @ResponseBody
    public Object updateAmountByCustId(OutsourceAllocationAmount amount, String isAllCase) {
        if (null == amount.getThePushDay() || "".equals(amount.getThePushDay())) {
            return renderError("请输入正确的留案时间！");
        }
        if (null == amount.getTransfer() || "".equals(amount.getTransfer())) {
            return renderError("请输入正确的移交时间！");
        }
        if (null == amount.getCustId() || "".equals(amount.getCustId())) {
            return renderError("数据异常,请重新登陆！");
        }
        outsourceAllocationAmountService.updateAmountByCustId(amount, isAllCase);
        return renderSuccess("数据更新完成！");
    }

    @PostMapping("/updateAmountHisByCustId")
    @ResponseBody
    public Object updateAmountHisByCustId(OutsourceAllocationAmountHis amountHis) {
        if (null == amountHis.getThePushDay() || "".equals(amountHis.getThePushDay())) {
            return renderError("请输入正确的留案时间！");
        }
        if (null == amountHis.getTransfer() || "".equals(amountHis.getTransfer())) {
            return renderError("请输入正确的移交时间！");
        }
        if (null == amountHis.getId() || "".equals(amountHis.getId())) {
            return renderError("数据异常,请重新登陆！");
        }
        outsourceAllocationAmountService.updateAmountHisByCustId(amountHis);
        return renderSuccess("数据更新完成！");
    }

    /**
     * 导出全量余额数据
     *
     * @param amount
     * @param request
     * @param response
     */
    @GetMapping("/downloadAllAmountExp")
    @ResponseBody
    public void downloadAllAmountExp(OutsourceAllocationAmount amount, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> amountMap = new HashMap<>();
        // 创建查询参数
        amountMap.put("company", amount.getCompany());
        amountMap.put("startTransferTime", amount.getStartTransferTime());
        amountMap.put("endTransfertTime", amount.getEndTransferTime());
        amountMap.put("startThePushDayTime", amount.getStartThePushDayTime());
        amountMap.put("endThePushDayTime", amount.getEndThePushDayTime());

        outsourceAllocationAmountService.exportInAllAmountByMap(amountMap, request, response);
    }

    /**
     * 导出留案清单
     * @param amount
     * @param request
     * @param response
     */
    @GetMapping("/downloadAllLeaveExp")
    @ResponseBody
    public void downloadAllLeaveExp(OutsourceAllocationAmount amount, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> amountMap = new HashMap<>();
        // 创建查询参数
        amountMap.put("company", amount.getCompany());
        amountMap.put("startTransferTime", amount.getStartTransferTime());
        amountMap.put("endTransfertTime", amount.getEndTransferTime());
        amountMap.put("startThePushDayTime", amount.getStartThePushDayTime());
        amountMap.put("endThePushDayTime", amount.getEndThePushDayTime());
        amountMap.put("isLeaveCase", "Y");
        outsourceAllocationAmountService.exportInAllLeaveByMap(amountMap, request, response);
    }

    @PostMapping("/addAmount")
    @ResponseBody
    public Object addAmount(OutsourceAllocationAmount amount) {
        // 查询大总表数据
        if (StringUtils.isBlank(amount.getCustId())) {
            return renderError("身份证号不可以为空！");
        }
        if (StringUtils.isBlank(amount.getIous())) {
            return renderError("借据号不可以为空！");
        }
        if (null == amount.getTransfer()) {
            return renderError("移交日期不可以为空！");
        }
        if (null == amount.getThePushDay()) {
            return renderError("退催日期不可以为空！");
        }
        Map map = outsourceAllocationAmountService.addAmountData(amount);
        if (map.get("code") == "200") {
            return renderSuccess("数据添加完成！");
        } else {
            return renderError(map.get("msg").toString());
        }
    }
}
