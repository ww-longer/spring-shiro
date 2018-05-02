package com.sys.service;

import com.baomidou.mybatisplus.service.IService;
import com.sys.commons.result.PageInfo;
import com.sys.model.SysLog;

/**
 *
 * SysLog 表数据服务层接口
 *
 */
public interface ISysLogService extends IService<SysLog> {

    void selectDataGrid(PageInfo pageInfo);

}