package com.asha.vrlib.strategy.display;

import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.common.MDGLHandler;
import com.asha.vrlib.model.BarrelDistortionConfig;
import com.asha.vrlib.strategy.ModeManager;

/**
 * Created by hzqiujiadi on 16/3/19.
 * hzqiujiadi ashqalcn@gmail.com
 */
public class DisplayModeManager extends ModeManager implements IDisplayMode {

    public static int[] sModes = {MDVRLibrary.DISPLAY_MODE_NORMAL, MDVRLibrary.DISPLAY_MODE_GLASS};

    private boolean antiDistortionEnabled;
    private BarrelDistortionConfig barrelDistortionConfig;

    public DisplayModeManager(int mode, MDGLHandler handler) {
        super(mode, handler);
        this.mStrategyType = "AbsDisplayStrategy";
    }

    @Override
    protected int[] getModes() {
        return sModes;
    }

    protected AbsDisplayStrategy createStrategyDisplay(int mode) {
        switch (mode){
            case MDVRLibrary.DISPLAY_MODE_GLASS:
                return new GlassStrategy();
            case MDVRLibrary.DISPLAY_MODE_NORMAL:
            default:
                return new NormalStrategy();
        }
    }

    @Override
    public int getVisibleSize() {
        return getStrategyDisplay().getVisibleSize();
    }

    public void setAntiDistortionEnabled(boolean antiDistortionEnabled) {
        this.antiDistortionEnabled = antiDistortionEnabled;
    }

    public boolean isAntiDistortionEnabled() {
        return antiDistortionEnabled;
    }

    public void setBarrelDistortionConfig(BarrelDistortionConfig barrelDistortionConfig) {
        this.barrelDistortionConfig = barrelDistortionConfig;
    }

    public BarrelDistortionConfig getBarrelDistortionConfig() {
        return barrelDistortionConfig;
    }
}
