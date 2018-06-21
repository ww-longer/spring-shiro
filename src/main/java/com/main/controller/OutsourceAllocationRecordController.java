package com.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;

import java.util.Date;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

import com.sys.commons.base.BaseController;

import com.sys.commons.result.PageInfo;

import com.sys.commons.utils.DateUtils;
import com.sys.commons.utils.ExcelUtils;
import com.sys.commons.utils.FileUtils;

import com.sys.commons.utils.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
        condition.put("custId", record.getCustId());
        condition.put("ious", record.getIous());
        pageInfo.setCondition(condition);
        outsourceAllocationRecordService.selectPageInfo(pageInfo);
        return pageInfo;
    }

    @PostMapping("/matching")
    @ResponseBody
    public PageInfo matching(OutsourceAllocationRecord record) {
        PageInfo pageInfo = new PageInfo(record.getPage(), record.getRows(), record.getSort(), record.getOrder());
        Map<String, Object> condition = new HashMap<>();
        // 查询参数
        condition.put("custId", record.getCustId());
        condition.put("ious", record.getIous());
        pageInfo.setCondition(condition);
        outsourceAllocationRecordService.loadMatchingData(pageInfo);
        return pageInfo;
    }

    @PostMapping("/download")
    public void download(OutsourceAllocationRecord record, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (record == null || StringUtils.isBlank(record.getCustId())) {
                renderError("请填写要导出的客户身份证号!");
            }
            Map<String, Object> condition = new HashMap<>();
            // 查询参数
            condition.put("custId", record.getCustId());
            condition.put("ious", record.getIous());
            List<Map<String, Object>> mapList = outsourceAllocationRecordService.getMatchingData(condition);
            if (mapList.size() > 0) {
                // 写入文件数据解析
                XSSFWorkbook wb = new XSSFWorkbook();
                XSSFSheet sheet = wb.createSheet("sheet1");
                XSSFRow row = sheet.createRow(0);
                row.createCell(0).setCellValue("身份证号");
                row.createCell(1).setCellValue("已委外过");
                row.createCell(2).setCellValue("公司1");
                row.createCell(3).setCellValue("公司2");
                row.createCell(4).setCellValue("公司3");
                row.createCell(5).setCellValue("公司4");
                row.createCell(6).setCellValue("公司5");
                row.createCell(7).setCellValue("公司6");
                row.createCell(8).setCellValue("公司7");
                row.createCell(9).setCellValue("公司8");
                row.createCell(10).setCellValue("公司9");
                row.createCell(11).setCellValue("公司10");
                for (int i = 0; i < mapList.size(); i++) {
                    row = sheet.createRow(i + 1);
                    Map map = mapList.get(i);
                    row.createCell(0).setCellValue((String) map.get("custId"));
                    row.createCell(1).setCellValue((String) map.get("wg"));
                    row.createCell(2).setCellValue((String) map.get("ww1"));
                    row.createCell(3).setCellValue((String) map.get("ww2"));
                    row.createCell(4).setCellValue((String) map.get("ww3"));
                    row.createCell(5).setCellValue((String) map.get("ww4"));
                    row.createCell(6).setCellValue((String) map.get("ww5"));
                    row.createCell(7).setCellValue((String) map.get("ww6"));
                    row.createCell(8).setCellValue((String) map.get("ww7"));
                    row.createCell(9).setCellValue((String) map.get("ww8"));
                    row.createCell(10).setCellValue((String) map.get("ww9"));
                    row.createCell(11).setCellValue((String) map.get("ww10"));
                }
                String fileName = "已委外公司匹配清单_" + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".xlsx";
                // 文件下载到浏览器
                ExcelUtils.writeFileToClient(fileName, wb, request, response);
            }
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
            File tempFile = null;
            try {
                tempFile = FileUtils.multipartToFile(file);
                // 获取导入文件中的数据
                List<HashMap<String, Object>> listMap = ExcelUtils.loadAllExcelData(tempFile);
                Map<String, Object> map = outsourceAllocationRecordService.importInOutsourceExcel(listMap);
                Long endTime = System.currentTimeMillis();
                System.out.println("文件上传总耗时:" + (endTime-startTime));
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
            File tempFile = null;
            try {
                tempFile = FileUtils.multipartToFile(file);
                // 获取导入文件中的数据
                List<HashMap<String, Object>> listMap = ExcelUtils.loadAllExcelData(tempFile);
                Map<String, Object> map = outsourceAllocationRecordService.importAmountAndRecordExcel(listMap);
                Long endTime = System.currentTimeMillis();
                System.out.println("文件上传总耗时:" + (endTime-startTime));
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
