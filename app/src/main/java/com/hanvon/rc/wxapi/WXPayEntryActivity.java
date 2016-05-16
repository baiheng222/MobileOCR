package com.hanvon.rc.wxapi;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.hanvon.rc.R;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.orders.OrderDetail;
import com.hanvon.rc.orders.OrderDetailActivity;
import com.hanvon.rc.orders.OrderToPay;
import com.hanvon.rc.utils.LogUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
    private IWXAPI api;
	private static Handler handler;
	private OrderDetail orderDetail;
	private ProgressDialog pd;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.pay_result);
        
    	api = WXAPIFactory.createWXAPI(this, "wx021fab5878ea9288");
        api.handleIntent(getIntent(), this);

		LogUtil.i("----------WXPayEntryActivity onCreate()----------");
	//	initHandler();
	//	new MyHttpUtils(handler);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if(resp.errCode == -2){
			finish();
		}
		LogUtil.i("onPayFinish, errCode = " + resp.errCode);
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (resp.errCode == 0){
				//查询支付结果
				OrderToPay.instance.finish();
				if(OrderDetailActivity.install != null) {
					OrderDetailActivity.install.finish();
				}
				Intent intent = new Intent();
				intent.setClass(WXPayEntryActivity.this, OrderDetailActivity.class);
				intent.putExtra("OrderNumber", HanvonApplication.CurrentOid);
				intent.putExtra("from", "WXPAYACTIVITY");
				HanvonApplication.CurrentOid = "";
				startActivity(intent);
				finish();
			}
		}
	}
}