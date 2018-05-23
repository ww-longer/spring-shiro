package com.main.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.main.mapper.OutsourceAllocationAmountHisMapper;
import com.main.model.OutsourceAllocationAmountHis;
import com.main.service.OutsourceAllocationAmountHisService;
import com.sys.commons.result.PageInfo;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author jiewai
 * @since 2018-05-22
 */
@Service
public class OutsourceAllocationAmountHisServiceImpl extends ServiceImpl<OutsourceAllocationAmountHisMapper, OutsourceAllocationAmountHis> implements OutsourceAllocationAmountHisService {

    @Override
    public void selectPageInfo(PageInfo pageInfo) {

    }
}
