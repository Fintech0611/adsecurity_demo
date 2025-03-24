package com.example.demo.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class LdapUser {
    private String CN;
    private String userPrincipalName;
    private String givenName;
    private String sn;
    private String displayName;
}
