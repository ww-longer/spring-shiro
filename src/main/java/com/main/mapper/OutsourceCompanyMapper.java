package com.main.mapper;

import com.main.model.OutsourceCompany;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
  * 外包公司 Mapper 接口
 * </p>
 *
 * @author jiewai
 * @since 2018-05-03
 */
public interface OutsourceCompanyMapper extends BaseMapper<OutsourceCompany> {

    List<Map<String,Object>> selectAllList();
}