package com.main.controller;

import javax.validation.Valid;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.main.service.DataOnStockService;
import com.main.service.OutsourceCompanyService;
import com.sys.commons.base.BaseController;
import com.sys.commons.result.PageInfo;
import com.sys.commons.utils.ExcelUtils;
import com.sys.commons.utils.FileUtils;
import com.sys.commons.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.main.model.NewAddIous;
import com.main.service.NewAddIousService;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author jiewai
 * @since 2018-06-07
 */
@Controller
@RequestMapping("/sc/collection/newAddIous")
public class NewAddIousController extends BaseController {

    @Autowired
    private NewAddIousService newAddIousService;
    @Autowired
    private DataOnStockService dataOnStockService;
    @Autowired
    private OutsourceCompanyService outsourceCompanyService;

    @GetMapping("/manager")
    public String manager(Model model) {
        model.addAttribute("companies", outsourceCompanyService.loadAllCompany());
        return "sc_main/newAddIous/newAddIousList";
    }
    
    @PostMapping("/dataGrid")
    @ResponseBody
    public PageInfo dataGrid(NewAddIous addIous) {
        PageInfo pageInfo ;
        pageInfo = new PageInfo(addIous.getPage(), addIous.getRows(), addIous.getSort(), addIous.getOrder());
        Map<String, Object> map = new HashMap<>();
        // 创建查询参数
        map.put("custId", addIous.getCustId());
        map.put("ious", addIous.getIous());
        map.put("company", addIous.getCompany());
        pageInfo.setCondition(map);
        newAddIousService.selectPageInfo(pageInfo);
        return pageInfo;
    }
    
    /**
     * 文件上传页面加载
     * @return
     */
    @GetMapping("/importDataOnStockPage")
    public String importDataOnStockPage() {
        return "sc_main/newAddIous/dataOnStockUpload";
    }

    /**
     * 上次借据清单导入
     * @param newFile 导入文件
     * @return
     */
    @PostMapping("/uploadDataOnStockExp")
    @ResponseBody
    public Object uploadDataOnStockExp(@RequestParam("newFile") MultipartFile newFile) {
        if (!newFile.isEmpty()) {
            // 转换为 File
            File tempFile;
            try {
                tempFile = FileUtils.multipartToFile(newFile);
                // 获取导入文件中的数据
                List<HashMap<String, Object>> listMap = ExcelUtils.loadAllExcelData(tempFile);
                dataOnStockService.importdataOnStockExcel(listMap);
                return renderSuccess("文件导入成功！");
            } catch (IOException e) {
                e.printStackTrace();
                return renderError("文件读取失败！");
            }
        }else{
            return renderError("文件不存在！");
        }
    }
    
    /**
     * 委外新借据导入
     * @param iouss
     * @return
     */
    @PostMapping("/batchDispose")
    @ResponseBody
    public Object batchDispose(@RequestParam("iouss") String iouss) {
        if (StringUtils.isNotBlank(iouss)) {
            newAddIousService.updateIousData(iouss);
            return renderSuccess("导入成功！");
        } else {
            return renderError("请选择要委外的借据！");
        }
    }
    
    /**
     * 编辑
     * @param newAddIous q
     * @return w
     */
    @PostMapping("/edit")
    @ResponseBody
    public Object edit(@Valid NewAddIous newAddIous) {
        newAddIous.setUpdateTime(new Date());
        boolean b = newAddIousService.updateById(newAddIous);
        if (b) {
            return renderSuccess("编辑成功！");
        } else {
            return renderError("编辑失败！");
        }
    }
}
