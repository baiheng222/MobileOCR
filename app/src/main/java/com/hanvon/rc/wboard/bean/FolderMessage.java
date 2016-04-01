package com.hanvon.rc.wboard.bean;

public class FolderMessage
{
	private String name;//文件夹名
	private String count; //文件夹的文件个数
	private boolean isAdd;//是否是新增


	public FolderMessage(String title, String count) {
		this.name = title;
		this.count = count;
	}
	
	public FolderMessage(String name, String count, boolean isAdd) {
		this.name = name;
		this.count = count;
		this.isAdd = isAdd;
	}

	
	public String getTitle() {
		return name;
	}
	public void setTitle(String title) {
		this.name = title;
	}
	public String getCount() {
		return count;
	}
	public void setMsg(String count) {
		this.count = count;
	}
	public boolean getIsAdd() {
		return isAdd;
	}
	public void setIsAdd(boolean isAdd) {
		this.isAdd = isAdd;
	}
}
