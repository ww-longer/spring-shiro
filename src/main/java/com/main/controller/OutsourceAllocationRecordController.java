package com.main.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import java.io.*;
import java.net.URLEncoder;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

import com.sys.commons.base.BaseController;

import com.sys.commons.result.PageInfo;

import com.sys.commons.utils.ExcelUtils;
import com.sys.commons.utils.FileUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    @GetMapping("/outsource")
    public String manager() {
        return "main/outsourceAllocationRecord/outsourceAllocationRecordList";
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
    public void downloadExcel(OutsourceAllocationRecord record, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (record == null || record.getCustId() == null || "".equals(record.getCustId())) {
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
                for (int i = 0; i < mapList.size(); i++) {
                    row = sheet.createRow(i + 1);
                    Map map = mapList.get(i);
                    row.createCell(0).setCellValue((String) map.get("custId"));
                    row.createCell(1).setCellValue((String) map.get("wg"));
                    row.createCell(2).setCellValue((String) map.get("ww1"));
                    row.createCell(3).setCellValue((String) map.get("ww2"));
                    row.createCell(4).setCellValue((String) map.get("ww3"));
                    row.createCell(5).setCellValue((String) map.get("ww4"));
                }
                OutputStream fos = null;
                fos = response.getOutputStream();
                String userAgent = request.getHeader("USER-AGENT");
                String fileName = "已委外公司匹配清单";
                if (StringUtils.contains(userAgent, "Mozilla")) {
                    fileName = new String(fileName.getBytes(), "ISO8859-1");
                } else {
                    fileName = URLEncoder.encode(fileName, "utf8");
                }
                response.setCharacterEncoding("UTF-8");
                response.setContentType("multipart/form-data");
                response.setHeader("Content-Disposition", "Attachment;Filename=" + fileName + ".xlsx");
                wb.write(fos);
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        if (!file.isEmpty()) {
            // 转换为 File
            File tempFile = null;
            try {
                tempFile = FileUtils.multipartToFile(file);
                // 获取导入文件中的数据
                List<HashMap<String, Object>> listMap = ExcelUtils.loadAllExcelData(tempFile);
                outsourceAllocationRecordService.importInOutsourceExcel(listMap);
                return renderSuccess("文件导入成功！");
            } catch (IOException e) {
                e.printStackTrace();
                return renderError("文件读取失败！");
            }
        }else{
            return renderError("文件不存在！");
        }
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public Object delete(Long id) {

        if (true) {
            return renderSuccess("删除成功！");
        } else {
            return renderError("删除失败！");
        }
    }

    /**
     * 编辑
     *
     * @param model
     * @return
     */
    @GetMapping("/uploadPage")
    public String uploadPage(Model model) {
        return "main/outsourceAllocationRecord/excelUpload";
    }

    /**
     * 编辑
     *
     * @param
     * @return
     */
    @PostMapping("/edit")
    @ResponseBody
    public Object edit(@Valid OutsourceAllocationRecord outsourceAllocationRecord) {
        boolean b = outsourceAllocationRecordService.updateById(outsourceAllocationRecord);
        if (b) {
            return renderSuccess("编辑成功！");
        } else {
            return renderError("编辑失败！");
        }
    }


}
