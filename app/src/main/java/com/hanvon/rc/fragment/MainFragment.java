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


/**
 * Created by fanjianmin on 16-3-16.
 */
public class MainFragment extends BaseFragment
{
    private final String TAG = "MainFragment";
    private TextView mFastRec ;
    private TextView mExactRec;
    private ImageView mLeftMenu;

    @Override
    protected View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_fragment, container, false);
        mFastRec = (TextView) view.findViewById(R.id.tv_auto);
        mFastRec.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                MainActivity act = (MainActivity)getActivity();
                act.startCameraActivity();
            }
        });
        mExactRec = (TextView) view.findViewById(R.id.tv_exact);
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
        return view;
    }
}
