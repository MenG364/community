package com.meng.community.controller;

import com.meng.community.service.IDateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @authoer: MenG364
 * @createDate:2022/7/5
 * @description:
 */

@Controller
public class DateController {

    @Autowired
    private IDateService dateService;

    //统计页面
    @RequestMapping(value = "/data",method = {RequestMethod.GET,RequestMethod.POST})
    public String getDataPage(){
        return "site/admin/data";
    }

    //统计网站UV
    @PostMapping("/data/uv")
    public String getUV(Model model, @DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        Long uv = dateService.calculateUV(start, end);
        model.addAttribute("uvResult",uv);
        model.addAttribute("uvStartDate",start);
        model.addAttribute("uvEndDate",end);

        return "forward:/data";
    }

    //统计活跃用户
    @PostMapping("/data/dau")
    public String getDAU(Model model, @DateTimeFormat(pattern = "yyyy-MM-dd") Date start, @DateTimeFormat(pattern = "yyyy-MM-dd") Date end){
        Long dau = dateService.calculateDAU(start, end);
        model.addAttribute("dauResult",dau);
        model.addAttribute("dauStartDate",start);
        model.addAttribute("dauEndDate",end);
        return "forward:/data";
    }


}
