package org.example;

import com.jcraft.jsch.*;

import java.io.*;
import java.util.Vector;

public class Sftp {

    private Session session = null;
    private Channel channel = null;
    private ChannelSftp channelSftp = null;

    public static Sftp create(String host, String userName, String password, int port) throws JSchException {
        return new Sftp(host, userName, password, port);
    }

    private Sftp(String host, String userName, String password, int port) throws JSchException {

        JSch jSch = new JSch();

        session = jSch.getSession(userName, host, port);
        session.setPassword(password);

        // 프로퍼티 설정
        java.util.Properties config = new java.util.Properties();
        // 접속 시 hostkeychecking 여부
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        session.connect();
    }

    // SFTP 연결 메소드
    public void connect() throws JSchException{
        // SFTP 접속
        channel = session.openChannel("sftp");
        // SFTP 연결
        channel.connect();

        channelSftp = (ChannelSftp) channel;
    }

    public void mkdir(String dir, String mkdirName) throws SftpException {

        if (!this.exists(dir + "/" + mkdirName)) {
            channelSftp.cd(dir);
            channelSftp.mkdir(mkdirName);
        }
    }

    public boolean exists(String path) {

        Vector res = null;

        try {
            res = channelSftp.ls(path);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                return false;
            }
        }
        return res != null && !res.isEmpty();
    }

    // SFTP로 전달 파일 업로드
    public boolean upload(String dir, File file) throws SftpException, IOException {

        boolean isUpload = false;

        try(FileInputStream in = new FileInputStream(file);){

            // 해당 디렉토리 접근
            channelSftp.cd(dir);
            // 전달 파일 업로드
            channelSftp.put(in, file.getName());

            // 파일 업로드 성공 여부
            if (this.exists(dir +"/"+file.getName())) {
                isUpload = true;
            }
        }

        return isUpload;
    }

    // 해당 디렉토리 접근하여 파일 다운로드
    public void download(String dir, String downloadFileName, String path) throws SftpException, IOException{

        // 해당 디렉토리 접근
        channelSftp.cd(dir);

        // SFTP로 해당 파일 다운로드
        try(InputStream in = channelSftp.get(downloadFileName);
            FileOutputStream out = new FileOutputStream(new File(path));) {

            int i;
            while ((i = in.read()) != -1) {
                out.write(i);
            }
        }
    }

    // SFTP 연결 종료 메소드
    public void disconnect() {
        channelSftp.quit();
        session.disconnect();
    }
}
