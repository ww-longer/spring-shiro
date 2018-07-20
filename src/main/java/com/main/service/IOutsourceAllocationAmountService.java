package com.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.main.model.OutsourceAllocationAmount;
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
public interface IOutsourceAllocationAmountService extends IService<OutsourceAllocationAmount> {

    void selectPageInfo(PageInfo pageInfo);

    void importInOutsourceAmountExcel(List<Map<String, Object>> listMap);

    void exportInAllAmountByMap(Map<String, Object> amountMap, HttpServletRequest request, HttpServletResponse response);

    void updateAmountByCustId(OutsourceAllocationAmount amount, String isAllCase);

    Map addAmountData(OutsourceAllocationAmount amount);

    List<OutsourceAllocationAmount> loadAmountByMaps(Map<String, Object> amountMap);

    void updateAmountHisByCustId(OutsourceAllocationAmountHis amountHis);

<<<<<<< HEAD
    void exportInAllLeaveByMap(Map<String, Object> amountMap, HttpServletRequest request, HttpServletResponse response);

    List<Map<String, Object>> loadAllOutCompanyCaseNum();

    List<Map<String, Object>> loadAllOutCaseAgecdNum();

    List<Map<String, Object>> loadAllOutAgecdCaseCount();

    List<Map<String, Object>> loadAllOutAgecdCaseAAmount();
=======
    List<Map<String, Object>> loadAllOutCompanyCaseNum();

    void exportInAllLeaveByMap(Map<String, Object> amountMap, HttpServletRequest request, HttpServletResponse response);
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
}
