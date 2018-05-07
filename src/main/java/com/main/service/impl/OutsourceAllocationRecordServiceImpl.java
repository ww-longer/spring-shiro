package com.main.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.main.mapper.OutsourceCompanyMapper;
import com.main.model.OutsourceAllocationRecord;
import com.main.mapper.OutsourceAllocationRecordMapper;
import com.main.service.IOutsourceAllocationRecordService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.sys.commons.result.PageInfo;
import com.sys.model.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @param custIds
     * @return
     */
    public List<Map<String, Object>> dataMatching(String[] custIds){
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
}
