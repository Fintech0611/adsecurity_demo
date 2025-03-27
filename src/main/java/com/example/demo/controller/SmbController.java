package com.example.demo.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.SmbService;

@RequestMapping("/smb")
@Controller
public class SmbController {

    private List<Map<String, Object>> fileList;

    @Autowired
    private SmbService smbService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("fileList", fileList);
        model.addAttribute("folderName", "");
        return "demo/smb";
    }

    @RequestMapping(path = "/portal", method = RequestMethod.POST)
    public String requestMethodName(@RequestParam(value = "action", required = true) String action,
            @RequestParam(value = "folderName", required = true) String folerName, Model model) {
        model.addAttribute("folderName", folerName);
        if (action.equals("list")) {
            model.addAttribute("fileList", smbService.listFiles(folerName));
        } else if (action.equals("create")) {
            smbService.createFolder(folerName);
        } else if (action.equals("delete")) {
            smbService.deleteFolder(folerName);
        }
        return "demo/smb";
    }

}
