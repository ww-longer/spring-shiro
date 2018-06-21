package com.main.controller;

import javax.validation.Valid;

import java.util.HashMap;
import java.util.Date;
import java.util.Map;

import com.sys.commons.base.BaseController;
import com.sys.commons.result.PageInfo;
import com.sys.commons.shiro.ShiroUser;
import com.sys.commons.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.main.model.OutsourceCompany;
import com.main.service.OutsourceCompanyService;

/**
 * <p>
 * 外包公司 前端控制器
 * </p>
 *
 * @author jiewai
 * @since 2018-06-07
 */
@Controller
@RequestMapping("/sc/collection/company")
public class OutsourceCompanyController extends BaseController {

    @Autowired
    private OutsourceCompanyService outsourceCompanyService;
    
    @GetMapping("/manager")
    public String manager() {
        return "sc_main/companyManager/companyList";
    }
    
    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(OutsourceCompany company) {
        PageInfo pageInfo;
        pageInfo = new PageInfo(company.getPage(), company.getRows(), company.getSort(), company.getOrder());
        Map<String, Object> objectMap = new HashMap<>();
        // 创建查询参数
        if (StringUtils.isNotBlank(company.getCompany())) {
            objectMap.put("company","%" + company.getCompany() + "%");
        }
        pageInfo.setCondition(objectMap);
        outsourceCompanyService.selectPageInfo(pageInfo);
        return pageInfo;
    }
    
    /**
     * 添加页面
     * @return
     */
    @GetMapping("/addPage")
    public String addPage() {
        return "sc_main/companyManager/addCompanyPage";
    }
    
    /**
     * 添加
     * @param 
     * @return
     */
    @PostMapping("/add")
    @ResponseBody
    public Object add(@Valid OutsourceCompany outsourceCompany) {
        ShiroUser user = this.getShiroUser();
        outsourceCompany.setCreateDate(new Date());
        outsourceCompany.setCreateUser(user.getLoginName());
        boolean b = outsourceCompanyService.insert(outsourceCompany);
        if (b) {
            return renderSuccess("添加成功！");
        } else {
            return renderError("添加失败！");
        }
    }
    
    /**
     * 删除
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @ResponseBody
    public Object delete(Long id) {
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("id",id);
        boolean b = outsourceCompanyService.deleteByMap(objectMap);
        if (b) {
            return renderSuccess("删除成功！");
        } else {
            return renderError("删除失败！");
        }
    }
    
    /**
     * 编辑
     * @param model
     * @param id
     * @return
     */
    @GetMapping("/editPage")
    public String editPage(Model model, Long id) {
        OutsourceCompany outsourceCompany = outsourceCompanyService.selectById(id);
        model.addAttribute("company", outsourceCompany);
        return "sc_main/companyManager/updateCompanyPage";
    }
    
    /**
     * 编辑
     * @param 
     * @return
     */
    @PostMapping("/edit")
    @ResponseBody
    public Object edit(OutsourceCompany outsourceCompany) {
        OutsourceCompany company = outsourceCompanyService.selectById(outsourceCompany.getId());
        boolean b = false;
        if (company != null) {
            company.setAddress(outsourceCompany.getAddress());
            company.setCompanyNumber(outsourceCompany.getCompanyNumber());
            company.setCompany(outsourceCompany.getCompany());
            b = outsourceCompanyService.updateById(company);
        }
        if (b) {
            return renderSuccess("编辑成功！");
        } else {
            return renderError("编辑失败！");
        }
    }
}
