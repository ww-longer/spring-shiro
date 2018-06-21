package com.main.mapper;

import com.main.model.DataOnStock;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author jiewai
 * @since 2018-06-07
 */
public interface DataOnStockMapper extends BaseMapper<DataOnStock> {

    void clearAllData();

    List<DataOnStock> selectAllList();

}