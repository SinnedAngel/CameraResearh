package com.example.cameraframeworktest;

import java.util.LinkedList;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ListAdapter;

public class OrientedListView2 extends AdapterView<ListAdapter>
{
	private static final int LAYOUT_MODE_BELOW = 0;
	private static final int LAYOUT_MODE_ABOVE = 1;

	private static final int TOUCH_STATE_RESTING = 0;
	private static final int TOUCH_STATE_CLICK = 1;
	private static final int TOUCH_STATE_SCROLL = 2;

	private static final int TOUCH_SCROLL_THRESHOLD = 10;

	private static final int INVALID_INDEX = -1;

	private ListAdapter mAdapter;

	private LinkedList<View> mCachedItemViews = new LinkedList<View>();

	public int mTouchStartX = 0;
	public int mTouchStartY = 0;
	public int mTouchState = TOUCH_STATE_RESTING;

	public int mListTopStart = 0;
	public int mListTop = 0;

	private int mFirstItemPosition = -1;
	private int mLastItemPosition = -1;
	private int mListTopOffset = 0;

	private Rect mRect;

	private Runnable mLongPressRunnable;

	public OrientedListView2(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}

	public OrientedListView2(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public OrientedListView2(Context context)
	{
		super(context);
	}

	@Override
	public ListAdapter getAdapter()
	{
		return mAdapter;
	}

	@Override
	public View getSelectedView()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAdapter(ListAdapter adapter)
	{
		mAdapter = adapter;
		removeAllViewsInLayout();
		requestLayout();
	}

	@Override
	public void setSelection(int position)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom)
	{
		super.onLayout(changed, left, top, right, bottom);

		if (mAdapter == null)
			return;

		if (getChildCount() == 0)
		{
			mLastItemPosition = -1;
			fillListDown(mListTop, 0);
		}
		else
		{
			int offset = mListTop + mListTopOffset - getChildAt(0).getTop();
			removeNonVisibleViews(offset);
			fillList(offset);
		}

		positionItems();
		invalidate();
	}

	private void addAndMeasureChild(final View child, final int layoutMode)
	{
		LayoutParams params = child.getLayoutParams();
		if (params == null)
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		final int index = layoutMode == LAYOUT_MODE_ABOVE ? 0 : -1;
		addViewInLayout(child, index, params, true);

		int itemWidth = getWidth();
		child.measure(MeasureSpec.EXACTLY | itemWidth, MeasureSpec.UNSPECIFIED);
	}

	private void positionItems()
	{
		int top = mListTop + mListTopOffset;
		int childCount = getChildCount();

		int thisWidth = getWidth();

		for (int index = 0; index < childCount; index++)
		{
			View child = getChildAt(index);

			int width = child.getMeasuredWidth();
			int height = child.getMeasuredHeight();
			int left = (thisWidth - width) / 2;

			child.layout(left, top, left + width, top + height);
			top += height;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if (getChildCount() == 0)
			return false;

		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				startTouch(event);
				break;

			case MotionEvent.ACTION_MOVE:
				if (mTouchState == TOUCH_STATE_CLICK)
					startScrollIfNeeded(event);
				if (mTouchState == TOUCH_STATE_SCROLL)
					scrollList((int) event.getY() - mTouchStartY);
				break;

			case MotionEvent.ACTION_UP:
				if (mTouchState == TOUCH_STATE_CLICK)
					clickChildAt((int) event.getX(), (int) event.getY());
				endTouch();
				break;

			default:
				endTouch();
				break;
		}

		return true;
	}

	@Override
	public boolean onInterceptTouchEvent(final MotionEvent event)
	{
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				startTouch(event);
				return false;

			case MotionEvent.ACTION_MOVE:
				return startScrollIfNeeded(event);

			default:
				endTouch();
				return false;
		}
	}

	private void fillListDown(int bottomEdge, final int offset)
	{
		int height = getHeight();
		int count = mAdapter.getCount();
		while (bottomEdge + offset < height && mLastItemPosition < count - 1)
		{
			mLastItemPosition++;
			final View newBottomChild = mAdapter.getView(mLastItemPosition, getCachedView(), this);
			addAndMeasureChild(newBottomChild, LAYOUT_MODE_BELOW);
			bottomEdge += newBottomChild.getMeasuredHeight();
		}
	}

	private void fillListUp(int topEdge, final int offset)
	{
		while (topEdge + offset > 0 && mFirstItemPosition > 0)
		{
			mFirstItemPosition--;
			final View newTopChild = mAdapter.getView(mFirstItemPosition, getCachedView(), this);
			addAndMeasureChild(newTopChild, LAYOUT_MODE_ABOVE);
			final int childHeight = newTopChild.getMeasuredHeight();
			topEdge -= childHeight;

			mListTopOffset -= childHeight;
		}
	}

	private void fillList(final int offset)
	{
		final int bottomEdge = getChildAt(getChildCount() - 1).getBottom();
		fillListDown(bottomEdge, offset);

		final int topEdge = getChildAt(0).getTop();
		fillListUp(topEdge, offset);
	}

	private View getCachedView()
	{
		if (mCachedItemViews.size() != 0)
			return mCachedItemViews.removeFirst();

		return null;
	}

	private void removeNonVisibleViews(final int offset)
	{
		int childCount = getChildCount();

		if (mLastItemPosition != mAdapter.getCount() - 1 && childCount > 1)
		{
			View firstChild = getChildAt(0);
			while (firstChild != null && firstChild.getBottom() + offset < 0)
			{
				removeViewInLayout(firstChild);
				childCount--;
				mCachedItemViews.addLast(firstChild);
				mFirstItemPosition++;

				mListTopOffset += firstChild.getMeasuredHeight();

				if (childCount > 1)
					firstChild = getChildAt(0);
				else
					firstChild = null;
			}
		}

		if (mFirstItemPosition != 0 && childCount > 1)
		{
			View lastChild = getChildAt(childCount - 1);
			int height = getHeight();
			while (lastChild != null && lastChild.getTop() + offset > height)
			{
				removeViewInLayout(lastChild);
				childCount--;
				mCachedItemViews.addLast(lastChild);
				mLastItemPosition--;

				if (childCount > 1)
					lastChild = getChildAt(childCount - 1);
				else
					lastChild = null;
			}
		}
	}

	private void longClickChild(final int index)
	{
		final View itemView = getChildAt(index);
		final int position = mFirstItemPosition + index;
		final long id = mAdapter.getItemId(position);
		final OnItemLongClickListener listener = getOnItemLongClickListener();
		if (listener != null)
			listener.onItemLongClick(this, itemView, position, id);
	}

	private void startLongPressCheck()
	{
		if (mLongPressRunnable == null)
		{
			mLongPressRunnable = new Runnable()
			{
				@Override
				public void run()
				{
					if (mTouchState == TOUCH_STATE_CLICK)
					{
						final int index = getContainingChildIndex(mTouchStartX, mTouchStartY);
						if (index != INVALID_INDEX)
							longClickChild(index);
					}
				}
			};
		}

		postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout());
	}

	private void startTouch(final MotionEvent event)
	{
		mTouchStartX = (int) event.getX();
		mTouchStartY = (int) event.getY();
		mListTopStart = getChildAt(0).getTop() - mListTopOffset;

		startLongPressCheck();

		mTouchState = TOUCH_STATE_CLICK;
	}

	private void endTouch()
	{
		removeCallbacks(mLongPressRunnable);

		mTouchState = TOUCH_STATE_RESTING;
	}

	private boolean startScrollIfNeeded(final MotionEvent event)
	{
		final int xPos = (int) event.getX();
		final int yPos = (int) event.getY();
		if (xPos < mTouchStartX - TOUCH_SCROLL_THRESHOLD || xPos > mTouchStartX + TOUCH_SCROLL_THRESHOLD
				|| yPos < mTouchStartY - TOUCH_SCROLL_THRESHOLD || yPos > mTouchStartY + TOUCH_SCROLL_THRESHOLD)
		{
			removeCallbacks(mLongPressRunnable);
			mTouchState = TOUCH_STATE_SCROLL;
			return true;
		}
		return false;
	}

	private void scrollList(final int scrolledDistance)
	{
		mListTop = mListTopStart + scrolledDistance;
		requestLayout();
	}

	private int getContainingChildIndex(final int x, final int y)
	{
		if (mRect == null)
			mRect = new Rect();

		int count = getChildCount();

		for (int index = 0; index < count; index++)
		{
			getChildAt(index).getHitRect(mRect);
			if (mRect.contains(x, y))
				return index;
		}

		return INVALID_INDEX;
	}

	private void clickChildAt(final int x, final int y)
	{
		final int index = getContainingChildIndex(x, y);
		if (index != INVALID_INDEX)
		{
			final View itemView = getChildAt(index);
			final int position = mFirstItemPosition + index;
			final long id = mAdapter.getItemId(position);
			performItemClick(itemView, position, id);
		}
	}
}
