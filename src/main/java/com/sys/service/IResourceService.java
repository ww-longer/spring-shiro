package com.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.service.IService;
import com.sys.commons.result.Tree;
import com.sys.commons.shiro.ShiroUser;
import com.sys.model.Resource;

/**
 *
 * Resource 表数据服务层接口
 *
 */
public interface IResourceService extends IService<Resource> {

    List<Resource> selectAll(Integer id);

    List<Tree> selectAllMenu();

    List<Tree> selectAllTree(Integer id);

    List<Tree> selectTree(ShiroUser shiroUser, Integer rId);

    List<Resource> selectByRPId(int resourceMenu);
}