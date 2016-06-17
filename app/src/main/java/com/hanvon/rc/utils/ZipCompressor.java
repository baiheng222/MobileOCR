package com.hanvon.rc.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.apache.tools.zip.ZipFile;


/**
 * @ClassName: ZipCompressor
 * @CreateTime Apr 28, 2013 1:12:16 PM
 * @author : Mayi
 * @Description: 压缩文件的通用工具类-采用org.apache.tools.zip.ZipOutputStream实现，较复杂。
 *
 */
public class ZipCompressor {
    static final int BUFFER = 8192;
    private File zipFile;

    /**
     * 压缩文件构造函数
     * @param pathName 压缩的文件存放目录
     */
    public ZipCompressor(String pathName) {
        zipFile = new File(pathName);
    }

    /**
     * 执行压缩操作
     * @param srcPathName 被压缩的文件/文件夹
     */
    public void compressExe(String srcPathName) {
        File file = new File(srcPathName);
        if (!file.exists()){
            throw new RuntimeException(srcPathName + "不存在！");
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(zipFile);
            CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream,new CRC32());
            ZipOutputStream out = new ZipOutputStream(cos);
            String basedir = "";
            compressByType(file, out, basedir);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.i("执行压缩操作时发生异常:"+e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断是目录还是文件，根据类型（文件/文件夹）执行不同的压缩方法
     * @param file
     * @param out
     * @param basedir
     */
    private void compressByType(File file, ZipOutputStream out, String basedir) {
            /* 判断是目录还是文件 */
        if (file.isDirectory()) {
            LogUtil.i("压缩：" + basedir + file.getName());
            this.compressDirectory(file, out, basedir);
        } else {
            LogUtil.i("压缩：" + basedir + file.getName());
            this.compressFile(file, out, basedir);
        }
    }

    /**
     * 压缩一个目录
     * @param dir
     * @param out
     * @param basedir
     */
    private void compressDirectory(File dir, ZipOutputStream out, String basedir) {
        if (!dir.exists()){
            return;
        }

        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
                /* 递归 */
            compressByType(files[i], out, basedir + dir.getName() + "/");
        }
    }

    /**
     * 压缩一个文件
     * @param file
     * @param out
     * @param basedir
     */
    private void compressFile(File file, ZipOutputStream out, String basedir) {
        if (!file.exists()) {
            return;
        }
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            ZipEntry entry = new ZipEntry(basedir + file.getName());
            out.putNextEntry(entry);
            int count;
            byte data[] = new byte[BUFFER];
            while ((count = bis.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            bis.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * @param:zipFilePath String,releasePath String
     * @return void
     * @description:Decompress A File
     */
    /*
    @SuppressWarnings("unchecked")
    public static void decompressFile(String zipFilePath,String releasePath) throws IOException
    {
        ZipFile zipFile = new ZipFile(zipFilePath);
        Enumeration<ZipEntry> enumeration = zipFile.getEntries();
        InputStream inputStream = null;
        FileOutputStream fileOutputStream = null;
        ZipEntry zipEntry = null;
        String zipEntryNameStr ="";
        String[] zipEntryNameArray = null;
        while (enumeration.hasMoreElements()) {
            zipEntry = enumeration.nextElement();
            zipEntryNameStr = zipEntry.getName();
            zipEntryNameArray = zipEntryNameStr.split("/");
            String path = releasePath;
            File root = new File(releasePath);
            if(!root.exists())
            {
                root.mkdir();
            }
            for (int i = 0; i < zipEntryNameArray.length; i++) {
                if(i<zipEntryNameArray.length-1)
                {
                    path = path + File.separator+zipEntryNameArray[i];
                    new File(StringTools.conversionSpecialCharacters(path)).mkdir();
                }
                else
                {
                    if(StringTools.conversionSpecialCharacters(zipEntryNameStr).endsWith(File.separator))
                    {
                        new File(releasePath + zipEntryNameStr).mkdir();
                    }
                    else
                    {
                        inputStream = zipFile.getInputStream(zipEntry);
                        fileOutputStream = new FileOutputStream(new File(
                                StringTools.conversionSpecialCharacters(releasePath + zipEntryNameStr)));
                        byte[] buf = new byte[1024];
                        int len;
                        while ((len = inputStream.read(buf)) > 0)
                        {
                            fileOutputStream.write(buf, 0, len);
                        }
                        inputStream.close();
                        fileOutputStream.close();
                    }
                }
            }
        }
        zipFile.close();
    }
    */


    /**
     * 调用org.apache.tools.zip实现解压缩，支持目录嵌套和中文名
     * 也可以使用java.util.zip不过如果是中文的话，解压缩的时候文件名字会是乱码。原因是解压缩软件的编码格式跟java.util.zip.ZipInputStream的编码字符集(固定是UTF-8)不同
     *
     * @param zipFileName
     *            要解压缩的文件
     * @param outputDirectory
     *            要解压到的目录
     * @throws Exception
     */
    public static ArrayList<String> unZip(String zipFileName, String outputDirectory)
    {
        boolean flag = false;
        ArrayList<String> filelist = new ArrayList<String>();
        try
        {
            ZipFile zipFile = new org.apache.tools.zip.ZipFile(zipFileName);
            java.util.Enumeration e = zipFile.getEntries();
            ZipEntry zipEntry = null;
            createDirectory(outputDirectory, "");
            while (e.hasMoreElements())
            {
                zipEntry = (ZipEntry) e.nextElement();
                LogUtil.i("unziping " + zipEntry.getName());
                if (zipEntry.isDirectory())
                {
                    String name = zipEntry.getName();
                    name = name.substring(0, name.length() - 1);
                    File f = new File(outputDirectory + File.separator + name);
                    f.mkdir();
                    LogUtil.i("创建目录：" + outputDirectory + File.separator + name);
                }
                else
                {
                    String fileName = zipEntry.getName();
                    fileName = fileName.replace('\\', '/');
                    // System.out.println("测试文件1：" +fileName);
                    if (fileName.indexOf("/") != -1)
                    {
                        createDirectory(outputDirectory, fileName.substring(0, fileName.lastIndexOf("/")));
                        fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
                    }

                    File f = new File(outputDirectory + File.separator + zipEntry.getName());
                    LogUtil.i("unzip file name is " + f.getAbsolutePath());
                    f.createNewFile();
                    filelist.add(f.getAbsolutePath());
                    InputStream in = zipFile.getInputStream(zipEntry);
                    FileOutputStream out = new FileOutputStream(f);

                    byte[] by = new byte[1024];
                    int c;
                    while ((c = in.read(by)) != -1)
                    {
                        out.write(by, 0, c);
                    }
                    out.close();
                    in.close();
                }
                flag = true;
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        //return flag;
        return filelist;
    }

    /**
     * 创建目录
     *
     * @param directory
     *            父目录
     * @param subDirectory
     *            子目录
     */
    private static void createDirectory(String directory, String subDirectory)
    {
        String dir[];
        File fl = new File(directory);
        try
        {
            if (subDirectory == "" && fl.exists() != true)
            {
                fl.mkdir();
            }
            else if (subDirectory != "")
            {
                dir = subDirectory.replace('\\', '/').split("/");
                for (int i = 0; i < dir.length; i++)
                {
                    File subFile = new File(directory + File.separator + dir[i]);
                    if (subFile.exists() == false)
                    {
                        subFile.mkdir();
                    }
                    directory += File.separator + dir[i];
                }
            }
        }
        catch (Exception ex)
        {
            System.out.println(ex.getMessage());
        }
    }


}