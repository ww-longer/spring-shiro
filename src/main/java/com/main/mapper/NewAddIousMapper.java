package com.main.mapper;

import com.baomidou.mybatisplus.plugins.Page;
import com.main.model.NewAddIous;
import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
  *  Mapper 接口
 * </p>
 *
 * @author jiewai
 * @since 2018-06-07
 */
public interface NewAddIousMapper extends BaseMapper<NewAddIous> {

    List<Map<String,Object>> selectPageInfo(Page<Map<String, Object>> page, Map<String, Object> condition);

    void clearAllDate();

    NewAddIous loadOneByMaps(Map<String, Object> mapParam);

    List<NewAddIous> loadNewAddIousByMaps(Map<String, Object> objectMap);

    List<NewAddIous> loadAllNewAddIous();
}