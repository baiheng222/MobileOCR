package com.hanvon.mobileocr.wboard.bean;

import java.io.Serializable;

public class PhotoItem implements Serializable
{
	private static final long serialVersionUID = 8682674788506891598L;
	private int  photoID;
	private boolean select;
	private boolean visible;
	private String path;
	private String seq;
	private String photoDate;

	public PhotoItem(int id, String path, String date) {
		photoID = id;
		select = false;
		visible = false;
		seq = "";
		this.path=path;
		this.photoDate = date;
	}
	
	public PhotoItem(int id,boolean flag) {
		photoID = id;
		select = flag;
	}
	
	
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getPhotoID() {
		return photoID;
	}
	public void setPhotoID(int photoID) {
		this.photoID = photoID;
	}
	public boolean isSelect() {
		return select;
	}
	public void setSelect(boolean select) {
		this.select = select;
	}
	@Override
	public String toString() {
		return "PhotoItem [photoID=" + photoID + ", select=" + select + "]";
	}

	public boolean getVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getPhotoDate() {
		return photoDate;
	}

	public void setPhotoDate(String photoDate) {
		this.photoDate = photoDate;
	}
	
}
