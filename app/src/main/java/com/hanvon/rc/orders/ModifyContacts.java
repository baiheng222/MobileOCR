package com.hanvon.rc.orders;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hanvon.rc.R;
import com.hanvon.rc.utils.ClearEditText;
import com.hanvon.rc.utils.ConnectionDetector;
import com.hanvon.rc.utils.InfoMsg;
import com.hanvon.rc.utils.LogUtil;
import com.hanvon.rc.utils.MyHttpUtils;
import com.hanvon.rc.utils.RequestJson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Desc:
 * @Auth: chenxzhuang
 * @Time: 2016/5/16 0016.
 */
public class ModifyContacts extends Activity implements View.OnClickListener {

    private ClearEditText CtContactsName;
    private ClearEditText CtContactsPhone;
    private TextView Tvensure;
    private TextView TvModifyTitle;
    private ImageView Ivback;

    private String name = "";
    private String phone = "";

    private static Handler handler;
    private ProgressDialog pd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.modify_contacts);

        TvModifyTitle = (TextView)findViewById(R.id.modify_title);
        CtContactsName = (ClearEditText)findViewById(R.id.evalprice_editname);
        CtContactsPhone = (ClearEditText)findViewById(R.id.evalprice_editphone);
        Tvensure = (TextView)findViewById(R.id.evalprice_editsure);
        Ivback = (ImageView)findViewById(R.id.modifyname_back);

        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            phone = intent.getStringExtra("phone");
        }
        if((name != null)&&(!name.equals(""))){
            CtContactsName.setText(name);
        }
        if((phone != null)&&(!phone.equals(""))){
            CtContactsPhone.setText(phone);
        }
        CtContactsName.setOnClickListener(this);
        CtContactsPhone.setOnClickListener(this);
        Tvensure.setOnClickListener(this);
        Ivback.setOnClickListener(this);
        TvModifyTitle.setText(R.string.contacts_title);

        initHandler();
        new MyHttpUtils(handler);
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.evalprice_editsure:
                name = CtContactsName.getText().toString();
                phone = CtContactsPhone.getText().toString();
                if(phone.length() == 0 || phone.length() != 11) {
                    Toast.makeText(this,"联系人电话号码长度不合法!",Toast.LENGTH_SHORT).show();
                    break;
                }else{
                    Pattern p = Pattern.compile("[1][3587]+\\d{9}");
                    Matcher m = p.matcher(phone);
                    if(!m.matches() ){
                        Toast.makeText(this,"联系人电话号码不合法", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(new ConnectionDetector(this).isConnectingTOInternet()) {
                        pd = ProgressDialog.show(this, "", "");
                        RequestJson.ModifyContactsMsg(name, phone,"");
                }else{
                    Toast.makeText(this,"请检查网络是否连通!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.modifyname_back:
                finish();
                break;
        }
    }

    public void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Object obj = msg.obj;
                LogUtil.i(obj.toString());
                JSONObject json = null;
                try {
                    json = new JSONObject(obj.toString());
                    if (!json.get("code").equals("0")) {
                        PdDimiss();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                switch (msg.what) {
                    case InfoMsg.ORDER_CONTACTS_MODIFY_TYPE:
                        SharedPreferences mSharedPreferences=getSharedPreferences("BitMapUrl", Activity.MODE_MULTI_PROCESS);
                        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
                        mEditor.putString("contactsname", name);
                        mEditor.putString("contactsphone", phone);
                        mEditor.commit();
                        Intent intent = new Intent();
                        intent.putExtra("name",name);
                        intent.putExtra("phone", phone);
                        setResult(2, intent);
                        finish();
                        break;
                }
            }
        };
    }

    private void PdDimiss(){
        if ((null != pd)&&(pd.isShowing())){
            pd.dismiss();
        }
    }
}
