package com.main.service;

import com.main.model.OutsourceAllocationRecord;
import com.baomidou.mybatisplus.service.IService;
import com.main.model.OutsourceCompany;
import com.sys.commons.result.PageInfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 委外分配日期记录表 服务类
 * </p>
 *
 * @author jiewai
 * @since 2018-05-03
 */
public interface IOutsourceAllocationRecordService extends IService<OutsourceAllocationRecord> {

    void selectPageInfo(PageInfo pageInfo);

    void loadMatchingData(PageInfo pageInfo, List<OutsourceCompany> outList);

    void getMatchingData(Map<String, Object> condition, List<OutsourceCompany> outList, HttpServletRequest request, HttpServletResponse response);

    Map<String, Object> importInOutsourceExcel(List<HashMap<String, Object>> listMap);

    void downLoadAllDate(HttpServletRequest request, HttpServletResponse response);

    Map<String,Object> importAmountAndRecordExcel(List<HashMap<String, Object>> listMap);
}
