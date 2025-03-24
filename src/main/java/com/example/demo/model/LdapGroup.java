package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LdapGroup {
    private String groupName;
    private LdapUser[] members;
}
