package com.example.demo.service;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;

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
        List<LdapGroup> groups = new ArrayList<>();

        try {
            groups = ldapTemplate.search(
                    LdapQueryBuilder.query().searchScope(SearchScope.SUBTREE)
                            .filter("objectClass=group"),
                    (AttributesMapper<LdapGroup>) attrs -> {
                        Attribute sAMAccountName = attrs.get("sAMAccountName");
                        System.out.println("gourpName=" + (String) sAMAccountName.get());
                        String description = attrs.get("description") == null ? ""
                                : (String) attrs.get("description").get();
                        System.out.println("description=" + description);

                        Attribute member = attrs.get("member");

                        List<LdapUser> members = new ArrayList<>();
                        if (member != null) {
                            NamingEnumeration<?> memberEnum = member.getAll();
                            while (memberEnum.hasMore()) {
                                members.add(LdapUser.builder().cn((String) memberEnum.next()).build());
                            }
                        }
                        return LdapGroup.builder()
                                .groupName((String) sAMAccountName.get())
                                .description(description)
                                .members(members)
                                .build();
                    });
        } catch (Exception e) {
            e.printStackTrace();
            ;
        }

        return groups;
    }

    /**
     * セキュリティグループを作成する
     * 
     * @param groupName   グループ名
     * @param description 説明
     */
    public void createGroup(String groupName, String description, Boolean isGroup, String password) {
        try {
            // 设置组属性
            Attributes groupAttributes = new BasicAttributes();
            BasicAttribute objectClass = new BasicAttribute("objectClass");
            objectClass.add("top");
            if (isGroup) {
                objectClass.add("group"); // Windows AD 使用 "group"
                BasicAttribute member = new BasicAttribute("member");
                member.add("cn=Administrator,cn=Users,dc=jerry,dc=com");
                groupAttributes.put(member);
                groupAttributes.put("description", description);
            } else {
                objectClass.add("person");
                objectClass.add("organizationalPerson");
                objectClass.add("user");
                groupAttributes.put("userPrincipalName", groupName + "@jerry.com");
                groupAttributes.put("userPassword", password);
                // groupAttributes.put("unicodePwd ", encodePassword(password));
                // groupAttributes.put("userAccountControl", String.valueOf(512));
            }

            groupAttributes.put(objectClass);
            groupAttributes.put("cn", groupName);
            groupAttributes.put("sAMAccountName", groupName);

            // 创建组
            ldapTemplate.bind("cn=" + groupName + ",cn=Users", null, groupAttributes);
            System.out.println("グループ【" + groupName + "】を作成しました");
        } catch (Exception e) {
            System.err.println("グループ作成失敗" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * セキュリティグループのメンバーを削除する
     * 
     * @param groupName グループ名
     * @return メンバーリスト
     */
    public void deleteGroup(String groupName) {
        try {
            String groupDn = "cn=" + groupName + ",cn=Users";
            // グループメンバーを削除
            removeGroupMembers(groupDn);

            // グループ削除
            ldapTemplate.unbind(groupDn);
            System.out.println("グループを削除しました。" + groupName);
        } catch (Exception e) {
            System.err.println("グループ削除失敗" + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * グループメンバーを追加する
     * 
     * @param groupName グループ名
     */
    public void addGroupMember(String groupName) {
        String groupDn = "cn=" + groupName + ",cn=Users";
        try {
            BasicAttribute member = new BasicAttribute("member");
            member.add("cn=Administrator,cn=Users,dc=jerry,dc=com");
            ldapTemplate.modifyAttributes(
                    groupDn,
                    new ModificationItem[] {
                            new ModificationItem(
                                    DirContext.ADD_ATTRIBUTE,
                                    member)
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * グループメンバーを削除する
     * 
     * @param groupName グループ名
     * @return
     */
    public void removeGroupMembers(String groupName) {
        String groupDn = "cn=" + groupName + ",cn=Users";
        try {
            ldapTemplate.modifyAttributes(
                    groupDn,
                    new ModificationItem[] {
                            new ModificationItem(
                                    DirContext.REMOVE_ATTRIBUTE,
                                    new BasicAttribute("member"))
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // パスワードエンコード関数
    private String encodePassword(String password) {
        String quotedPassword = "\"" + password + "\"";
        try {
            byte[] unicodePwd = quotedPassword.getBytes("UTF-16LE");
            return Base64.getEncoder().encodeToString(unicodePwd);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("パスワードのエンコードエラー", e);
        }
    }
}
