package com.example.demo.service;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.FileAttributes;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;

import io.micrometer.common.util.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Service
public class SmbService {

    @Value("${smb.host}")
    private String host;

    @Value("${smb.username}")
    private String username;

    @Value("${smb.password}")
    private String password;

    @Value("${smb.domain}")
    private String domain;

    @Value("${smb.share}")
    private String shareName;

    /**
     * SMB 共有フォルダへ接続
     */
    private DiskShare connect() throws IOException {
        SMBClient client = new SMBClient();
        Connection connection = client.connect(host);
        AuthenticationContext ac = new AuthenticationContext(username, password.toCharArray(), domain);
        Session session = connection.authenticate(ac);
        return (DiskShare) session.connectShare("共有フォルダ");
    }

    /**
     * フォルダ内のファイル・フォルダ一覧を取得
     * 
     * @param folderPath フォルダパス
     */
    public List<Map<String, Object>> listFiles(String folderPath) {
        List<Map<String, Object>> fileList = new ArrayList<>();
        try (DiskShare share = connect()) {
            if (StringUtils.isEmpty(folderPath) || folderPath.equals("/")) {
                folderPath = "";
            } else if (!share.folderExists(folderPath)) {
                throw new IOException("フォルダが存在しません: " + folderPath);
            }

            // ルートパスを指定 ("", "/" どちらも可)
            for (FileIdBothDirectoryInformation item : share.list(folderPath)) {
                String name = item.getFileName();
                if (!name.equals(".") && !name.equals("..")) {
                    Map<String, Object> fileInfo = new HashMap<>();
                    fileInfo.put("name", name);
                    // フォルダかファイルかを判定
                    if (item.getFileAttributes() == (item.getFileAttributes()
                            & FileAttributes.FILE_ATTRIBUTE_DIRECTORY.getValue())) {
                        fileInfo.put("type", "folder");
                    } else {
                        fileInfo.put("type", "file");
                    }
                    fileList.add(fileInfo);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileList;
    }

    /**
     * ファイルの読み込み
     * 
     * @param filePath ファイルパス
     */
    public String readFile(String filePath) {
        try (DiskShare share = connect()) {
            if (share.fileExists(filePath)) {
                File file = share.openFile(filePath,
                        new HashSet<>(Arrays.asList(AccessMask.GENERIC_READ)),
                        null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN, null);

                byte[] buffer = new byte[(int) file.getFileInformation().getStandardInformation().getEndOfFile()];
                file.read(buffer, 0, 0, buffer.length);
                return new String(buffer, StandardCharsets.UTF_8);
            } else {
                throw new IOException("ファイルが存在しません: " + filePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * ファイルの書き込み
     */
    public void writeFile(String filePath, String content) throws IOException {
        try (DiskShare share = connect()) {
            File file = share.openFile(filePath,
                    new HashSet<>(Arrays.asList(AccessMask.GENERIC_WRITE)),
                    null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_CREATE, null);

            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            file.write(contentBytes, 0, 0, contentBytes.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * フォルダの作成
     * 
     * @param folderPath フォルダパス
     */
    public void createFolder(String folderPath) {
        try (DiskShare share = connect()) {
            if (!share.folderExists(folderPath)) {
                share.mkdir(folderPath);
                System.out.println("フォルダ作成完了: " + folderPath);
            } else {
                System.out.println("フォルダは既に存在しています: " + folderPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * フォルダの削除
     * 
     * @param folderPath フォルダパス
     */
    public void deleteFolder(String folderPath) {
        try (DiskShare share = connect()) {
            if (share.folderExists(folderPath)) {
                share.rmdir(folderPath, true);
                System.out.println("フォルダ削除完了: " + folderPath);
            } else {
                System.out.println("フォルダが存在しません: " + folderPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
