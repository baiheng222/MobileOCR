package com.hanvon.rc.fragment;

import android.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
/**
 * Created by baiheng222 on 16-3-16.
 */
public abstract class BaseFragment extends Fragment
{
    protected Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedinstanceState)
    {
        mActivity = getActivity();
        return initView(inflater, container, savedinstanceState);
    }

    protected abstract View initView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
}
