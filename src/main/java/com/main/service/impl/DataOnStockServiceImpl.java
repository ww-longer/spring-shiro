package com.main.service.impl;

import com.main.mapper.NewAddIousMapper;
import com.main.model.DataOnStock;
import com.main.mapper.DataOnStockMapper;
import com.main.model.NewAddIous;
import com.main.service.DataOnStockService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author jiewai
 * @since 2018-06-07
 */
@Service
public class DataOnStockServiceImpl extends ServiceImpl<DataOnStockMapper, DataOnStock> implements DataOnStockService {

    @Autowired
    private DataOnStockMapper dataOnStockMapper;

    @Override
    @Transactional
    public void importdataOnStockExcel(List<HashMap<String, Object>> listMap) {
        // 1.删除现在表中的所有数据
        dataOnStockMapper.clearAllData();
        // 2.循环写入当前表格数据
        for (int i = 0; i < listMap.size(); i++) {
            HashMap<String, Object> maps = listMap.get(i);
            if (maps != null) {
                DataOnStock onStock = new DataOnStock();
                onStock.setCustId(maps.get("证件号码").toString());
                onStock.setIous(maps.get("合同编号").toString());
                dataOnStockMapper.insert(onStock);
            }
        }
    }


}
