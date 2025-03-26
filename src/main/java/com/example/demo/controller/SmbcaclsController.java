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

    private AclCheck aclCheck = new AclCheck(false, false, false, false, AclConst.check1, AclConst.check2,
            AclConst.check3, AclConst.check4);

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("aclCheck", aclCheck)
                .addAttribute("userAccount", "JERRY\\testGroup");
        return "demo/dacl";
    }

    @RequestMapping(path = "/portal", method = RequestMethod.POST)
    public String portal(@RequestParam(value = "action", required = true) String action,
            @RequestParam(value = "chkGrp", required = true) List<Long> chkGrp,
            @RequestParam(value = "userAccount", required = true) String userAccount, AclCheck aclCheck, Model model) {

        if (action.equals("query")) {
            daclService.query(aclCheck, userAccount);
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
