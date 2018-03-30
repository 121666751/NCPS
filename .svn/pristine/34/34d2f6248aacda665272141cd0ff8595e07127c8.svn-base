package com.adtec.ncps.ftp;

import it.sauronsoftware.ftp4j.FTPClient; 
import com.adtec.ncps.ftp.FTPToolkit; 

/** 
* 简单测试下 
* 
* @author leizhimin 2009-11-30 12:25:42 
*/ 
public class Test { 
        public static void main(String args[]) throws Exception { 
                String ftpip = "160.161.12.181"; 
                int ftpport = 21; 
                String ftpuser = "snqt"; 
                String ftppswd = "snqt"; 

                FTPClient client = FTPToolkit.makeFtpConnection(ftpip, ftpport, ftpuser, ftppswd); 
                FTPToolkit.upload(client, "C:\\splash.bmp", "/home/snqt/tmp"); 
                FTPToolkit.download(client, "/home/snqt/tmp/splash.bmp", "D:\\"); 
                FTPToolkit.closeConnection(client); 
        } 
}
