package org.example;

import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        Sftp sftp = null;

        try {
            // 해당 서버에서 SFTP 연결할 HOSTNAME, IP, PWD, PORT 설정
            sftp = Sftp.create("192.168.0.121", "root", "fcpass604*", 31622);

            // SFTP 연결
            sftp.connect();

            // 업로드 파일 지정
            File file = new File("./sample.txt");
            // SFTP로 전송 할 파일 경로 지정 및 업로드 파일 전송
            sftp.upload("/usr/testDir/upload_file", file);

            System.out.println("upload success");

        } catch (JSchException | SftpException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            // SFTP 종료
            if(sftp != null) sftp.disconnect();
        }
    }
}