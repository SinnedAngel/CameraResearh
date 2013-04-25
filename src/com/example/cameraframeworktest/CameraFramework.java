package com.example.cameraframeworktest;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;

public class CameraFramework extends FragmentActivity
{
	private ArrayList<TakenPictureItem> mItemList = new ArrayList<TakenPictureItem>();

	private TakenPictureAdapter mAdapter;

	private OrientedListView mPager;

	private int mCellSize = 0;
	private AbsListView.LayoutParams mCellParams;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_framework);

		mPager = (OrientedListView) findViewById(R.id.takenPicturePager);

		for (int i = 0; i < 50; i++)
		{
			TakenPictureItem item = new TakenPictureItem();
			mItemList.add(item);
		}

		mAdapter = new TakenPictureAdapter(this, R.layout.taken_picture_item, mItemList);

		mPager.setAdapter(mAdapter);
		mPager.setDynamics(new SimpleDynamics(0.9f, 0.6f));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.camera_framework, menu);
		return true;
	}

	public class TakenPictureItem
	{

	}

	public class TakenPictureAdapter extends ArrayAdapter<TakenPictureItem>
	{
		private LayoutInflater mInflater;

		public TakenPictureAdapter(Context context, int textViewResourceId, List<TakenPictureItem> objects)
		{
			super(context, textViewResourceId, objects);

			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent)
		{
			if (convertView == null)
				convertView = mInflater.inflate(R.layout.taken_picture_item, null);

			if (mCellSize > 0)
			{
				convertView.setLayoutParams(mCellParams);
			}

			return convertView;
		}
	}
}
