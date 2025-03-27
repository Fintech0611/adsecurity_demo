package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.constant.AclConst;
import com.example.demo.model.AclCheck;
import com.example.demo.service.DaclService;

import lombok.Data;

@RequestMapping("/dacl")
@Controller
public class SmbcaclsController {

    @Autowired
    private DaclService daclService;

    private Boolean check1 = true;
    private Boolean check2 = true;
    private Boolean check3 = true;
    private Boolean check4 = true;

    private AclCheck aclCheck = new AclCheck(false, false, false, false, AclConst.check1, AclConst.check2,
            AclConst.check3, AclConst.check4);

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("check1", check1)
                .addAttribute("check2", check2)
                .addAttribute("check3", check3)
                .addAttribute("check4", check4)
                .addAttribute("userAccount", "JERRY\\testGroup");
        return "demo/dacl";
    }

    @RequestMapping(path = "/portal", method = RequestMethod.POST)
    public String portal(@RequestParam(value = "action", required = true) String action,
            @RequestParam(value = "chkGrp", required = false) List<Long> chkGrp,
            @RequestParam(value = "userAccount", required = true) String userAccount, Model model) {

        if (action.equals("query")) {
            aclCheck.check1=false;
            aclCheck.check2=false;
            aclCheck.check3=false;
            aclCheck.check4=false;
            daclService.query(aclCheck, userAccount);
            model.addAttribute("check1", aclCheck.check1)
                    .addAttribute("check2", aclCheck.check2)
                    .addAttribute("check3", aclCheck.check3)
                    .addAttribute("check4", aclCheck.check4)
                    .addAttribute("userAccount", "JERRY\\testGroup");
        } else if (action.equals("add")) {
            daclService.Add(chkGrp, userAccount);
        } else if (action.equals("delete")) {
            daclService.Delete(chkGrp, userAccount);
        }
        model.addAttribute("aclCheck", aclCheck);
        model.addAttribute("userAccount", userAccount);
        return "demo/dacl";
    }
}
