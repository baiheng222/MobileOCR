package com.hanvon.mobileocr.wboard.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class PhotoAlbum implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;   //相册名字
	private String count; //图片数量
	private int  bitmap;  // 相册第一张图片
	private String path; //相册第一张图片path
	private String dir_id; //相册目录的ID
	
	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}
	private List<PhotoItem> bitList = new ArrayList<PhotoItem>();
	
	public PhotoAlbum() {
	}
	
	
	public PhotoAlbum(String name, String count, int bitmap) {
		super();
		this.name = name;
		this.count = count;
		this.bitmap = bitmap;
	}


	public String getDir_id() {
		return dir_id;
	}


	public void setDir_id(String dir_id) {
		this.dir_id = dir_id;
	}


	public List<PhotoItem> getBitList() {
		return bitList;
	}


	public void setBitList(List<PhotoItem> bitList) {
		this.bitList = bitList;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public int getBitmap() {
		return bitmap;
	}
	public void setBitmap(int bitmap) {
		this.bitmap = bitmap;
	}
	@Override
	public String toString() {
		return "PhotoAlbum [name=" + name + ", count=" + count + ", bitmap="
				+ bitmap + ", bitList=" + bitList + "]";
	}
}
