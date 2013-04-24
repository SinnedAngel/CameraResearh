package com.example.cameraframeworktest;

import java.util.ArrayList;

import com.tokobagus.core.Logger;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

public class TakenPicturePager extends ViewPager
{
	private TakenPictureAdapter mAdapter;
	private Context mContext;
	private FragmentManager mManager;

	private ArrayList<TakenPictureFragment> mFragments = new ArrayList<TakenPictureFragment>();

	public TakenPicturePager(Context context)
	{
		super(context);
		initialize(context, null);
	}

	public TakenPicturePager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context, attrs);
	}

	private void initialize(Context context, AttributeSet attrs)
	{
		FragmentActivity activity = (FragmentActivity) context;
		mManager = activity.getSupportFragmentManager();

		mAdapter = new TakenPictureAdapter(mManager);
		setAdapter(mAdapter);
	}

	public void addItem()
	{
		TakenPictureFragment fragment = new TakenPictureFragment();
		mFragments.add(fragment);
		mAdapter.notifyDataSetChanged();
	}

	public class TakenPictureAdapter extends FragmentPagerAdapter
	{

		public TakenPictureAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			TakenPictureFragment fragment = mFragments.get(position);

			Logger.d("Fragment " + position + "; Parent " + fragment.getParentFragment());

			return fragment;
		}

		@Override
		public int getCount()
		{
			return mFragments.size();
		}

	}
}
