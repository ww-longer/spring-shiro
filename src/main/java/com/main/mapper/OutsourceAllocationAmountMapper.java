package com.main.mapper;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.main.model.OutsourceAllocationAmount;

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
public interface OutsourceAllocationAmountMapper extends BaseMapper<OutsourceAllocationAmount> {

    Integer insertInfo(OutsourceAllocationAmount amount);

    OutsourceAllocationAmount selectById(int id);

    List<Map<String,Object>> selectPageInfo(Page<Map<String, Object>> page, Map<String, Object> condition);

    List<OutsourceAllocationAmount> loadOutsourceAmountByMaps(Map<String, Object> map);

    void deleteOutsourceAmountOverdue(Map<String, Object> map);

    List<OutsourceAllocationAmount> loadOutsourceAmountAllLists();

    List<OutsourceAllocationAmount> loadCommonAmountByMaps(Map<String, Object> map);

    List<String> selectAllTransfer(Map<String, Object> objectMap);

    List<Map<String,Object>> searchAllCompanyCaseNum();
}