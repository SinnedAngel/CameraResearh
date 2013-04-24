package com.example.cameraframeworktest;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.GridView;
import android.widget.LinearLayout;

public class TakenPictureGridView extends GridView
{
	private int mCellSize;
	private OnMeasureListener mOnMeasureListener;
	private int mNumColumns;
	private int mOrientation = LinearLayout.HORIZONTAL;

	public TakenPictureGridView(Context context)
	{
		super(context);
		initialize(context, null);
	}

	public TakenPictureGridView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context, attrs);
	}

	public TakenPictureGridView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context, attrs);
	}

	private void initialize(Context context, AttributeSet attrs)
	{
		List<String> attrsName = new ArrayList<String>();

		if (attrs != null)
		{
			int attrCount = attrs.getAttributeCount();
			for (int i = 0; i < attrCount; i++)
			{
				String name = attrs.getAttributeName(i);

				if (name.equalsIgnoreCase("numcolumns"))
				{
					mNumColumns = attrs.getAttributeIntValue(i, 0);
				}
			}
		}

		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TakenPicture);
		if (a != null)
		{
			mOrientation = a.getInt(R.styleable.TakenPicture_orientation, LinearLayout.HORIZONTAL);

			setOrientation(mOrientation);

			a.recycle();
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		mCellSize = getWidth() / getNumColumns();

		int heightPadding = getPaddingTop() + getPaddingBottom();

		if (mCellSize > (getHeight() - heightPadding))
			mCellSize = getHeight() - heightPadding;

		if (mOnMeasureListener != null)
			mOnMeasureListener.onMeasure(widthMeasureSpec, heightMeasureSpec);

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	public int getCellSize()
	{
		return mCellSize;
	}

	@Override
	@ExportedProperty
	public int getNumColumns()
	{
		if (Build.VERSION.SDK_INT >= 11)
			return super.getNumColumns();

		else
			return mNumColumns;
	}

	@Override
	public void setNumColumns(int numColumns)
	{
		if (mOrientation == LinearLayout.VERTICAL)
			numColumns = 1;

		mNumColumns = numColumns;
		super.setNumColumns(numColumns);
	}

	public void setOrientation(int orientation)
	{
		mOrientation = orientation;
		if (orientation == LinearLayout.VERTICAL)
			setNumColumns(1);
	}

	public void setOnMeasureListener(OnMeasureListener mOnMeasureListener)
	{
		this.mOnMeasureListener = mOnMeasureListener;
	}

	public interface OnMeasureListener
	{
		public void onMeasure(int widthMeasureSpec, int heightMeasureSpec);
	}
}
