package com.example.opencvexample;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;

import org.opencv.android.JavaCameraView;

import java.util.List;

public class CameraView extends JavaCameraView {

    private String mPictureFileName;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public List<android.hardware.Camera.Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(int width, int height) {
        disconnectCamera();
        mMaxHeight = height;
        mMaxWidth = width;
        connectCamera(getWidth(), getHeight());
    }

    public android.hardware.Camera.Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

}
