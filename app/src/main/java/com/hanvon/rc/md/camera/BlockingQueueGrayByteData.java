package com.hanvon.rc.md.camera;

public class BlockingQueueGrayByteData extends
		BlockingQueueGenerics<GrayByteData> {

	private static BlockingQueueGrayByteData blockingQueueGrayByteData;

	public BlockingQueueGrayByteData() {
		super();
		setBlockingQueueGrayByteData(this);
	}

	public static BlockingQueueGrayByteData getBlockingQueueGrayByteData() {
		return blockingQueueGrayByteData;
	}

	public static void setBlockingQueueGrayByteData(
			BlockingQueueGrayByteData blockingQueueGrayByteData) {
		BlockingQueueGrayByteData.blockingQueueGrayByteData = blockingQueueGrayByteData;
	}
}
