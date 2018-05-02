package com.main.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sys.commons.base.BaseController;
import com.sys.service.IUserService;

/**
 * @description：用户管理
 * @author：zhixuan.wang
 * @date：2015/10/1 14:51
 */
@Controller
@RequestMapping("/statistics")
public class StatisticalController extends BaseController {
    @Autowired
    private IUserService userService;

    /**
     * 统计图界面加载
     *
     * @return
     */
    @GetMapping("/load")
    public String loadPage() {
    	String t = "";
        return "statistical/statisticalChart";
    }

}