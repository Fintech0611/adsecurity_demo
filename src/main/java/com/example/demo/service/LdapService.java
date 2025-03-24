package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.ldap.LdapName;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapNameBuilder;
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

        try {
            groups = ldapTemplate.search(
                    LdapQueryBuilder.query().searchScope(SearchScope.SUBTREE)
                            .filter("objectClass=group"),
                    (AttributesMapper<LdapGroup>) attrs -> {
                        Attribute sAMAccountName = attrs.get("sAMAccountName");
                        System.out.println("gourpName=" + (String) sAMAccountName.get());

                        return LdapGroup.builder().groupName((String) sAMAccountName.get()).build();
                    });
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }

        return groups;
    }

    /**
     * セキュリティグループのメンバーを取得する
     * 
     * @param groupName グループ名
     * @return メンバーリスト
     */
    public List<LdapUser> getMembersOfGroup(String groupName) {
        return null;
    }

    /**
     * セキュリティグループを作成する
     * 
     * @param groupName
     * @param description
     */
    public void createGroup(String groupName, String description) {
        try {
            // 设置组属性
            Attributes groupAttributes = new BasicAttributes();
            BasicAttribute objectClass = new BasicAttribute("objectClass");
            objectClass.add("top");
            objectClass.add("group"); // Windows AD 使用 "group"
            BasicAttribute member = new BasicAttribute("member");
            member.add("cn=Administrator,cn=Users,dc=jerry,dc=com");
            groupAttributes.put(member);

            groupAttributes.put(objectClass);
            groupAttributes.put("cn", groupName);
            groupAttributes.put("sAMAccountName", groupName);
            groupAttributes.put("description", description);

            // 创建组
            ldapTemplate.bind("cn=" + groupName + ",cn=Users", null, groupAttributes);
            System.out.println("グループ【" + groupName + "】を作成しました");
        } catch (Exception e) {
            System.err.println("グループ失敗" + e.getMessage());
            e.printStackTrace();
        }
    }
}
