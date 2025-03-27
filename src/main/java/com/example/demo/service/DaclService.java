package com.example.demo.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.AclCheck;

@Service
public class DaclService {

    private final String sharePath = "//192.168.3.100/共有フォルダ";
    private final String folder = "test";
    private final String user = "Administrator@jerry.com";
    private final String password = "Qin711019";

    public void query(AclCheck aclCheck, String userAccount) {
        Long aclBase = 0x100000L;

        try {
            // 构建 smbcacls 命令
            String command = String.format(
                    "smbcacls %s \"%s\" -U %s%%%s | grep -F \"ACL:%s\"",
                    sharePath, folder, user, password, userAccount);

            System.out.println("command : " + command);
            // 直接用 Runtime 执行命令
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", command });

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {

                if (line.contains(userAccount)) {
                    System.out.println(line);

                    int startIndex = line.lastIndexOf("/");
                    String accesMask = line.substring(startIndex + 3);
                    Long acl = Long.parseLong(accesMask, 16);
                    acl = acl - aclBase;

                    if ((aclCheck.checkValue1.equals(acl & aclCheck.checkValue1))) {
                        aclCheck.check1 = true;
                    }
                    if ((aclCheck.checkValue2.equals(acl & aclCheck.checkValue2))) {
                        aclCheck.check2 = true;
                    }
                    if ((aclCheck.checkValue3.equals(acl & aclCheck.checkValue3))) {
                        aclCheck.check3 = true;
                    }
                    if ((aclCheck.checkValue4.equals(acl & aclCheck.checkValue4))) {
                        aclCheck.check4 = true;
                    }
                }
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("権限付与失敗 errorCode: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Add(List<Long> accessMasks, String userAccount) {
        Long acl = 0x100000L;
        for (Long accessMask : accessMasks) {
            acl |= accessMask;
        }

        String hexAcl = "0x00" + Long.toHexString(acl);

        StringBuilder output = new StringBuilder();
        try {
            // 构建 smbcacls 命令
            String command = String.format(
                    "smbcacls %s \"%s\" -U %s%%%s %s \"ACL:%s:ALLOWED/OI|CI/%s\"",
                    sharePath, folder, user, password, "-a", userAccount, hexAcl);

            System.out.println("command : " + command);
            // 直接用 Runtime 执行命令
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", command });

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("権限付与失敗 errorCode: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void Delete(List<Long> accessMasks, String userAccount) {
        Long acl = 0x100000L;
        for (Long accessMask : accessMasks) {
            acl |= accessMask;
        }

        String hexAcl = "0x00" + Long.toHexString(acl);

        StringBuilder output = new StringBuilder();
        try {
            // 构建 smbcacls 命令
            String command = String.format(
                    "smbcacls %s \"%s\" -U %s%%%s %s \"ACL:%s:ALLOWED/OI|CI/%s\"",
                    sharePath, folder, user, password, "-D", userAccount, hexAcl);

            System.out.println("command : " + command);
            // 直接用 Runtime 执行命令
            Process process = Runtime.getRuntime().exec(new String[] { "/bin/bash", "-c", command });

            // 读取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("権限削除失敗 errorCode: " + exitCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
