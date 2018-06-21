package com.main.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.main.mapper.OutsourceAllocationAmountHisMapper;
import com.main.model.OutsourceAllocationAmountHis;
import com.main.service.OutsourceAllocationAmountHisService;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.DateUtils;
import com.sys.commons.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jiewai
 * @since 2018-05-22
 */
@Service
public class OutsourceAllocationAmountHisServiceImpl extends ServiceImpl<OutsourceAllocationAmountHisMapper, OutsourceAllocationAmountHis> implements OutsourceAllocationAmountHisService {

    @Autowired
    private OutsourceAllocationAmountHisMapper outsourceAllocationAmountHisMapper;
    @Override
    public void selectPageInfo(PageInfo pageInfo) {
        Page<Map<String, Object>> page = new Page<>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<Map<String, Object>> list = outsourceAllocationAmountHisMapper.selectPageInfo(page, pageInfo.getCondition());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    @Override
    public void loadAmountHisByMaps(Map<String, Object> condition, HttpServletRequest request, HttpServletResponse response) {
        List<OutsourceAllocationAmountHis> mapList = outsourceAllocationAmountHisMapper.loadAmountHisByMaps(condition);
        // 写入文件数据解析
        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("sheet1");
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("姓名");
        row.createCell(1).setCellValue("身份证号");
        row.createCell(2).setCellValue("手机号");
        row.createCell(3).setCellValue("借据号码");
        row.createCell(4).setCellValue("最新催收金额");
        row.createCell(5).setCellValue("最新账龄");
        row.createCell(6).setCellValue("移交日");
        row.createCell(7).setCellValue("退案日期");
        row.createCell(8).setCellValue("公司");
        row.createCell(9).setCellValue("备注");
        for (int i = 0; i < mapList.size(); i++) {
            row = sheet.createRow(i + 1);
            OutsourceAllocationAmountHis amountHis = mapList.get(i);
            row.createCell(0).setCellValue(amountHis.getName());
            row.createCell(1).setCellValue(amountHis.getCustId());
            row.createCell(2).setCellValue(amountHis.getTelNumber());
            row.createCell(3).setCellValue(amountHis.getIous());
            row.createCell(4).setCellValue(amountHis.getNowCollectionAmount());
            row.createCell(5).setCellValue(amountHis.getNowAgecd());
            row.createCell(6).setCellValue(DateUtils.dateToString(amountHis.getTransfer(), "yyyy-MM-dd"));
            row.createCell(7).setCellValue(DateUtils.dateToString(amountHis.getThePushDay(), "yyyy-MM-dd"));
            row.createCell(8).setCellValue(amountHis.getCompany());
            row.createCell(9).setCellValue(amountHis.getRemarks());
        }
        String fileName = "退催清单_" + DateUtils.dateToString(new Date(), "yyyyMMdd_HHmmss") + ".xlsx";
        // 文件下载到浏览器
        ExcelUtils.writeFileToClient(fileName, wb, request, response);
    }

    @Override
    public List<OutsourceAllocationAmountHis> loadAmountHisByMaps(Map<String, Object> condition) {
        return outsourceAllocationAmountHisMapper.loadAmountHisByMaps(condition);
    }

    @Override
    public OutsourceAllocationAmountHis loadAmountHisById(Integer id) {
        return outsourceAllocationAmountHisMapper.selectOneById(id);
    }
}
