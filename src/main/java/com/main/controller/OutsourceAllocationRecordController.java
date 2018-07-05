package com.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import com.main.mapper.OutsourceCompanyMapper;
import com.main.model.OutsourceCompany;
import com.sys.commons.base.BaseController;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.ExcelUtils;
import com.sys.commons.utils.FileUtils;

import com.sys.commons.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.main.model.OutsourceAllocationRecord;
import com.main.service.IOutsourceAllocationRecordService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 委外分配日期记录表 前端控制器
 * </p>
 *
 * @author jiewai
 * @since 2018-05-03
 */
@Controller
@RequestMapping("/sc/collection")
public class OutsourceAllocationRecordController extends BaseController {

    @Autowired
    private IOutsourceAllocationRecordService outsourceAllocationRecordService;
    @Autowired
    private OutsourceCompanyMapper companyMapper;
    @GetMapping("/outsource")
    public String manager() {
        return "sc_main/outsourceAllocationRecord/outsourceAllocationRecordList";
    }

    @PostMapping("/search")
    @ResponseBody
    public PageInfo dataGrid(OutsourceAllocationRecord record) {
        PageInfo pageInfo = new PageInfo(record.getPage(), record.getRows(), record.getSort(), record.getOrder());
        Map<String, Object> condition = new HashMap<>();
        // 查询参数
        if (StringUtils.isNotBlank(record.getName())) {
            condition.put("name","%" + record.getName() + "%");
        }
        condition.put("custId", record.getCustId());
        condition.put("ious", record.getIous());
        pageInfo.setCondition(condition);
        outsourceAllocationRecordService.selectPageInfo(pageInfo);
        return pageInfo;
    }

    /**
     * 委外过的公司匹配
     * @param record
     * @param model
     * @return
     */
    @PostMapping("/matching")
    @ResponseBody
    public PageInfo matching(OutsourceAllocationRecord record, Model model) {
        PageInfo pageInfo = new PageInfo(record.getPage(), record.getRows(), record.getSort(), record.getOrder());
        List<OutsourceCompany> outList = companyMapper.selectAllList();
        Map<String, Object> condition = new HashMap<>();
        // 查询参数
        condition.put("custId", record.getCustId());
        condition.put("ious", record.getIous());
        pageInfo.setCondition(condition);
        outsourceAllocationRecordService.loadMatchingData(pageInfo, outList);
        model.addAttribute("outList", outList);
        model.addAttribute("counts", outList.size());
        return pageInfo;
    }

    /**
     * 导出所有委外总表数据
     * @param record
     * @param request
     * @param response
     */
    @PostMapping("/download")
    public void download(OutsourceAllocationRecord record, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (record == null || StringUtils.isBlank(record.getCustId())) {
                renderError("请填写要导出的客户身份证号!");
            }
            List<OutsourceCompany> outList = companyMapper.selectAllList();
            Map<String, Object> condition = new HashMap<>();
            // 查询参数
            condition.put("custId", record.getCustId());
            condition.put("ious", record.getIous());
            outsourceAllocationRecordService.getMatchingData(condition, outList, request, response);
        } catch (Exception e) {
            e.printStackTrace();
            renderError("导出文件异常!");
        }
    }


    /**
     * 下载导出所有数据
     *
     * @return
     */
    @GetMapping("/downloadAll")
    public void downloadAll(HttpServletRequest request, HttpServletResponse response) {
        outsourceAllocationRecordService.downLoadAllDate(request, response);
    }

    /**
     * 委外中文件导入
     *
     * @param
     * @return
     */
    @PostMapping("/uploadInOutsourceExcel")
    @ResponseBody
    public Object uploadInOutsourceExcel(@RequestParam("file") MultipartFile file){
        Long startTime = System.currentTimeMillis();
        if (!file.isEmpty()) {
            // 转换为 File
            File tempFile;
            try {
                tempFile = FileUtils.multipartToFile(file);
                // 获取导入文件中的数据
                List<Map<String, Object>> listMap = ExcelUtils.loadAllExcelData(tempFile);
                Map<String, Object> map = outsourceAllocationRecordService.importInOutsourceExcel(listMap);
                Long endTime = System.currentTimeMillis();
                System.out.println("委外总表文件上传总耗时:" + (endTime-startTime));
                if (map.get("code").equals("200")) {
                    return renderSuccess(map.get("msg"));
                } else {
                    return renderError(map.get("msg").toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return renderError("文件读取失败！");
            }
        }else{
            return renderError("文件不存在！");
        }
    }

    /**
     * 文件导入页面加载
     * @return
     */
    @GetMapping("/uploadPage")
    public String uploadPage() {
        return "sc_main/outsourceAllocationRecord/excelUpload";
    }


    /**
     * 移交案件添加
     * @return
     */
    @GetMapping("/uploadAmountAndRecordPage")
    public String uploadAmountAndRecordPage() {
        return "sc_main/outsourceAllocationRecord/uploadAmountAndRecord";
    }

    /**
     * 导入移交数据
     * @param file
     * @return
     */
    @PostMapping("/uploadAmountAndRecordExcel")
    @ResponseBody
    public Object uploadAmountAndRecordExcel(@RequestParam("file") MultipartFile file){
        Long startTime = System.currentTimeMillis();
        if (!file.isEmpty()) {
            // 转换为 File
            File tempFile;
            try {
                tempFile = FileUtils.multipartToFile(file);
                // 获取导入文件中的数据
                List<Map<String, Object>> listMap = ExcelUtils.loadAllExcelData(tempFile);
                Map<String, Object> map = outsourceAllocationRecordService.importAmountAndRecordExcel(listMap);
                Long endTime = System.currentTimeMillis();
                System.out.println("移交文件上传总耗时:" + (endTime-startTime));
                if (map.get("code").equals("200")) {
                    return renderSuccess(map.get("msg"));
                } else {
                    return renderError(map.get("msg").toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                return renderError("文件读取失败！");
            }
        }else{
            return renderError("文件不存在！");
        }
    }

}
