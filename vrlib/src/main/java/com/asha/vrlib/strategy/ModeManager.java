package com.asha.vrlib.strategy;

import android.app.Activity;

import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.common.MDGLHandler;
import com.asha.vrlib.common.MDMainHandler;
import com.asha.vrlib.strategy.display.AbsDisplayStrategy;
import com.asha.vrlib.strategy.interactive.AbsInteractiveStrategy;
import com.asha.vrlib.strategy.projection.AbsProjectionStrategy;

import java.util.Arrays;

/**
 * Created by hzqiujiadi on 16/3/19.
 * hzqiujiadi ashqalcn@gmail.com
 */
public abstract class ModeManager {
    private int mMode;
    private IModeStrategy mStrategy;
    private MDVRLibrary.INotSupportCallback mCallback;
    private MDGLHandler mGLHandler;

    public ModeManager(int mode, MDGLHandler handler) {
        this.mGLHandler = handler;
        this.mMode = mode;
    }

    /**
     * must call after new instance
     * @param activity activity
     */
    public void prepare(Activity activity, MDVRLibrary.INotSupportCallback callback){
        mCallback = callback;
        initMode(activity,mMode);
    }

    protected String mStrategyType = "AbsProjectionStrategy";

    protected AbsProjectionStrategy createStrategyProjection(int mode) { return null; }

    protected AbsDisplayStrategy createStrategyDisplay(int mode) { return null; }

    protected AbsInteractiveStrategy createStrategyInteractive(int mode) { return null; }

    abstract protected int[] getModes();

    private void initMode(Activity activity, final int mode){
        if (mStrategy != null){
            off(activity);
        }
        if(mStrategyType == "AbsProjectionStrategy")
            mStrategy = createStrategyProjection(mode);
        else if(mStrategyType == "AbsDisplayStrategy")
            mStrategy = createStrategyDisplay(mode);
        else if(mStrategyType == "AbsInteractiveStrategy")
            mStrategy = createStrategyInteractive(mode);
        if (!mStrategy.isSupport(activity)){
            MDMainHandler.sharedHandler().post(new Runnable() {
                @Override
                public void run() {
                    if (mCallback != null) mCallback.onNotSupport(mode);
                }
            });
        } else {
            on(activity);
        }
    }

    public void switchMode(final Activity activity){
        int[] modes = getModes();
        int mode = getMode();
        int index = Arrays.binarySearch(modes, mode);
        int nextIndex = (index + 1) %  modes.length;
        int nextMode = modes[nextIndex];

        switchMode(activity, nextMode);
    }

    public void switchMode(final Activity activity, final int mode){
        if (mode == getMode()) return;
        mMode = mode;

        initMode(activity, mMode);
    }

    public void on(final Activity activity) {
        final IModeStrategy tmpStrategy = mStrategy;
        if (tmpStrategy.isSupport(activity)){
            getGLHandler().post(new Runnable() {
                @Override
                public void run() {
                    tmpStrategy.on(activity);
                }
            });
        }
    }

    public void off(final Activity activity) {
        final IModeStrategy tmpStrategy = mStrategy;
        if (tmpStrategy.isSupport(activity)){
            getGLHandler().post(new Runnable() {
                @Override
                public void run() {
                    tmpStrategy.off(activity);
                }
            });
        }
    }

    protected IModeStrategy getStrategy() {
        return mStrategy;
    }

    protected AbsProjectionStrategy getStrategyProjection() {
        return (AbsProjectionStrategy)mStrategy;
    }

    protected AbsInteractiveStrategy getStrategyInteractive() {
        return (AbsInteractiveStrategy)mStrategy;
    }

    protected AbsDisplayStrategy getStrategyDisplay() {
        return (AbsDisplayStrategy)mStrategy;
    }

    public int getMode() {
        return mMode;
    }

    public MDGLHandler getGLHandler() {
        return mGLHandler;
    }
}
