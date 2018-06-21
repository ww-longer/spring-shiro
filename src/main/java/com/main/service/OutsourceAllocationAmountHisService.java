package com.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.main.model.OutsourceAllocationAmountHis;
import com.sys.commons.result.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

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

    void loadAmountHisByMaps(Map<String, Object> condition, HttpServletRequest request, HttpServletResponse response);

    List<OutsourceAllocationAmountHis> loadAmountHisByMaps(Map<String, Object> condition);

    OutsourceAllocationAmountHis loadAmountHisById(Integer id);
}
