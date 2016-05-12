package com.hanvon.rc.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hanvon.rc.R;
import com.hanvon.rc.activity.MainActivity;
import com.hanvon.rc.application.HanvonApplication;


/**
 * Created by fanjianmin on 16-3-16.
 */
public class MainFragment extends BaseFragment implements View.OnClickListener
{
    private final String TAG = "MainFragment";
    private TextView mFastRec ;
    private TextView mExactRec;
    private ImageView mLeftMenu;
    private ImageView mIvFastRec;
    private ImageView mIvExactRec;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_fragment, container, false);

        mIvFastRec = (ImageView) view.findViewById(R.id.iv_auto);
        mIvFastRec.setOnClickListener(this);

        mFastRec = (TextView) view.findViewById(R.id.tv_auto);
        mFastRec.setOnClickListener(this);

        mIvExactRec = (ImageView) view.findViewById(R.id.iv_exact);
        mIvExactRec.setOnClickListener(this);

        mExactRec = (TextView) view.findViewById(R.id.tv_exact);
        mExactRec.setOnClickListener(this);

        mLeftMenu = (ImageView) view.findViewById(R.id.iv_left_menu);
        mLeftMenu.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d(TAG, "!!! onClick !!");
                MainActivity act = (MainActivity)getActivity();
                act.openMenu();
            }
        });

        HanvonApplication.isAccurateRecg = false;
        return view;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.tv_auto:
            case R.id.iv_auto:
                HanvonApplication.isAccurateRecg = false;
                MainActivity act = (MainActivity)getActivity();
                act.startCameraActivity();
                break;

            case R.id.iv_exact:
            case R.id.tv_exact:
                HanvonApplication.isAccurateRecg = true;
                MainActivity act2 = (MainActivity)getActivity();
                act2.startExactActivity();
                break;

        }
    }
}
