package com.adtec.ncps.ftp;


import it.sauronsoftware.ftp4j.FTPClient; 
import it.sauronsoftware.ftp4j.FTPFile; 
import com.adtec.ncps.ftp.PathToolkit; 
import com.adtec.ncps.ftp.*;
import java.io.File; 
import java.util.List; 

/** 
* TTP客户端工具 
* 
* @author leizhimin 2009-11-30 10:20:17 
*/ 
public final class FTPToolkit { 

        private FTPToolkit() { 
        } 

        /** 
         * 创建FTP连接 
         * 
         * @param host         主机名或IP 
         * @param port         ftp端口 
         * @param username ftp用户名 
         * @param password ftp密码 
         * @return 一个客户端 
         */ 
        public static FTPClient makeFtpConnection(String host, int port, String username, String password) { 
                FTPClient client = new FTPClient(); 
                try { 
                        client.connect(host, port); 
                        client.login(username, password); 
                } catch (Exception e) { 
                        throw new FTPRuntimeException(e); 
                } 
                return client; 
        } 

        /** 
         * FTP下载文件到本地一个文件夹,如果本地文件夹不存在，则创建必要的目录结构 
         * 
         * @param client                    FTP客户端 
         * @param remoteFileName    FTP文件 
         * @param localFolderPath 存的本地目录 
         */ 
        public static void download(FTPClient client, String remoteFileName, String localFolderPath) { 
                int x = isExist(client, remoteFileName); 
                MyFtpListener listener = MyFtpListener.instance(FTPOptType.UP); 
                File localFolder = new File(localFolderPath); 
                if (localFolder.isFile()) { 
                        throw new FTPRuntimeException("所要的下载保存的地方是一个文件，无法保存！"); 
                } else { 
                        if (!localFolder.exists()) 
                                localFolder.mkdirs(); 
                } 
                if (x == FTPFile.TYPE_FILE) { 
                        String localfilepath = PathToolkit.formatPath4File(localFolderPath + File.separator + new File(remoteFileName).getName()); 
                        try { 
                        	    client.setType(client.TYPE_BINARY);
                                if (listener != null) 
                                        client.download(remoteFileName, new File(localfilepath), listener); 
                                else 
                                        client.download(remoteFileName, new File(localfilepath)); 
                        } catch (Exception e) { 
                                throw new FTPRuntimeException(e); 
                        } 
                } else { 
                        throw new FTPRuntimeException("所要下载的文件" + remoteFileName + "不存在！"); 
                } 
        } 

        /** 
         * FTP上传本地文件到FTP的一个目录下 
         * 
         * @param client                     FTP客户端 
         * @param localfile                本地文件 
         * @param remoteFolderPath FTP上传目录 
         */ 
        public static void upload(FTPClient client, File localfile, String remoteFolderPath) { 
                remoteFolderPath = PathToolkit.formatPath4FTP(remoteFolderPath); 
                MyFtpListener listener = MyFtpListener.instance(FTPOptType.UP); 
                try { 
                        client.changeDirectory(remoteFolderPath); 
                        client.setType(client.TYPE_BINARY);
                        if (!localfile.exists()) throw new FTPRuntimeException("所要上传的FTP文件" + localfile.getPath() + "不存在！"); 
                        if (!localfile.isFile()) throw new FTPRuntimeException("所要上传的FTP文件" + localfile.getPath() + "是一个文件夹！"); 
                        if (listener != null) 
                                client.upload(localfile, listener); 
                        else 
                                client.upload(localfile); 
                        client.changeDirectory("/"); 
                } catch (Exception e) { 
                        throw new FTPRuntimeException(e); 
                } 
        } 

        /** 
         * FTP上传本地文件到FTP的一个目录下 
         * 
         * @param client                     FTP客户端 
         * @param localfilepath        本地文件路径 
         * @param remoteFolderPath FTP上传目录 
         */ 
        public static void upload(FTPClient client, String localfilepath, String remoteFolderPath) { 
                File localfile = new File(localfilepath); 
                upload(client, localfile, remoteFolderPath); 
        } 

        /** 
         * 批量上传本地文件到FTP指定目录上 
         * 
         * @param client                     FTP客户端 
         * @param localFilePaths     本地文件路径列表 
         * @param remoteFolderPath FTP上传目录 
         */ 
        public static void uploadListPath(FTPClient client, List<String> localFilePaths, String remoteFolderPath) { 
                remoteFolderPath = PathToolkit.formatPath4FTP(remoteFolderPath); 
                try { 
                        client.changeDirectory(remoteFolderPath); 
                        MyFtpListener listener = MyFtpListener.instance(FTPOptType.UP); 
                        for (String path : localFilePaths) { 
                                File file = new File(path); 
                                if (!file.exists()) throw new FTPRuntimeException("所要上传的FTP文件" + path + "不存在！"); 
                                if (!file.isFile()) throw new FTPRuntimeException("所要上传的FTP文件" + path + "是一个文件夹！"); 
                                if (listener != null) 
                                        client.upload(file, listener); 
                                else 
                                        client.upload(file); 
                        } 
                        client.changeDirectory("/"); 
                } catch (Exception e) { 
                        throw new FTPRuntimeException(e); 
                } 
        } 

        /** 
         * 批量上传本地文件到FTP指定目录上 
         * 
         * @param client                     FTP客户端 
         * @param localFiles             本地文件列表 
         * @param remoteFolderPath FTP上传目录 
         */ 
        public static void uploadListFile(FTPClient client, List<File> localFiles, String remoteFolderPath) { 
                try { 
                        client.changeDirectory(remoteFolderPath); 
                        remoteFolderPath = PathToolkit.formatPath4FTP(remoteFolderPath); 
                        MyFtpListener listener = MyFtpListener.instance(FTPOptType.UP); 
                        for (File file : localFiles) { 
                                if (!file.exists()) throw new FTPRuntimeException("所要上传的FTP文件" + file.getPath() + "不存在！"); 
                                if (!file.isFile()) throw new FTPRuntimeException("所要上传的FTP文件" + file.getPath() + "是一个文件夹！"); 
                                if (listener != null) 
                                        client.upload(file, listener); 
                                else 
                                        client.upload(file); 
                        } 
                        client.changeDirectory("/"); 
                } catch (Exception e) { 
                        throw new FTPRuntimeException(e); 
                } 
        } 


        /** 
         * 判断一个FTP路径是否存在，如果存在返回类型(FTPFile.TYPE_DIRECTORY=1、FTPFile.TYPE_FILE=0、FTPFile.TYPE_LINK=2) 
         * 如果文件不存在，则返回一个-1 
         * 
         * @param client         FTP客户端 
         * @param remotePath FTP文件或文件夹路径 
         * @return 存在时候返回类型值(文件0，文件夹1，连接2)，不存在则返回-1 
         */ 
        public static int isExist(FTPClient client, String remotePath) { 
                remotePath = PathToolkit.formatPath4FTP(remotePath); 
                int x = -1; 
                FTPFile[] list = null; 
                try { 
                        list = client.list(remotePath); 
                } catch (Exception e) { 
                        return -1; 
                } 
                if (list.length > 1) return FTPFile.TYPE_DIRECTORY; 
                else if (list.length == 1) { 
                        FTPFile f = list[0]; 
                        if (f.getType() == FTPFile.TYPE_DIRECTORY) return FTPFile.TYPE_DIRECTORY; 
                        //假设推理判断 
                        String _path = remotePath + "/" + f.getName(); 
                        try { 
                                int y = client.list(_path).length; 
                                if (y == 1) return FTPFile.TYPE_DIRECTORY; 
                                else return FTPFile.TYPE_FILE; 
                        } catch (Exception e) { 
                                return FTPFile.TYPE_FILE; 
                        } 
                } else { 
                        try { 
                                client.changeDirectory(remotePath); 
                                return FTPFile.TYPE_DIRECTORY; 
                        } catch (Exception e) { 
                                return -1; 
                        } 
                } 
        } 

        /** 
         * 关闭FTP连接，关闭时候像服务器发送一条关闭命令 
         * 
         * @param client FTP客户端 
         * @return 关闭成功，或者链接已断开，或者链接为null时候返回true，通过两次关闭都失败时候返回false 
         */ 

        public static boolean closeConnection(FTPClient client) { 
                if (client == null) return true; 
                if (client.isConnected()) { 
                        try { 
                                client.disconnect(true); 
                                return true; 
                        } catch (Exception e) { 
                                try { 
                                        client.disconnect(false); 
                                } catch (Exception e1) { 
                                        e1.printStackTrace(); 
                                        return false; 
                                } 
                        } 
                } 
                return true; 
        } 
}

 



