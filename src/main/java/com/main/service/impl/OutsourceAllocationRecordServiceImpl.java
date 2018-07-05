package com.main.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.main.mapper.OutsourceAllocationAmountHisMapper;
import com.main.mapper.OutsourceAllocationAmountMapper;
import com.main.mapper.OutsourceCompanyMapper;
import com.main.model.*;
import com.main.mapper.OutsourceAllocationRecordMapper;
import com.main.service.IOutsourceAllocationRecordService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.main.service.NewAddIousService;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.*;

/**
 * <p>
 * 委外分配日期记录表 服务实现类
 * </p>
 *
 * @author jiewai
 * @since 2018-05-03
 */
@Service
public class OutsourceAllocationRecordServiceImpl extends ServiceImpl<OutsourceAllocationRecordMapper, OutsourceAllocationRecord> implements IOutsourceAllocationRecordService {

    @Autowired
    private OutsourceAllocationRecordMapper outsourceAllocationRecordMapper;
    @Autowired
    private OutsourceAllocationAmountMapper outsourceAllocationAmountMapper;
    @Autowired
    private OutsourceAllocationAmountHisMapper outsourceAllocationAmountHisMapper;
    @Autowired
    private OutsourceCompanyMapper companyMapper;
    @Autowired
    private NewAddIousService newAddIousService;

    @Value("${filePath}")
    public String filePath;

    @Override
    public void selectPageInfo(PageInfo pageInfo) {
        Page<Map<String, Object>> page = new Page<>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<Map<String, Object>> list = outsourceAllocationRecordMapper.selectPageInfo(page, pageInfo.getCondition());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    /**
     * 委外过的公司匹配
     *
     * @param pageInfo
     * @param outList
     */
    @Override
    public void loadMatchingData(PageInfo pageInfo, List<OutsourceCompany> outList) {
        Page<Map<String, Object>> page = new Page<>(pageInfo.getNowpage(), pageInfo.getSize());
        // 解析前端传入custId
        String custIdStr = pageInfo.getCondition().get("custId") + "";
        custIdStr = custIdStr.replace("，", ",").replace("|", ",").replace("\t", ",").replace(" ", ",")
                .replace(";", ",").replace("/", ",").replace("\n", ",").replace("\r", ",");
        String[] custIds = custIdStr.split(",");
        List<Map<String, Object>> mapLists = dataMatching(custIds, outList), mapList = new ArrayList<>();
        // 逻辑分页
        int start = (page.getCurrent() - 1) * page.getSize(), end = page.getCurrent() * page.getSize();
        if (start >= mapLists.size()) {
            start = (mapLists.size() / page.getSize()) * page.getSize();
            end = mapLists.size();
        }
        if (end > mapLists.size()) end = mapLists.size();
        for (int i = start; i < end; i++) {
            Map<String, Object> map = mapLists.get(i);
            mapList.add(map);
        }
        pageInfo.setRows(mapList);
        pageInfo.setTotal(mapLists.size());
    }

    /**
     * 加载导出匹配数据信息
     *
     * @param condition
     * @param outList
     * @param request
     *@param response @return
     */
    @Override
    public void getMatchingData(Map<String, Object> condition, List<OutsourceCompany> outList, HttpServletRequest request, HttpServletResponse response) {
        String custIdStr = condition.get("custId") + "";
        custIdStr = custIdStr.replace("，", ",").replace("|", ",").replace("\t", ",").replace(" ", ",")
                .replace(";", ",").replace("/", ",").replace("\n", ",").replace("\r", ",");
        String[] custIds = custIdStr.split(",");
        List<Map<String, Object>> mapList = dataMatching(custIds, outList);
        if (mapList.size() > 0) {
            // 写入文件数据解析
            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet("sheet1");
            XSSFRow row = sheet.createRow(0);
            row.createCell(0).setCellValue("身份证号");
            row.createCell(1).setCellValue("已委外过");
            for (int i = 0; i < 10; i++) {
                if (i < outList.size()) {
                    row.createCell(i + 2).setCellValue(outList.get(i).getCompany());
                } else {
                    row.createCell(i + 2).setCellValue("公司" + (i + 1));
                }
            }
            for (int i = 0; i < mapList.size(); i++) {
                row = sheet.createRow(i + 1);
                Map map = mapList.get(i);
                row.createCell(0).setCellValue((String) map.get("custId"));
                row.createCell(1).setCellValue((String) map.get("wg"));
                for (int j = 0; j < 10; j++) {
                    row.createCell(j + 2).setCellValue((String) map.get("ww" + (j + 1)));
                }
            }
            String fileName = "已委外公司匹配清单_" + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".xlsx";
            // 文件下载到浏览器
            ExcelUtils.writeFileToClient(fileName, wb, request, response);
        }
    }

    /**
     * 根据客户证件号码查找匹配的历史信息
     *
     * @param custIds
     * @return
     */
    private List<Map<String, Object>> dataMatching(String[] custIds, List<OutsourceCompany> outList) {
        // 创建封装对象集合
        List<Map<String, Object>> mapList = new ArrayList<>();
        // 获取全部外包公司
        List<String> outs = new ArrayList<>();
        for (int i = 0; i < outList.size(); i++) {
            OutsourceCompany objectMap = outList.get(i);
            outs.add(objectMap.getCompany());
        }
        // 查询所有的要查询的借据
        StringBuilder buffer = new StringBuilder();
        for (int j = 0; j < custIds.length; j++) {
            if (j == custIds.length - 1) {
                buffer.append("'").append(custIds[j]).append("'");
            } else {
                buffer.append("'").append(custIds[j]).append("',");
            }
        }
        Map<String, Object> mapObj = new HashMap<>(), map;
        Map<String, List<OutsourceAllocationRecord>> mapLists = new HashMap<>();
        mapObj.put("custIds", buffer.toString());
        List<OutsourceAllocationRecord> allLists = outsourceAllocationRecordMapper.loadRecordByMaps(mapObj);
        // 循环组装(map(custId, List<借据>))
        for (int i = 0; i < allLists.size(); i++) {
            OutsourceAllocationRecord record = allLists.get(i);
            List<OutsourceAllocationRecord> lists = mapLists.get(record.getCustId());
            if (null == lists) {
                lists = new ArrayList<>();
            }
            lists.add(record);
            mapLists.put(record.getCustId(), lists);
        }

        for (int j = 0; j < custIds.length && allLists.size() > 0; j++) {
            map = new HashMap<>();
            map.put("custId", custIds[j]);
            // 创建一个存储当前查询客户的所有委外过的公司
            Set<String> setStr = new HashSet<>();
            // 根据传入custId 查询委外记录数据
            List<OutsourceAllocationRecord> lists = mapLists.get(custIds[j]);
            //List<OutsourceAllocationRecord> lists = outsourceAllocationRecordMapper.loadRecordByMaps(map);
            for (int i = 0; lists != null && i < lists.size(); i++) {
                OutsourceAllocationRecord objectMap = lists.get(i);
                // 包含于所有外包公司之中
                String existStr = objectMap.getDcaDistribution();
                if (outs.contains(existStr)) {
                    // 判断是否存在于set集合之中
                    if (!setStr.contains(existStr)) {
                        setStr.add(existStr);
                    }
                }
            }
            StringBuilder wgStr = new StringBuilder();
            int s = 0;
            if (setStr.size() > 0) {
                for (String str : setStr) {
                    s++;
                    if (s == setStr.size()) {
                        wgStr.insert(0, str);
                    } else {
                        wgStr.insert(0,"," + str);
                    }
                    for (int i = 0, m = 1; i < outs.size(); i++, m++) {
                        if (str.equals(outs.get(i))) {
                            // 委外过的公司置空
                            map.put("ww" + m, "");
                        } else {
                            if (!"".equals(map.get("ww" + m))) {
                                map.put("ww" + m, outs.get(i));
                            }
                        }
                    }
                }
            } else {
                for (int i = 0, m = 1; i < outs.size(); i++, m++) {
                    map.put("ww" + m, outs.get(i));
                }
            }
            map.put("wg", wgStr.toString());
            mapList.add(map);
        }
        return mapList;
    }

    /**
     * 写入 EXCEL 表格中的数据
     *
     * @param listMap
     */
    @Override
    @Transactional
    public Map<String, Object> importInOutsourceExcel(List<Map<String, Object>> listMap) {
        Map<String, Object> resultMap = new HashMap<>();
        // 1.清除当前表所有数据
        outsourceAllocationRecordMapper.cleanAllData();
        // 2.写入当前读取到的Excel表格中数据
        for (int i = 0; i < listMap.size(); i++) {
            Map map = listMap.get(i);
            String name = map.get("姓名") == null ? "": map.get("姓名").toString();
            String custId = map.get("证件号码") == null ? "": map.get("证件号码").toString();
            String telNumber = map.get("手机号") == null ? "" : map.get("手机号").toString();
            String ious = map.get("借据号码") == null ? "" : map.get("借据号码").toString();
            String totalAmount = map.get("分期总金额(当前在贷本金）") == null ? "" : map.get("分期总金额(当前在贷本金）").toString();
            String nextRefundDay = map.get("下一还款日") == null ? "" : map.get("下一还款日").toString();
            String amountOverride = map.get("逾期金额（R）") == null ? "" : map.get("逾期金额（R）").toString();
            String overduePrincipal = map.get("逾期本金") == null ? "" : map.get("逾期本金").toString();
            String overdueAccrual = map.get("逾期利息") == null ? "" : map.get("逾期利息").toString();
            String defaultInterest = map.get("罚息") == null ? "" : map.get("罚息").toString();
            String ageCd = map.get("帐龄（R）") == null ? "" : map.get("帐龄（R）").toString();
            String overdue = map.get("逾期天数") == null ? "" : map.get("逾期天数").toString();
            String netLendingPlatform = map.get("网络贷款平台") == null ? "" : map.get("网络贷款平台").toString();
            String isSoleProprietorship = map.get("是否独资") == null ? "" : map.get("是否独资").toString();
            String dcaDistribution = map.get("委外分配") == null ? "" : map.get("委外分配").toString();
            String theCaseDistribution = map.get("案件类型") == null ? "" : map.get("案件类型").toString();
            String turnOverDay = map.get("移交日期") == null ? "" : map.get("移交日期").toString();
            String productName = map.get("产品名称") == null ? "" : map.get("产品名称").toString();
            String totalAging = map.get("分期总期数") == null ? "": map.get("分期总期数").toString();
            String contractCreateDate = map.get("合同生成日期") == null ? "" : map.get("合同生成日期").toString();
            String remarks = map.get("备注") == null ? "" : map.get("备注").toString();
            String createType = map.get("类型") == null ? "" : map.get("类型").toString();
            if (name.indexOf("VLOOKUP") >= 0 || custId.indexOf("VLOOKUP") >= 0 ||
                    telNumber.indexOf("VLOOKUP") >= 0 || ious.indexOf("VLOOKUP") >= 0 ||
                    totalAmount.indexOf("VLOOKUP") >= 0 || nextRefundDay.indexOf("VLOOKUP") >= 0 ||
                    amountOverride.indexOf("VLOOKUP") >= 0 || overduePrincipal.indexOf("VLOOKUP") >= 0 ||
                    overdueAccrual.indexOf("VLOOKUP") >= 0 || defaultInterest.indexOf("VLOOKUP") >= 0 ||
                    ageCd.indexOf("VLOOKUP") >= 0 || overdue.indexOf("VLOOKUP") >= 0 ||
                    netLendingPlatform.indexOf("VLOOKUP") >= 0 || dcaDistribution.indexOf("VLOOKUP") >= 0 ||
                    turnOverDay.indexOf("VLOOKUP") >= 0 || totalAging.indexOf("VLOOKUP") >= 0 ||
                    contractCreateDate.indexOf("VLOOKUP") >= 0 || dcaDistribution.indexOf("VLOOKUP") >= 0
                    ) {
                resultMap.put("code", "300");
                resultMap.put("msg", "表格中可能包含有函数!");
                return resultMap;
            }

            if (!StringUtils.isEmpty(map.get("证件号码"))) {
                OutsourceAllocationRecord record = new OutsourceAllocationRecord();
                record.setName(name);
                record.setCustId(custId);
                record.setTelNumber(telNumber);
                record.setIous(ious);
                record.setTotalAmount(totalAmount);
                record.setNextRefundDay(nextRefundDay);
                record.setAmountOverride(amountOverride);
                record.setOverduePrincipal(overduePrincipal);
                record.setOverdueAccrual(overdueAccrual);
                record.setDefaultInterest(defaultInterest);
                record.setAgeCd(ageCd);
                record.setOverdue(overdue);
                record.setNetLendingPlatform(netLendingPlatform);
                record.setIsSoleProprietorship(isSoleProprietorship);
                record.setDcaDistribution(dcaDistribution);
                record.setTheCaseDistribution(theCaseDistribution);
                record.setTurnOverDay(turnOverDay);
                record.setProductName(productName);
                record.setTotalAging(totalAging);
                record.setContractCreateDate(contractCreateDate);
                record.setRemarks(remarks);
                record.setCreateDate(new Date());
                record.setCreateType(createType);
                outsourceAllocationRecordMapper.insert(record);
            }
        }
        resultMap.put("code", "200");
        return resultMap;
    }

    /**
     * 导出所有表数据
     *
     * @param request
     * @param response
     */
    @Override
    public void downLoadAllDate(HttpServletRequest request, HttpServletResponse response) {
        // 查询所有表数据
        List<OutsourceAllocationRecord> allList = outsourceAllocationRecordMapper.selectAllList();
        String path = filePath + File.separator + "Record";
        printAllRecordInfo(allList, path + File.separator + DateUtils.dateToString(new Date(), "yyyyMMdd"), "全部");
        Map<String, List<OutsourceAllocationRecord>> recordListMap = new HashMap<>();
        // 获取每个公司下的还款
        for (int i = 0; i < allList.size(); i++) {
            OutsourceAllocationRecord record = allList.get(i);
            // 获取到当前公司下的所有数据集合,如果为空,
            List<OutsourceAllocationRecord> records = recordListMap.get(record.getDcaDistribution());
            if (records == null) {
                records = new ArrayList<>();
            }
            records.add(record);
            recordListMap.put(record.getDcaDistribution(), records);
        }
        // 遍历所有公司
        List<OutsourceCompany> companyList = companyMapper.selectAllList();
        for (int i = 0; i < companyList.size(); i++) {
            OutsourceCompany company = companyList.get(i);
            List<OutsourceAllocationRecord> recordList = recordListMap.get(company.getCompany());
            printAllRecordInfo(recordList, path + File.separator + DateUtils.dateToString(new Date(), "yyyyMMdd"), company.getCompany());
        }
        // 压缩文件
        FileOutputStream fos;
        String zipPath = new File(path + File.separator + DateUtils.dateToString(new Date(), "yyyyMMdd")).toString();
        String zipName =  "委外大总表_" + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".zip";
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

    private void printAllRecordInfo(List<OutsourceAllocationRecord> lists, String path, String fileName) {
        // 写入文件数据解析
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("sheet1");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("姓名");
        row.createCell(1).setCellValue("证件号码");
        row.createCell(2).setCellValue("手机号");
        row.createCell(3).setCellValue("借据号码");
        row.createCell(4).setCellValue("分期总金额(当前在贷本金）");
        row.createCell(5).setCellValue("下一还款日");
        row.createCell(6).setCellValue("逾期金额（R）");
        row.createCell(7).setCellValue("逾期本金");
        row.createCell(8).setCellValue("逾期利息");
        row.createCell(9).setCellValue("罚息");
        row.createCell(10).setCellValue("帐龄（R）");
        row.createCell(11).setCellValue("逾期天数");
        row.createCell(12).setCellValue("网络贷款平台");
        row.createCell(13).setCellValue("是否独资");
        row.createCell(14).setCellValue("委外分配");
        row.createCell(15).setCellValue("案件类型");
        row.createCell(16).setCellValue("移交日期");
        row.createCell(17).setCellValue("产品名称");
        row.createCell(18).setCellValue("分期总期数");
        row.createCell(19).setCellValue("合同生成日期");
        row.createCell(20).setCellValue("备注");
        row.createCell(21).setCellValue("类型");
        // 写出所有表数据
        for (int i = 0; i < lists.size(); i++) {
            row = sheet.createRow(i + 1);
            OutsourceAllocationRecord record = lists.get(i);
            if (record != null) {
                if (StringUtils.isNotBlank(record.getCreateType()) &&
                        record.getCreateType().indexOf(DateUtils.dateToString(new Date(), "yyyy-MM-dd")) >= 0) {
                    CellStyle cellStyle = wb.createCellStyle();
                    if (record.getCreateType().indexOf("新增") >= 0) {
                        cellStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    }else{
                        cellStyle.setFillForegroundColor(IndexedColors.YELLOW1.getIndex());
                        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                    }
                    Cell cell = row.createCell(0);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getName());
                    cell = row.createCell(1);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getCustId());
                    cell = row.createCell(2);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getTelNumber());
                    cell = row.createCell(3);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getIous());
                    cell = row.createCell(4);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getTotalAmount());
                    cell = row.createCell(5);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getNextRefundDay());
                    cell = row.createCell(6);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getAmountOverride());
                    cell = row.createCell(7);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getOverduePrincipal());
                    cell = row.createCell(8);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getOverdueAccrual());
                    cell = row.createCell(9);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getDefaultInterest());
                    cell = row.createCell(10);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getAgeCd());
                    cell = row.createCell(11);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getOverdue());
                    cell = row.createCell(12);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getNetLendingPlatform());
                    cell = row.createCell(13);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getIsSoleProprietorship());
                    cell = row.createCell(14);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getDcaDistribution());
                    cell = row.createCell(15);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getTheCaseDistribution());
                    cell = row.createCell(16);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getTurnOverDay());
                    cell = row.createCell(17);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getProductName());
                    cell = row.createCell(18);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getTotalAging());
                    cell = row.createCell(19);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getContractCreateDate());
                    cell = row.createCell(20);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getRemarks());
                    cell = row.createCell(21);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(record.getCreateType());
                }else{
                    row.createCell(0).setCellValue(record.getName());
                    row.createCell(1).setCellValue(record.getCustId());
                    row.createCell(2).setCellValue(record.getTelNumber());
                    row.createCell(3).setCellValue(record.getIous());
                    row.createCell(4).setCellValue(record.getTotalAmount());
                    row.createCell(5).setCellValue(record.getNextRefundDay());
                    row.createCell(6).setCellValue(record.getAmountOverride());
                    row.createCell(7).setCellValue(record.getOverduePrincipal());
                    row.createCell(8).setCellValue(record.getOverdueAccrual());
                    row.createCell(9).setCellValue(record.getDefaultInterest());
                    row.createCell(10).setCellValue(record.getAgeCd());
                    row.createCell(11).setCellValue(record.getOverdue());
                    row.createCell(12).setCellValue(record.getNetLendingPlatform());
                    row.createCell(13).setCellValue(record.getIsSoleProprietorship());
                    row.createCell(14).setCellValue(record.getDcaDistribution());
                    row.createCell(15).setCellValue(record.getTheCaseDistribution());
                    row.createCell(16).setCellValue(record.getTurnOverDay());
                    row.createCell(17).setCellValue(record.getProductName());
                    row.createCell(18).setCellValue(record.getTotalAging());
                    row.createCell(19).setCellValue(record.getContractCreateDate());
                    row.createCell(20).setCellValue(record.getRemarks());
                    row.createCell(21).setCellValue(record.getCreateType());
                }
            }
        }
        String name = fileName + "_" + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".xlsx";
        // 输出 Excel 文件到本地磁盘
        ExcelUtils.printExcelFileToLocal(path, name, wb);
    }


    @Override
    public Map<String, Object> importAmountAndRecordExcel(List<Map<String, Object>> listMap) {
        Date date = new Date(), transfer, thePushDay, maxThePushDay = null;
        Map<String, Object> resultMap = new HashMap<>(), objectMap;
        // 2.写入当前读取到的Excel表格中数据
        for (int i = 0; i < listMap.size(); i++) {
            Map map = listMap.get(i);
            String name = map.get("姓名") == null ? "": map.get("姓名").toString();
            String custId = map.get("证件号码") == null ? "": map.get("证件号码").toString();
            String telNumber = map.get("手机号") == null ? "" : map.get("手机号").toString();
            String ious = map.get("借据号码") == null ? "" : map.get("借据号码").toString();
            String totalAmount = map.get("分期总金额(当前在贷本金）") == null ? "" : map.get("分期总金额(当前在贷本金）").toString();
            String nextRefundDay = map.get("下一还款日") == null ? "" : map.get("下一还款日").toString();
            String amountOverrideStr = map.get("逾期金额（R）") == null ? "" : map.get("逾期金额（R）").toString();
            String overduePrincipal = map.get("逾期本金") == null ? "" : map.get("逾期本金").toString();
            String overdueAccrual = map.get("逾期利息") == null ? "" : map.get("逾期利息").toString();
            String defaultInterest = map.get("罚息") == null ? "" : map.get("罚息").toString();
            String ageCdStr = map.get("帐龄（R）") == null ? "" : map.get("帐龄（R）").toString();
            String overdue = map.get("逾期天数") == null ? "" : map.get("逾期天数").toString();
            String netLendingPlatform = map.get("网络贷款平台") == null ? "" : map.get("网络贷款平台").toString();
            String isSoleProprietorship = map.get("是否独资") == null ? "" : map.get("是否独资").toString();
            String dcaDistribution = map.get("委外分配") == null ? "" : map.get("委外分配").toString();
            String theCaseDistribution = map.get("案件类型") == null ? "" : map.get("案件类型").toString();
            String turnOverDay = map.get("移交日期") == null ? "" : map.get("移交日期").toString();
            String productName = map.get("产品名称") == null ? "" : map.get("产品名称").toString();
            String totalAging = map.get("分期总期数") == null ? "": map.get("分期总期数").toString();
            String contractCreateDate = map.get("合同生成日期") == null ? "" : map.get("合同生成日期").toString();
            String remarks = map.get("备注") == null ? "" : map.get("备注").toString();
            if ((name.indexOf("VLOOKUP") >= 0) || (custId.indexOf("VLOOKUP") >= 0) ||
                    (telNumber.indexOf("VLOOKUP") >= 0) || (ious.indexOf("VLOOKUP") >= 0) ||
                    (totalAmount.indexOf("VLOOKUP") >= 0) || (nextRefundDay.indexOf("VLOOKUP") >= 0) ||
                    (amountOverrideStr.indexOf("VLOOKUP") >= 0) || (overduePrincipal.indexOf("VLOOKUP") >= 0) ||
                    (overdueAccrual.indexOf("VLOOKUP") >= 0) || (defaultInterest.indexOf("VLOOKUP") >= 0) ||
                    (ageCdStr.indexOf("VLOOKUP") >= 0) || (overdue.indexOf("VLOOKUP") >= 0) ||
                    (netLendingPlatform.indexOf("VLOOKUP") >= 0) || (dcaDistribution.indexOf("VLOOKUP") >= 0) ||
                    (turnOverDay.indexOf("VLOOKUP") >= 0) || totalAging.contains("VLOOKUP") ||
                    (contractCreateDate.indexOf("VLOOKUP") >= 0) || (dcaDistribution.indexOf("VLOOKUP") >= 0)
                    ) {
                resultMap.put("code", "300");
                resultMap.put("msg", "表格中可能包含有函数!");
                return resultMap;
            }
            if (StringUtils.isNotBlank(custId) && StringUtils.isNotBlank(ious)) {
                double nowCollectionAmount = 0;
                int ageCd = 0;
                if (StringUtils.isNumber(amountOverrideStr)) {
                    nowCollectionAmount = Double.parseDouble(amountOverrideStr);
                }
                if (StringUtils.isNumber(ageCdStr)) {
                    ageCd = Integer.parseInt(ageCdStr);
                }
                transfer = DateUtils.strToDate(turnOverDay, "yyyyMMdd");
                if (ageCd <= 2) {
                    thePushDay = DateUtils.getSomeMouthDay(transfer, 1, 0);
                }else{
                    thePushDay = DateUtils.getSomeMouthDay(transfer, 2, 0);
                }
                // 查询当前客户的所有借据
                objectMap = new HashMap<>();
                objectMap.put("custId", custId);
                // 查询当前客户在余额表是否有借据存在,根据不同情况进行移交日和出催日的计算
                List<OutsourceAllocationAmount> amountList = outsourceAllocationAmountMapper.loadCommonAmountByMaps(objectMap);
                OutsourceAllocationAmount amount1;
                // 清空最大退案日
                if (null == amountList || amountList.size() <= 0) {
                    maxThePushDay = null;
                }
                for (int j = 0; null != amountList && j < amountList.size(); j++) {
                    amount1 = amountList.get(j);
                    // 判断是否有金额大于0的当前借据
                    /**
                     * 为防止错误数据查产生,如果在发生移交借据移交时余额表中还存在有金额大于零的借据时,把余额表中的当前借据删除,且修改当前
                     * 历史表中的数据,
                     */
                    if (ious.equals(amount1.getIous()) && amount1.getNowCollectionAmount() > 0) {
                        objectMap = new HashMap<>();
                        objectMap.put("id", amount1.getId());
                        outsourceAllocationAmountMapper.deleteByMap(objectMap);
                        // 更新历史表数据
                        OutsourceAllocationAmountHis amountHis = outsourceAllocationAmountHisMapper.selectOneById(amount1.getId());
                        amountHis.setThePushDay(date);
                        amountHis.setUpdateTime(date);
                        outsourceAllocationAmountHisMapper.updateById(amountHis);
                    }
                    // 获取当前客户最迟退案日(排除当前借据和金额为零的)
                    if (amount1.getNowCollectionAmount() > 0 && !ious.equals(amount1.getIous())) {
                        if (maxThePushDay == null || DateUtils.differentDaysByMillisecond(maxThePushDay, amount1.getThePushDay()) > 0) {
                            maxThePushDay = amount1.getThePushDay();
                        }
                    }
                }
                // 取最大退案日
                if (DateUtils.differentDaysByMillisecond(thePushDay, maxThePushDay) > 0) {
                    thePushDay = maxThePushDay;
                }
                // 同步退案日期
                for (int j = 0; null != amountList && j < amountList.size(); j++) {
                    amount1 = amountList.get(j);
                    if (!"Y".equals(amount1.getIsLeaveCase()) && amount1.getNowCollectionAmount() > 0) {
                        amount1.setThePushDay(thePushDay);
                        amount1.setUpdateTime(date);
                        outsourceAllocationAmountMapper.updateById(amount1);

                        // 历史表同步
                        OutsourceAllocationAmountHis amountHis = outsourceAllocationAmountHisMapper.selectOneById(amount1.getId());
                        amountHis.setThePushDay(thePushDay);
                        amountHis.setUpdateTime(date);
                        outsourceAllocationAmountHisMapper.updateById(amountHis);
                    }
                }
                // 添加到余额历史表
                OutsourceAllocationAmountHis amountHis = new OutsourceAllocationAmountHis();
                amountHis.setName(name);
                amountHis.setCustId(custId);
                amountHis.setTelNumber(telNumber);
                amountHis.setIous(ious);
                amountHis.setNowCollectionAmount(nowCollectionAmount);
                amountHis.setNowAgecd(ageCd);
                amountHis.setTransferAgecd(ageCd);
                amountHis.setTransfer(transfer);
                amountHis.setThePushDay(thePushDay);
                amountHis.setCompany(dcaDistribution);
                amountHis.setUpdateTime(date);
                amountHis.setCreatDate(date);
                amountHis.setTransferAmount(nowCollectionAmount);
                amountHis.setRemarks(remarks + DateUtils.dateToString(date, "yyyy-MM-dd") + "_移交");
                outsourceAllocationAmountHisMapper.insertInfo(amountHis);

                // 添加到余额表
                OutsourceAllocationAmount amount = new OutsourceAllocationAmount();
                amount.setId(amountHis.getId());
                amount.setName(name);
                amount.setCustId(custId);
                amount.setTelNumber(telNumber);
                amount.setIous(ious);
                amount.setNowCollectionAmount(nowCollectionAmount);
                amount.setNowAgecd(ageCd);
                amount.setTransferAgecd(ageCd);
                amount.setTransfer(transfer);
                amount.setThePushDay(thePushDay);
                amount.setCompany(dcaDistribution);
                amount.setUpdateTime(date);
                amount.setCreatDate(date);
                amount.setTransferAmount(nowCollectionAmount);
                amount.setRemarks(remarks + DateUtils.dateToString(date, "yyyy-MM-dd") + "_移交");
                outsourceAllocationAmountMapper.insertInfo(amount);

                // 添加到大总表
                OutsourceAllocationRecord record = new OutsourceAllocationRecord();
                record.setName(name);
                record.setCustId(custId);
                record.setTelNumber(telNumber);
                record.setIous(ious);
                record.setTotalAmount(totalAmount);
                record.setNextRefundDay(nextRefundDay);
                record.setAmountOverride(amountOverrideStr);
                record.setOverduePrincipal(overduePrincipal);
                record.setOverdueAccrual(overdueAccrual);
                record.setDefaultInterest(defaultInterest);
                record.setAgeCd(ageCdStr);
                record.setOverdue(overdue);
                record.setNetLendingPlatform(netLendingPlatform);
                record.setIsSoleProprietorship(isSoleProprietorship);
                record.setDcaDistribution(dcaDistribution);
                record.setTheCaseDistribution(theCaseDistribution);
                record.setTurnOverDay(turnOverDay);
                record.setProductName(productName);
                record.setTotalAging(totalAging);
                record.setContractCreateDate(contractCreateDate);
                record.setRemarks(remarks + DateUtils.dateToString(date, "yyyy-MM-dd") + "_移交");
                record.setCreateDate(date);
                record.setCreateType("移交_" + DateUtils.dateToString(date, "yyyy-MM-dd"));
                outsourceAllocationRecordMapper.insert(record);

                // 从新借据表中剔除移交案件
                objectMap = new HashMap<>();
                objectMap.put("ious", ious);
                newAddIousService.deleteByMap(objectMap);
                // 更新客户公司
                objectMap = new HashMap<>();
                objectMap.put("custId", custId);
                List<NewAddIous> newAddIouses = newAddIousService.loadNewIousBy(objectMap);
                for (int j = 0; j < newAddIouses.size(); j++) {
                    NewAddIous addIous = newAddIouses.get(j);
                    addIous.setCompany(dcaDistribution);
                    addIous.setUpdateTime(date);
                    newAddIousService.updateById(addIous);
                }
            }
        }
        resultMap.put("code", "200");
        return resultMap;
    }

}
