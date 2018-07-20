package com.main.service;

import com.baomidou.mybatisplus.service.IService;
import com.main.model.OutsourceAllocationRepayment;
import com.main.model.TurnOverRepayment;
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
public interface OutsourceAllocationRepaymentService extends IService<OutsourceAllocationRepayment> {

    void selectPageInfo(PageInfo pageInfo);

    void exportInAllRepaymentByMap(Map<String, Object> objectMap, HttpServletRequest request, HttpServletResponse response);

    void exportInAmountAndRepaymentByMap(Map<String, Object> objectMap, HttpServletRequest request, HttpServletResponse response);

    OutsourceAllocationRepayment loadRepaymentById(Integer id);

<<<<<<< HEAD
    void importRepaymentHisExcel(List<Map<String, Object>> listMap);
=======
    void importRepaymentHisExcel(List<HashMap<String, Object>> listMap);
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790

    void selectRepaymentRatePageInfo(PageInfo pageInfo);

    void exportInRepaymentReportByMap(Map<String, Object> objectMap, HttpServletRequest request, HttpServletResponse response);

    void exportCompanyRankingExp(HttpServletRequest request, HttpServletResponse response);
<<<<<<< HEAD

    Map<String, List<Map<String, Object>>> loadRepaymentRateCaseNum(Map<String, Object> objectMap);

    Map<String, Object> loadCompanyDivideAmountNum(Map<String, Object> objectMap);

    Map<String, List<TurnOverRepayment>> loadTurnOverRepaymentRateNum(Map<String, Object> objectMap);

    void downloadTurnOverRepaymentRate(Map<String, Object> objectMap, HttpServletRequest request, HttpServletResponse response);
=======
>>>>>>> 820b10959a43b0fbc60804f4f0f01cc98886e790
}
