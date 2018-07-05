package com.main.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.main.mapper.OutsourceAllocationAmountHisMapper;
import com.main.mapper.OutsourceAllocationAmountMapper;
import com.main.mapper.OutsourceAllocationRecordMapper;
import com.main.model.*;
import com.main.mapper.NewAddIousMapper;
import com.main.service.NewAddIousService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.DateUtils;
import com.sys.commons.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jiewai
 * @since 2018-06-07
 */
@Service
public class NewAddIousServiceImpl extends ServiceImpl<NewAddIousMapper, NewAddIous> implements NewAddIousService {

    @Autowired
    private OutsourceAllocationAmountMapper outsourceAllocationAmountMapper;
    @Autowired
    private OutsourceAllocationAmountHisMapper outsourceAllocationAmountHisMapper;
    @Autowired
    private OutsourceAllocationRecordMapper outsourceAllocationRecordMapper;
    @Autowired
    private NewAddIousMapper newAddIousMapper;

    @Override
    public void selectPageInfo(PageInfo pageInfo) {
        Page<Map<String, Object>> page = new Page<>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<Map<String, Object>> list = newAddIousMapper.selectPageInfo(page, pageInfo.getCondition());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    @Override
    @Transactional
    public void updateIousData(String iouss) {
        String[] iousArray = iouss.split(",");
        StringBuffer stringBuffer = new StringBuffer("");
        for (int i = 0; i < iousArray.length; i++) {
            String ious = iousArray[i];
            if (i == iousArray.length - 1) {
                stringBuffer.append("'" + ious + "'");
            }else{
                stringBuffer.append("'" + ious + "'").append(",");
            }
        }
        // 查找当前选中的所有借据数据
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("iouss", stringBuffer.toString());
        List<NewAddIous> newAddIouses = newAddIousMapper.loadNewAddIousByMaps(objectMap);
        for (int i = 0; i < newAddIouses.size(); i++) {
            NewAddIous addIous = newAddIouses.get(i);
            Date transfer, thePushDayjs = null, maxDate = null, date = new Date(), fmtDate = DateUtils.strToDate(DateUtils.dateToString(date, "yyyy-MM-dd"), "yyyy-MM-dd");
            int jsAgced;
            boolean isAdd = false;
            // 1.更具当前身份证号查询当前客户的所有余额表数据
            objectMap = new HashMap<>();
            objectMap.put("custId", addIous.getCustId());
            // 查询当前客户在余额表是否有借据存在,根据不同情况进行移交日和出催日的计算
            List<OutsourceAllocationAmount> amountList = outsourceAllocationAmountMapper.loadCommonAmountByMaps(objectMap);
            // 查询大总表当前借据
            objectMap = new HashMap<>();
            objectMap.put("custId", addIous.getCustId());
            objectMap.put("ious", addIous.getIous());
            objectMap.put("orderStr", "TURN_OVER_DAY");
            objectMap.put("orderAsc", "DESC");
            List<OutsourceAllocationRecord> recordList;
            // 余额表有当前客户借据再催
            OutsourceAllocationAmount thisAmount = null , custAmount = null, amount1;
            for (int j = 0; null != amountList && j < amountList.size(); j++) {
                amount1 = amountList.get(j);
                if (addIous.getIous().equals(amount1.getIous())) {
                    thisAmount = amount1;
                }
                // 判断当前客户是否金额有不为0的借据
                if (amount1.getNowCollectionAmount() > 0) {
                    custAmount = amount1;
                    if (maxDate == null || DateUtils.differentDaysByMillisecond(maxDate, amount1.getTransfer()) > 0) {
                        maxDate = amount1.getTransfer();
                    }
                    if (thePushDayjs == null || DateUtils.differentDaysByMillisecond(thePushDayjs, amount1.getThePushDay()) > 0) {
                        thePushDayjs = amount1.getThePushDay();
                    }
                }
            }
            // 取得大总表中最大移交日借据
            OutsourceAllocationRecord thisRecord = null;
            if (maxDate == null) {
                maxDate = date;
            }
            if (null != amountList && amountList.size() > 0) {
                /**
                 * 02
                 * 存在当前客户数据
                 * 判断是否有当前借据
                 */
                if (null != thisAmount) {
                    /**
                     *021
                     * 判断是否所有借据都为0
                     */
                    if(null == custAmount){
                        /**
                         * 0211
                         * 没有金额的,新建
                         */
                        isAdd = true;
                        transfer = fmtDate;
                        jsAgced = addIous.getNowAgecd();
                    }else{
                        /**
                         * 0212
                         * 有金额的
                         * 最大移交日或加一天,新建
                         */
                        isAdd = true;
                        if (DateUtils.differentDaysByMillisecond(maxDate, thisAmount.getTransfer()) == 0) {
                            // 当前移交日加一天
                            transfer = DateUtils.getSomeday(thisAmount.getTransfer(), 1);
                        }else{
                            // 取最大移交日
                            transfer = maxDate;
                        }
                        jsAgced = addIous.getNowAgecd();
                    }
                } else {
                    /**
                     * 022
                     * 有当前客户
                     * 没有当前借据
                     * 判断是否所有借据都为0
                     */
                    if(null == custAmount){
                        /**
                         * 0221
                         * 有不为 0 的借据
                         * 存在的新建
                         */
                        isAdd = true;
                        transfer = fmtDate;
                        jsAgced = addIous.getNowAgecd();
                    }else{
                        /**
                         * 0222
                         * 取最大当前
                         */
                        isAdd = true;
                        // 当前移交日加一天
                        // 取最大移交日
                        transfer = maxDate;
                        jsAgced = addIous.getNowAgecd();
                    }
                }
            } else {
                /**
                 * 没有客户的直接添加
                 */
                isAdd = true;
                transfer = fmtDate;
                jsAgced = addIous.getNowAgecd();
            }

            Date thePushDay;  // 退催日期计算 (移交账龄M1,2的委外周期1月减1天,移交账龄M3以上的委外周期2月减1天)
            if (jsAgced > 0 && jsAgced <= 2) {
                thePushDay = DateUtils.getSomeMouthDay(transfer, 1, 0);
            } else {
                thePushDay = DateUtils.getSomeMouthDay(transfer, 2, 0);
            }
            // 取最大退案日
            if (DateUtils.differentDaysByMillisecond(thePushDay, thePushDayjs) > 0) {
                thePushDay = thePushDayjs;
            }
            // 同步退案日期
            for (int j = 0; null != amountList && j < amountList.size(); j++) {
                amount1 = amountList.get(j);
                if (!"Y".equals(amount1.getIsLeaveCase()) && amount1.getNowCollectionAmount() > 0) {
                    amount1.setThePushDay(thePushDay);
                    amount1.setUpdateTime(date);
                    outsourceAllocationAmountMapper.updateById(amount1);
                }
            }
            // 判断大总表数据是否为空
            if (null == thisRecord) {
                objectMap = new HashMap<>();
                objectMap.put("custId", addIous.getCustId());
                objectMap.put("orderStr", "TURN_OVER_DAY");
                objectMap.put("orderAsc", "DESC");
                recordList =  outsourceAllocationRecordMapper.loadRecordByMaps(objectMap);
                thisRecord = recordList.get(0);
            }
            OutsourceAllocationAmountHis amountHis = new OutsourceAllocationAmountHis();
            amountHis.setName(thisRecord.getName());
            amountHis.setCustId(addIous.getCustId());
            amountHis.setTelNumber(thisRecord.getTelNumber());
            amountHis.setIous(addIous.getIous());
            amountHis.setNowCollectionAmount(addIous.getNowCollectionAmount());
            amountHis.setNowAgecd(addIous.getNowAgecd());
            amountHis.setTransferAgecd(addIous.getNowAgecd());
            amountHis.setTransfer(transfer);
            amountHis.setThePushDay(thePushDay);
            amountHis.setCompany(thisRecord.getDcaDistribution());
            amountHis.setTransferAmount(addIous.getOverduePrincipal());
            amountHis.setCreatDate(date);
            amountHis.setRemarks("SYS_" + DateUtils.dateToString(date, "yyyy-MM-dd") + "_新增");
            outsourceAllocationAmountHisMapper.insertInfo(amountHis);

            // 添加到余额表
            OutsourceAllocationAmount amount = new OutsourceAllocationAmount();
            amount.setId(amountHis.getId());
            amount.setName(thisRecord.getName());
            amount.setCustId(addIous.getCustId());
            amount.setTelNumber(thisRecord.getTelNumber());
            amount.setIous(addIous.getIous());
            amount.setNowCollectionAmount(addIous.getNowCollectionAmount());
            amount.setNowAgecd(addIous.getNowAgecd());
            amount.setTransferAgecd(addIous.getNowAgecd());
            amount.setTransfer(transfer);
            amount.setThePushDay(thePushDay);
            amount.setCompany(thisRecord.getDcaDistribution());
            amount.setTransferAmount(addIous.getOverduePrincipal());
            amount.setCreatDate(date);
            amount.setRemarks("SYS_" + DateUtils.dateToString(date, "yyyy-MM-dd") + "_新增");
            outsourceAllocationAmountMapper.insertInfo(amount);

            if (isAdd) {
                // 添加到大总表
                OutsourceAllocationRecord allocationRecord = new OutsourceAllocationRecord();
                allocationRecord.setName(thisRecord.getName());
                allocationRecord.setCustId(addIous.getCustId());
                allocationRecord.setTelNumber(thisRecord.getTelNumber());
                allocationRecord.setIous(addIous.getIous());
                allocationRecord.setTotalAmount(addIous.getTotalAmount() + "");
                allocationRecord.setNextRefundDay(addIous.getNextRefundDay());
                allocationRecord.setAmountOverride(addIous.getNowCollectionAmount() + "");
                allocationRecord.setOverduePrincipal(addIous.getOverduePrincipal() + "");
                allocationRecord.setOverdueAccrual(addIous.getOverdueAccrual() + "");
                allocationRecord.setDefaultInterest(addIous.getDefaultInterest() + "");
                allocationRecord.setAgeCd(addIous.getNowAgecd() + "");
                allocationRecord.setOverdue(addIous.getOverdue() + "");
                allocationRecord.setNetLendingPlatform(addIous.getNetLendingPlatform());
                allocationRecord.setIsSoleProprietorship(addIous.getIsSoleProprietorship());
                allocationRecord.setDcaDistribution(thisRecord.getDcaDistribution() + "");
                allocationRecord.setTheCaseDistribution("M" + addIous.getNowAgecd() +"案件");
                allocationRecord.setTurnOverDay(DateUtils.dateToString(transfer, "yyyyMMdd") + "");
                allocationRecord.setProductName(addIous.getProductName());
                allocationRecord.setTotalAging(addIous.getTotalAging() + "");
                allocationRecord.setContractCreateDate(addIous.getContractCreateDate());
                allocationRecord.setRemarks("SYS_" + DateUtils.dateToString(date, "yyyy-MM-dd HH:mm:ss") + "_新增");
                allocationRecord.setCreateDate(date);
                allocationRecord.setCreateType("新增_" + DateUtils.dateToString(date, "yyyy-MM-dd"));
                outsourceAllocationRecordMapper.insert(allocationRecord);
            }

            objectMap = new HashMap<>();
            objectMap.put("id", addIous.getId());
            newAddIousMapper.deleteByMap(objectMap);
        }
    }

    @Override
    public List<NewAddIous> loadNewIousBy(Map<String, Object> objectMap) {
        return newAddIousMapper.loadNewAddIousByMaps(objectMap);
    }

}
