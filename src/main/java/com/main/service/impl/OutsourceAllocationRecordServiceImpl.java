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
import com.sys.commons.utils.ExcelUtils;
import com.sys.commons.utils.DateUtils;
import com.sys.commons.utils.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
     */
    @Override
    public void loadMatchingData(PageInfo pageInfo) {
        // 解析前端传入custId
        String custIdStr = pageInfo.getCondition().get("custId") + "";
        custIdStr = custIdStr.replace("，", ",").replace("|", ",").replace("\t", ",").replace(" ", ",")
                .replace(";", ",").replace("/", ",").replace("\n", ",").replace("\r", ",");
        String[] custIds = custIdStr.split(",");
        List<Map<String, Object>> mapList = dataMatching(custIds);
        pageInfo.setRows(mapList);
        pageInfo.setTotal(mapList.size());
    }

    /**
     * 加载导出匹配数据信息
     *
     * @param condition
     * @return
     */
    @Override
    public List<Map<String, Object>> getMatchingData(Map<String, Object> condition) {
        String custIdStr = condition.get("custId") + "";
        custIdStr = custIdStr.replace("，", ",").replace("|", ",").replace("\t", ",").replace(" ", ",")
                .replace(";", ",").replace("/", ",").replace("\n", ",").replace("\r", ",");
        String[] custIds = custIdStr.split(",");
        return dataMatching(custIds);
    }

    /**
     * 根据客户证件号码查找匹配的历史信息
     *
     * @param custIds
     * @return
     */
    public List<Map<String, Object>> dataMatching(String[] custIds) {
        // 创建封装对象集合
        List<Map<String, Object>> mapList = new ArrayList<>();
        // 获取全部外包公司
        List<OutsourceCompany> outList = companyMapper.selectAllList();
        List<String> outs = new ArrayList<>();
        for (int i = 0; i < outList.size(); i++) {
            OutsourceCompany objectMap = outList.get(i);
            outs.add(objectMap.getCompany());
        }
        for (int j = 0; j < custIds.length; j++) {
            Map<String, Object> map = new HashMap<>();
            map.put("custId", custIds[j]);
            // 创建一个存储当前查询客户的所有委外过的公司
            Set<String> setStr = new HashSet<>();
            // 根据传入custId 查询委外记录数据
            List<OutsourceAllocationRecord> lists = outsourceAllocationRecordMapper.loadRecordByMaps(map);
            for (int i = 0; i < lists.size(); i++) {
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
            String wgStr = "";
            int s = 0;
            if (setStr.size() > 0) {
                for (String str : setStr) {
                    s++;
                    if (s == setStr.size()) {
                        wgStr += str;
                    } else {
                        wgStr += str + ",";
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
            map.put("wg", wgStr);
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
    public Map<String, Object> importInOutsourceExcel(List<HashMap<String, Object>> listMap) {
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
        List<Map<String, Object>> allList = outsourceAllocationRecordMapper.selectAllList();
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
        // 写出所有表数据
        for (int i = 0; i < allList.size(); i++) {
            row = sheet.createRow(i + 1);
            Map map = allList.get(i);
            if (map != null) {
                row.createCell(0).setCellValue(map.get("name").toString());
                row.createCell(1).setCellValue(map.get("custId").toString());
                row.createCell(2).setCellValue(map.get("telNumber").toString());
                row.createCell(3).setCellValue(map.get("ious").toString());
                row.createCell(4).setCellValue(map.get("totalAmount").toString());
                row.createCell(5).setCellValue(map.get("nextRefundDay").toString());
                row.createCell(6).setCellValue(map.get("amountOverride").toString());
                row.createCell(7).setCellValue(map.get("overduePrincipal").toString());
                row.createCell(8).setCellValue(map.get("overdueAccrual").toString());
                row.createCell(9).setCellValue(map.get("defaultInterest").toString());
                row.createCell(10).setCellValue(map.get("ageCd").toString());
                row.createCell(11).setCellValue(map.get("overdue").toString());
                row.createCell(12).setCellValue(map.get("netLendingPlatform").toString());
                row.createCell(13).setCellValue(map.get("isSoleProprietorship").toString());
                row.createCell(14).setCellValue(map.get("dcaDistribution").toString());
                row.createCell(15).setCellValue(map.get("theCaseDistribution").toString());
                row.createCell(16).setCellValue(map.get("turnOverDay").toString());
                row.createCell(17).setCellValue(map.get("productName").toString());
                row.createCell(18).setCellValue(map.get("totalAging").toString());
                row.createCell(19).setCellValue(map.get("contractCreateDate").toString());
                row.createCell(20).setCellValue(map.get("remarks").toString());
            }
        }
        String fileName = "委外大总表_" + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".xlsx";
        // 文件下载到浏览器
        ExcelUtils.writeFileToClient(fileName, wb, request, response);
    }

    @Override
    public Map<String, Object> importAmountAndRecordExcel(List<HashMap<String, Object>> listMap) {
        Date date = new Date(), transfer, thePushDay;
        Map<String, Object> resultMap = new HashMap<>();
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
                amountHis.setRemarks(remarks);
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
                amount.setRemarks(remarks);
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
                record.setRemarks(remarks);
                outsourceAllocationRecordMapper.insert(record);

                // 从新借据表中剔除移交案件
                Map<String, Object> objectMap = new HashMap<>();
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
