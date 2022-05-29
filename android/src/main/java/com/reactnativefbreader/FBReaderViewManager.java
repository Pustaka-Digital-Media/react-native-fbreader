package com.reactnativefbreader;

import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.reactnativefbreader.impl.TextWidgetImpl;

import org.fbreader.book.Book;
import org.fbreader.format.BookException;
import org.fbreader.text.FixedPosition;
import org.fbreader.text.TextInterface;
import org.fbreader.text.view.style.BaseStyle;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.view.options.ColorProfile;

import java.lang.ref.WeakReference;
import java.util.Collections;

public class FBReaderViewManager extends SimpleViewManager<FrameLayout> implements TextWidgetImpl.TextWidgetListener, View.OnLayoutChangeListener {
    public static final String REACT_CLASS = "FBReaderView";

    private static WeakReference<TextWidget> weakReference;

    @Nullable
    public static TextWidget getCurrentWidget() {
        WeakReference<TextWidget> ref = weakReference;
        if (ref != null) {
            return ref.get();
        }
        return null;
    }

    private TextWidgetImpl textWidget;
    private ProgressBar progressBar;

    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @NonNull
    public FrameLayout createViewInstance(@NonNull ThemedReactContext reactContext) {
        FrameLayout frameLayout = new FrameLayout(reactContext);

        progressBar = new ProgressBar(reactContext, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        textWidget = new TextWidgetImpl(reactContext);
        textWidget.setTextWidgetListener(this);
        textWidget.addOnLayoutChangeListener(this);
        frameLayout.addView(textWidget,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );
        frameLayout.addView(progressBar,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                )
        );
        weakReference = new WeakReference<>(textWidget);
        return frameLayout;
    }

    @ReactProp(name = "background")
    public void setBackground(View view, String value) {
        if (value != null && !value.isEmpty()) {
            final ColorProfile profile = textWidget.colorProfile();
            profile.wallpaper.setValue(String.format("wallpapers/%s.jpg", value));
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

  @ReactProp(name = "textColor")
    public void setTextColor(View view, String value) {
        if (value != null && !value.isEmpty()) {
            final ColorProfile profile = textWidget.colorProfile();
            profile.text.setValue(value);
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "colorProfile")
    public void setColorProfile(View view, String value) {
        if (value != null && !value.isEmpty()) {
            textWidget.setColorProfileName(value);
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "fontSize")
    public void setFontSize(View view, Integer value) {
        if (value != null) {
            final BaseStyle baseStyle = textWidget.baseStyle();
            baseStyle.fontSize.setValue(value);
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "searchInText")
    public void setSearchInText(View view, String value) {
        if (value != null && !value.isEmpty()) {
            textWidget.searchInText(value);
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "tocReference")
    public void setTocReference(View view, Integer ref) {
        if (ref != null) {
            textWidget.jumpTo(new FixedPosition(ref, 0, 0));
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "page")
    public void setPage(View view, Integer value) {
        if (value != null) {
            textWidget.gotoPage(value);
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "book")
    public void setBook(View view, String value) {
        if (value == null || value.isEmpty()) return;
        final Book book = new Book(value.hashCode(), Collections.singletonList(value), null, null, null);
        if ((new TextInterface(view.getContext())).readMetainfo(book)) {
            try {
                progressBar.setVisibility(View.VISIBLE);
                textWidget.setBook(book);
                progressBar.setVisibility(View.GONE);
            } catch (BookException e) {
            }
        }
    }

    @Override
    public void onContentUpdated(WritableMap map) {
        ReactContext reactContext = (ReactContext) textWidget.getContext();
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("FBReaderViewContentUpdateEvent", map);
    }

    @Override
    public void onLayoutChange(View view, int l, int t, int r, int b, int i4, int i5, int i6, int i7) {
        Rect rect = new Rect(l, t, r, b);
        rect.width();
        rect.height();
    }
}
