package com.sys.service.impl;

import java.io.Serializable;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.sys.commons.result.Tree;
import com.sys.commons.shiro.ShiroUser;
import com.sys.mapper.ResourceMapper;
import com.sys.mapper.RoleMapper;
import com.sys.mapper.RoleResourceMapper;
import com.sys.mapper.UserRoleMapper;
import com.sys.model.Resource;
import com.sys.service.IResourceService;

/**
 *
 * Resource 表数据服务层接口实现类
 *
 */
@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements IResourceService {
    private static final int RESOURCE_MENU = 0; // 菜单

    @Autowired
    private ResourceMapper resourceMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleResourceMapper roleResourceMapper;
    
    @Override
    public List<Resource> selectAll(Integer id) {
        EntityWrapper<Resource> wrapper = new EntityWrapper<Resource>();
        if (null != id) {
            wrapper.addFilter("pid = {0}", id);
        }
        wrapper.orderBy("seq");
        return resourceMapper.selectList(wrapper);
    }
    
    public List<Resource> selectByType(Integer type) {
        EntityWrapper<Resource> wrapper = new EntityWrapper<Resource>();
        Resource resource = new Resource();
        wrapper.setEntity(resource);
        wrapper.addFilter("resource_type = {0}", type);
        wrapper.orderBy("seq");
        return resourceMapper.selectList(wrapper);
    }
    
    @Override
    public List<Tree> selectAllMenu() {
        List<Tree> trees = new ArrayList<Tree>();
        // 查询所有菜单
        List<Resource> resources = this.selectByType(RESOURCE_MENU);
        if (resources == null) {
            return trees;
        }
        for (Resource resource : resources) {
            Tree tree = new Tree();
            tree.setId(resource.getId());
            tree.setPid(resource.getPid());
            tree.setText(resource.getName());
            tree.setIconCls(resource.getIcon());
            tree.setAttributes(resource.getUrl());
            tree.setState(resource.getOpened());
            trees.add(tree);
        }
        return trees;
    }
    
    @Override
    public List<Tree> selectAllTree(Integer id) {
        // 获取所有的资源 tree形式，展示
        List<Tree> trees = new ArrayList<Tree>();
        List<Resource> resources = this.selectAll(id);
        if (resources == null) {
            return trees;
        }
        for (Resource resource : resources) {
            Tree tree = new Tree();
            tree.setId(resource.getId());
            tree.setPid(resource.getPid());
            tree.setText(resource.getName());
            tree.setIconCls(resource.getIcon());
            tree.setAttributes(resource.getUrl());
            tree.setState(resource.getOpened());
            trees.add(tree);
        }
        return trees;
    }
    
    @Override
    public List<Tree> selectTree(ShiroUser shiroUser, Integer pId) {
        List<Tree> trees = new ArrayList<Tree>();
        // shiro中缓存的用户角色
        Set<String> roles = shiroUser.getRoles();
        if (roles == null) {
            return trees;
        }
        // 如果有超级管理员权限
        if (roles.contains("admin")) {
            List<Resource> resourceList;
            if (null == pId || "".equals(pId)) {
                resourceList = this.selectByType(RESOURCE_MENU);
            } else {
                resourceList = this.selectByRPId(pId);
            }
            if (resourceList == null) {
                return trees;
            }
            for (Resource resource : resourceList) {
                Tree tree = new Tree();
                tree.setId(resource.getId());
                tree.setPid(resource.getPid());
                tree.setText(resource.getName());
                tree.setIconCls(resource.getIcon());
                tree.setAttributes(resource.getUrl());
                tree.setOpenMode(resource.getOpenMode());
                tree.setState(resource.getOpened());
                trees.add(tree);
            }
            return trees;
        }
        // 普通用户
        List<Long> roleIdList = userRoleMapper.selectRoleIdListByUserId(shiroUser.getId());
        if (roleIdList == null) {
            return trees;
        }
        List<Resource> resourceLists = roleMapper.selectResourceListByRoleIdList(roleIdList);
        if (resourceLists == null) {
            return trees;
        }
        for (Resource resource : resourceLists) {
            Tree tree = new Tree();
            tree.setId(resource.getId());
            tree.setPid(resource.getPid());
            tree.setText(resource.getName());
            tree.setIconCls(resource.getIcon());
            tree.setAttributes(resource.getUrl());
            tree.setOpenMode(resource.getOpenMode());
            tree.setState(resource.getOpened());
            trees.add(tree);
        }
        return trees;
    }

    @Override
    public List<Resource> selectByRPId(int pId) {
        Map<String, Object> map = new HashMap<>();
        map.put("pid", pId);
        map.put("seq", "ASC");
        return resourceMapper.selectResourceList(map);
    }

    @Override
	public boolean deleteById(Serializable resourceId) {
		roleResourceMapper.deleteByResourceId(resourceId);
		return super.deleteById(resourceId);
	}
}