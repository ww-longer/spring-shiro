package com.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sys.commons.base.BaseController;

/**
 *
 */
@Controller
@RequestMapping("/statistics")
public class StatisticalController extends BaseController {

    /**
     * 统计图界面加载
     *
     * @return
     */
    @GetMapping("/load")
    public String loadPage() {
        return "statistical/statisticalChart";
    }

}