package com.example.demo.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LdapGroup {
    private String groupName;
    private String description;
    private List<LdapUser> members;
}
