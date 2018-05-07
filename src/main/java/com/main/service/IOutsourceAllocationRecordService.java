package com.main.service;

import com.main.model.OutsourceAllocationRecord;
import com.baomidou.mybatisplus.service.IService;
import com.sys.commons.result.PageInfo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 委外分配日期记录表 服务类
 * </p>
 *
 * @author jiewai
 * @since 2018-05-03
 */
public interface IOutsourceAllocationRecordService extends IService<OutsourceAllocationRecord> {

    void selectPageInfo(PageInfo pageInfo);

    void loadMatchingData(PageInfo pageInfo);

    List<Map<String,Object>> getMatchingData(Map<String, Object> condition);
}
