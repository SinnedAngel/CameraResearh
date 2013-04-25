package com.example.cameraframeworktest;

public abstract class Dynamics
{
	private static final int MAX_TIMESTEP = 50;

	protected float mPosition;
	protected float mVelocity;
	protected float mMaxPosition = Float.MAX_VALUE;
	protected float mMinPosition = -Float.MAX_VALUE;
	protected long mLastTime = 0;

	public void setState(final float position, final float velocity, final long now)
	{
		mVelocity = velocity;
		mPosition = position;
		mLastTime = now;
	}

	public float getPosition()
	{
		return mPosition;
	}

	public float getVelocity()
	{
		return mVelocity;
	}

	public boolean isAtRest(final float velocityTolerance, final float positionTolerance)
	{
		final boolean standingStill = Math.abs(mVelocity) < velocityTolerance;
		final boolean withinLimit = mPosition - positionTolerance < mMaxPosition
				&& mPosition + positionTolerance > mMinPosition;
		return standingStill && withinLimit;
	}

	public void setMaxPosition(final float maxPosition)
	{
		mMaxPosition = maxPosition;
	}

	public void setMinPosition(final float minPosition)
	{
		mMinPosition = minPosition;
	}

	public void update(final long now)
	{
		int dt = (int) (now - mLastTime);
		if (dt > MAX_TIMESTEP)
			dt = MAX_TIMESTEP;

		onUpdate(dt);

		mLastTime = now;
	}

	protected float getDistanceToLimit()
	{
		float distanceToLimit = 0;

		if (mPosition > mMaxPosition)
			distanceToLimit = mMaxPosition - mPosition;
		else if (mPosition < mMinPosition)
			distanceToLimit = mMinPosition - mPosition;

		return distanceToLimit;
	}

	abstract protected void onUpdate(int dt);
}
