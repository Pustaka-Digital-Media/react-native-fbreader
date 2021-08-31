package com.reactnativefbreader;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class FBReader extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "FBReader";

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void showTableOfContents(String path) {

    }
}
