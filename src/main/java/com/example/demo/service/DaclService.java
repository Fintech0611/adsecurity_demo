package com.example.demo.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DaclService {

    private final String sharePath = "//192.168.3.100/共有フォルダ";
    private final String folder = "test";
    private final String user = "Administrator@jerry.com";
    private final String password = "Qin711019";

    public void Add(List<Long> accessMasks, String userAccount) {
        Long acl = 0x100000L;
        for (Long accessMask : accessMasks) {
            acl |= accessMask;
        }

        String hexAcl = Long.toHexString(acl);

        StringBuilder output = new StringBuilder();
        try {
            // 构建 smbcacls 命令
            String command = String.format(
                    "smbcacls %s \"%s\" -U %s%%%s %s ACL:%s:ALLOWED/OC|OI/%s",
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

        String hexAcl = Long.toHexString(acl);

        StringBuilder output = new StringBuilder();
        try {
            // 构建 smbcacls 命令
            String command = String.format(
                    "smbcacls %s \"%s\" -U %s%%%s %s ACL:%s:ALLOWED/OC|OI/%s",
                    sharePath, folder, user, password, "-d", userAccount, hexAcl);

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
}
