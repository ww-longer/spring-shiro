package com.main.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.main.model.OutsourceCompany;
import com.main.mapper.OutsourceCompanyMapper;
import com.main.service.OutsourceCompanyService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.sys.commons.result.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 外包公司 服务实现类
 * </p>
 *
 * @author jiewai
 * @since 2018-06-07
 */
@Service
public class OutsourceCompanyServiceImpl extends ServiceImpl<OutsourceCompanyMapper, OutsourceCompany> implements OutsourceCompanyService {

    @Autowired
    private OutsourceCompanyMapper outsourceCompanyMapper;

    @Override
    public void selectPageInfo(PageInfo pageInfo) {
        Page<Map<String, Object>> page = new Page<>(pageInfo.getNowpage(), pageInfo.getSize());
        page.setOrderByField(pageInfo.getSort());
        page.setAsc(pageInfo.getOrder().equalsIgnoreCase("asc"));
        List<Map<String, Object>> list = outsourceCompanyMapper.selectPageInfo(page, pageInfo.getCondition());
        pageInfo.setRows(list);
        pageInfo.setTotal(page.getTotal());
    }

    @Override
    public OutsourceCompany selectById(Long id) {
        return outsourceCompanyMapper.selectById(id);
    }

    @Override
    public List<OutsourceCompany> loadAllCompany() {
        return outsourceCompanyMapper.selectAllList();
    }
}
