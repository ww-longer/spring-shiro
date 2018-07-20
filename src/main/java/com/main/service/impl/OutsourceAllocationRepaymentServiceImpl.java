package com.main.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.main.mapper.OutsourceAllocationAmountMapper;
import com.main.mapper.OutsourceAllocationRepaymentMapper;
import com.main.mapper.OutsourceCompanyMapper;
import com.main.model.*;
import com.main.service.OutsourceAllocationRepaymentService;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
<<<<<<< HEAD
=======
import java.lang.reflect.InvocationTargetException;
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
import java.text.DecimalFormat;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jiewai
 * @since 2018-05-22
 */
@Service
public class OutsourceAllocationRepaymentServiceImpl extends ServiceImpl<OutsourceAllocationRepaymentMapper, OutsourceAllocationRepayment> implements OutsourceAllocationRepaymentService {

    @Autowired
    private OutsourceAllocationRepaymentMapper outsourceAllocationRepaymentMapper;
    @Autowired
    private OutsourceAllocationAmountMapper outsourceAllocationAmountMapper;
    @Autowired
    private OutsourceCompanyMapper outsourceCompanyMapper;

    @Value("${filePath}")
    public String filePath;

    @Override
    public void selectPageInfo(PageInfo pageInfo) {
        Page<Map<String, Object>> page = new Page<>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<Map<String, Object>> list = outsourceAllocationRepaymentMapper.selectPageInfo(page, pageInfo.getCondition());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    @Override
    public void exportInAllRepaymentByMap(Map<String, Object> objectMap, HttpServletRequest request, HttpServletResponse response) {
        List<OutsourceAllocationRepayment> lists = outsourceAllocationRepaymentMapper.loadAllRepaymentByMaps(objectMap);
        // 创建用来存储各个公司的所有数据
        Map<String, List<OutsourceAllocationRepayment>> stringListMap = new HashMap<>();
        Workbook wb = new XSSFWorkbook();
        String fileName = "还款记录_" + DateUtils.dateToString(new Date(), "yyyyMMdd_HHmmss") + ".xlsx";
        printRepaymentExp("全部还款", lists, wb);
        for (int i = 0; i < lists.size(); i++) {
            OutsourceAllocationRepayment repayment = lists.get(i);
            // 获取到当前公司下的所有数据集合,如果为空,
            List<OutsourceAllocationRepayment> repayments = stringListMap.get(repayment.getCompany());
            if (repayments == null) repayments = new ArrayList<>();
            repayments.add(repayment);
            stringListMap.put(repayment.getCompany(), repayments);
        }
        for (String key : stringListMap.keySet()) {
            List<OutsourceAllocationRepayment> repayments = stringListMap.get(key);
            printRepaymentExp(key, repayments, wb);
        }
        ExcelUtils.writeFileToClient(fileName, wb, request, response);
    }

    private void printRepaymentExp(String sheetName, List<OutsourceAllocationRepayment> repayments, Workbook wb) {
        Sheet sheet = wb.createSheet(sheetName);
        setRepaymentExcel(sheet, wb, repayments);
    }

    private void printRepaymentAndAmountExp(String fileName, List<OutsourceAllocationAmount> amounts, List<OutsourceAllocationRepayment> repayments) {
        Workbook wb = new XSSFWorkbook();
        // 写入余额
        Sheet sheet1 = wb.createSheet("余额");
        setAmountExcel(sheet1, wb, amounts);
        // 写入还款
        Sheet sheet2 = wb.createSheet("还款");
        setRepaymentExcel(sheet2, wb, repayments);
        String path = filePath + File.separator + "RepaymentAndAmount" + File.separator + DateUtils.dateToString(new Date(), "yyyyMMdd");
        String name = fileName + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".xlsx";
        // 输出 Excel 文件到本地磁盘
        ExcelUtils.printExcelFileToLocal(path, name, wb);
    }

    /**
     * 余额写入
     *
     * @param sheet
     * @param amountList
     */
    private void setAmountExcel(Sheet sheet, Workbook wb, List<OutsourceAllocationAmount> amountList) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("姓名");
        row.createCell(2).setCellValue("证件号码");
        row.createCell(3).setCellValue("手机号");
        row.createCell(4).setCellValue("借据号码");
        row.createCell(5).setCellValue("最新逾期催收金额");
        row.createCell(6).setCellValue("最新账龄");
        row.createCell(7).setCellValue("移交日期");
        row.createCell(8).setCellValue("退案日期");
        row.createCell(9).setCellValue("创建日期");
        row.createCell(10).setCellValue("公司");
        row.createCell(11).setCellValue("备注");
        OutsourceAllocationAmount amount;
        if (null != amountList) {
            for (int i = 0; i < amountList.size(); i++) {
                amount = amountList.get(i);
                row = sheet.createRow(i + 1);
                if (amount != null) {
                    if (DateUtils.dateToString(new Date(), "yyyy-MM-dd").equals(DateUtils.dateToString(amount.getCreatDate(), "yyyy-MM-dd"))) {
                        CellStyle cellStyle = wb.createCellStyle();
                        if (amount.getRemarks() != null && amount.getRemarks().indexOf("新增") >= 0) {
                            cellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
                            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        } else {
                            cellStyle.setFillForegroundColor(IndexedColors.YELLOW1.getIndex());
                            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        }
                        Cell cell = row.createCell(0);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(amount.getIous() + DateUtils.dateToString(amount.getTransfer(), "yyyyMMdd"));
                        cell = row.createCell(1);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(amount.getName());
                        cell = row.createCell(2);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(amount.getCustId());
                        cell = row.createCell(3);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(amount.getTelNumber());
                        cell = row.createCell(4);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(amount.getIous());
                        cell = row.createCell(5);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(amount.getNowCollectionAmount());
                        cell = row.createCell(6);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(amount.getNowAgecd());
                        cell = row.createCell(7);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(DateUtils.dateToString(amount.getTransfer(), "yyyy-MM-dd"));
                        cell = row.createCell(8);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(DateUtils.dateToString(amount.getThePushDay(), "yyyy-MM-dd"));
                        cell = row.createCell(9);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(DateUtils.dateToString(amount.getCreatDate(), "yyyy-MM-dd"));
                        cell = row.createCell(10);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(amount.getCompany());
                        cell = row.createCell(11);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(amount.getRemarks());
                    } else {
                        row.createCell(0).setCellValue(amount.getIous() + DateUtils.dateToString(amount.getTransfer(), "yyyyMMdd"));
                        row.createCell(1).setCellValue(amount.getName());
                        row.createCell(2).setCellValue(amount.getCustId());
                        row.createCell(3).setCellValue(amount.getTelNumber());
                        row.createCell(4).setCellValue(amount.getIous());
                        row.createCell(5).setCellValue(amount.getNowCollectionAmount());
                        row.createCell(6).setCellValue(amount.getNowAgecd());
                        row.createCell(7).setCellValue(DateUtils.dateToString(amount.getTransfer(), "yyyy-MM-dd"));
                        row.createCell(8).setCellValue(DateUtils.dateToString(amount.getThePushDay(), "yyyy-MM-dd"));
                        row.createCell(9).setCellValue(DateUtils.dateToString(amount.getCreatDate(), "yyyy-MM-dd"));
                        row.createCell(10).setCellValue(amount.getCompany());
                        row.createCell(11).setCellValue(amount.getRemarks());
                    }
                }
            }
        }
    }

    /**
     * 导出还款写入EXCEL
     *
     * @param sheet
     * @param repayments
     */
    private void setRepaymentExcel(Sheet sheet, Workbook wb, List<OutsourceAllocationRepayment> repayments) {
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("姓名");
        row.createCell(2).setCellValue("证件号码");
        row.createCell(3).setCellValue("手机号");
        row.createCell(4).setCellValue("借据号码");
        row.createCell(5).setCellValue("还款金额");
        row.createCell(6).setCellValue("还款日期");
        row.createCell(7).setCellValue("移交金额");
        row.createCell(8).setCellValue("移交日期");
        row.createCell(9).setCellValue("移交账龄");
        row.createCell(10).setCellValue("是否部分还款");
        row.createCell(11).setCellValue("产品名称");
        row.createCell(12).setCellValue("公司");
        row.createCell(13).setCellValue("备注");
        OutsourceAllocationRepayment repayment;
        Date date = new Date();
        if (null != repayments) {
            for (int i = 0; i < repayments.size(); i++) {
                repayment = repayments.get(i);
                row = sheet.createRow(i + 1);
                if (repayment != null) {
                    String hkStr;
<<<<<<< HEAD
                    Boolean isColor = false;
                    if (repayment.getIsSumRefund() == 1) {
=======
                    if (repayment.getIsSumRefund() == 1 &&
                            DateUtils.dateToString(date, "yyyy-MM-dd").equals(DateUtils.dateToString(repayment.getCreatDate(), "yyyy-MM-dd"))) {
                        CellStyle cellStyle = wb.createCellStyle();
                        cellStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN1.getIndex());
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                        hkStr = "部分";
                        if(DateUtils.dateToString(date, "yyyy-MM-dd").equals(DateUtils.dateToString(repayment.getCreatDate(), "yyyy-MM-dd"))) {
                            isColor = true;
                        }
                    } else {
                        hkStr = "全部";
                    }
                    if (isColor) {
                        CellStyle cellStyle = wb.createCellStyle();
                        cellStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN1.getIndex());
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        Cell cell = row.createCell(0);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getIous() + DateUtils.dateToString(repayment.getTransfer(), "yyyyMMdd"));
                        cell = row.createCell(1);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getName());
                        cell = row.createCell(2);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getCustId());
                        cell = row.createCell(3);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getTelNumber());
                        cell = row.createCell(4);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getIous());
                        cell = row.createCell(5);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getCurAmount());
                        cell = row.createCell(6);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(DateUtils.dateToString(repayment.getRepaymentDate(), "yyyy-MM-dd"));
                        cell = row.createCell(7);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getHandOverAmount());
                        cell = row.createCell(8);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(DateUtils.dateToString(repayment.getTransfer(), "yyyy-MM-dd"));
                        cell = row.createCell(9);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getHandOverAgecd());
                        cell = row.createCell(10);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(hkStr);
                        cell = row.createCell(11);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getProductName());
                        cell = row.createCell(12);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getCompany());
                        cell = row.createCell(13);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getRemarks());
                    }else{
                        row.createCell(0).setCellValue(repayment.getIous() + DateUtils.dateToString(repayment.getTransfer(), "yyyyMMdd"));
                        row.createCell(1).setCellValue(repayment.getName());
                        row.createCell(2).setCellValue(repayment.getCustId());
                        row.createCell(3).setCellValue(repayment.getTelNumber());
                        row.createCell(4).setCellValue(repayment.getIous());
                        row.createCell(5).setCellValue(repayment.getCurAmount());
                        row.createCell(6).setCellValue(DateUtils.dateToString(repayment.getRepaymentDate(), "yyyy-MM-dd"));
                        row.createCell(7).setCellValue(repayment.getHandOverAmount());
                        row.createCell(8).setCellValue(DateUtils.dateToString(repayment.getTransfer(), "yyyy-MM-dd"));
                        row.createCell(9).setCellValue(repayment.getHandOverAgecd());
                        row.createCell(10).setCellValue(hkStr);
                        row.createCell(11).setCellValue(repayment.getProductName());
                        row.createCell(12).setCellValue(repayment.getCompany());
                        row.createCell(13).setCellValue(repayment.getRemarks());
                    }
                }
            }
        }
    }

    @Override
    public void exportInAmountAndRepaymentByMap(Map<String, Object> objectMap, HttpServletRequest request, HttpServletResponse response) {
        // 查询所有的余额记录
        List<OutsourceAllocationAmount> amountLists = outsourceAllocationAmountMapper.loadCommonAmountByMaps(new HashMap<String, Object>());
        // 查询还款记录
        List<OutsourceAllocationRepayment> repaymentLists = outsourceAllocationRepaymentMapper.loadAllRepaymentByMaps(objectMap);
        String fileName = "余额+还款记录_";
        // 总数据表
        printRepaymentAndAmountExp("全部_" + fileName, amountLists, repaymentLists);
        // 创建用来存储各个公司的所有数据
        Map<String, List<OutsourceAllocationAmount>> amountListMap = new HashMap<>();
        Map<String, List<OutsourceAllocationRepayment>> repaymentListMap = new HashMap<>();
        // 获取每个公司下的余额
        for (int i = 0; i < amountLists.size(); i++) {
            OutsourceAllocationAmount amount = amountLists.get(i);
            List<OutsourceAllocationAmount> amounts = amountListMap.get(amount.getCompany());
            if (amounts == null) amounts = new ArrayList<>();
            amounts.add(amount);
            amountListMap.put(amount.getCompany(), amounts);
        }
        // 获取每个公司下的还款
        for (int i = 0; i < repaymentLists.size(); i++) {
            OutsourceAllocationRepayment repayment = repaymentLists.get(i);
            // 获取到当前公司下的所有数据集合,如果为空,
            List<OutsourceAllocationRepayment> repayments = repaymentListMap.get(repayment.getCompany());
            if (repayments == null) repayments = new ArrayList<>();
            repayments.add(repayment);
            repaymentListMap.put(repayment.getCompany(), repayments);
        }
        // 遍历所有公司
        List<OutsourceCompany> companyList = outsourceCompanyMapper.selectAllList();
        for (int i = 0; i < companyList.size(); i++) {
            OutsourceCompany company = companyList.get(i);
            List<OutsourceAllocationAmount> amounts = amountListMap.get(company.getCompany());
            List<OutsourceAllocationRepayment> repayments = repaymentListMap.get(company.getCompany());
            printRepaymentAndAmountExp(company.getCompany() + "_" + fileName, amounts, repayments);
        }

        // 压缩文件
        String path = filePath + File.separator + "RepaymentAndAmount";
        FileOutputStream fos;
        String zipPath = new File(path + File.separator + DateUtils.dateToString(new Date(), "yyyyMMdd")).toString();
        String zipName = fileName + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".zip";
        try {
            fos = new FileOutputStream(path + File.separator + zipName);
            ZipUtils.toZip(zipPath, fos, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 下载文件到浏览器
        FileUtils.printFileToLocal(path, zipName, request, response);
        // 删除目录下文件
        FileUtils.delAllFile(path);
    }

    @Override
    public OutsourceAllocationRepayment loadRepaymentById(Integer id) {
        return outsourceAllocationRepaymentMapper.selectById(id);
    }

    @Override
    public void importRepaymentHisExcel(List<Map<String, Object>> listMap) {
        Date date = new Date();
        for (int i = 0; i < listMap.size(); i++) {
            Map<String, Object> map = listMap.get(i);
            if (map != null) {
                String name = map.get("姓名") == null ? "" : map.get("姓名").toString();
                String custId = map.get("证件号码") == null ? "" : map.get("证件号码").toString();
                String telNumber = map.get("手机号") == null ? "" : map.get("手机号").toString();
                String ious = map.get("借据号码") == null ? "" : map.get("借据号码").toString();
                String transferStr = map.get("移交日期") == null ? "" : map.get("移交日期").toString();
                String curAmountStr = map.get("金额") == null ? "" : map.get("金额").toString();
                String repaymentDateStr = map.get("还款日期") == null ? "" : map.get("还款日期").toString();
                String company = map.get("公司") == null ? "" : map.get("公司").toString();
                String handOverAgecdStr = map.get("移交账龄") == null ? "" : map.get("移交账龄").toString();
                String handOverAmountStr = map.get("移交金额") == null ? "" : map.get("移交金额").toString();

                Date transfer = date, repaymentDate = date;
                double curAmount = 0, handOverAmount = 0;
                int handOverAgecd = 0;
                if (StringUtils.isNotBlank(transferStr)) transfer = DateUtils.strToDate(transferStr, "yyyyMMdd");
<<<<<<< HEAD
                if (StringUtils.isNotBlank(repaymentDateStr))repaymentDate = DateUtils.strToDate(repaymentDateStr, "yyyyMMdd");
=======
                if (StringUtils.isNotBlank(repaymentDateStr))
                    repaymentDate = DateUtils.strToDate(repaymentDateStr, "yyyyMMdd");
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                if (StringUtils.isNumber(curAmountStr)) curAmount = Double.parseDouble(curAmountStr);
                if (StringUtils.isNumber(handOverAmountStr)) handOverAmount = Double.parseDouble(handOverAmountStr);
                if (StringUtils.isNumber(handOverAgecdStr)) handOverAgecd = Integer.parseInt(handOverAgecdStr);

                OutsourceAllocationRepayment repayment = new OutsourceAllocationRepayment();
                repayment.setName(name);
                repayment.setCustId(custId);
                repayment.setTelNumber(telNumber);
                repayment.setIous(ious);
                repayment.setTransfer(transfer);
                repayment.setCurAmount(curAmount);
                repayment.setRepaymentDate(repaymentDate);
                repayment.setCompany(company);
                repayment.setHandOverAgecd(handOverAgecd);
                repayment.setHandOverAmount(handOverAmount);
                repayment.setCreatDate(date);
                repayment.setIsSumRefund(0); //默认全额
                repayment.setRemarks("历史导入数据");
                outsourceAllocationRepaymentMapper.insert(repayment);
            }
        }
    }

    /**
     * 还款率报表分页查询
     *
     * @param pageInfo
     */
    @Override
    public void selectRepaymentRatePageInfo(PageInfo pageInfo) {
        Page<Map<String, Object>> page = new Page<>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<RepaymentReport> mapLists = selectRepaymentRateInfo(pageInfo.getCondition()), mapList = new ArrayList<>();
        // 逻辑分页
        int start = (page.getCurrent() - 1) * page.getSize(), end = page.getCurrent() * page.getSize();
        if (start >= mapLists.size()) {
            start = (mapLists.size() / page.getSize()) * page.getSize();
            end = mapLists.size();
        }
        if (end > mapLists.size()) end = mapLists.size();
        for (int i = start; i < end; i++) {
            RepaymentReport repaymentReport = mapLists.get(i);
            mapList.add(repaymentReport);
        }
        pageInfo.setRows(mapList);
        pageInfo.setTotal(mapLists.size());
    }

    /**
     * 还款率表表下载
     *
     * @param objectMap 查询参数
     * @param request
     * @param response
     */
    @Override
    public void exportInRepaymentReportByMap(Map<String, Object> objectMap, HttpServletRequest request, HttpServletResponse response) {
        if (null == objectMap.get("company") || StringUtils.isBlank(objectMap.get("company").toString())) {
            List<RepaymentReport> mapLists = selectRepaymentRateInfo(objectMap);
            List<String> transferStrLists = outsourceAllocationAmountMapper.selectAllTransfer(objectMap);
            // 写出总表
            printRepaymentReportExp("全部_", mapLists, transferStrLists);
            // 遍历所有公司
            List<OutsourceCompany> companyList = outsourceCompanyMapper.selectAllList();
            for (int i = 0; i < companyList.size(); i++) {
                OutsourceCompany company = companyList.get(i);
                objectMap = new HashMap<>();
                objectMap.put("company", company.getCompany());
                List<RepaymentReport> mapList = selectRepaymentRateInfo(objectMap);
                List<String> transferStrList = outsourceAllocationAmountMapper.selectAllTransfer(objectMap);
                printRepaymentReportExp(company.getCompany() + "_", mapList, transferStrList);
            }
            // 文件下载到浏览器
            // 压缩文件
            String path = filePath + File.separator + "RepaymentReport", fileName = "还款率统计_";
            FileOutputStream fos;
            String zipPath = new File(path + File.separator + DateUtils.dateToString(new Date(), "yyyyMMdd")).toString();
            String zipName = fileName + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".zip";
            try {
                fos = new FileOutputStream(path + File.separator + zipName);
                ZipUtils.toZip(zipPath, fos, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 下载文件到浏览器
            FileUtils.printFileToLocal(path, zipName, request, response);
            // 删除目录下文件
            FileUtils.delAllFile(path);
        }else{
            List<RepaymentReport> mapLists = selectRepaymentRateInfo(objectMap);
            List<String> transferStrLists = outsourceAllocationAmountMapper.selectAllTransfer(objectMap);
            // 写入还款率
            Workbook wb = new XSSFWorkbook();
            printAllRepaymentReportExp(wb, mapLists, transferStrLists);
            String name = objectMap.get("company") + "_" + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".xlsx";
            ExcelUtils.writeFileToClient(name, wb, request, response);
        }
    }

    /**
     * 还款率Excel文件写出到本地文件
     * @param fileName
     * @param mapLists
     * @param transferStrList
     */
    private void printRepaymentReportExp(String fileName, List<RepaymentReport> mapLists, List<String> transferStrList) {
        // 写入还款率
        Workbook wb = new XSSFWorkbook();
        printAllRepaymentReportExp(wb, mapLists, transferStrList);
        String path = filePath + File.separator + "RepaymentReport" + File.separator + DateUtils.dateToString(new Date(), "yyyyMMdd");
        String name = fileName + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".xlsx";
        // 输出 Excel 文件到本地磁盘
        ExcelUtils.printExcelFileToLocal(path, name, wb);
    }

    /**
     * 计算导出委外公司排名
     *
     * @param request
     * @param response
     */
    @Override
    public void exportCompanyRankingExp(HttpServletRequest request, HttpServletResponse response) {
        // 遍历所有公司
        List<OutsourceCompany> companyList = outsourceCompanyMapper.selectAllList();
        Map<String, Object> comapyMaps = new HashMap<>(), objectMap, rrMaps;
        Set<String> sets = new LinkedHashSet<>();
        for (int i = 0; i < companyList.size(); i++) {
            OutsourceCompany company = companyList.get(i);
            objectMap = new HashMap<>();
            rrMaps = new HashMap<>();
            objectMap.put("company", company.getCompany());
            List<RepaymentReport> mapLists = selectRepaymentRateInfo(objectMap);
            // 遍历获取
            for (int j = 0; j < mapLists.size(); j++) {
                RepaymentReport report = mapLists.get(j);
                if (report != null && report.getTransfer().indexOf("小计") > 0) {
                    report.setCompany(company.getCompany());
                    rrMaps.put(report.getTransfer(), report);
                    sets.add(report.getTransfer());
                }
            }
            comapyMaps.put(company.getCompany(), rrMaps);
        }
        Map<String, Map<String, Object>> mapList = new HashMap<>();
        // 循环账龄
        for (int i = 1; i <= 7; i++) {
            // 循环移交
            for (String str : sets) {
                // 移交小计
                Map<String, Object> rankMaps = mapList.get(str);
                if (null == rankMaps) {
                    rankMaps = new HashMap<>();
                    rankMaps.put("transfer", str);
                }
                List<RankingInfo> infoList = new ArrayList<>();
                // 取得每个移交日下的公司的当前账龄的还款率
                for (int j = 0; j < companyList.size(); j++) {
                    OutsourceCompany company = companyList.get(j);
                    // 当前公司下的所有移交还款率
<<<<<<< HEAD
                    Map comapyMap = (Map) comapyMaps.get(company.getCompany());
=======
                    Map<String, Object> comapyMap = (Map<String, Object>) comapyMaps.get(company.getCompany());
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                    // 取得当前公司下的当前移交
                    RepaymentReport report = (RepaymentReport) comapyMap.get(str);
                    Double hkl = 0D, yj = null;
                    String hklStr = "", yjStr = "";
                    if (null != report) {
                        switch (i) {
                            case 1:
                                hklStr = report.getM1hkl();
                                yjStr = report.getM1yj();
                                break;
                            case 2:
                                hklStr = report.getM2hkl();
                                yjStr = report.getM2yj();
                                break;
                            case 3:
                                hklStr = report.getM3hkl();
                                yjStr = report.getM3yj();
                                break;
                            case 4:
                                hklStr = report.getM4hkl();
                                yjStr = report.getM4yj();
                                break;
                            case 5:
                                hklStr = report.getM5hkl();
                                yjStr = report.getM5yj();
                                break;
                            case 6:
                                hklStr = report.getM6hkl();
                                yjStr = report.getM6yj();
                                break;
                            default:
                                hklStr = report.getM7hkl();
                                yjStr = report.getM7yj();
                                break;
                        }
                    }
<<<<<<< HEAD
                    if (StringUtils.isNumber(hklStr)) hkl = Double.parseDouble(hklStr);
                    if (StringUtils.isNumber(yjStr)) yj = Double.parseDouble(yjStr);
=======
                    if (StringUtils.isNumber(hklStr)) {
                        hkl = Double.parseDouble(hklStr);
                    }
                    if (StringUtils.isNumber(yjStr)) {
                        yj = Double.parseDouble(yjStr);
                    }
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                    RankingInfo rankingInfo = new RankingInfo();
                    rankingInfo.setCompany(company.getCompany());
                    rankingInfo.setTransfer(str);
                    if (StringUtils.isNotBlank(yjStr) && null != yj && yj > 0) {
                        rankingInfo.setRate(hkl);
                    } else {
                        rankingInfo.setRate(-1D);
                    }
                    infoList.add(rankingInfo);
                }
                // 根据还款率排序,从大到小
                Collections.sort(infoList);
                List<RankingInfo> infoList1 = new ArrayList<>();
                int count = 0;
                for (int j = 0; j < infoList.size(); j++) {
                    RankingInfo rankingInfo = infoList.get(j);
                    // 统计当前移交中委外公司个数
<<<<<<< HEAD
                    if (rankingInfo.getRate() >= 0) count++;
=======
                    if (rankingInfo.getRate() >= 0) {
                        count++;
                    }
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                }
                for (int j = 0, p = 1; j < infoList.size(); j++) {
                    RankingInfo rankingInfo = infoList.get(j);
                    /**
<<<<<<< HEAD
                     * 移交的公司数量小于等于 公司数/2 家时,当前有移交的公司排名全部都为第 公司数/2 + 1 + j名
=======
                     * 移交的公司数量小于等于 公司数/2 家时,当前有移交的公司排名全部都为第 公司数/2 + 1 名
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                     * 公司数量大于 公司数/2 时,有移交的公司排名正常计算
                     * 没有移交的公司排名为 0
                     */
                    if (rankingInfo.getRate() >= 0) {
                        /**
                         * 当前账龄数据移交中的公司总数 <= 公司数/2 (取小) 时
                         * 且所有移交中的公司排名都为 公司数/2 (取小) + 1
                         * 还款率为 0 时,排名为最后一名,即公司数
                         */
                        if (rankingInfo.getRate() == 0) {
                            rankingInfo.setRanking(companyList.size());
                        } else {
                            if (count <= companyList.size() / 2) {
                                if (j < count) {
<<<<<<< HEAD
                                    rankingInfo.setRanking(companyList.size() / 2 + 1 + j);
=======
                                    rankingInfo.setRanking(companyList.size() / 2 + 1);
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                                } else {
                                    rankingInfo.setRanking(0);
                                }
                            } else {
                                if (j < count) {
                                    rankingInfo.setRanking(p);
                                    p++;
                                } else {
                                    rankingInfo.setRanking(0);
                                }
                            }
                        }
                    } else {
                        rankingInfo.setRanking(0);
                    }
                    infoList1.add(rankingInfo);
                }
                for (int j = 0; j < companyList.size(); j++) {
                    OutsourceCompany company = companyList.get(j);
                    for (int k = 0; k < infoList1.size(); k++) {
                        RankingInfo rankingInfo = infoList.get(k);
                        // 当前名次, 取得当前公司的排名数据
                        if (company.getCompany().equals(rankingInfo.getCompany())) {
<<<<<<< HEAD
                            rankMaps.put(company.getCompany() + i, rankingInfo.getRanking() > 0 ? rankingInfo.getRanking() : "");
=======
                            rankMaps.put(company.getCompany() + i, rankingInfo.getRanking());
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                            break;
                        }
                    }
                }
                mapList.put(str, rankMaps);
            }
        }
        List<Map<String, Object>> mapLists = new ArrayList<>();
        for (String key : mapList.keySet()) {
            Map<String, Object> map = mapList.get(key);
            mapLists.add(map);
        }
        Workbook wb = new XSSFWorkbook();
        printAllCompanyRankExp(wb, mapLists, companyList);
        String fileName = "委外公司排名_" + DateUtils.dateToString(new Date(), "yyyyMMdd_HHmmss") + ".xlsx";
        // 文件下载到浏览器
        ExcelUtils.writeFileToClient(fileName, wb, request, response);
    }

<<<<<<< HEAD
    @Override
    public Map<String, List<Map<String, Object>>> loadRepaymentRateCaseNum(Map<String, Object> objectMap) {
        List<Map<String, Object>> mapList = outsourceAllocationRepaymentMapper.selectRepaymentRateCaseNum(objectMap);
        // 数据初步组装
        Map<String, List<Map<String, Object>>> listMaps = new HashMap<>(), result = new HashMap<>();
        // 最大还款天数(T + N)
        int maxDaySum = 0;
        Map<String, Double> countMaps = new HashMap<>();
        for (int i = 0; i < mapList.size(); i++) {
            Map<String, Object> map = mapList.get(i);
            if (null != map) {
                String daySumStr = map.get("daySum").toString();
                String curAmountStr = map.get("curAmount").toString();
                double countAmount = 0, curAmount = 0, amount = 0;
                int daySum;
                if (StringUtils.isNumber(daySumStr)) {
                    daySum = Integer.parseInt(daySumStr);
                    if (daySum > maxDaySum) maxDaySum = daySum;
                }
                if (StringUtils.isNumber(curAmountStr)) curAmount = Double.parseDouble(curAmountStr);
                // 每月一组数
                List<Map<String, Object>> lists = listMaps.get(map.get("month").toString());
                if (null == lists) lists = new ArrayList<>();
                if (lists.size() > 0) {
                    Map<String, Object> map1 = lists.get(lists.size()-1);
                    String amountStr = map1.get("countAmount").toString();
                    if (StringUtils.isNumber(amountStr)) countAmount = Double.parseDouble(amountStr);
                }
                map.put("countAmount", countAmount + curAmount);
                lists.add(map);
                listMaps.put(map.get("month").toString(), lists);
                // 当前月的总额
                if (countMaps.size() > 0 && countMaps.get(map.get("month").toString()) != null) {
                    amount = countMaps.get(map.get("month").toString());
                }
                countMaps.put(map.get("month").toString(), amount + curAmount);
            }
        }
        for (String key : listMaps.keySet()) {
            List<Map<String, Object>> lists = listMaps.get(key), resuleList = new ArrayList<>();
            double countAmount = 0;
            for (int i = 1; i <= maxDaySum; i ++) {
                Map<String, Object> newMap = new HashMap<>();
                for (int j = 0; j < lists.size(); j ++) {
                    Map<String, Object> map = lists.get(j);
                    // T 加 N 天于 i 相等
                    if (null != map && map.get("daySum").toString().equals(i+"")) {
                        String thisAmountStr = map.get("countAmount").toString();
                        double thisAmount = 0;
                        if (StringUtils.isNumber(thisAmountStr)) thisAmount = Double.parseDouble(thisAmountStr);
                        countAmount = thisAmount;
                        newMap.put("x", i);
                        newMap.put("curAmount", thisAmount);
                        newMap.put("y", (thisAmount / countMaps.get(key)) * 100.0);
                        break;
                    }
                    if (j == (lists.size() - 1)) {
                        newMap.put("x", i);
                        newMap.put("curAmount", 0);
                        newMap.put("y", (countAmount / countMaps.get(key)) * 100.0);
                    }
                }
                resuleList.add(newMap);
                result.put(key, resuleList);
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> loadCompanyDivideAmountNum(Map<String, Object> paramMap) {
        // 遍历所有公司
        List<OutsourceCompany> companyList = outsourceCompanyMapper.selectAllList();
        Map<String, Object> comapyMaps = new HashMap<>(), objectMap, rrMaps;
        Integer agecd = 0;
        if (StringUtils.isNumber(paramMap.get("agecd") == null ? "" : paramMap.get("agecd").toString())) {
            agecd = Integer.parseInt(paramMap.get("agecd").toString());
        }
        Set<String> sets = new LinkedHashSet<>();
        for (int i = 0; i < companyList.size(); i++) {
            OutsourceCompany company = companyList.get(i);
            objectMap = new HashMap<>();
            rrMaps = new HashMap<>();
            objectMap.put("company", company.getCompany());
            if (agecd < 7) {
                if (agecd != 0) objectMap.put("agecd", agecd);
            } else {
                objectMap.put("agecds", agecd);
            }
            List<RepaymentReport> mapLists = selectRepaymentRateInfo(objectMap);
            // 遍历获取
            for (int j = 0; j < mapLists.size(); j++) {
                RepaymentReport report = mapLists.get(j);
                if (report != null && report.getTransfer().indexOf("小计") > 0) {
                    report.setCompany(company.getCompany());
                    rrMaps.put(report.getTransfer(), report);
                    sets.add(report.getTransfer());
                }
            }
            comapyMaps.put(company.getCompany(), rrMaps);
        }
        Map<String, Object> mapList = new HashMap<>();
        List lists = new ArrayList();
        Date date = new Date();
        List<String> dateList = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            Date thisDate = DateUtils.getSomeMouthDay(date, -i, 0);
            dateList.add(DateUtils.dateToString(thisDate, "yyyyMM"));
        }
        mapList.put("dateList", dateList);

        // 取得每家公司
        int p = 1;
        for (String keys : comapyMaps.keySet()) {
            Map<String, Object> companyMap1 = new HashMap<>(), companyMap2 = new HashMap<>();
            Map<String, Object> map = (Map<String, Object>) comapyMaps.get(keys);
            Boolean isNull = true;
            double[] array1 = new double[dateList.size()], array2 = new double[dateList.size()], array3 = new double[dateList.size()];
            // 遍历时间轴
            for (int j = 0; j < dateList.size(); j++) {
                String dStr = dateList.get(j);
                for (String key : map.keySet()) {
                    if (key.indexOf(dStr) != -1) {
                        RepaymentReport report = (RepaymentReport) map.get(key);
                        double yjSum = 0, hkSum = 0;
                        // 汇总计算移交,还款
                        String yj1Str = report.getM1yj();
                        String yj2Str = report.getM2yj();
                        String yj3Str = report.getM3yj();
                        String yj4Str = report.getM4yj();
                        String yj5Str = report.getM5yj();
                        String yj6Str = report.getM6yj();
                        String yj7Str = report.getM7yj();
                        String hk1Str = report.getM1hk();
                        String hk2Str = report.getM2hk();
                        String hk3Str = report.getM3hk();
                        String hk4Str = report.getM4hk();
                        String hk5Str = report.getM5hk();
                        String hk6Str = report.getM6hk();
                        String hk7Str = report.getM7hk();

                        if(StringUtils.isNumber(yj1Str)) yjSum += Double.parseDouble(yj1Str);
                        if(StringUtils.isNumber(yj2Str)) yjSum += Double.parseDouble(yj2Str);
                        if(StringUtils.isNumber(yj3Str)) yjSum += Double.parseDouble(yj3Str);
                        if(StringUtils.isNumber(yj4Str)) yjSum += Double.parseDouble(yj4Str);
                        if(StringUtils.isNumber(yj5Str)) yjSum += Double.parseDouble(yj5Str);
                        if(StringUtils.isNumber(yj6Str)) yjSum += Double.parseDouble(yj6Str);
                        if(StringUtils.isNumber(yj7Str)) yjSum += Double.parseDouble(yj7Str);

                        if(StringUtils.isNumber(hk1Str)) hkSum += Double.parseDouble(hk1Str);
                        if(StringUtils.isNumber(hk2Str)) hkSum += Double.parseDouble(hk2Str);
                        if(StringUtils.isNumber(hk3Str)) hkSum += Double.parseDouble(hk3Str);
                        if(StringUtils.isNumber(hk4Str)) hkSum += Double.parseDouble(hk4Str);
                        if(StringUtils.isNumber(hk5Str)) hkSum += Double.parseDouble(hk5Str);
                        if(StringUtils.isNumber(hk6Str)) hkSum += Double.parseDouble(hk6Str);
                        if(StringUtils.isNumber(hk7Str)) hkSum += Double.parseDouble(hk7Str);

                        array1[j] = (yjSum - hkSum) * 1.0;
                        array2[j] = hkSum * 1.0;
                        array3[j] = yjSum * 1.0;
                        isNull = false;
                        break;
                    }
                }
                if (isNull) {
                    array1[j] = 0;
                    array2[j] = 0;
                    array3[j] = 0;
                }
            }
            companyMap1.put("yjSum", array3);
            companyMap2.put("yjSum", array3);
            companyMap1.put("data", array1);
            companyMap2.put("data", array2);
            companyMap1.put("name", keys + "移交");
            companyMap2.put("name", keys + "还款");
            companyMap1.put("stack", "mm" + p);
            companyMap2.put("stack", "mm" + p);
            lists.add(companyMap1);
            lists.add(companyMap2);
            p ++;
        }
        mapList.put("datas",lists);
        return mapList;
    }

    /**
     * 还款率报折线图数据查询
     * @param objectMap
     * @return
     */
    @Override
    public Map<String, List<TurnOverRepayment>> loadTurnOverRepaymentRateNum(Map<String, Object> objectMap) {
        Map<String, List<TurnOverRepayment>> maps = new HashMap<>();
        // 汇总每月数据
        List<TurnOverRepayment> repaymentList = selectTurnOverRepaymentInfo(objectMap), totalList = new ArrayList<>();
        maps.put("turnOver", repaymentList);
        Map<String, TurnOverRepayment> repaymentMap = new HashMap<>();
        int maxCount = 0;
        for (int i = 0; i < repaymentList.size(); i++) {
            TurnOverRepayment overRepayment = repaymentList.get(i);
            if (overRepayment.getThisDaySum() != null && maxCount < overRepayment.getThisDaySum().length) maxCount = overRepayment.getThisDaySum().length;
        }
        for (int i = 0; i < repaymentList.size(); i++) {
            TurnOverRepayment overRepayment = repaymentList.get(i), repayment;
            String transferStr = overRepayment.getTransfer();
            String transfer = transferStr.substring(0, 6);
            repayment = repaymentMap.get(transfer);
            if (repayment == null) {
                repayment = new TurnOverRepayment();
                repayment.setTransfer(transfer);
                repayment.setAmount(0D);
                repayment.setCurAmount(0D);
                repayment.setHandOverAmount(0D);
                repayment.setMaxDays(0);
                repayment.setDaySums(new Double[maxCount]);
                repayment.setxArray(new Double[maxCount]);
                repayment.setThisDaySum(new Double[maxCount]);
            }
            Double amount = overRepayment.getAmount(), curAmount = overRepayment.getCurAmount(),
                    handOverAmount = overRepayment.getHandOverAmount();
            int maxDay = repayment.getMaxDays();
            if (maxDay < overRepayment.getMaxDays()) maxDay = overRepayment.getMaxDays();
            if (amount == null) amount = 0D;
            if (curAmount == null) curAmount = 0D;
            if (handOverAmount == null) handOverAmount = 0D;
            repayment.setAmount(repayment.getAmount() + amount);
            repayment.setCurAmount(repayment.getCurAmount() + curAmount);
            repayment.setHandOverAmount(repayment.getHandOverAmount() + handOverAmount);
            Double[] doubles = repayment.getThisDaySum(), doubles1 = repayment.getDaySums();
            for (int j = 0; overRepayment.getThisDaySum() != null && j < maxCount; j++) {
                Double thisSum = overRepayment.getThisDaySum()[j], daySum = overRepayment.getDaySums()[j],
                        newSum = doubles[j], newSum1 = doubles1[j];
                if (newSum == null) newSum = 0D;
                if (thisSum != null) {
                    doubles[j] = thisSum + newSum;
                }else{
                    doubles[j] = newSum;
                }
                if (daySum != null) {
                    if (newSum1 == null) newSum1 = 0D;
                    doubles1[j] = daySum + newSum1;
                }
            }
            repayment.setDaySums(doubles1);
            repayment.setThisDaySum(doubles);
            repayment.setMaxDays(maxDay);
            repaymentMap.put(transfer, repayment);
        }

        // 计算还款率
        for (String key : repaymentMap.keySet()) {
            TurnOverRepayment overRepayment = repaymentMap.get(key);
            Double[] doubles = new Double[maxCount], xArry = new Double[maxCount], doubles1 = overRepayment.getThisDaySum();
            Double countSum = 0D;
            for (int i = 0; i < doubles1.length && i < overRepayment.getMaxDays(); i++) {
                Double aDouble = doubles1[i];
                if (aDouble != null){
                    // 遍历所有的还款记录, 累加
                    countSum += aDouble;
                    doubles[i] = countSum;
                }
            }
            // 计算还款率
            overRepayment.setThisDaySum(doubles);
            for (int i = 0; i < doubles.length; i++) {
                Double aDouble = doubles[i];
                if (aDouble != null && overRepayment.getAmount() > 0) {
                    xArry[i] = aDouble / overRepayment.getAmount() * 100.0;
                }
            }
            overRepayment.setxArray(xArry);
            totalList.add(overRepayment);
        }
        // 排序
        Collections.sort(totalList);
        maps.put("totalList", totalList);
        return maps;
    }

    /**
     * 递增还款率,还款额 T + 1 报表导出
     * @param objectMap
     * @param request
     * @param response
     */
    @Override
    public void downloadTurnOverRepaymentRate(Map<String, Object> objectMap, HttpServletRequest request, HttpServletResponse response) {
        List<TurnOverRepayment> repaymentList = selectTurnOverRepaymentInfo(objectMap);
        Workbook wb = new XSSFWorkbook();
        Sheet sheet1 = wb.createSheet("还款额"), sheet2 = wb.createSheet("还款率");
        if (repaymentList != null && repaymentList.size() > 0) {
            TurnOverRepayment overRepayment = repaymentList.get(0);
            // 还款额
            int hkeCount = overRepayment.getDaySums().length;
            if (hkeCount > 0) {
                Row row1 = sheet1.createRow(0);
                Row row2 = sheet2.createRow(0);
                row1.createCell(0).setCellValue("移交日");
                row1.createCell(1).setCellValue("总移交金额");
                row2.createCell(0).setCellValue("移交日");
                row2.createCell(1).setCellValue("总移交金额");
                for (int i = 0; i < hkeCount; i++) {
                    row1.createCell(i + 2).setCellValue("第" +(i + 1)+ "天还款");
                    row2.createCell(i + 2).setCellValue("第" +(i + 1)+ "天还款");
                }
                DecimalFormat df = new DecimalFormat("#0.00");
                // 写入表体内容
                for (int i = 0; i < repaymentList.size(); i++) {
                    TurnOverRepayment repayment = repaymentList.get(i);
                    // 还款额
                    Double[] hkeArrays = repayment.getDaySums();
                    Double[] hklArrays = repayment.getxArray();
                    row1 = sheet1.createRow(i + 1);
                    row2 = sheet2.createRow(i + 1);
                    row1.createCell(0).setCellValue(repayment.getTransfer());
                    row1.createCell(1).setCellValue(repayment.getAmount());
                    row2.createCell(0).setCellValue(repayment.getTransfer());
                    row2.createCell(1).setCellValue(repayment.getAmount());
                    for (int j = 0, m = 2; hkeArrays != null && j < hkeArrays.length; j++) {
                        Double hke = hkeArrays[j], hkl = hklArrays[j];
                        String hkeStr = "", hklStr = "";
                        if (hke != null) hkeStr = df.format(hke);
                        if (hkl != null) hklStr = df.format(hkl) + "%";
                        row1.createCell(m).setCellValue(hkeStr);
                        row2.createCell(m).setCellValue(hklStr);
                        m ++;
                    }
                }
            }
        }
        String fileName = "还款额+还款率_" + DateUtils.dateToString(new Date(), "yyyyMMdd_HHmmss") + ".xlsx";
        // 文件下载到浏览器
        ExcelUtils.writeFileToClient(fileName, wb, request, response);
    }

    private List<TurnOverRepayment> selectTurnOverRepaymentInfo(Map<String, Object> condition) {
        List<Map<String, Object>> repaymentList = outsourceAllocationRepaymentMapper.selectTurnOverRepaymentInfo(condition);
        if (condition.get("month") != null) {
            String monthStr = (String) condition.get("month");
            condition.put("month", monthStr.replaceAll("-",""));
        }
        if (condition.get("monthStart") != null) {
            String monthStr = (String) condition.get("monthStart");
            condition.put("monthStart", monthStr.replaceAll("-",""));
        }
        List<Map<String, Object>> recordList = outsourceAllocationRepaymentMapper.selectRecordStatementInfo(condition);
        Map<String, Map<String, Object>> allMaps = new HashMap<>();
        List<TurnOverRepayment> mapLists = new ArrayList<>();
        // 移交金额统计
        for (int i = 0; i < recordList.size(); i++) {
            Map<String, Object> map = recordList.get(i), maps = null;
            String turnOverDayStr = map.get("turnOverDay").toString();
            String amountOverrideStr = map.get("amountOverride").toString();
            double amountOverride = 0, amount = 0;
            if (StringUtils.isNumber(amountOverrideStr)) amountOverride = Double.parseDouble(amountOverrideStr);
            if (StringUtils.isNotBlank(turnOverDayStr)) {
                maps = allMaps.get(turnOverDayStr);
                if (maps == null) {
                    maps = new HashMap<>();
                    maps.put("transfer", turnOverDayStr);
                }
                // 移交统计计算
                String amountStr = maps.get("amount") == null ? "" : maps.get("amount").toString();
                if (StringUtils.isNumber(amountStr)) amount = Double.parseDouble(amountStr);
                maps.put("amount", amountOverride + amount);
            }
            allMaps.put(turnOverDayStr, maps);
        }
        // 获取最大日期
        int maxDaySum = 0;
        for (int i = 0; i < repaymentList.size(); i++) {
            Map<String, Object> map = repaymentList.get(i);
            int thisday = 0;
            if (null != map.get("daySum") && StringUtils.isNumber(map.get("daySum").toString())) {
                thisday = Integer.parseInt(map.get("daySum").toString());
            }
            if (maxDaySum < thisday) maxDaySum = thisday;
        }
        // 还款金额统计 + 计算还款率
        for (int i = 0; i < repaymentList.size(); i++) {
            Map<String, Object> map = repaymentList.get(i), maps = null;
            String transfer = map.get("month") == null ? "" : map.get("month").toString();
            String handOverAmountStr = map.get("handOverAmount") == null ? "" : map.get("handOverAmount").toString();
            String curAmountStr = map.get("curAmount") == null ? "" : map.get("curAmount").toString();
            String daySumStr = map.get("daySum") == null ? "" : map.get("daySum").toString();
            int daySum = 0, maxDay = 0;
            double handOverAmount = 0, curAmount = 0;
            if (StringUtils.isNumber(daySumStr)) daySum = Integer.parseInt(daySumStr);
            if (StringUtils.isNumber(handOverAmountStr)) handOverAmount = Double.parseDouble(handOverAmountStr);
            if (StringUtils.isNumber(curAmountStr)) curAmount = Double.parseDouble(curAmountStr);
            if (StringUtils.isNotBlank(transfer)) {
                maps = allMaps.get(transfer);
                if (maps == null) {
                    maps = new HashMap<>();
                    maps.put("transfer", transfer);
                }
                String handOverAmountStrs = maps.get("handOverAmount") == null ? "" : maps.get("handOverAmount").toString();
                String curAmountStrs = maps.get("curAmount") == null ? "" : maps.get("curAmount").toString();
                String amountStrs = maps.get("amount") == null ? "" : maps.get("amount").toString();
                double handOverAmounts = 0, curAmounts = 0, amount = 0;
                if (StringUtils.isNumber(handOverAmountStrs)) handOverAmounts = Double.parseDouble(handOverAmountStrs);
                if (StringUtils.isNumber(curAmountStrs)) curAmounts = Double.parseDouble(curAmountStrs);
                if (StringUtils.isNumber(amountStrs)) amount = Double.parseDouble(amountStrs);
                maps.put("handOverAmount", handOverAmount + handOverAmounts);
                maps.put("curAmount", curAmount + curAmounts);
                Double[] arrays = (Double[]) maps.get("daySums"), xArray = (Double[]) maps.get("xArray"),
                        thisDaySum = (Double[]) maps.get("thisDaySum");
                if (null == arrays) arrays = new Double[maxDaySum];
                if (null == xArray) xArray = new Double[maxDaySum];
                if (null == thisDaySum) thisDaySum = new Double[maxDaySum];
                for (int j = 0; j < arrays.length; j++) {
                    // 逾期天数是从1开始的
                    if (j + 1 == daySum) {
                        arrays[j] = handOverAmount + handOverAmounts;
                        xArray[j] = (handOverAmount + handOverAmounts) / amount * 100.0;
                        thisDaySum[j] = handOverAmount;
                        if (maxDay < daySum) maxDay = daySum;
                        break;
                    } else {
                        Double v = arrays[j];
                        if (v == null && daySum != 0) {
                            arrays[j] = handOverAmounts;
                            xArray[j] = handOverAmounts / amount * 100.0;
                            thisDaySum[j] = 0D;
                        }
                    }
                }
                maps.put("daySums", arrays);
                maps.put("maxDays", maxDay);
                maps.put("xArray", xArray);
                maps.put("thisDaySum", thisDaySum);
            }
            allMaps.put(transfer, maps);
        }
        // map转对象
        for (String key: allMaps.keySet()) {
            Map<String, Object> map = allMaps.get(key);
            TurnOverRepayment repayment = new TurnOverRepayment();
            repayment = BeanUtils.mapToBean(map, repayment);
            mapLists.add(repayment);
        }
        // 排序
        Collections.sort(mapLists);
        return mapLists;
    }

    /**
     * 输出委外公司排名
=======
    /**
     *
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
     * @param wb
     * @param mapLists
     */
    private void printAllCompanyRankExp(Workbook wb, List<Map<String, Object>> mapLists, List<OutsourceCompany> companyList) {
        Sheet sheet = wb.createSheet("sheet1");
        Row row = sheet.createRow(0);
        // 写入表头
<<<<<<< HEAD
        row.createCell(0).setCellValue("移交月份");
=======
        row.createCell(0).setCellValue("移交日月份");
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
        for (int i = 0, m = 1; i < companyList.size(); i++) {
            OutsourceCompany company = companyList.get(i);
            for (int j = 1; j <= 7; j++) {
                row.createCell(m).setCellValue(company.getCompany() + j);
                m ++;
            }
        }
        // 写入表体内容
        for (int i = 0; i < mapLists.size(); i++) {
            Map<String, Object> map = mapLists.get(i);
            row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(map.get("transfer").toString());
            for (int j = 0, m = 1; j < companyList.size(); j++) {
                OutsourceCompany company = companyList.get(j);
                for (int k = 1; k <= 7; k++) {
                    row.createCell(m).setCellValue(map.get(company.getCompany() + k).toString());
                    m ++;
                }
            }
        }
    }

    /**
     * 还款率写入Excel
     * @param wb
     * @param mapLists
     * @param lists
     */
    private void printAllRepaymentReportExp(Workbook wb, List<RepaymentReport> mapLists, List<String> lists) {
        Sheet sheet = wb.createSheet("还款率");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("移交日");
        row.createCell(1).setCellValue("M1移交");
        row.createCell(2).setCellValue("M2移交");
        row.createCell(3).setCellValue("M3移交");
        row.createCell(4).setCellValue("M4移交");
        row.createCell(5).setCellValue("M5移交");
        row.createCell(6).setCellValue("M6移交");
        row.createCell(7).setCellValue("M7+移交");
        row.createCell(8).setCellValue("M1还款");
        row.createCell(9).setCellValue("M2还款");
        row.createCell(10).setCellValue("M3还款");
        row.createCell(11).setCellValue("M4还款");
        row.createCell(12).setCellValue("M5还款");
        row.createCell(13).setCellValue("M6还款");
        row.createCell(14).setCellValue("M7+还款");
        row.createCell(15).setCellValue("M1还款率(%)");
        row.createCell(16).setCellValue("M2还款率(%)");
        row.createCell(17).setCellValue("M3还款率(%)");
        row.createCell(18).setCellValue("M4还款率(%)");
        row.createCell(19).setCellValue("M5还款率(%)");
        row.createCell(20).setCellValue("M6还款率(%)");
        row.createCell(21).setCellValue("M7+还款率(%)");
        row.createCell(22).setCellValue("M1实际还款");
        row.createCell(23).setCellValue("M2实际还款");
        row.createCell(24).setCellValue("M3实际还款");
        row.createCell(25).setCellValue("M4实际还款");
        row.createCell(26).setCellValue("M5实际还款");
        row.createCell(27).setCellValue("M6实际还款");
        row.createCell(28).setCellValue("M7+实际还款");
        for (int i = 0; i < mapLists.size(); i++) {
            RepaymentReport report = mapLists.get(i);
            if (null != report) {
                row = sheet.createRow(i + 1);
                if (report.getTransfer().indexOf("小计") > 0) {
                    CellStyle cellStyle = wb.createCellStyle();
                    cellStyle.setFillForegroundColor(IndexedColors.TURQUOISE1.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    Cell cell = row.createCell(0);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getTransfer());
                    cell = row.createCell(1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM1yj());
                    cell = row.createCell(2);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM2yj());
                    cell = row.createCell(3);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM3yj());
                    cell = row.createCell(4);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM4yj());
                    cell = row.createCell(5);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM5yj());
                    cell = row.createCell(6);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM6yj());
                    cell = row.createCell(7);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM7yj());
                    cell = row.createCell(8);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM1hk());
                    cell = row.createCell(9);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM2hk());
                    cell = row.createCell(10);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM3hk());
                    cell = row.createCell(11);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM4hk());
                    cell = row.createCell(12);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM5hk());
                    cell = row.createCell(13);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM6hk());
                    cell = row.createCell(14);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM7hk());
                    cell = row.createCell(15);
                    cell.setCellStyle(cellStyle);
<<<<<<< HEAD
                    cell.setCellValue(report.getM1hkl() == null ? "" : report.getM1hkl() + "%");
                    cell = row.createCell(16);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM2hkl() == null ? "" : report.getM2hkl() + "%");
                    cell = row.createCell(17);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM3hkl() == null ? "" : report.getM3hkl() + "%");
                    cell = row.createCell(18);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM4hkl() == null ? "" : report.getM4hkl() + "%");
                    cell = row.createCell(19);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM5hkl() == null ? "" : report.getM5hkl() + "%");
                    cell = row.createCell(20);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM6hkl() == null ? "" : report.getM6hkl() + "%");
                    cell = row.createCell(21);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM7hkl() == null ? "" : report.getM7hkl() + "%");
=======
                    cell.setCellValue(report.getM1hkl());
                    cell = row.createCell(16);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM2hkl());
                    cell = row.createCell(17);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM3hkl());
                    cell = row.createCell(18);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM4hkl());
                    cell = row.createCell(19);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM5hkl());
                    cell = row.createCell(20);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM6hkl());
                    cell = row.createCell(21);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM7hkl());
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                    cell = row.createCell(22);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM1sjhk());
                    cell = row.createCell(23);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM2sjhk());
                    cell = row.createCell(24);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM3sjhk());
                    cell = row.createCell(25);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM4sjhk());
                    cell = row.createCell(26);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM5sjhk());
                    cell = row.createCell(27);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM6sjhk());
                    cell = row.createCell(28);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(report.getM7sjhk());
                } else {
                    if (lists.contains(report.getTransfer())) {
                        CellStyle cellStyle = wb.createCellStyle();
                        cellStyle.setFillForegroundColor(IndexedColors.LAVENDER.getIndex());
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                        Cell cell = row.createCell(0);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(report.getTransfer());
                    } else {
                        row.createCell(0).setCellValue(report.getTransfer());
                    }
                    row.createCell(1).setCellValue(report.getM1yj());
                    row.createCell(2).setCellValue(report.getM2yj());
                    row.createCell(3).setCellValue(report.getM3yj());
                    row.createCell(4).setCellValue(report.getM4yj());
                    row.createCell(5).setCellValue(report.getM5yj());
                    row.createCell(6).setCellValue(report.getM6yj());
                    row.createCell(7).setCellValue(report.getM7yj());
                    row.createCell(8).setCellValue(report.getM1hk());
                    row.createCell(9).setCellValue(report.getM2hk());
                    row.createCell(10).setCellValue(report.getM3hk());
                    row.createCell(11).setCellValue(report.getM4hk());
                    row.createCell(12).setCellValue(report.getM5hk());
                    row.createCell(13).setCellValue(report.getM6hk());
                    row.createCell(14).setCellValue(report.getM7hk());
<<<<<<< HEAD
                    row.createCell(15).setCellValue(report.getM1hkl() == null ? "" : report.getM1hkl() + "%");
                    row.createCell(16).setCellValue(report.getM2hkl() == null ? "" : report.getM2hkl() + "%");
                    row.createCell(17).setCellValue(report.getM3hkl() == null ? "" : report.getM3hkl() + "%");
                    row.createCell(18).setCellValue(report.getM4hkl() == null ? "" : report.getM4hkl() + "%");
                    row.createCell(19).setCellValue(report.getM5hkl() == null ? "" : report.getM5hkl() + "%");
                    row.createCell(20).setCellValue(report.getM6hkl() == null ? "" : report.getM6hkl() + "%");
                    row.createCell(21).setCellValue(report.getM7hkl() == null ? "" : report.getM7hkl() + "%");
=======
                    row.createCell(15).setCellValue(report.getM1hkl());
                    row.createCell(16).setCellValue(report.getM2hkl());
                    row.createCell(17).setCellValue(report.getM3hkl());
                    row.createCell(18).setCellValue(report.getM4hkl());
                    row.createCell(19).setCellValue(report.getM5hkl());
                    row.createCell(20).setCellValue(report.getM6hkl());
                    row.createCell(21).setCellValue(report.getM7hkl());
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                    row.createCell(22).setCellValue(report.getM1sjhk());
                    row.createCell(23).setCellValue(report.getM2sjhk());
                    row.createCell(24).setCellValue(report.getM3sjhk());
                    row.createCell(25).setCellValue(report.getM4sjhk());
                    row.createCell(26).setCellValue(report.getM5sjhk());
                    row.createCell(27).setCellValue(report.getM6sjhk());
                    row.createCell(28).setCellValue(report.getM7sjhk());
                }
            }
        }
    }

    /**
     * 还款率统计计算
     *
     * @param condition 查询参数
     * @return list
     */
    private List<RepaymentReport> selectRepaymentRateInfo(Map<String, Object> condition) {
        List<Map<String, Object>> recordList = outsourceAllocationRepaymentMapper.selectRecordStatementInfo(condition);
        List<RepaymentReport> mapLists = new ArrayList<>();
        List<Map<String, Object>> repaymentList = outsourceAllocationRepaymentMapper.selectRepaymentRateInfo(condition);
        Map<String, Map<String, Object>> allMaps = new HashMap<>(), rateMaps = new HashMap<>();
        // 移交金额统计
        for (int i = 0; i < recordList.size(); i++) {
            Map<String, Object> map = recordList.get(i), maps = null;
            String turnOverDayStr = map.get("turnOverDay").toString();
            String ageCdStr = map.get("ageCd").toString();
            String amountOverrideStr = map.get("amountOverride").toString();
            int ageCd = 0;
            double amountOverride = 0;
<<<<<<< HEAD
            if (StringUtils.isNumber(ageCdStr)) ageCd = Integer.parseInt(ageCdStr);
            if (StringUtils.isNumber(amountOverrideStr)) amountOverride = Double.parseDouble(amountOverrideStr);
=======
            if (StringUtils.isNumber(ageCdStr)) {
                ageCd = Integer.parseInt(ageCdStr);
            }
            if (StringUtils.isNumber(amountOverrideStr)) {
                amountOverride = Double.parseDouble(amountOverrideStr);
            }
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
            if (StringUtils.isNotBlank(turnOverDayStr)) {
                maps = allMaps.get(turnOverDayStr);
                if (maps == null) {
                    maps = new HashMap<>();
                    maps.put("transfer", turnOverDayStr);
                }
                // 移交统计计算
<<<<<<< HEAD
                if (ageCd >= 7) {
                    String amountStr = maps.get("m7yj") == null ? "" : maps.get("m7yj").toString();
                    double amount = 0;
                    if (StringUtils.isNumber(amountStr)) amount = Double.parseDouble(amountStr);
                    maps.put("m7yj", amountOverride + amount);
                }else{
                    for (int j = 0; j < 6; j++) {
                        if (ageCd == j + 1) {
                            maps.put("m" + (j + 1) + "yj", amountOverride);
                            break;
                        }
=======
                for (int j = 0; j < 7; j++) {
                    if (ageCd == j + 1) maps.put("m" + (j + 1) + "yj", amountOverride);
                    if (ageCd > 7) {
                        String amountStr = maps.get("m7yj") == null ? "" : maps.get("m7yj").toString();
                        double amount = 0;
                        if (StringUtils.isNumber(amountStr)) {
                            amount = Double.parseDouble(amountStr);
                        }
                        maps.put("m7yj", amountOverride + amount);
                        break;
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                    }
                }
            }
            allMaps.put(turnOverDayStr, maps);
        }
        // 还款金额统计
        for (int i = 0; i < repaymentList.size(); i++) {
            Map<String, Object> map = repaymentList.get(i), maps = null;
            String transfer = map.get("transfer").toString();
            String handOverAgeCdStr = map.get("handOverAgeCd").toString();
            String handOverAmountStr = map.get("handOverAmount").toString();
            String curAmountStr = map.get("curAmount").toString();
            int handOverAgeCd = 0;
            double handOverAmount = 0, curAmount = 0;
<<<<<<< HEAD
            if (StringUtils.isNumber(handOverAgeCdStr)) handOverAgeCd = Integer.parseInt(handOverAgeCdStr);
            if (StringUtils.isNumber(handOverAmountStr)) handOverAmount = Double.parseDouble(handOverAmountStr);
            if (StringUtils.isNumber(curAmountStr)) curAmount = Double.parseDouble(curAmountStr);
=======
            if (StringUtils.isNumber(handOverAgeCdStr)) {
                handOverAgeCd = Integer.parseInt(handOverAgeCdStr);
            }
            if (StringUtils.isNumber(handOverAmountStr)) {
                handOverAmount = Double.parseDouble(handOverAmountStr);
            }
            if (StringUtils.isNumber(curAmountStr)) {
                curAmount = Double.parseDouble(curAmountStr);
            }
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
            if (StringUtils.isNotBlank(transfer)) {
                maps = allMaps.get(transfer);
                if (maps == null) {
                    maps = new HashMap<>();
                    maps.put("transfer", transfer);
                }
<<<<<<< HEAD
                // 实际还款移交金额 + 实际还款金额
                if (handOverAgeCd > 7) {
                    String m7hkStr = maps.get("m7hk") == null ? "" : maps.get("m7hk").toString();
                    String m7sjhkStr = maps.get("m7sjhk") == null ? "" : maps.get("m7sjhk").toString();
                    double m7hk = 0, m7sjhk = 0;
                    if (StringUtils.isNumber(m7hkStr)) m7hk = Double.parseDouble(m7hkStr);
                    if (StringUtils.isNumber(m7sjhkStr)) m7sjhk = Double.parseDouble(m7sjhkStr);
                    maps.put("m7hk", handOverAmount + m7hk);
                    maps.put("m7sjhk", curAmount + m7sjhk);
                } else {
                    for (int j = 0; j < 6; j++) {
                        if (handOverAgeCd == j + 1) {
                            maps.put("m" + (j + 1) + "hk", handOverAmount);
                            maps.put("m" + (j + 1) + "sjhk", curAmount);
                            break;
                        }
=======
                // 实际还款移交金额
                for (int j = 0; j < 7; j++) {
                    if (handOverAgeCd == j + 1) maps.put("m" + (j + 1) + "hk", handOverAmount);
                    if (handOverAgeCd > 7) {
                        String amountStr = maps.get("m7hk") == null ? "" : maps.get("m7hk").toString();
                        double amount = 0;
                        if (StringUtils.isNumber(amountStr)) {
                            amount = Double.parseDouble(amountStr);
                        }
                        maps.put("m7hk", handOverAmount + amount);
                        break;
                    }
                }
                // 实际还款金额
                for (int j = 0; j < 7; j++) {
                    if (handOverAgeCd == j + 1) maps.put("m" + (j + 1) + "sjhk", curAmount);
                    if (handOverAgeCd > 7) {
                        String amountStr = maps.get("m7sjhk") == null ? "" : maps.get("m7sjhk").toString();
                        double amount = 0;
                        if (StringUtils.isNumber(amountStr)) {
                            amount = Double.parseDouble(amountStr);
                        }
                        maps.put("m7sjhk", curAmount + amount);
                        break;
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
                    }
                }
            }
            allMaps.put(transfer, maps);
        }

        // 按照月份合计
        DecimalFormat df = new DecimalFormat("#0.00");
        Iterator<String> iter = allMaps.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            Map<String, Object> map = allMaps.get(key);
            if (null != map) {
                String transfer = key.substring(0, 6) + "小计";
                Map<String, Object> maps = rateMaps.get(transfer);
                if (maps == null) {
                    maps = new HashMap<>();
                    maps.put("transfer", transfer);
                    maps.put("m1yj", map.get("m1yj"));
                    maps.put("m2yj", map.get("m2yj"));
                    maps.put("m3yj", map.get("m3yj"));
                    maps.put("m4yj", map.get("m4yj"));
                    maps.put("m5yj", map.get("m5yj"));
                    maps.put("m6yj", map.get("m6yj"));
                    maps.put("m7yj", map.get("m7yj"));
                    maps.put("m1hk", map.get("m1hk"));
                    maps.put("m2hk", map.get("m2hk"));
                    maps.put("m3hk", map.get("m3hk"));
                    maps.put("m4hk", map.get("m4hk"));
                    maps.put("m5hk", map.get("m5hk"));
                    maps.put("m6hk", map.get("m6hk"));
                    maps.put("m7hk", map.get("m7hk"));
                    maps.put("m1sjhk", map.get("m1sjhk"));
                    maps.put("m2sjhk", map.get("m2sjhk"));
                    maps.put("m3sjhk", map.get("m3sjhk"));
                    maps.put("m4sjhk", map.get("m4sjhk"));
                    maps.put("m5sjhk", map.get("m5sjhk"));
                    maps.put("m6sjhk", map.get("m6sjhk"));
                    maps.put("m7sjhk", map.get("m7sjhk"));
                } else {
                    String m1yjStr = map.get("m1yj") == null ? "" : map.get("m1yj").toString(), sm1yjStr = maps.get("m1yj") == null ? "" : maps.get("m1yj").toString();
                    String m2yjStr = map.get("m2yj") == null ? "" : map.get("m2yj").toString(), sm2yjStr = maps.get("m2yj") == null ? "" : maps.get("m2yj").toString();
                    String m3yjStr = map.get("m3yj") == null ? "" : map.get("m3yj").toString(), sm3yjStr = maps.get("m3yj") == null ? "" : maps.get("m3yj").toString();
                    String m4yjStr = map.get("m4yj") == null ? "" : map.get("m4yj").toString(), sm4yjStr = maps.get("m4yj") == null ? "" : maps.get("m4yj").toString();
                    String m5yjStr = map.get("m5yj") == null ? "" : map.get("m5yj").toString(), sm5yjStr = maps.get("m5yj") == null ? "" : maps.get("m5yj").toString();
                    String m6yjStr = map.get("m6yj") == null ? "" : map.get("m6yj").toString(), sm6yjStr = maps.get("m6yj") == null ? "" : maps.get("m6yj").toString();
                    String m7yjStr = map.get("m7yj") == null ? "" : map.get("m7yj").toString(), sm7yjStr = maps.get("m7yj") == null ? "" : maps.get("m7yj").toString();
                    String m1hkStr = map.get("m1hk") == null ? "" : map.get("m1hk").toString(), sm1hkStr = maps.get("m1hk") == null ? "" : maps.get("m1hk").toString();
                    String m2hkStr = map.get("m2hk") == null ? "" : map.get("m2hk").toString(), sm2hkStr = maps.get("m2hk") == null ? "" : maps.get("m2hk").toString();
                    String m3hkStr = map.get("m3hk") == null ? "" : map.get("m3hk").toString(), sm3hkStr = maps.get("m3hk") == null ? "" : maps.get("m3hk").toString();
                    String m4hkStr = map.get("m4hk") == null ? "" : map.get("m4hk").toString(), sm4hkStr = maps.get("m4hk") == null ? "" : maps.get("m4hk").toString();
                    String m5hkStr = map.get("m5hk") == null ? "" : map.get("m5hk").toString(), sm5hkStr = maps.get("m5hk") == null ? "" : maps.get("m5hk").toString();
                    String m6hkStr = map.get("m6hk") == null ? "" : map.get("m6hk").toString(), sm6hkStr = maps.get("m6hk") == null ? "" : maps.get("m6hk").toString();
                    String m7hkStr = map.get("m7hk") == null ? "" : map.get("m7hk").toString(), sm7hkStr = maps.get("m7hk") == null ? "" : maps.get("m7hk").toString();
                    String m1sjhkStr = map.get("m1sjhk") == null ? "" : map.get("m1sjhk").toString(), sm1sjhkStr = maps.get("m1sjhk") == null ? "" : maps.get("m1sjhk").toString();
                    String m2sjhkStr = map.get("m2sjhk") == null ? "" : map.get("m2sjhk").toString(), sm2sjhkStr = maps.get("m2sjhk") == null ? "" : maps.get("m2sjhk").toString();
                    String m3sjhkStr = map.get("m3sjhk") == null ? "" : map.get("m3sjhk").toString(), sm3sjhkStr = maps.get("m3sjhk") == null ? "" : maps.get("m3sjhk").toString();
                    String m4sjhkStr = map.get("m4sjhk") == null ? "" : map.get("m4sjhk").toString(), sm4sjhkStr = maps.get("m4sjhk") == null ? "" : maps.get("m4sjhk").toString();
                    String m5sjhkStr = map.get("m5sjhk") == null ? "" : map.get("m5sjhk").toString(), sm5sjhkStr = maps.get("m5sjhk") == null ? "" : maps.get("m5sjhk").toString();
                    String m6sjhkStr = map.get("m6sjhk") == null ? "" : map.get("m6sjhk").toString(), sm6sjhkStr = maps.get("m6sjhk") == null ? "" : maps.get("m6sjhk").toString();
                    String m7sjhkStr = map.get("m7sjhk") == null ? "" : map.get("m7sjhk").toString(), sm7sjhkStr = maps.get("m7sjhk") == null ? "" : maps.get("m7sjhk").toString();
                    double m1yj = 0, m2yj = 0, m3yj = 0, m4yj = 0, m5yj = 0, m6yj = 0, m7yj = 0,
                            sm1yj = 0, sm2yj = 0, sm3yj = 0, sm4yj = 0, sm5yj = 0, sm6yj = 0, sm7yj = 0;
                    double m1hk = 0, m2hk = 0, m3hk = 0, m4hk = 0, m5hk = 0, m6hk = 0, m7hk = 0,
                            sm1hk = 0, sm2hk = 0, sm3hk = 0, sm4hk = 0, sm5hk = 0, sm6hk = 0, sm7hk = 0;
                    double m1sjhk = 0, m2sjhk = 0, m3sjhk = 0, m4sjhk = 0, m5sjhk = 0, m6sjhk = 0, m7sjhk = 0,
                            sm1sjhk = 0, sm2sjhk = 0, sm3sjhk = 0, sm4sjhk = 0, sm5sjhk = 0, sm6sjhk = 0, sm7sjhk = 0;
                    if (StringUtils.isNumber(m1yjStr)) m1yj = Double.parseDouble(m1yjStr);
                    if (StringUtils.isNumber(m2yjStr)) m2yj = Double.parseDouble(m2yjStr);
                    if (StringUtils.isNumber(m3yjStr)) m3yj = Double.parseDouble(m3yjStr);
                    if (StringUtils.isNumber(m4yjStr)) m4yj = Double.parseDouble(m4yjStr);
                    if (StringUtils.isNumber(m5yjStr)) m5yj = Double.parseDouble(m5yjStr);
                    if (StringUtils.isNumber(m6yjStr)) m6yj = Double.parseDouble(m6yjStr);
                    if (StringUtils.isNumber(m7yjStr)) m7yj = Double.parseDouble(m7yjStr);
                    if (StringUtils.isNumber(sm1yjStr)) sm1yj = Double.parseDouble(sm1yjStr);
                    if (StringUtils.isNumber(sm2yjStr)) sm2yj = Double.parseDouble(sm2yjStr);
                    if (StringUtils.isNumber(sm3yjStr)) sm3yj = Double.parseDouble(sm3yjStr);
                    if (StringUtils.isNumber(sm4yjStr)) sm4yj = Double.parseDouble(sm4yjStr);
                    if (StringUtils.isNumber(sm5yjStr)) sm5yj = Double.parseDouble(sm5yjStr);
                    if (StringUtils.isNumber(sm6yjStr)) sm6yj = Double.parseDouble(sm6yjStr);
                    if (StringUtils.isNumber(sm7yjStr)) sm7yj = Double.parseDouble(sm7yjStr);

                    if (StringUtils.isNumber(m1hkStr)) m1hk = Double.parseDouble(m1hkStr);
                    if (StringUtils.isNumber(m2hkStr)) m2hk = Double.parseDouble(m2hkStr);
                    if (StringUtils.isNumber(m3hkStr)) m3hk = Double.parseDouble(m3hkStr);
                    if (StringUtils.isNumber(m4hkStr)) m4hk = Double.parseDouble(m4hkStr);
                    if (StringUtils.isNumber(m5hkStr)) m5hk = Double.parseDouble(m5hkStr);
                    if (StringUtils.isNumber(m6hkStr)) m6hk = Double.parseDouble(m6hkStr);
                    if (StringUtils.isNumber(m7hkStr)) m7hk = Double.parseDouble(m7hkStr);
                    if (StringUtils.isNumber(sm1hkStr)) sm1hk = Double.parseDouble(sm1hkStr);
                    if (StringUtils.isNumber(sm2hkStr)) sm2hk = Double.parseDouble(sm2hkStr);
                    if (StringUtils.isNumber(sm3hkStr)) sm3hk = Double.parseDouble(sm3hkStr);
                    if (StringUtils.isNumber(sm4hkStr)) sm4hk = Double.parseDouble(sm4hkStr);
                    if (StringUtils.isNumber(sm5hkStr)) sm5hk = Double.parseDouble(sm5hkStr);
                    if (StringUtils.isNumber(sm6hkStr)) sm6hk = Double.parseDouble(sm6hkStr);
                    if (StringUtils.isNumber(sm7hkStr)) sm7hk = Double.parseDouble(sm7hkStr);

                    if (StringUtils.isNumber(m1sjhkStr)) m1sjhk = Double.parseDouble(m1sjhkStr);
                    if (StringUtils.isNumber(m2sjhkStr)) m2sjhk = Double.parseDouble(m2sjhkStr);
                    if (StringUtils.isNumber(m3sjhkStr)) m3sjhk = Double.parseDouble(m3sjhkStr);
                    if (StringUtils.isNumber(m4sjhkStr)) m4sjhk = Double.parseDouble(m4sjhkStr);
                    if (StringUtils.isNumber(m5sjhkStr)) m5sjhk = Double.parseDouble(m5sjhkStr);
                    if (StringUtils.isNumber(m6sjhkStr)) m6sjhk = Double.parseDouble(m6sjhkStr);
                    if (StringUtils.isNumber(m7sjhkStr)) m7sjhk = Double.parseDouble(m7sjhkStr);
                    if (StringUtils.isNumber(sm1sjhkStr)) sm1sjhk = Double.parseDouble(sm1sjhkStr);
                    if (StringUtils.isNumber(sm2sjhkStr)) sm2sjhk = Double.parseDouble(sm2sjhkStr);
                    if (StringUtils.isNumber(sm3sjhkStr)) sm3sjhk = Double.parseDouble(sm3sjhkStr);
                    if (StringUtils.isNumber(sm4sjhkStr)) sm4sjhk = Double.parseDouble(sm4sjhkStr);
                    if (StringUtils.isNumber(sm5sjhkStr)) sm5sjhk = Double.parseDouble(sm5sjhkStr);
                    if (StringUtils.isNumber(sm6sjhkStr)) sm6sjhk = Double.parseDouble(sm6sjhkStr);
                    if (StringUtils.isNumber(sm7sjhkStr)) sm7sjhk = Double.parseDouble(sm7sjhkStr);

                    maps.put("transfer", transfer);
                    maps.put("m1yj", df.format(m1yj + sm1yj));
                    maps.put("m2yj", df.format(m2yj + sm2yj));
                    maps.put("m3yj", df.format(m3yj + sm3yj));
                    maps.put("m4yj", df.format(m4yj + sm4yj));
                    maps.put("m5yj", df.format(m5yj + sm5yj));
                    maps.put("m6yj", df.format(m6yj + sm6yj));
                    maps.put("m7yj", df.format(m7yj + sm7yj));
                    maps.put("m1hk", df.format(m1hk + sm1hk));
                    maps.put("m2hk", df.format(m2hk + sm2hk));
                    maps.put("m3hk", df.format(m3hk + sm3hk));
                    maps.put("m4hk", df.format(m4hk + sm4hk));
                    maps.put("m5hk", df.format(m5hk + sm5hk));
                    maps.put("m6hk", df.format(m6hk + sm6hk));
                    maps.put("m7hk", df.format(m7hk + sm7hk));
                    maps.put("m1sjhk", df.format(m1sjhk + sm1sjhk));
                    maps.put("m2sjhk", df.format(m2sjhk + sm2sjhk));
                    maps.put("m3sjhk", df.format(m3sjhk + sm3sjhk));
                    maps.put("m4sjhk", df.format(m4sjhk + sm4sjhk));
                    maps.put("m5sjhk", df.format(m5sjhk + sm5sjhk));
                    maps.put("m6sjhk", df.format(m6sjhk + sm6sjhk));
                    maps.put("m7sjhk", df.format(m7sjhk + sm7sjhk));
                }
                rateMaps.put(transfer, maps);
                rateMaps.put(key, map);
            }
        }

        // 数据合并转换,计算还款率
        iter = rateMaps.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            Map<String, Object> map = rateMaps.get(key);
            //还款率计算
            if (null != map) {
                String m1yjStr = map.get("m1yj") == null ? "" : map.get("m1yj").toString();
                String m2yjStr = map.get("m2yj") == null ? "" : map.get("m2yj").toString();
                String m3yjStr = map.get("m3yj") == null ? "" : map.get("m3yj").toString();
                String m4yjStr = map.get("m4yj") == null ? "" : map.get("m4yj").toString();
                String m5yjStr = map.get("m5yj") == null ? "" : map.get("m5yj").toString();
                String m6yjStr = map.get("m6yj") == null ? "" : map.get("m6yj").toString();
                String m7yjStr = map.get("m7yj") == null ? "" : map.get("m7yj").toString();
                String m1hkStr = map.get("m1hk") == null ? "" : map.get("m1hk").toString();
                String m2hkStr = map.get("m2hk") == null ? "" : map.get("m2hk").toString();
                String m3hkStr = map.get("m3hk") == null ? "" : map.get("m3hk").toString();
                String m4hkStr = map.get("m4hk") == null ? "" : map.get("m4hk").toString();
                String m5hkStr = map.get("m5hk") == null ? "" : map.get("m5hk").toString();
                String m6hkStr = map.get("m6hk") == null ? "" : map.get("m6hk").toString();
                String m7hkStr = map.get("m7hk") == null ? "" : map.get("m7hk").toString();
                if (StringUtils.isNumber(m1yjStr) && StringUtils.isNumber(m1hkStr)) {
                    double yj = Double.parseDouble(m1yjStr), hk = Double.parseDouble(m1hkStr);
                    if (yj > 0) map.put("m1hkl", df.format(hk / yj * 100));
                }
                if (StringUtils.isNumber(m2yjStr) && StringUtils.isNumber(m2hkStr)) {
                    double yj = Double.parseDouble(m2yjStr), hk = Double.parseDouble(m2hkStr);
                    if (yj > 0) map.put("m2hkl", df.format(hk / yj * 100));
                }
                if (StringUtils.isNumber(m3yjStr) && StringUtils.isNumber(m3hkStr)) {
                    double yj = Double.parseDouble(m3yjStr), hk = Double.parseDouble(m3hkStr);
                    if (yj > 0) map.put("m3hkl", df.format(hk / yj * 100));
                }
                if (StringUtils.isNumber(m4yjStr) && StringUtils.isNumber(m4hkStr)) {
                    double yj = Double.parseDouble(m4yjStr), hk = Double.parseDouble(m4hkStr);
                    if (yj > 0) map.put("m4hkl", df.format(hk / yj * 100));
                }
                if (StringUtils.isNumber(m5yjStr) && StringUtils.isNumber(m5hkStr)) {
                    double yj = Double.parseDouble(m5yjStr), hk = Double.parseDouble(m5hkStr);
                    if (yj > 0) map.put("m5hkl", df.format(hk / yj * 100));
                }
                if (StringUtils.isNumber(m6yjStr) && StringUtils.isNumber(m6hkStr)) {
                    double yj = Double.parseDouble(m6yjStr), hk = Double.parseDouble(m6hkStr);
                    if (yj > 0) map.put("m6hkl", df.format(hk / yj * 100));
                }
                if (StringUtils.isNumber(m7yjStr) && StringUtils.isNumber(m7hkStr)) {
                    double yj = Double.parseDouble(m7yjStr), hk = Double.parseDouble(m7hkStr);
                    if (yj > 0) map.put("m7hkl", df.format(hk / yj * 100));
                }
                // 将Map转换成bean
                RepaymentReport repaymentReport = new RepaymentReport();
                repaymentReport = BeanUtils.mapToBean(map, repaymentReport);
                mapLists.add(repaymentReport);
            }
        }
        // 排序
        Collections.sort(mapLists);
        return mapLists;
    }


}
