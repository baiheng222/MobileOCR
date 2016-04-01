package com.hanvon.rc.md.camera;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockingQueueGenerics<T>{

	private BlockingQueue<T> t=new ArrayBlockingQueue<T>(1);
	public boolean isEmpty(){
		if(t!=null){
			if(t.isEmpty()){
				return true;
			}
		}
		return false;
	}
	public void putData(T data){
		if(t!=null){
			if(!t.isEmpty()){
				t.clear();
			}
			try {
				t.put(data);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public T takeData(){
		try {
			return t.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}	
	public void clear(){
		t.clear();
	}
}
