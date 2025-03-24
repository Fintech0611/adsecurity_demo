package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.SearchControls;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

import com.example.demo.model.LdapGroup;
import com.example.demo.model.LdapUser;

@Service
public class LdapService {

    @Autowired
    private LdapTemplate ldapTemplate;

    /**
     * すべてのセキュリティグループを取得する
     * 
     * @return セキュリティグループリスト
     */
    public List<LdapGroup> search() {
        List<LdapGroup> groups = new ArrayList<LdapGroup>();

        groups = ldapTemplate.search(
                LdapQueryBuilder.query().searchScope(SearchScope.SUBTREE)
                        .filter("objectClass=group"),
                (AttributesMapper<LdapGroup>) attrs -> {
                    Attribute sAMAccountName = attrs.get("sAMAccountName");
                    System.out.println("gourpName=" + (String) sAMAccountName.get());

                    return LdapGroup.builder().groupName((String) sAMAccountName.get()).build();
                });

        return groups;
    }

    /**
     * セキュリティグループのメンバーを取得する
     * 
     * @param groupName グループ名
     * @return メンバーリスト
     */
    public LdapUser[] getMembersOfGroup(String groupName) {
        return null;
    }
}
