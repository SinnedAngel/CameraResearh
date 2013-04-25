package com.example.cameraframeworktest;

public class SimpleDynamics extends Dynamics
{
	private float mFrictionFactor;
	private float mSnapToFactor;

	public SimpleDynamics(final float frictionFactor, final float snapFactor)
	{
		mFrictionFactor = frictionFactor;
		mSnapToFactor = snapFactor;
	}

	@Override
	protected void onUpdate(int dt)
	{
		mVelocity += getDistanceToLimit() * mSnapToFactor;
		mPosition += mVelocity * dt / 1000;
		mVelocity *= mFrictionFactor;
	}
}
