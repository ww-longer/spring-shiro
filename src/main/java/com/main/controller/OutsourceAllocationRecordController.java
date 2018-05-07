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

import org.apache.commons.lang.StringUtils;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.main.model.OutsourceAllocationRecord;
import com.main.service.IOutsourceAllocationRecordService;

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
     * 添加页面
     *
     * @return
     */
    @GetMapping("/addPage")
    public String addPage() {
        return "admin/outsourceAllocationRecord/outsourceAllocationRecordAdd";
    }

    /**
     * 添加
     *
     * @param
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Object add(@Valid OutsourceAllocationRecord outsourceAllocationRecord) {

        if (true) {
            return renderSuccess("添加成功！");
        } else {
            return renderError("添加失败！");
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
     * @param id
     * @return
     */
    @GetMapping("/editPage")
    public String editPage(Model model, Long id) {
        OutsourceAllocationRecord outsourceAllocationRecord = outsourceAllocationRecordService.selectById(id);
        model.addAttribute("outsourceAllocationRecord", outsourceAllocationRecord);
        return "admin/outsourceAllocationRecord/outsourceAllocationRecordEdit";
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
