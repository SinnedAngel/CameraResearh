package com.example.cameraframeworktest;

import java.util.LinkedList;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;

public class OrientationListView extends AdapterView<ListAdapter>
{
	private ListAdapter mAdapter;
	private DataSetObserver mObserver;

	private LinkedList<View> mLoadedViews;
	private LinkedList<View> mRecycledViews;

	private int mSelectedIndex = -1;
	private int mFirstViewIndex = -1;
	private int mSideBuffer = 3;
	private int mLastOrientation = -1;

	private int mCellSize = -1;

	private OnGlobalLayoutListener orientationChangeListener = new OnGlobalLayoutListener()
	{
		@Override
		public void onGlobalLayout()
		{
			getViewTreeObserver().removeOnGlobalLayoutListener(orientationChangeListener);
			setSelection(mSelectedIndex);
		}
	};

	public OrientationListView(Context context)
	{
		super(context);
		initialize(context, null);
	}

	public OrientationListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context, attrs);
	}

	public OrientationListView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}

	public void initialize(Context context, AttributeSet attrs)
	{
		mLoadedViews = new LinkedList<View>();
		mRecycledViews = new LinkedList<View>();
		final ViewConfiguration configuration = ViewConfiguration.get(context);

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TakenPicture);
		if (a != null)
		{
			mCellSize = a.getDimensionPixelSize(R.styleable.TakenPicture_cellSize, -1);
			a.recycle();
		}
	}

	@Override
	public ListAdapter getAdapter()
	{
		return mAdapter;
	}

	@Override
	public View getSelectedView()
	{
		if (mSelectedIndex > -1 && mAdapter.getCount() > mSelectedIndex && getChildCount() > mSelectedIndex)
		{
			return getChildAt(mSelectedIndex);
		}
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter)
	{
		if (mAdapter != null && mObserver != null)
			mAdapter.unregisterDataSetObserver(mObserver);

		mAdapter = adapter;

		if (mAdapter != null)
		{
			mObserver = new DataSetObserver()
			{
				@Override
				public void onChanged()
				{
					// int index = mSelectedIndex;
					// if (index == -1)
					// index = 0;
					//
					// if (getChildCount() > index)
					// index = getChildCount() - 1;
					//
					// View v = getChildAt(index);
				}
			};

			mAdapter.registerDataSetObserver(mObserver);

			if (mAdapter != null && mAdapter.getCount() > 0)
				setSelection(0);
		}
	}

	@Override
	public void setSelection(int position)
	{
		mSelectedIndex = position;

		if (mAdapter == null)
			return;

		if (position < -1)
			position = 0;

		if (position > mAdapter.getCount() - 1)
			position = mAdapter.getCount() - 1;

		recycleViews();

		View currentView = getView(position, true);
		mLoadedViews.addLast(currentView);

		for (int i = 1; mSideBuffer - i >= 0; i++)
		{
			int leftIndex = position - i;
			int rightIndex = position + i;

			if (leftIndex >= 0)
				mLoadedViews.addFirst(getView(leftIndex, false));
			if (rightIndex < mAdapter.getCount())
				mLoadedViews.addLast(getView(rightIndex, true));
		}

		requestLayout();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);

		final int count = getChildCount();
		for (int i = 0; i < count; i++)
		{
			getChildAt(i).measure(mCellSize, mCellSize);
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		final int count = getChildCount();
		int childLeft = 0;
		for (int i = 0; i < count; i++)
		{
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE)
			{
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	private View setupView(View view, boolean addToEnd, boolean recycle)
	{
		ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) view.getLayoutParams();
		if (params == null)
		{
			if (mCellSize == -1)
				params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
						ViewGroup.LayoutParams.WRAP_CONTENT, 0);
			else
				params = new AbsListView.LayoutParams(mCellSize, mCellSize, 0);
		}

		if (recycle)
			attachViewToParent(view, addToEnd ? -1 : 0, params);
		else
			addViewInLayout(view, addToEnd ? -1 : 0, params, true);

		return view;
	}

	private View getView(int position, boolean addToEnd, View convertView)
	{
		View view = mAdapter.getView(position, convertView, this);
		if (view != convertView && convertView != null)
			mRecycledViews.add(convertView);
		return setupView(view, addToEnd, view == convertView);
	}

	private View getView(int position, boolean addToEnd)
	{
		return getView(position, addToEnd, getRecycledView());
	}

	private void recycleView(View v)
	{
		if (v == null)
			return;

		mRecycledViews.add(v);
		detachViewFromParent(v);
	}

	private void recycleViews()
	{
		while (!mLoadedViews.isEmpty())
		{
			recycleView(mLoadedViews.remove());
		}
	}

	private View getRecycledView()
	{
		if (!mRecycledViews.isEmpty())
			return mRecycledViews.remove();

		return null;
	}

	@Override
	protected void onConfigurationChanged(Configuration newConfig)
	{
		if (newConfig.orientation != mLastOrientation)
		{
			mLastOrientation = newConfig.orientation;
			getViewTreeObserver().addOnGlobalLayoutListener(orientationChangeListener);
		}
	}
}
