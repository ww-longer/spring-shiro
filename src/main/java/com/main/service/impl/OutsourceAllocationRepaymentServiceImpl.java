package com.main.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.main.mapper.OutsourceAllocationAmountMapper;
import com.main.mapper.OutsourceAllocationRepaymentMapper;
import com.main.mapper.OutsourceCompanyMapper;
import com.main.model.OutsourceAllocationAmount;
import com.main.model.OutsourceAllocationRepayment;
import com.main.model.OutsourceCompany;
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
            if (repayments == null) {
                repayments = new ArrayList<>();
            }
            repayments.add(repayment);
            stringListMap.put(repayment.getCompany(), repayments);
        }
        Iterator<String> iter = stringListMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            List<OutsourceAllocationRepayment> repayments = stringListMap.get(key);
            printRepaymentExp(key, repayments, wb);
        }
        ExcelUtils.writeFileToClient(fileName, wb, request, response);
    }

    private void printRepaymentExp(String sheetName, List<OutsourceAllocationRepayment> repayments, Workbook wb) {
        Sheet sheet = wb.createSheet(sheetName);
        CellStyle cellStyle = wb.createCellStyle();
        setRepaymentExcel(sheet, cellStyle, repayments);
    }

    private void printRepaymentAndAmountExp(String fileName, List<OutsourceAllocationAmount> amounts, List<OutsourceAllocationRepayment> repayments) {
        Workbook wb = new XSSFWorkbook();
        // 写入余额
        Sheet sheet1 = wb.createSheet("余额");
        CellStyle cellStyle = wb.createCellStyle();
        setAmountExcel(sheet1, wb, amounts);
        // 写入还款
        Sheet sheet2 = wb.createSheet("还款");
        setRepaymentExcel(sheet2, cellStyle, repayments);
        // 输出 Excel 文件到本地磁盘
        ExcelUtils.printExcelFileToLocal(filePath + File.separator + DateUtils.dateToString(new Date(), "yyyyMMdd"), fileName + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".xlsx", wb);
    }

    /**
     * 余额写入
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
        row.createCell(8).setCellValue("创建日期");
        row.createCell(9).setCellValue("公司");
        row.createCell(10).setCellValue("备注");
        OutsourceAllocationAmount amount;
        if (null != amountList) {
            for (int i = 0; i < amountList.size(); i++) {
                amount = amountList.get(i);
                row = sheet.createRow(i + 1);
                if (amount != null) {
                    if (DateUtils.dateToString(new Date(), "yyyy-MM-dd").equals(DateUtils.dateToString(amount.getCreatDate(), "yyyy-MM-dd"))) {
                        CellStyle cellStyle = wb.createCellStyle();
                        if (amount.getRemarks() != null && amount.getRemarks().indexOf("SYS") >= 0) {
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
                        cell.setCellValue(DateUtils.dateToString(amount.getCreatDate(), "yyyy-MM-dd HH:mm:ss"));
                        cell = row.createCell(9);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(amount.getCompany());
                        cell = row.createCell(10);
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
                        row.createCell(8).setCellValue(DateUtils.dateToString(amount.getCreatDate(), "yyyy-MM-dd HH:mm:ss"));
                        row.createCell(9).setCellValue(amount.getCompany());
                        row.createCell(10).setCellValue(amount.getRemarks());
                    }
                }
            }
        }

    }

    /**
     * 还款写入
     * @param sheet
     * @param cellStyle
     * @param repayments
     */
    private void setRepaymentExcel(Sheet sheet, CellStyle cellStyle, List<OutsourceAllocationRepayment> repayments) {
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
        row.createCell(9).setCellValue("是否部分还款");
        row.createCell(10).setCellValue("公司");
        row.createCell(11).setCellValue("备注");
        OutsourceAllocationRepayment repayment;
        if (null != repayments) {
            for (int i = 0; i < repayments.size(); i++) {
                repayment = repayments.get(i);
                row = sheet.createRow(i + 1);
                if (repayment != null) {
                    cellStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN1.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    String hkStr;
                    if (repayment.getIsSumRefund() == 1) {
                        hkStr = "部分";
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
                        cell.setCellValue(hkStr);
                        cell = row.createCell(10);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getCompany());
                        cell = row.createCell(11);
                        cell.setCellStyle(cellStyle);
                        cell.setCellValue(repayment.getRemarks());
                    } else {
                        hkStr = "全部";
                        row.createCell(0).setCellValue(repayment.getIous() + DateUtils.dateToString(repayment.getTransfer(), "yyyyMMdd"));
                        row.createCell(1).setCellValue(repayment.getName());
                        row.createCell(2).setCellValue(repayment.getCustId());
                        row.createCell(3).setCellValue(repayment.getTelNumber());
                        row.createCell(4).setCellValue(repayment.getIous());
                        row.createCell(5).setCellValue(repayment.getCurAmount());
                        row.createCell(6).setCellValue(DateUtils.dateToString(repayment.getRepaymentDate(), "yyyy-MM-dd"));
                        row.createCell(7).setCellValue(repayment.getHandOverAmount());
                        row.createCell(8).setCellValue(DateUtils.dateToString(repayment.getTransfer(), "yyyy-MM-dd"));
                        row.createCell(9).setCellValue(hkStr);
                        row.createCell(10).setCellValue(repayment.getCompany());
                        row.createCell(11).setCellValue(repayment.getRemarks());
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
            if (amounts == null) {
                amounts = new ArrayList<>();
            }
            amounts.add(amount);
            amountListMap.put(amount.getCompany(), amounts);
        }
        // 获取每个公司下的还款
        for (int i = 0; i < repaymentLists.size(); i++) {
            OutsourceAllocationRepayment repayment = repaymentLists.get(i);
            // 获取到当前公司下的所有数据集合,如果为空,
            List<OutsourceAllocationRepayment> repayments = repaymentListMap.get(repayment.getCompany());
            if (repayments == null) {
                repayments = new ArrayList<>();
            }
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
        FileOutputStream fos = null;
        String zipPath = new File(filePath + File.separator + DateUtils.dateToString(new Date(), "yyyyMMdd")).toString();
        String zipName =  fileName + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".zip";
        try {
            fos = new FileOutputStream(filePath + File.separator + zipName);
            ZipUtils.toZip(zipPath, fos, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 下载文件到浏览器
        FileUtils.printFileToLocal(filePath, zipName, request, response);
        // 删除目录下文件
        FileUtils.delAllFile(filePath);
    }

    @Override
    public OutsourceAllocationRepayment loadRepaymentById(Integer id) {
        return outsourceAllocationRepaymentMapper.selectById(id);
    }

    @Override
    public void importRepaymentHisExcel(List<HashMap<String, Object>> listMap) {
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
                if (StringUtils.isNotBlank(repaymentDateStr)) repaymentDate = DateUtils.strToDate(repaymentDateStr, "yyyyMMdd");
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

}
