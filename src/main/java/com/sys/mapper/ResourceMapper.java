package com.sys.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.sys.model.Resource;

import java.util.List;
import java.util.Map;

/**
 *
 * Resource 表数据库控制层接口
 *
 */
public interface ResourceMapper extends BaseMapper<Resource> {

    List<Resource> selectResourceList(Map<String, Object> map);
}