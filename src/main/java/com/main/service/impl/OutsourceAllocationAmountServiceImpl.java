package com.main.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.main.mapper.*;
import com.main.model.*;
import com.main.service.IOutsourceAllocationAmountService;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.DateUtils;
import com.sys.commons.utils.ExcelUtils;
import com.sys.commons.utils.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
public class OutsourceAllocationAmountServiceImpl extends ServiceImpl<OutsourceAllocationAmountMapper, OutsourceAllocationAmount> implements IOutsourceAllocationAmountService {

    @Autowired
    private OutsourceAllocationAmountMapper outsourceAllocationAmountMapper;
    @Autowired
    private OutsourceAllocationAmountHisMapper outsourceAllocationAmountHisMapper;
    @Autowired
    private OutsourceAllocationRecordMapper outsourceAllocationRecordMapper;
    @Autowired
    private OutsourceAllocationRepaymentMapper OutsourceAllocationRepaymentMapper;
    @Autowired
    private DataOnStockMapper dataOnStockMapper;
    @Autowired
    private NewAddIousMapper newAddIousMapper;

    @Override
    public void selectPageInfo(PageInfo pageInfo) {
        Page<Map<String, Object>> page = new Page<>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<Map<String, Object>> list = outsourceAllocationAmountMapper.selectPageInfo(page, pageInfo.getCondition());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    /**
     * 委外余额更新
     *
     * @param listMaps
     */
    @Override
    @Transactional
    public void importInOutsourceAmountExcel(List<HashMap<String, Object>> listMaps) {
        // 0.初始操作,确认新增借据
        List<HashMap<String, Object>> listMap = new ArrayList<>();
        initNewIous(listMap, listMaps);
        // 1.删除已经到期的委外退崔数据
        // deleteOutsourceAmountOverdue();
        // 2.更新当前上传的数据
        Map<String, Object> map;
        List<OutsourceAllocationAmount> amountList;
        // 创建一个存储当前查询客户的所有委外过的公司
        Set<String> setStr = new HashSet<>();
        Date date = new Date();
        // 只跑新借据以外的数据
        for (int i = 0; i < listMap.size(); i++) {
            Map maps = listMap.get(i);
            if (maps != null) {
                map = new HashMap<>();
                String name = maps.get("姓名").toString();
                String custId = maps.get("证件号码").toString();
                String telNumber = maps.get("手机号码").toString();
                String ious = maps.get("合同编号").toString();
                String nowCollectionAmountStr = maps.get("最新逾期欠款金额").toString();
                String nowAgecdStr = maps.get("最新账龄").toString();
                setStr.add(ious);
                map.put("ious", ious);
                map.put("nowCollectionAmount", 0);
                map.put("isNotList", "Y");
                amountList = outsourceAllocationAmountMapper.loadOutsourceAmountByMaps(map);
                OutsourceAllocationAmountHis amountHis;
                if (amountList != null && amountList.size() > 0) { // 余额表中数据是否存在(存在的一定是委外的数据)
                    // 更新余额表数据
                    OutsourceAllocationAmount amount = amountList.get(0);
                    amount.setLastAgecd(amount.getNowAgecd());
                    amount.setLastCollectionAmount(amount.getNowCollectionAmount());
                    Integer nowAgecd = null;
                    if (StringUtils.isNumber(nowAgecdStr)) {
                        nowAgecd = Integer.parseInt(nowAgecdStr);
                    }
                    // 当天导入最新的逾期金额
                    double nowCollectionAmount = 0;
                    if (StringUtils.isNumber(nowCollectionAmountStr)) {
                        nowCollectionAmount = Double.parseDouble(nowCollectionAmountStr);
                    }
                    amount.setNowAgecd(nowAgecd);
                    amount.setNowCollectionAmount(nowCollectionAmount);
                    amount.setUpdateTime(date);
                    outsourceAllocationAmountMapper.updateById(amount);
                    amountHis = outsourceAllocationAmountHisMapper.selectOneById(amount.getId());
                    if (amountHis != null) {    //余额历史表中数据存在
                        // 更新余额全量表数据
                        amountHis.setLastAgecd(amount.getNowAgecd());
                        amountHis.setLastCollectionAmount(amount.getNowCollectionAmount());
                        amountHis.setNowAgecd(nowAgecd);
                        amountHis.setNowCollectionAmount(nowCollectionAmount);
                        amountHis.setUpdateTime(date);
                        outsourceAllocationAmountHisMapper.updateById(amountHis);
                    }
                    // 计算还款(今天-昨天 < 0 ,表示有还款)
                    if (nowCollectionAmount - amount.getLastCollectionAmount() < 0) {
                        OutsourceAllocationRepayment repayment = new OutsourceAllocationRepayment();
                        repayment.setName(name);
                        repayment.setCustId(custId);
                        repayment.setTelNumber(telNumber);
                        repayment.setIous(ious);
                        // 还款金额(昨天减去今天的余额)
                        repayment.setCurAmount(amount.getLastCollectionAmount() - nowCollectionAmount);
                        // 用来计算还款日,还款日期不能大于退案日期
                        Date rd = DateUtils.getSomeday(new Date(), -1);
                        if (DateUtils.differentDaysByMillisecond(amount.getThePushDay(), rd) > 0) {
                            rd = amount.getThePushDay();
                        }
                        repayment.setRepaymentDate(rd);
                        repayment.setTransfer(amount.getTransfer());
                        repayment.setHandOverAgecd(amount.getTransferAgecd());
                        repayment.setHandOverAmount(amount.getLastCollectionAmount());  // 昨日催收金额
                        repayment.setCompany(amount.getCompany());
                        repayment.setCreatDate(new Date()); // 创建时间_当前系统时间
                        repayment.setRemarks(amount.getRemarks());
                        repayment.setIsSumRefund(1);    //部分还款
                        OutsourceAllocationRepaymentMapper.insert(repayment);
                    }
                } else {
                    // 查询是否是委外案件
                    map = new HashMap<>();
                    map.put("custId", custId);
                    map.put("ious", ious);
                    map.put("orderStr", "TURN_OVER_DAY");   // 移交日期
                    map.put("orderAsc", "DESC");
                    List<OutsourceAllocationRecord> mapList = outsourceAllocationRecordMapper.loadRecordByMaps(map);
                    if (mapList != null && mapList.size() > 0) { // 只要不为空,当前客户就是委外客户,且已经分配给委外公司
                        OutsourceAllocationRecord record = null;
                        for (int j = 0; j < mapList.size(); j++) {
                            OutsourceAllocationRecord oar = mapList.get(j);
                            // 取得借据号相同的
                            if (oar.getIous().equals(ious)) {
                                record = oar;
                                break;
                            }
                        }
                        if (record == null) {
                            record = mapList.get(0);
                        }
                        // 写入当前数据
                        OutsourceAllocationAmount amount = new OutsourceAllocationAmount();
                        amountHis = new OutsourceAllocationAmountHis();
                        String jsAgcedStr = record.getAgeCd();
                        String company = record.getDcaDistribution();     // 公司
                        double nowCollectionAmount = 0;
                        if (StringUtils.isNotBlank(nowCollectionAmountStr)) {
                            nowCollectionAmount = Double.parseDouble(nowCollectionAmountStr);
                        }
                        // nowAgecd - 新账龄, jsAgced - 入催账龄
                        int nowAgecd = 0, jsAgced = 0;
                        if (StringUtils.isNumber(nowAgecdStr)) {
                            nowAgecd = Integer.parseInt(nowAgecdStr);
                        }
                        if (StringUtils.isNumber(jsAgcedStr)) {
                            jsAgced = Integer.parseInt(jsAgcedStr);
                        }
                        // 移交日期
                        Date transfer = DateUtils.strToDate(record.getTurnOverDay(), "yyyyMMdd");
                        Date thePushDay;  // 退催日期计算 (移交账龄M1,2的委外周期1月减1天,移交账龄M3以上的委外周期2月减1天)
                        if (jsAgced > 0 && jsAgced <= 2) {
                            thePushDay = DateUtils.getSomeMouthDay(transfer, 1, 0);
                        } else {
                            thePushDay = DateUtils.getSomeMouthDay(transfer, 2, 0);
                        }
                        // 根据身份证号查询当前客户的所有已经在委外中的所有借据
                        map = new HashMap<>();
                        map.put("custId", custId);
                        amountList = outsourceAllocationAmountMapper.loadOutsourceAmountByMaps(map);
                        for (int j = 0; j < amountList.size(); j++) {
                            OutsourceAllocationAmount qaa = amountList.get(j);
                            // 计算退催日(取最大)
                            if (DateUtils.differentDaysByMillisecond(thePushDay, qaa.getThePushDay()) > 0) {
                                thePushDay = qaa.getThePushDay();
                            }
                            // 移交日计算(如果存在借据号相同,金额为0 的数据,当前借据移交日增加一天) ???
                            if (qaa.getIous().equals(ious) && qaa.getNowCollectionAmount() <= 0) {
                                transfer = DateUtils.getSomeday(transfer, 1);
                            }
                        }

                        amountHis.setName(name);
                        amountHis.setCustId(custId);
                        amountHis.setTelNumber(telNumber);
                        amountHis.setIous(ious);
                        amountHis.setNowCollectionAmount(nowCollectionAmount);
                        amountHis.setNowAgecd(nowAgecd);
                        amountHis.setTransferAgecd(jsAgced);
                        amountHis.setTransfer(transfer);
                        amountHis.setThePushDay(thePushDay);
                        amountHis.setCompany(company);
                        amountHis.setCreatDate(date);
                        outsourceAllocationAmountHisMapper.insertInfo(amountHis);

                        amount.setId(amountHis.getId());
                        amount.setName(name);
                        amount.setCustId(custId);
                        amount.setTelNumber(telNumber);
                        amount.setIous(ious);
                        amount.setNowCollectionAmount(nowCollectionAmount);
                        amount.setNowAgecd(nowAgecd);
                        amount.setTransferAgecd(jsAgced);
                        amount.setTransfer(transfer);
                        amount.setThePushDay(thePushDay);
                        amount.setCompany(company);
                        amount.setCreatDate(date);
                        outsourceAllocationAmountMapper.insertInfo(amount);
                    }
                }
            }
        }
        // 3.完全退催数据更新
        map = new HashMap<>();
        map.put("nowCollectionAmount", 0);
        List<OutsourceAllocationAmount> amountLists = outsourceAllocationAmountMapper.loadOutsourceAmountByMaps(map);
        for (int i = 0; i < amountLists.size(); i++) {
            OutsourceAllocationAmount amount = amountLists.get(i);
            // 如果不存在,就更新
            if (!setStr.contains(amount.getIous())) {

                amount.setLastAgecd(amount.getNowAgecd());
                amount.setLastCollectionAmount(amount.getNowCollectionAmount());
                amount.setNowAgecd(0);
                amount.setNowCollectionAmount(0d);
                amount.setUpdateTime(date);
                outsourceAllocationAmountMapper.updateById(amount);

                OutsourceAllocationAmountHis amountHis = outsourceAllocationAmountHisMapper.selectOneById(amount.getId());
                amountHis.setLastAgecd(amount.getNowAgecd());
                amountHis.setLastCollectionAmount(amount.getNowCollectionAmount());
                amountHis.setNowAgecd(0);
                amountHis.setNowCollectionAmount(0d);
                amountHis.setUpdateTime(date);
                outsourceAllocationAmountHisMapper.updateById(amountHis);

                OutsourceAllocationRepayment repayment = new OutsourceAllocationRepayment();
                repayment.setName(amount.getName());
                repayment.setCustId(amount.getCustId());
                repayment.setTelNumber(amount.getTelNumber());
                repayment.setIous(amount.getIous());
                // 还款金额(昨天的余额)
                repayment.setCurAmount(amount.getLastCollectionAmount());
                // 用来计算还款日,还款日期不能大于退案日期
                Date rd = DateUtils.getSomeday(new Date(), -1);
                if (DateUtils.differentDaysByMillisecond(amount.getThePushDay(), rd) > 0) {
                    rd = amount.getThePushDay();
                }
                repayment.setRepaymentDate(rd);
                repayment.setTransfer(amount.getTransfer());
                repayment.setHandOverAgecd(amount.getTransferAgecd());
                repayment.setHandOverAmount(amount.getLastCollectionAmount());  // 昨日催收金额
                repayment.setCompany(amount.getCompany());
                repayment.setCreatDate(date); // 创建时间_当前系统时间
                repayment.setIsSumRefund(0);    //全额还款
                repayment.setRemarks(amount.getRemarks());
                OutsourceAllocationRepaymentMapper.insert(repayment);
            }
        }

        // 4.退催日复核计算
        amountLists = outsourceAllocationAmountMapper.loadOutsourceAmountAllLists();
        for (int i = 0; i < amountLists.size(); i++) {
            OutsourceAllocationAmount amount = amountLists.get(i);
            // 查询当前客户的所有借据(根据身份证号)
            map = new HashMap<>();
            map.put("custId", amount.getCustId());
            amountList = outsourceAllocationAmountMapper.loadOutsourceAmountByMaps(map);
            Date thePushDay = null;     //退催时间
            int isUpdate = 0; // 判断时间更新次数
            List<OutsourceAllocationAmount> jsList = new ArrayList<>();
            for (int j = 0; j < amountList.size(); j++) {
                OutsourceAllocationAmount qaa = amountList.get(j);
                Date nowThePushDay;
                // 逾期金额大于 0 ,且不是留案案件
                if (qaa.getNowCollectionAmount() != null && qaa.getNowCollectionAmount() > 0
                        && !"Y".equals(qaa.getIsLeaveCase())) {
                    // 从新计算退催日(移交账龄M1,2的委外周期1月减1天,移交账龄M3以上的委外周期2月减1天)
                    if (qaa.getTransferAgecd() > 0 && qaa.getTransferAgecd() <= 2) {
                        nowThePushDay = DateUtils.getSomeMouthDay(qaa.getTransfer(), 1, 0);
                    } else {
                        nowThePushDay = DateUtils.getSomeMouthDay(qaa.getTransfer(), 2, 0);
                    }
                    // date2-date1 > 0 记录最大退催时间
                    if (DateUtils.differentDaysByMillisecond(thePushDay, nowThePushDay) > 0) {
                        thePushDay = qaa.getThePushDay();
                        isUpdate++;
                    }
                }
                jsList.add(qaa);
            }
            if (thePushDay != null && isUpdate >= 2) {
                // 复核过的退催日更新
                for (int j = 0; j < jsList.size(); j++) {
                    OutsourceAllocationAmount qaa = jsList.get(j);
                    qaa.setThePushDay(thePushDay);
                    qaa.setUpdateTime(date);
                    outsourceAllocationAmountMapper.updateById(qaa);
                    // 更新 his 表数据
                    OutsourceAllocationAmountHis amountHis = outsourceAllocationAmountHisMapper.selectOneById(qaa.getId());
                    amountHis.setThePushDay(thePushDay);
                    amountHis.setUpdateTime(date);
                    outsourceAllocationAmountHisMapper.updateById(amountHis);
                }
            } else {
                // 所有的借据都已出催 ,暂时不做任何操作,等待出催日之后使之出催
            }
        }
        // 5.再次删除已经到期的委外退崔数据(退催日复核完成以后会存在部分退催的情况)
        deleteOutsourceAmountOverdue();
    }

    /**
     * 根据昨天存量借据清单找出今日入催新借据
     *
     * @param listMap
     * @param listMaps
     */
    private void initNewIous(List<HashMap<String, Object>> listMap, List<HashMap<String, Object>> listMaps) {
        // 1.初始准备,查询所有昨天借据
        Date date = new Date();
        List<DataOnStock> dataOnStocks = dataOnStockMapper.selectAllList();
        // 2.删除昨天借据
        dataOnStockMapper.clearAllData();
        List<String> iousStr = new ArrayList<>(), nowIousStr = new ArrayList<>(), allGeneralList = new ArrayList<>();
        for (int i = 0; i < dataOnStocks.size(); i++) {
            DataOnStock stock = dataOnStocks.get(i);
            iousStr.add(stock.getIous());
        }
        // 查询所有新入催的清单
        Map<String, NewAddIous> addIousMap = new HashMap<>();
        List<NewAddIous> newAddIouses = newAddIousMapper.loadAllNewAddIous();
        for (int i = 0; i < newAddIouses.size(); i++) {
            NewAddIous addIous = newAddIouses.get(i);
            addIousMap.put(addIous.getIous(), addIous);
            nowIousStr.add(addIous.getIous());
        }
        // 查询委外大总表
        List<OutsourceAllocationRecord> outsourceAllocationRecords = outsourceAllocationRecordMapper.loadRecordALLCustData();
        for (int i = 0; i < outsourceAllocationRecords.size(); i++) {
            OutsourceAllocationRecord record = outsourceAllocationRecords.get(i);
            allGeneralList.add(record.getCustId());
        }
        // 定义一个存放当天借据ious的list
        List<String> iouss = new ArrayList<>();
        for (int i = 0; i < listMaps.size(); i++) {
            HashMap<String, Object> map = listMaps.get(i);
            String nowCollectionAmountStr = map.get("最新逾期欠款金额").toString();
            String nowAgecdStr = map.get("最新账龄").toString();
            String custId = map.get("证件号码").toString();
            String ious = map.get("合同编号").toString();
            String productName = map.get("产品名称").toString();
            String netLendingPlatform = map.get("二级渠道ID").toString();
            String overdueStr = map.get("最新逾期天数").toString();
            String overduePrincipalStr = map.get("本期应还本金").toString();

            // 历史存量借据不存在的话
            if (null != iousStr && iousStr.size() > 0) {
                Integer nowAgecd = 0, overdue = 0;
                double nowCollectionAmount = 0, overduePrincipal = 0;
                if (StringUtils.isNumber(nowAgecdStr)) {
                    nowAgecd = Integer.parseInt(nowAgecdStr);
                }
                if (StringUtils.isNumber(nowCollectionAmountStr)) {
                    nowCollectionAmount = Double.parseDouble(nowCollectionAmountStr);
                }
                if (StringUtils.isNumber(overdueStr)) {
                    overdue = Integer.parseInt(overdueStr);
                }
                if (StringUtils.isNumber(overduePrincipalStr)) {
                    overduePrincipal = Double.parseDouble(overduePrincipalStr);
                }

                OutsourceAllocationRecord record = null;
                // 从已有新借据中剔除,
                if (nowIousStr.contains(ious)) {
                    record = outsourceAllocationRecordMapper.selectMaxTurnByCustId(custId);
                    // 更新当前新借据
                    NewAddIous addIous = addIousMap.get(ious);
                    addIous.setNowAgecd(nowAgecd);
                    addIous.setNowCollectionAmount(nowCollectionAmount);
                    addIous.setOverdue(overdue);
                    addIous.setOverduePrincipal(overduePrincipal);
                    addIous.setUpdateTime(date);
                    addIous.setCompany(record.getDcaDistribution());
                    newAddIousMapper.updateById(addIous);
                } else {
                    // 从当天借据中剔除新入催, 当天的借据在昨天数据中不存在,则表示新入催
                    if (iousStr.contains(ious)) {
                        listMap.add(map);
                    } else {
                        // 判断当前客户是否存在于委外大总表中,如果不存在则不认为是委外借据
                        if (allGeneralList.contains(custId)) {
                            record = outsourceAllocationRecordMapper.selectMaxTurnByCustId(custId);
                            if(null != record){
                                // 添加到数据库
                                NewAddIous addIous = new NewAddIous();
                                addIous.setCustId(custId);
                                addIous.setIous(ious);
                                addIous.setNowAgecd(nowAgecd);
                                addIous.setNowCollectionAmount(nowCollectionAmount);
                                addIous.setCompany(record.getDcaDistribution());
                                addIous.setOverdue(overdue);
                                addIous.setProductName(productName);
                                addIous.setNetLendingPlatform(netLendingPlatform);
                                addIous.setOverduePrincipal(overduePrincipal);
                                addIous.setUpdateTime(date);
                                addIous.setCreatDate(date);
                                newAddIousMapper.insert(addIous);
                            }
                        }
                    }
                }
            } else {
                listMap.add(map);
            }
            // 3.当天借据存库
            addCurrStock(ious, custId);
            iouss.add(ious);
        }
        // 新借据还清的出催处理
        newAddIouses = newAddIousMapper.loadAllNewAddIous();
        for (int i = 0; i < newAddIouses.size(); i++) {
            NewAddIous addIous = newAddIouses.get(i);
            if (!iouss.contains(addIous.getIous())) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", addIous.getId());
                newAddIousMapper.deleteByMap(map);
            }
        }
    }

    private void addCurrStock(String ious, String custId) {
        DataOnStock stock = new DataOnStock();
        stock.setIous(ious);
        stock.setCustId(custId);
        dataOnStockMapper.insert(stock);
    }

    private void deleteOutsourceAmountOverdue() {
        Map<String, Object> map = new HashMap<>();
        map.put("thePushDay", DateUtils.dateToString(new Date(), "yyyy-MM-dd"));
        outsourceAllocationAmountMapper.deleteOutsourceAmountOverdue(map);
    }

    @Override
    public void exportInAllAmountByMap(Map<String, Object> amountMap, HttpServletRequest request, HttpServletResponse response) {
        List<OutsourceAllocationAmount> amountLists = outsourceAllocationAmountMapper.loadCommonAmountByMaps(amountMap);
        Map<String, List<OutsourceAllocationAmount>> singleMap = new HashMap<>();
        OutsourceAllocationAmount amount;
        Workbook wb = new XSSFWorkbook();
        printAllAmountExp("全部余额数据", amountLists, wb);
        for (int i = 0; i < amountLists.size(); i++) {
            amount = amountLists.get(i);
            List<OutsourceAllocationAmount> amounts = singleMap.get(amount.getCompany());
            if (amounts == null) {
                amounts = new ArrayList<>();
            }
            amounts.add(amount);
            singleMap.put(amount.getCompany(), amounts);
        }
        Iterator<String> iter = singleMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            List<OutsourceAllocationAmount> repayments = singleMap.get(key);
            printAllAmountExp(key, repayments, wb);
        }
        String fileName = "余额表_" + DateUtils.dateToString(new Date(), "yyyyMMdd_HHmmss") + ".xlsx";
        // 文件下载到浏览器
        ExcelUtils.writeFileToClient(fileName, wb, request, response);
    }

    /**
     * 案件留案更新
     * @param allocationAmount
     */
    @Override
    public void updateAmountByCustId(OutsourceAllocationAmount allocationAmount) {
        Date date = new Date();
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("custId", allocationAmount.getCustId());
        List<OutsourceAllocationAmount> amountLists = outsourceAllocationAmountMapper.loadCommonAmountByMaps(objectMap);
        for (int i = 0; i < amountLists.size(); i++) {
            OutsourceAllocationAmount amount = amountLists.get(i);
            amount.setTransfer(allocationAmount.getTransfer());
            amount.setThePushDay(allocationAmount.getThePushDay());
            amount.setUpdateTime(date);
            amount.setIsLeaveCase(allocationAmount.getIsLeaveCase());
            amount.setLeaveCaseDate(date);
            // 借据号相同的借据更新其公司
            if (amount.getIous().equals(allocationAmount.getIous())) {
                amount.setCompany(allocationAmount.getCompany());
            }
            outsourceAllocationAmountMapper.updateById(amount);

            OutsourceAllocationAmountHis amountHis = outsourceAllocationAmountHisMapper.selectOneById(amount.getId());
            amountHis.setTransfer(allocationAmount.getTransfer());
            amountHis.setThePushDay(allocationAmount.getThePushDay());
            amountHis.setUpdateTime(date);
            amountHis.setIsLeaveCase(allocationAmount.getIsLeaveCase());
            amountHis.setLeaveCaseDate(date);
            // 借据号相同的借据更新其公司
            if (amountHis.getIous().equals(allocationAmount.getIous())) {
                amountHis.setCompany(allocationAmount.getCompany());
            }
            outsourceAllocationAmountHisMapper.updateById(amountHis);
        }
    }

    @Override
    public Map addAmountData(OutsourceAllocationAmount amount) {
        Map maps = new HashMap(), map;
        // 查询大总表
        map = new HashMap<>();
        map.put("custId", amount.getCustId());
        map.put("ious", amount.getIous());
        List<OutsourceAllocationRecord> mapList = outsourceAllocationRecordMapper.loadRecordByMaps(map);
        if (null != mapList && mapList.size() > 0) {
            // 和现在客户数据保持统一,查询余额表是否存在
            map.put("nowCollectionAmount", 0);
            map.put("isNotList", "Y");
            List<OutsourceAllocationAmount> amount1 = outsourceAllocationAmountMapper.loadOutsourceAmountByMaps(map);
            if (amount1.size() > 0) {
                maps.put("msg", "当前借据已存在!");
            } else {
                map = new HashMap<>();
                map.put("custId", amount.getCustId());
                map.put("isNotList", "Y");
                List<OutsourceAllocationAmount> amount2 = outsourceAllocationAmountMapper.loadOutsourceAmountByMaps(map);
                Date date = new Date();
                if (amount2.size() > 0) {
                    OutsourceAllocationAmount amount3 = amount2.get(0);
                    amount.setThePushDay(amount3.getThePushDay());
                    amount.setCreatDate(date);
                    amount.setCompany(amount3.getCompany());
                    amount.setName(amount3.getName());
                    amount.setTelNumber(amount3.getTelNumber());
                }
                amount.setCreatDate(date);
                outsourceAllocationAmountMapper.insertInfo(amount);

                OutsourceAllocationAmountHis amountHis = new OutsourceAllocationAmountHis();
                amountHis.setId(amount.getId());
                amountHis.setName(amount.getName());
                amountHis.setCustId(amount.getCustId());
                amountHis.setTelNumber(amount.getTelNumber());
                amountHis.setIous(amount.getIous());
                amountHis.setNowCollectionAmount(amount.getNowCollectionAmount());
                amountHis.setTransferAgecd(amount.getTransferAgecd());
                amountHis.setNowAgecd(amount.getNowAgecd());
                amountHis.setTransfer(amount.getTransfer());
                amountHis.setThePushDay(amount.getThePushDay());
                amountHis.setCompany(amount.getCompany());
                amountHis.setIsLeaveCase(amount.getIsLeaveCase());
                amountHis.setCreatDate(date);
                outsourceAllocationAmountHisMapper.insertInfo(amountHis);
                maps.put("code", "200");
            }
        } else {
            maps.put("msg", "当前借据不在委外大总表!");
        }
        return maps;
    }

    @Override
    public List<OutsourceAllocationAmount> loadAmountByMaps(Map<String, Object> amountMap) {
        return outsourceAllocationAmountMapper.loadCommonAmountByMaps(amountMap);
    }

    private void printAllAmountExp(String sheetName, List<OutsourceAllocationAmount> amountList, Workbook wb) {
        Sheet sheet = wb.createSheet(sheetName);
        CellStyle cellStyle = wb.createCellStyle();
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("序号");
        row.createCell(1).setCellValue("姓名");
        row.createCell(2).setCellValue("证件号码");
        row.createCell(3).setCellValue("手机号");
        row.createCell(4).setCellValue("借据号码");
        row.createCell(5).setCellValue("最新逾期催收金额");
        row.createCell(6).setCellValue("最新账龄");
        row.createCell(7).setCellValue("移交日期");
        row.createCell(8).setCellValue("公司");
        OutsourceAllocationAmount amount;
        for (int i = 0; i < amountList.size(); i++) {
            amount = amountList.get(i);
            row = sheet.createRow(i + 1);
            if (amount != null) {
                if (DateUtils.dateToString(new Date(), "yyyy-MM-dd").equals(DateUtils.dateToString(amount.getCreatDate(), "yyyy-MM-dd"))) {
                    cellStyle.setFillForegroundColor(IndexedColors.YELLOW1.getIndex());
                    cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
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
                    cell.setCellValue(amount.getCompany());
                } else {
                    row.createCell(0).setCellValue(amount.getIous() + DateUtils.dateToString(amount.getTransfer(), "yyyyMMdd"));
                    row.createCell(1).setCellValue(amount.getName());
                    row.createCell(2).setCellValue(amount.getCustId());
                    row.createCell(3).setCellValue(amount.getTelNumber());
                    row.createCell(4).setCellValue(amount.getIous());
                    row.createCell(5).setCellValue(amount.getNowCollectionAmount());
                    row.createCell(6).setCellValue(amount.getNowAgecd());
                    row.createCell(7).setCellValue(DateUtils.dateToString(amount.getTransfer(), "yyyy-MM-dd"));
                    row.createCell(8).setCellValue(amount.getCompany());
                }
            }
        }
    }

}
