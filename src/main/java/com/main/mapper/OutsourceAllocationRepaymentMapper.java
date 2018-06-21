package com.main.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.main.model.OutsourceAllocationRepayment;

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
public interface OutsourceAllocationRepaymentMapper extends BaseMapper<OutsourceAllocationRepayment> {

    List<Map<String,Object>> selectPageInfo(Page<Map<String, Object>> page, Map<String, Object> condition);

    List<OutsourceAllocationRepayment> loadAllRepaymentByMaps(Map<String, Object> objectMap);

    OutsourceAllocationRepayment selectById(Long id);
}