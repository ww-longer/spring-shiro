package com.main.service;

import com.main.model.NewAddIous;
import com.baomidou.mybatisplus.service.IService;
import com.sys.commons.result.PageInfo;

import java.util.HashMap;
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
public interface NewAddIousService extends IService<NewAddIous> {

    void selectPageInfo(PageInfo pageInfo);

    void updateIousData(String iouss);

    List<NewAddIous> loadNewIousBy(Map<String, Object> objectMap);
}
