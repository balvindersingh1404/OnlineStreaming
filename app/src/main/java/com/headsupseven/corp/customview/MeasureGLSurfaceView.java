package com.headsupseven.corp.customview;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.headsupseven.corp.media.IRenderView;
import com.headsupseven.corp.media.MeasureHelper;

/**
 * Created by Nam Nguyen on 4/13/2017.
 */

public class MeasureGLSurfaceView extends GLSurfaceView implements IRenderView {
    private MeasureHelper mMeasureHelper;

    public MeasureGLSurfaceView(Context context) {
        super(context);
        initView(context);
    }

    public MeasureGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context)
    {
        mMeasureHelper = new MeasureHelper(this);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean shouldWaitForResize() {
        return false;
    }


    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            requestLayout();
        }
    }

    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        if (videoSarNum > 0 && videoSarDen > 0) {
            mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
            requestLayout();
        }
    }

    public void setVideoRotation(int degree) {
        mMeasureHelper.setVideoRotation(degree);
        setRotation(degree);
    }

    public void setAspectRatio(int aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }

    @Override
    public void addRenderCallback(@NonNull IRenderCallback callback) {

    }

    @Override
    public void removeRenderCallback(@NonNull IRenderCallback callback) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }
}
