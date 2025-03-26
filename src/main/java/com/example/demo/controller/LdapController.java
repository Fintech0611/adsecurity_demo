package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.service.LdapService;

import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping("/ldap")
@Controller
public class LdapController {

    @Autowired
    private LdapService ldapService;

    @RequestMapping(path = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("groups", null);
        return "demo/adsecurity";
    }

    @RequestMapping(path = "/portal", method = RequestMethod.POST)
    public String search(@RequestParam(value = "action", required = false) String action,
            @RequestParam(value = "newGroupName", required = false) String newGroupName,
            @RequestParam(value = "newDescription", required = false) String newDescription, Model model) {
        if (action.equals("search")) {
            model.addAttribute("groups", ldapService.search());
        } else if (action.equals("addGroup")) {
            ldapService.createGroup(newGroupName, newDescription);
            // model.addAttribute("groups", ldapService.search());
        } else if (action.startsWith("delete")) {
            ldapService.deleteGroup(action.replace("delete", ""));
        } else if (action.startsWith("addMember")) {
            ldapService.addGroupMember(action.replace("addMember", ""));
        } else if (action.startsWith("removeMember")) {
            ldapService.removeGroupMembers(action.replace("removeMember", ""));
        }
        return "demo/adsecurity";
    }

}
