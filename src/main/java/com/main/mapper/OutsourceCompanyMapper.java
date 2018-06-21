package com.main.mapper;

import com.baomidou.mybatisplus.plugins.Page;
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

    List<OutsourceCompany> selectAllList();

    List<Map<String,Object>> selectPageInfo(Page<Map<String, Object>> page, Map<String, Object> condition);

    OutsourceCompany selectById(Long id);
}