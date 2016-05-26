package com.hanvon.rc.orders;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.application.HanvonApplication;
import com.hanvon.rc.utils.LogUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/4/23 0023.
 */
public class OrderEvalPrices extends Activity implements View.OnClickListener {
    private ImageView IvBack;
    private TextView TvPricesInfo;
    private TextView TvFilename;
    private TextView TvPages;
    private TextView TvBytes;
    private TextView TvWaitTime;
    private TextView TvSysInfo;
    private EditText ETinfo;
    private TextView TvPrices;
    private TextView TvOrderPrices;
    private ImageView TvPay;

    private TextView TvContactsName;
    private TextView TvContactsPhone;
    private TextView TvContactsModify;

    private OrderDetail orderDetail;

    public static OrderEvalPrices instance = null;
    private String resultFileType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.evalprice);

        Intent intent = getIntent();
        if (intent != null) {
            orderDetail = (OrderDetail)intent.getSerializableExtra("ordetail");
            resultFileType = intent.getStringExtra("resultfiletype");
        }
        InitView();
    }

    public void InitView(){
        IvBack = (ImageView)findViewById(R.id.evalprice_back);
        TvPricesInfo = (TextView)findViewById(R.id.evalprice_priceinfo);
        TvFilename = (TextView)findViewById(R.id.evalprice_name);
        TvPages = (TextView)findViewById(R.id.evalprice_page);
        TvBytes = (TextView)findViewById(R.id.evalprice_bytes);
        TvWaitTime = (TextView)findViewById(R.id.evalprice_waittime);
        TvPrices = (TextView)findViewById(R.id.evalprice_prices);
        TvSysInfo = (TextView)findViewById(R.id.evalprice_sysinfo);
        ETinfo = (EditText)findViewById(R.id.evalprice_editinfo);
        TvOrderPrices = (TextView)findViewById(R.id.evalprice_evalprice);
        TvPay = (ImageView)findViewById(R.id.evalprice_topay);
        TvContactsModify = (TextView)findViewById(R.id.modify_name);
        TvContactsName = (TextView)findViewById(R.id.eval_name);
        TvContactsPhone = (TextView)findViewById(R.id.eval_phone);

        IvBack.setOnClickListener(this);
        TvPay.setOnClickListener(this);
        TvPricesInfo.setOnClickListener(this);
        TvContactsModify.setOnClickListener(this);

       if("null".equals(orderDetail.getOrderPhone()) || "".equals(orderDetail.getOrderPhone())) {
           orderDetail.setOrderPhone("");
           if("null".equals(orderDetail.getOrderName())){
               orderDetail.setOrderName("");
           }
           SharedPreferences mSharedPreferences = getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
           String name = mSharedPreferences.getString("contactsname", "");
           String phone = mSharedPreferences.getString("contactsphone", "");
           orderDetail.setOrderPhone(phone);
           orderDetail.setOrderName(name);
           if ((phone == null) || ("".equals(phone))) {
               if (HanvonApplication.userFlag == 0) { //如果之前未保留过收货人信息，汉王用户手机注册的则手机号既是默认的手机号
                   String hvnname = HanvonApplication.hvnName;
                   if (hvnname != null) {
                       Pattern p = Pattern.compile("[1][3587]+\\d{9}");
                       Matcher m = p.matcher(hvnname);
                       if (m.matches()) {
                           //说明用户名是手机号
                           orderDetail.setOrderPhone(hvnname);
                       }
                   }
               }
           }
           if("".equals(orderDetail.getOrderName())){
               orderDetail.setOrderName(HanvonApplication.hvnName);
           }
       }
        if("null".equals(orderDetail.getOrderName()) || "".equals(orderDetail.getOrderName())) {
            orderDetail.setOrderName(HanvonApplication.hvnName);
        }

        TvContactsName.setText(orderDetail.getOrderName());
        TvContactsPhone.setText(orderDetail.getOrderPhone());
        TvFilename.setText(orderDetail.getOrderFileNanme());
        TvPages.setText(orderDetail.getOrderFilesPages()+" 张");
        TvBytes.setText(orderDetail.getOrderFilesBytes()+" 字节");
        TvWaitTime.setText(orderDetail.getOrderWaitTime());
        TvPrices.setText(orderDetail.getOrderPrice()+" 元");
        TvSysInfo.setText("现在下单，识别结果可在 "+orderDetail.getOrderFinshTime()+" 后查收，我们将以短信和邮件的形式通知您。");
        TvOrderPrices.setText(orderDetail.getOrderPrice()+" 元");
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.evalprice_back:
                finish();
                break;
            case R.id.evalprice_priceinfo:
                AlertDialog.Builder builder = new AlertDialog.Builder(OrderEvalPrices.this);
                builder.setMessage("收费标准每千字30元");
                builder.setTitle("收费标准");
                builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            case R.id.evalprice_topay:
                if(null == orderDetail.getOrderPhone() || "".equals(orderDetail.getOrderPhone())){
                    Toast.makeText(this,"联系号码不允许为空，请修改!",Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent intent = new Intent();
                intent.setClass(this,OrderToPay.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("ordetail", orderDetail);
                intent.putExtras(bundle);
                intent.putExtra("from", "EVAL_PRICES");
                intent.putExtra("resultfiletype", resultFileType);
                LogUtil.i("*************resultFileType:" + resultFileType);
                startActivity(intent);
                finish();
                break;
            case R.id.modify_name:
                Intent intent1 = new Intent();
                intent1.setClass(this,ModifyContacts.class);
                intent1.putExtra("name", orderDetail.getOrderName());
                intent1.putExtra("phone",orderDetail.getOrderPhone());
                startActivityForResult(intent1, 1);
                break;
            default:
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtil.i("------------------");
        switch (resultCode) { //resultCode为回传的标记，我在B中回传的是RESULT_OK
            case 2:
                String name = data.getStringExtra("name");
                String phone = data.getStringExtra("phone");
                String contactid = data.getStringExtra("contactId");
                orderDetail.setOrderName(name);
                orderDetail.setOrderPhone(phone);
                orderDetail.setContactId(contactid);
                TvContactsName.setText(orderDetail.getOrderName());
                TvContactsPhone.setText(orderDetail.getOrderPhone());
                break;
            default:
                break;
        }
    }
}
