package com.hanvon.rc.orders.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.orders.OrderDetailActivity;
import com.hanvon.rc.orders.OrderToPay;
import com.hanvon.rc.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class AliPay  {

	// 商户PID
	public static final String PARTNER = "2088021262536315";
	// 商户收款账号
	public static final String SELLER = "1944971055@qq.com";
	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAM3GnkudoTEmhCqn" +
			"IyV+WIYeeh1qTCbN6+TWwVgpo74EvLnKPq/XHzGaEtL8m1SjtQpqQuhvNfV2RZ1c" +
			"QAQGmXN9grwAcKnyGnfJ3X3Y7Nzo26EbB6Qu2275Cs57Uc7CNCxsTWdodUgsHUI1" +
			"zzZdD/gug4zap2Uube/nMN1C8yvnAgMBAAECgYB/SG4946EDYAm8wGmzFXX4b/2l" +
			"GE1Ga3WQtW4e9JK+RPvgCEPCTNSUv/MI4wvJzQF9EcGVOMUtshzZe2h1lQdxFl7L" +
			"pCrM+S+MU5rqmKVafvAvMjA3wDVzZfuokFq9YZPMzrVunkYLeiNK1PHh1MAOA+Ol" +
			"NPSgDtc3csUvXgTugQJBAPCG7bDoMimljReq5WBBFsSkQm1ku38F8Kmss4PQ56y1" +
			"Ci2/bO9/pYzN2Du86fQjBqJ2+mcQpVg2D/hibGVTK2ECQQDbA2WDL//Se1LGDpX5" +
			"Ls3oO0WN+fGJsi6a2ecBoyUFJVGKfEM5T+eBf43LXFtL+mCNt+sF1Yo5rCVV4HnQ" +
			"GKRHAkAZyT1eQ+Zs1JTFvsqMgS3hswJ0G+KGAasFZcBxF0pfF6GZufYBzxt+dusB" +
			"rIUgaUjizgKWXhB73n/jzxlz23DBAkEApgB7DuZw1w7WfHxNvGN3epCCdcx/AUlm" +
			"/cQvzhPkWXQhy//HzEb+SC9wQDWulXYffQtsPi3O6UvLuL2+VrZ2vQJAeihXIKse" +
			"9DWLkdqxW8Iq2XD0pbRB6NGzdAqEMWcpxzP1rLd9NHEB4y9tcW7wSOaH9t7DYGlV" +
			"1HrYMiK1vjF1Pw==";
	// 支付宝公钥
	public static final String RSA_PUBLIC = "";
	private static final int SDK_PAY_FLAG = 1;

	private Context mContext;
	private Activity mActivity;
	public AliPay(Context context,Activity activity){
		this.mContext = context;
		this.mActivity = activity;
	}
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		@SuppressWarnings("unused")
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				/**
				 * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
				 * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
				 * docType=1) 建议商户依赖异步通知
				 */
				String resultInfo = payResult.getResult();// 同步返回需要验证的信息
				LogUtil.i("------resultInfo:"+resultInfo);
				String resultStatus = payResult.getResultStatus();
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent();
					intent.setClass(mContext, OrderDetailActivity.class);
					intent.putExtra("OrderNumber", HanvonApplication.CurrentOid);
					intent.putExtra("from", "ALIPAYACTIVITY");
					HanvonApplication.CurrentOid = "";
					mContext.startActivity(intent);
					OrderToPay.instance.finish();
				} else {
					// 判断resultStatus 为非"9000"则代表可能支付失败
					// "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(mContext, "支付结果确认中", Toast.LENGTH_SHORT).show();
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(mContext, "支付失败", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
			default:
				break;
			}
		};
	};

	/**
	 * call alipay sdk pay. 调用SDK支付
	 * 
	 */
	public void pay(String price,String oid) {
		String orderInfo = getOrderInfo("汉王识文-精准人工识别", "汉王识文-精准人工识别", "0.01",oid);

		/**
		 * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
		 */
		String sign = sign(orderInfo);
		try {
			/**
			 * 仅需对sign 做URL编码
			 */
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		/**
		 * 完整的符合支付宝参数规范的订单信息
		 */
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

		Runnable payRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(mActivity);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(payInfo, true);

				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(mActivity);
		String version = payTask.getVersion();
		Toast.makeText(mContext, version, Toast.LENGTH_SHORT).show();
	}

	/**
	 * create the order info. 创建订单信息
	 * 
	 */
	private String getOrderInfo(String subject, String body, String price,String oid) {

		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";

		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\""+ oid + "\"";

		// 商品名称
		orderInfo += "&subject=" + "\"" + subject + "\"";

		// 商品详情
		orderInfo += "&body=" + "\"" + body + "\"";

		// 商品金额
		orderInfo += "&total_fee=" + "\"" + price + "\"";

		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\"" + "http://rc.hwyun.com:9090/rws-cloud/rt/ws/v1/alipay/notify" + "\"";

		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";

		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";

		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";

		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"30m\"";

		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		//orderInfo += "&return_url=\"\"";

		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";

		return orderInfo;
	}

	/**
	 * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
	 * 
	 */
	private String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}

	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	private String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATE);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	private String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
