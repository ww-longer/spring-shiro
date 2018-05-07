package com.main.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.main.model.OutsourceAllocationRecord;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
  * 委外分配日期记录表 Mapper 接口
 * </p>
 *
 * @author jiewai
 * @since 2018-05-03
 */
public interface OutsourceAllocationRecordMapper extends BaseMapper<OutsourceAllocationRecord> {

    List<Map<String,Object>> selectPageInfo(Page<Map<String, Object>> page, Map<String, Object> condition);

    List<Map<String,Object>> selectByMaps(Map<String, Object> map);
}