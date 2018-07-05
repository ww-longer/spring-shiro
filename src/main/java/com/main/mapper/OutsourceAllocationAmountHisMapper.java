package com.main.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.main.model.OutsourceAllocationAmountHis;

import java.util.List;
import java.util.Map;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author jiewai
 * @since 2018-05-22
 */
public interface OutsourceAllocationAmountHisMapper extends BaseMapper<OutsourceAllocationAmountHis> {

    List<Map<String,Object>> selectPageInfo(Page<Map<String, Object>> page, Map<String, Object> condition);

    List<OutsourceAllocationAmountHis> loadAmountHisByMaps(Map<String, Object> condition);

    Integer insertInfo(OutsourceAllocationAmountHis amountHis);

    OutsourceAllocationAmountHis selectOneById(Integer id);

}