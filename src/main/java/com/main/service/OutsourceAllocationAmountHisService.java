package com.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.main.model.OutsourceAllocationAmountHis;
import com.sys.commons.result.PageInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiewai
 * @since 2018-05-22
 */
public interface OutsourceAllocationAmountHisService extends IService<OutsourceAllocationAmountHis> {

    void selectPageInfo(PageInfo pageInfo);
}
