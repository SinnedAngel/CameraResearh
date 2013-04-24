package com.example.cameraframeworktest;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TakenPictureFragment extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.taken_picture_item, container, false);
		LayoutParams params = (LayoutParams) view.getLayoutParams();
		if (params != null)
		{
			params.width = 48;
			params.height = 48;
		}
		return view;
	}
}
