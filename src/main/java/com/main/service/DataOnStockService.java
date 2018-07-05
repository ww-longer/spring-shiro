package com.main.service;

import com.main.model.DataOnStock;
import com.baomidou.mybatisplus.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author jiewai
 * @since 2018-06-07
 */
public interface DataOnStockService extends IService<DataOnStock> {

    void importdataOnStockExcel(List<Map<String, Object>> listMap);

}
