package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.DaclService;

@RequestMapping("/dacl")
@Controller
public class SmbcaclsController {

    @Autowired
    private DaclService daclService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("userAccount", "JERRY\\testGroup");
        return "demo/dacl";
    }

    @RequestMapping(path = "/portal", method = RequestMethod.POST)
    public String portal(@RequestParam(value = "action", required = true) String action,
            @RequestParam(value = "chkGrp", required = true) List<Long> chkGrp,
            @RequestParam(value = "userAccount", required = true) String userAccount, Model model) {

        if (action.equals("add")) {
            daclService.Add(chkGrp, userAccount);
        } else if (action.equals("delete")) {
            daclService.Delete(chkGrp, userAccount);
        }
        model.addAttribute("userAccount", userAccount);
        return "demo/dacl";
    }
}
