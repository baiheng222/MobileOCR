package com.hanvon.mobileocr.md.camera;

public class BlockingQueueGrayByteDataArray extends
		BlockingQueueGenerics<GrayByteData[]> {
	private static BlockingQueueGrayByteDataArray blockingQueueGrayByteDataArray;

	public BlockingQueueGrayByteDataArray() {
		super();
		setBlockingQueueGrayByteDataArray(this);
	}

	public static BlockingQueueGrayByteDataArray getBlockingQueueGrayByteDataArray() {
		return blockingQueueGrayByteDataArray;
	}

	public static void setBlockingQueueGrayByteDataArray(
			BlockingQueueGrayByteDataArray blockingQueueGrayByteDataArray) {
		BlockingQueueGrayByteDataArray.blockingQueueGrayByteDataArray = blockingQueueGrayByteDataArray;
	}

}
