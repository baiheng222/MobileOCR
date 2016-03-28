package com.hanvon.mobileocr.md.camera;

public class BlockingQueuePreviewData extends
		BlockingQueueGenerics<GrayByteData> {
	private static BlockingQueuePreviewData blockingQueuePreviewData;

	public BlockingQueuePreviewData() {
		super();
		setBlockingQueuePreviewData(this);
	}

	public static BlockingQueuePreviewData getBlockingQueuePreviewData() {
		return blockingQueuePreviewData;
	}

	public static void setBlockingQueuePreviewData(
			BlockingQueuePreviewData blockingQueuePreviewData) {
		BlockingQueuePreviewData.blockingQueuePreviewData = blockingQueuePreviewData;
	}

	
}
