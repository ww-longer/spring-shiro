package com.main.service;

import com.main.model.OutsourceCompany;
import com.baomidou.mybatisplus.service.IService;
import com.sys.commons.result.PageInfo;

import java.util.List;

/**
 * <p>
 * 外包公司 服务类
 * </p>
 *
 * @author jiewai
 * @since 2018-06-07
 */
public interface OutsourceCompanyService extends IService<OutsourceCompany> {

    void selectPageInfo(PageInfo pageInfo);

    OutsourceCompany selectById(Long id);

    List<OutsourceCompany> loadAllCompany();
}
