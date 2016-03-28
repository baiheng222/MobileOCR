package com.hanvon.mobileocr.utils;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

//import com.hanvon.wboard.bean.FolderMessage;

import com.hanvon.mobileocr.wboard.bean.FolderMessage;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil
{
	
	private static final String TAG = "FileUtil";
	private static String sdCardPath = "";
	private static String storagePath = "";
	private static final String DEF_FOLDER_NAME = "MyCamera";
	private static final String TEMP_FOLDER_NAME = "MyTemp";
	private static final String SYS_CAMERA = "Camera";
	
	public static int REPEATE_COUNT = 0;
	
	/**判断sd卡是否存在
	 * @return
	 */
	public static boolean isExistSDCard() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
		   return true;  
		}else  
		  return false;  
	}
	public static String getSDCadrPath(){
		File sdDir = null;
		if(isExistSDCard()){
			sdDir = Environment.getExternalStorageDirectory();//获取sd卡目录
			sdCardPath = sdDir.getAbsolutePath();
			return sdCardPath;
		}else{
			return null;
		}
		
	}
	
	/**
	 * exist
	 * @param filePath
	 * @return
	 */
	public static boolean exit(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}
	
	
	/**初始化临时SDCARD临时保存路径
	 * @return
	 */
	private static String initTempPath(){
		sdCardPath = getSDCadrPath() ;
		if(storagePath.equals("")){
			storagePath = sdCardPath +"/" + TEMP_FOLDER_NAME;
			File f = new File(storagePath);
			if(!f.exists()){
				f.mkdir();
			}
		}
		return storagePath;
	}
	/**初始化临时SDCARD最终保存路径
	 * @return
	 */
	private static String initPath(){
		if(storagePath.equals("")){
			storagePath = sdCardPath +"/" + DEF_FOLDER_NAME;
			File f = new File(storagePath);
			if(!f.exists()){
				f.mkdir();
			}
		}
		return storagePath;
	}
	
	
	/**保存Bitmap到sdcard临时文件夹 并返回图片的存储路径
	 * @param b
	 */
	public static String saveBitmap(Bitmap b){
		String path = initTempPath();
		long dataTake = System.currentTimeMillis();
		String jpegName = path + "/" + dataTake +".jpg";
		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "saveBitmap to MyTmep:成功");
		} catch (IOException e) {
			Log.i(TAG, "saveBitmap to MyTmep:失败");
			e.printStackTrace();
		}
		return jpegName;
	}
	/**保存Bitmap到sdcard的指定路径 并返回图片的存储路径
	 * @param b
	 */
	public static String saveBitmapToGivenPath(Bitmap b, String dst){
		String jpegName = dst;
		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "saveBitmap to MyTmep:成功");
		} catch (IOException e) {
			Log.i(TAG, "saveBitmap to MyTmep:失败");
			e.printStackTrace();
		}
		return jpegName;
	}
	/**保存Bitmap到sdcard的Mycamera文件夹 并返回图片的存储路径
	 * @param b
	 */
	public static String saveBitmap(Bitmap b, String fileName){
		
		String path = sdCardPath + "/" + DEF_FOLDER_NAME;
		String jpegName = path + "/" + fileName +".jpg";
//		long dataTake = System.currentTimeMillis();
		
		Log.i(TAG, "saveBitmap:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "saveBitmap:成功");
		} catch (IOException e) {
			Log.i(TAG, "saveBitmap:失败");
			e.printStackTrace();
		}
		return jpegName;
	}
	
	/**保存Bitmap到sdcard的我的图库的全部文件夹 并返回图片的存储路径
	 * @param b
	 */
	public static String saveBitmapToGallery(Bitmap b, String fileName, String folderName){
		String jpegName ;
		String path = sdCardPath +"/" + "universcan/MyGallery/" + folderName;
		File f = new File(path);
		if(!f.exists()){
			f.mkdir();
		}		
		if(fileName.isEmpty()){
			int count = findUnameingName(path);
			String str_count=  String.valueOf(count+1);
			jpegName = path +"/" + "未命名"+ str_count +".jpg"; 			
			
		}else{
			jpegName = path + "/" + fileName +".jpg";
		}		
		Log.i(TAG, "saveBitmap to MyGallery :jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "saveBitmap to MyGallery :成功");
			return jpegName;
		} catch (IOException e) {
			Log.i(TAG, "saveBitmap to MyGallery :失败");
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**保存Bitmap到系统相册 并返回图片的存储路径
	 * @param b
	 */
	public static String saveBitmapToCamrea(Bitmap b, String fileName){
		String jpegName;
		String path = sdCardPath +"/DCIM/"+ SYS_CAMERA;
//		jpegName = path + "/bc_" + dataTake +".jpg"; 
		File f = new File(path);
		if(!f.exists()){
			f.mkdirs();
		}
		if(fileName.isEmpty()){
			int count = findUnameingName(path);
			String str_count=  String.valueOf(count+1);
			jpegName = path +"/" + "未命名"+ str_count +".jpg"; 	
		}else{
			String oldName = fileName + ".jpg";//默认保存的名字
			if(isNameRepeate(oldName, f)){//查找该路径下是否有重复的文件
				//生成新的文件名			
				long dataTake = System.currentTimeMillis();
				String newName = fileName + "-" + dataTake +".jpg";
				jpegName = newName;
			}else{
				jpegName = path + "/" + fileName +".jpg";
			}
			
		}
		Log.i(TAG, "saveBitmapto系统相册:jpegName = " + jpegName);
		try {
			FileOutputStream fout = new FileOutputStream(jpegName);
			BufferedOutputStream bos = new BufferedOutputStream(fout);
			b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			bos.flush();
			bos.close();
			Log.i(TAG, "saveBitmap to系统相册:成功");
			return jpegName;
		} catch (IOException e) {
			Log.i(TAG, "saveBitmap to系统相册:失败");
			e.printStackTrace();
			jpegName = null;
		}
		if(b != null && !b.isRecycled()){
			b.recycle();
			b = null;
		}
		return jpegName;
	}
	
	/**
	 * readFileBytes
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static byte[] readFileBytes(String filePath) throws IOException
	{
		if (!exit(filePath)) {
			return null;
		}
		byte[] data = null;
		FileInputStream in = new FileInputStream(new File(filePath));
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] tmp = new byte[1024 * 10];
		int len = 0;
		while ((len = in.read(tmp)) != -1) {
			baos.write(tmp, 0, len);
		}
		in.close();
		data = baos.toByteArray();
		return data;
	}
	/**
     * 删除SD卡上的指定路径的文件
     * 若是文件夹 则删除文件夹和文件夹里的所有文件
     * @param fileName 
     */ 

    public static void deleteSDFile(String path) {

        File file = new File(path);
        if (file == null || !file.exists() ){
        	return ; 
        }
        if(file.isFile())  {
        	if(file.exists()){//文件存在
        		file.delete();
        	}
        	
        	return;
        }
    	if( file.isDirectory()) {
    		File[] chileFiles = file.listFiles();
    		if(chileFiles == null || chileFiles.length == 0){
    			file.delete();
    			return; 
    		}
    		for(int i=0; i<chileFiles.length; i++){//先删除文件夹中的文件
    			deleteSDFile(chileFiles[i].getAbsolutePath());
    		}
    		file.delete();//最后删除文件夹
    	}
        
         
    }
    
    
    
    /**
	 * 查找某文件夹下的未命名文件的最大数字
	 * @param path
	 * @param uname
	 * @return
	 */
	public static int findUnameingName(String path){
		String parentPath = path;
		int result = 0;
		File file = new File(path);
		if(file.isFile()){
			parentPath = file.getParent();
//			System.out.println(parentPath+"----------");
		}
		File[] childFiles = file.listFiles();
		if(childFiles == null || childFiles.length == 0){
			result = 0;
		}else{
			for(int i=0; i<childFiles.length; i++){
				int temp = getNameCount(childFiles[i].getName()); //未命名文件的数字	
//				System.out.println(temp+ "-temp---"+childFiles[i].getName());
				if(result <= temp ){
					result = temp;
				}
//				System.out.println("come++for" + result);
			}
		}
		return result;
		
	}
	
	/**
	 * 获取字符串未命名的数字
	 * @param str
	 * @return
	 */
	private static int getNameCount(String str){
		String src = str;
		String temp;
		int result = -1;
		int dotIndex = str.lastIndexOf(".");
//		System.out.println(dotIndex+ "dot");
		int unIndex = -1;
		if(src.startsWith("未命名")){
			unIndex = src.indexOf("未命名");//返回的是第一个字符的下表
//			System.out.println(unIndex +"un ------" );
			temp = src.substring(unIndex+1+2, dotIndex);	
			Pattern pattern = Pattern.compile("[0-9]*");
			Matcher matcher = pattern.matcher(temp);
			if(matcher.matches()){
//				System.out.println("temp");
				result = Integer.parseInt(temp);
//				System.out.println("getCount____"+result);
			}
			
		}
		return result;
		
	}
    /**  
     *   
     * @param src 被复制的文件路径 
     * @param des 复制的目标文件夹路径  
     * @param rewrite 是否重新创建文件  
     *   
     * <p>文件的复制操作方法  
     */  
//    public static void copySDFile(String src,String des, Boolean rewrite){
    public static String copySDFileToCamera(String src){
    	File fromFile = new File(src);
		String des = sdCardPath +"/DCIM/"+ SYS_CAMERA;	//系统相册文件夹
		long dataTake = System.currentTimeMillis();
		String jpegName = des + "/bc_" + dataTake +".jpg";
//		File toFile = new File(des);
		File toFile = new File(jpegName);
    	if(!fromFile.exists()){
    		return null;
    	}
    	if(!fromFile.isFile()){
    		return null;
    	}
    	if(!fromFile.canRead()){
    		return null;
    	}
    	if(!toFile.getParentFile().exists()){
    		toFile.getParentFile().mkdirs();
    	}
//    	if(toFile.exists() && rewrite){
//    		toFile.delete();
//    	}
    	try {
			FileInputStream in = new FileInputStream(fromFile);
			FileOutputStream out = new FileOutputStream(toFile);
			byte[] b = new byte[1024 * 10];
			int len;
			while((len = in.read(b)) != -1 ){
				out.write(b, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
    	return jpegName;
    }
    
   
    public static String copySDFile(String src, String dst, String name){
    	String path = null;
    	String fileName  = null;
    	if( name == null || name.equals("")){//若没有输入文件名
    		fileName = src.substring(src.lastIndexOf("/")+1); //取得源文件名
    	}else{
    		fileName = name; 
    	}
    	
    	File fromFile = new File(src);
		path = dst + "/" + fileName;
		File toFile = new File( dst + "/" + fileName);
    	if(!toFile.getParentFile().exists()){
    		toFile.getParentFile().mkdirs();
    	}
    	if(toFile.exists()){//如果该文件已经存在   		
    		return null;
    	}else{
    		try {
    			FileInputStream in = new FileInputStream(fromFile);
    			FileOutputStream out = new FileOutputStream(toFile);
    			byte[] b = new byte[1024 * 10];
    			int len;
    			while((len = in.read(b)) != -1 ){
    				out.write(b, 0, len);
    			}
    			in.close();
    			out.close();
    			
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
    		}catch(IOException e){
    			e.printStackTrace();
    		}
    		return path;
    	}
    }
    
    /**
     * 使用文件通道的方式复制文件
     * @param s 源文件
     * @param t 复制到的新文件
     */

     public static void fileChannelCopy(File s, File t) {
         FileInputStream fi = null;
         FileOutputStream fo = null;
         FileChannel in = null;
         FileChannel out = null;
         try {

             fi = new FileInputStream(s);
             fo = new FileOutputStream(t);
             in = fi.getChannel();//得到对应的文件通道
             out = fo.getChannel();//得到对应的文件通道
             in.transferTo(0, in.size(), out);//连接两个通道，并且从in通道读取，然后写入out通道
         } catch (IOException e) {
             e.printStackTrace();
         } finally {
             try {
                 fi.close();
                 in.close();
                 fo.close();
                 out.close();
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
     }
	/**
	 * @param fromFile 源文件，若是文件夹，则移动文件夹下的全部文件
	 * @param toFolder 目标文件
	 * @param fileName 移动后的文件名，若是null 表示文件名不变，否则文件名改变
	 * @return
	 */
	public static List<String> fileMove(String fromFile, String toFolder, String fileName){// 移动文件至目标文件夹，如果是移动的是文件夹，则移动文件夹内的全部文件，否则移动文件至文件夹
		
		String newPath = null;
		int count= 0;
		File srcDir = new File(fromFile);
		List<String> list = new ArrayList<String>();
		
		if(srcDir.isDirectory()){//如果是目录
			File[] files = srcDir.listFiles();// 将文件或文件夹放入文件集
			if (files == null)// 判断文件集是否为空
				return null;
			File moveDir = new File(toFolder);// 创建目标目录
			if (!moveDir.exists()) {// 判断目标目录是否存在
				moveDir.mkdirs();// 不存在则创建
			}
			for (int i = 0; i < files.length; i++) {// 遍历文件集
				if (files[i].isDirectory()) {// 如果是文件夹或目录,则递归调用fileMove方法，直到获得目录下的文件
					fileMove(files[i].getPath(), toFolder + "/" + files[i].getName(),null);// 递归移动文件
					files[i].delete();// 删除文件所在原目录
				}
				
				/*File dstDir = new File(toFolder);
				File moveFile = null;
				String oldName = files[i].getName();
				if(isNameRepeate(oldName, dstDir)){
					count++;
					//生成新的文件名					
					String newName = oldName.substring(0,oldName.lastIndexOf("."))+ "-" + System.currentTimeMillis()+ oldName.substring(oldName.lastIndexOf("."));
					moveFile = new File(dstDir.getPath()+"/"+ newName);	
				}else{
					moveFile = new File(dstDir.getPath()+"/"+oldName);
				}
				files[i].renameTo(moveFile);	
				list.add(moveFile.getAbsolutePath());*/
				File dstDir = new File(toFolder);
				File moveFile = getRemoveFile(files[i], dstDir,null);
				files[i].renameTo(moveFile);	
				list.add(moveFile.getAbsolutePath());
			}
			
		}
		else{//如果是文件
			
			File dstDir = new File(toFolder);
			File moveFile = getRemoveFile(srcDir, dstDir,fileName);
			srcDir.renameTo(moveFile);
			list.add(moveFile.getAbsolutePath());
			/*File dstDir = new File(toFolder);
			File moveFile = null;
			String srcFileName = srcDir.getName();
			if(isNameRepeate(srcFileName, dstDir)){
				count++;
				//生成新的文件名					
				String newName = srcFileName.substring(0,srcFileName.lastIndexOf(".")) + "-" + System.currentTimeMillis()+ srcFileName.substring(srcFileName.lastIndexOf("."));
				moveFile = new File(dstDir.getPath()+"/"+ newName);	
			}else{
				 moveFile = new File(dstDir.getPath()+"/"+ srcFileName);
			}
			srcDir.renameTo(moveFile);
			list.add(newPath);*/
			
//			if(dstDir.isDirectory()){
//				File moveFile = new File(dstDir.getPath() + "/" + srcDir.getName());
//				newPath = removeFileFromFolder(fromFile, moveFile, dstDir);
//			}
//			list.add(newPath);
		}
//		REPEATE_COUNT = count;
		return list;
	}

	/**
	 * @param src 源文件名
	 * @param dst 目的文件夹
	 * @param fileName 文件名
	 * @return
	 */
	private static File getRemoveFile(File src, File dst, String fileName){
		String oldName = src.getName();
		if(fileName != null&& !fileName.isEmpty()){
			oldName = fileName + oldName.substring(oldName.lastIndexOf("."));
		}
		File moveFile = null;
		if(isNameRepeate(oldName, dst)){
//			REPEATE_COUNT++;
			//生成新的文件名
			String newName = oldName.substring(0,oldName.lastIndexOf("."))+ "-" + System.currentTimeMillis()+ oldName.substring(oldName.lastIndexOf("."));
			moveFile = new File(dst.getPath()+"/"+ newName);
			
		}else{
			moveFile = new File(dst.getPath()+"/"+oldName);
		}
		return moveFile;
		
	}
	 public static void renameFile(String oldFolderPath, String dstFolderPath ){
	    	File srcfile = new File(oldFolderPath);
	    	File dstfile = new File(dstFolderPath);
	    	srcfile.renameTo(dstfile);
	    }
	public static boolean isNameRepeate(String name, File file){//查看file中是否存在文件名为name的文件
		
		if(file.isDirectory()){
			for(int i = 0 ;i<file.listFiles().length;i++){
				if(file.listFiles()[i].getName().equals(name)){
					return true;
				}
				
			}
		}
		return false;	
	}

	
	private static String removeFileFromFolder(String fromFile, File file, File moveDir) {
		String newName = file.getName();
		if(newName.equals(fromFile.substring(fromFile.lastIndexOf("/")+1))){
			 newName = newName.substring(0,newName.lastIndexOf(".")) + "-"+ System.currentTimeMillis() + newName.substring(newName.lastIndexOf("."));
		}
		File moveFile = new File(moveDir.getPath() + "/" + newName);// 将文件目录放入移动后的目录
		
		File oriFile = new File(fromFile);
		boolean ok = oriFile.renameTo(moveFile);// 移动文件
//		System.out.println(file + " 移动成功" );
		return moveFile.getAbsolutePath();	
	}
	public static void writeFileBytes(byte[] buffer, String filePath) throws IOException
	{
		File file = new File(filePath);
		if (exit(filePath)) {
			file.delete();
		} else {
			createFile(filePath);
		}
		FileOutputStream out = new FileOutputStream(file);
		out.write(buffer);
		out.close();
	}
	
	public static File createFile(String filePath) throws IOException
	{
    	String[] dirs = filePath.split("/");
    	String path = "/";
    	for (int i = 1; i < dirs.length-1; i++) {
    		path += dirs[i] + "/";
    		if (!exit(path)) {
				(new File(path)).mkdirs();
			}
		}
        File file = new File(filePath);
        file.createNewFile();
        return file;
    }
	
	public static int createFolder(String name){
		int result = 0;
		String path = getSDCadrPath() + "/universcan/MyGallery/" + name;
		File folder = new File(path);
		  if (!folder.exists()) {
			  folder.mkdirs();
			  result = 1;
		  }else{
			  result = 0;
		  }
		return result;
		
	}
	
	//剪切、移动操作的本质是对文件存储路径的修改.
	/**
     * 移动文件
     * @param source  需要移动的文件的路径
     * @param destination  目标路径
     */
    public static void moveFile(String source, String destination)
    {
            new File(source).renameTo(new File(destination));
    }
     
    /**
     * 移动文件
     * @param source 需要移动的文件
     * @param destination  目标文件
     */
    public static void moveFile(File source, File destination)
    {
            source.renameTo(destination); 
    }
	
    
    public static List<String> getFolderName(String path){
		List<String> list = new ArrayList<String>();
		File file = new File(path);
		if(file.isFile()){
			return null;
		}else{
			File childFiles[] = file.listFiles();
			if(childFiles == null || childFiles.length == 0){
				return null;
			}else{
				for(int i = 0; i<childFiles.length; i++){
					if(childFiles[i].isDirectory()){//文件是目录
						if(!childFiles[i].getName().equals("未分类")){
							list.add(childFiles[i].getName());
						}else{
							list.add(0,childFiles[i].getName());
						}
					}
				}
				return list;
			}
				
		}
	}


    public static List<FolderMessage> getFolderNameAndCount(String path)
	{
		List<FolderMessage> list = new ArrayList<FolderMessage>();
		FolderMessage msg = null;
		File file = new File(path);
		if(file.isFile())
		{
			return null;
		}else{
			File childFiles[] = file.listFiles();
			if(childFiles == null || childFiles.length == 0){
				return null;
			}else{
				for(int i = 0; i< childFiles.length; i++){
					if(childFiles[i].isDirectory()){//文件是目录 将文件夹的名字作为key 并将文件夹的文件个数作为value
//						System.out.println(childFiles[i].listFiles().length);
						msg = new FolderMessage(childFiles[i].getName(), String.valueOf(childFiles[i].listFiles().length));
						/*if(!msg.getTitle().equals("未分类")){
							list.add(msg);
						}else{
							list.add(0,msg);							
						}*/ //delete by wangfang 2015-02-03
						//add by wangfang 2015-02-03 start
						if(!msg.getTitle().equals("未分类")&& !msg.getTitle().equals("名片")){
							list.add(msg);
						}else if(msg.getTitle().equals("未分类")){
							list.add(0,msg);						
						}else if(msg.getTitle().equals("名片")){
							int len = list.size();
							if(len == 0){
								list.add(0,msg);
							}else{
								for(int j = 0; j<len; j++){
									if(list.get(j).getTitle().equals("未分类")){
										list.add(j+1,msg);
									}else{
										if(j == len - 1){
											list.add(0,msg);
										}
									}
										
								}
							}
							
						}
//							list.add(1,msg);
						//add by wangfang 2015-02-03 end
					}
				}
				return list;
			}
		}
			
	}

    
}
