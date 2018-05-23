package com.main.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.main.mapper.OutsourceCompanyMapper;
import com.main.model.OutsourceAllocationRecord;
import com.main.mapper.OutsourceAllocationRecordMapper;
import com.main.service.IOutsourceAllocationRecordService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.ExcelUtils;
import com.sys.commons.utils.DateUtils;
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
    private OutsourceCompanyMapper companyMapper;

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
        List<Map<String, Object>> outList = companyMapper.selectAllList();
        List<String> outs = new ArrayList<>();
        for (int i = 0; i < outList.size(); i++) {
            Map<String, Object> objectMap = outList.get(i);
            outs.add(objectMap.get("company").toString());
        }
        for (int j = 0; j < custIds.length; j++) {
            Map<String, Object> map = new HashMap<>();
            map.put("custId", custIds[j]);
            // 创建一个存储当前查询客户的所有委外过的公司
            Set<String> setStr = new HashSet<>();
            // 根据传入custId 查询委外记录数据
            List<Map<String, Object>> lists = outsourceAllocationRecordMapper.selectByMaps(map);
            for (int i = 0; i < lists.size(); i++) {
                Map<String, Object> objectMap = lists.get(i);
                // 包含于所有外包公司之中
                String existStr = objectMap.get("dcaDistribution").toString();
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
    public void importInOutsourceExcel(List<HashMap<String, Object>> listMap) {
        // 1.清除当前表所有数据
        outsourceAllocationRecordMapper.cleanAllData();
        // 2.写入当前读取到的Excel表格中数据
        for (int i = 0; i < listMap.size(); i++) {
            Map map = listMap.get(i);
            if(map.get("证件号码") != null && !"".equals(map.get("证件号码"))){
                OutsourceAllocationRecord record = new OutsourceAllocationRecord();
                record.setName(map.get("姓名") + "");
                record.setCustId(map.get("证件号码") + "");
                record.setTelNumber(map.get("手机号") + "");
                record.setIous(map.get("借据号码") + "");
                record.setTotalAmount(map.get("分期总金额(当前在贷本金）") + "");
                record.setNextRefundDay(map.get("下一还款日") + "");
                record.setAmountOverride(map.get("逾期金额（R）") + "");
                record.setOverduePrincipal(map.get("逾期本金") + "");
                record.setOverdueAccrual(map.get("逾期利息") + "");
                record.setDefaultInterest(map.get("罚息") + "");
                record.setAgeCd(map.get("帐龄（R）") + "");
                record.setOverdue(map.get("逾期天数") + "");
                record.setNetLendingPlatform(map.get("网络贷款平台") + "");
                record.setIsSoleProprietorship(map.get("是否独资") + "");
                record.setDcaDistribution(map.get("委外分配") + "");
                record.setTheCaseDistribution(map.get("案件类型") + "");
                record.setTurnOverDay(map.get("移交日期") + "");
                record.setProductName(map.get("产品名称") + "");
                record.setTotalAging(map.get("分期总期数") + "");
                record.setContractCreateDate(map.get("合同生成日期") + "");
                record.setRemarks(map.get("备注") + "");
                outsourceAllocationRecordMapper.insert(record);
            }
        }
    }

    /**
     * 导出所有表数据
     * @param request
     * @param response
     */
    @Override
    public void downLoadAllDate(HttpServletRequest request, HttpServletResponse response) {
        // 查询所有表数据
        List<Map<String,Object>> allList = outsourceAllocationRecordMapper.selectAllList();
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
            if(map != null){
                row.createCell(0).setCellValue(map.get("name") + "");
                row.createCell(1).setCellValue(map.get("custId") + "");
                row.createCell(2).setCellValue(map.get("telNumber") + "");
                row.createCell(3).setCellValue(map.get("ious") + "");
                row.createCell(4).setCellValue(map.get("totalAmount") + "");
                row.createCell(5).setCellValue(map.get("nextRefundDay") + "");
                row.createCell(6).setCellValue(map.get("amountOverride") + "");
                row.createCell(7).setCellValue(map.get("overduePrincipal") + "");
                row.createCell(8).setCellValue(map.get("overdueAccrual") + "");
                row.createCell(9).setCellValue(map.get("defaultInterest") + "");
                row.createCell(10).setCellValue(map.get("ageCd") + "");
                row.createCell(11).setCellValue(map.get("overdue") + "");
                row.createCell(12).setCellValue(map.get("netLendingPlatform") + "");
                row.createCell(13).setCellValue(map.get("isSoleProprietorship") + "");
                row.createCell(14).setCellValue(map.get("dcaDistribution") + "");
                row.createCell(15).setCellValue(map.get("theCaseDistribution") + "");
                row.createCell(16).setCellValue(map.get("turnOverDay") + "");
                row.createCell(17).setCellValue(map.get("productName") + "");
                row.createCell(18).setCellValue(map.get("totalAging") + "");
                row.createCell(19).setCellValue(map.get("contractCreateDate") + "");
                row.createCell(20).setCellValue(map.get("remarks") + "");
            }
        }
        String fileName = "委外大总表_" + DateUtils.dateToString(new Date(), "yyyyMMdd") + ".xlsx";
        // 文件下载到浏览器
        ExcelUtils.writeFileToClient(fileName, wb, request, response);
    }

}
