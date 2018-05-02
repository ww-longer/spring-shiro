package com.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.sys.commons.result.Tree;
import com.sys.model.Organization;

/**
 *
 * Organization 表数据服务层接口
 *
 */
public interface IOrganizationService extends IService<Organization> {

    List<Tree> selectTree();

    List<Organization> selectTreeGrid();

}