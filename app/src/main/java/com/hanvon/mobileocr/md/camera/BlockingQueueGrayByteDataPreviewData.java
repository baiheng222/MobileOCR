package com.hanvon.mobileocr.md.camera;

public class BlockingQueueGrayByteDataPreviewData extends BlockingQueueGenerics<GrayByteData>{

	private static BlockingQueueGrayByteDataPreviewData blockingQueueGrayByteDataPreviewData;
	public BlockingQueueGrayByteDataPreviewData(){
		super();
		setBlockingQueueGrayByteDataPreviewData(this);
	}
	public static BlockingQueueGrayByteDataPreviewData getBlockingQueueGrayByteDataPreviewData() {
		return blockingQueueGrayByteDataPreviewData;
	}
	public static void setBlockingQueueGrayByteDataPreviewData(
			BlockingQueueGrayByteDataPreviewData blockingQueueGrayByteDataPreviewData) {
		BlockingQueueGrayByteDataPreviewData.blockingQueueGrayByteDataPreviewData = blockingQueueGrayByteDataPreviewData;
	}
}
	