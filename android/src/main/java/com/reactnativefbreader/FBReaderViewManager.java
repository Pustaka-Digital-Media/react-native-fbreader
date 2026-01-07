package com.reactnativefbreader;

import android.util.Log;
import android.graphics.Rect;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import org.fbreader.book.Book;
import org.fbreader.text.TextInterface;
import org.fbreader.view.options.ViewOptions;
import org.fbreader.view.options.ColorProfile;
import org.fbreader.text.FixedPosition;
import org.fbreader.text.view.style.BaseStyle;
import org.fbreader.format.BookException;
import org.fbreader.text.widget.TextWidget;
import com.reactnativefbreader.impl.TextWidgetImpl;

import java.lang.ref.WeakReference;
import java.util.Collections;

public class FBReaderViewManager extends SimpleViewManager<FrameLayout> implements View.OnLayoutChangeListener {
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

    private TextWidgetImpl getTextWidget(FrameLayout layout) {
        if (layout == null) return null;
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof TextWidgetImpl) {
                return (TextWidgetImpl) child;
            }
        }
        return null;
    }

    private ProgressBar getProgressBar(FrameLayout layout) {
        if (layout == null) return null;
        for (int i = 0; i < layout.getChildCount(); i++) {
            View child = layout.getChildAt(i);
            if (child instanceof ProgressBar) {
                return (ProgressBar) child;
            }
        }
        return null;
    }


    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @NonNull
    public FrameLayout createViewInstance(@NonNull ThemedReactContext reactContext) {
        FrameLayout frameLayout = new FrameLayout(reactContext);

        ProgressBar progressBar = new ProgressBar(reactContext, null, android.R.attr.progressBarStyleLarge);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.GONE);

        TextWidgetImpl textWidget = new TextWidgetImpl(reactContext);
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
        if (value != null && !value.isEmpty() && view instanceof FrameLayout) {
            TextWidgetImpl textWidget = getTextWidget((FrameLayout) view);
            if (textWidget == null) return;
            final ColorProfile profile = textWidget.colorProfile();
            profile.wallpaper.setValue(String.format("wallpapers/%s.jpg", value));
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "colorProfile")
    public void setColorProfile(View view, String value) {
        if (value != null && !value.isEmpty() && view instanceof FrameLayout) {
            TextWidgetImpl textWidget = getTextWidget((FrameLayout) view);
            if (textWidget == null) return;
            textWidget.setColorProfileName(value.toLowerCase());
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "fontSize")
    public void setFontSize(View view, Integer value) {
        if (value != null && view instanceof FrameLayout) {
            TextWidgetImpl textWidget = getTextWidget((FrameLayout) view);
            if (textWidget == null) return;
            final BaseStyle baseStyle = textWidget.baseStyle();
            baseStyle.fontSize.setValue(value);
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "searchInText")
    public void setSearchInText(View view, String value) {
        if (value != null && !value.isEmpty() && view instanceof FrameLayout) {
            TextWidgetImpl textWidget = (TextWidgetImpl) ((FrameLayout) view).getChildAt(0);
            if (textWidget == null) return;
            textWidget.searchInText(value);
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "tocReference")
    public void setTocReference(View view, Integer ref) {
        if (ref != null && view instanceof FrameLayout) {
            TextWidgetImpl textWidget = (TextWidgetImpl) ((FrameLayout) view).getChildAt(0);
            if (textWidget == null) return;
            textWidget.jumpTo(new FixedPosition(ref, 0, 0));
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "page")
    public void setPage(View view, Integer value) {
        if (value != null && value > 0 && view instanceof FrameLayout) {
            TextWidgetImpl textWidget = getTextWidget((FrameLayout) view);
            if (textWidget == null) return;
            textWidget.gotoPage(value);
            textWidget.clearTextCaches();
            textWidget.invalidate();
        }
    }

    @ReactProp(name = "book")
    public void setBook(View view, final String value) {
        if (value == null || value.isEmpty() || !(view instanceof FrameLayout)) {
            Log.d("FBReaderViewManager", "setBook: Invalid view or empty path");
            return;
        }
        final FrameLayout layout = (FrameLayout) view;
        final TextWidgetImpl textWidget = getTextWidget(layout);
        final ProgressBar progressBar = getProgressBar(layout);
        if (textWidget == null) {
            Log.d("FBReaderViewManager", "setBook: textWidget is null");
            return;
        }
        
        Log.d("FBReaderViewManager", "setBook: Attempting to load " + value);
        final Book book = new Book(value.hashCode(), Collections.singletonList(value), null, null, null);
        if ((new TextInterface(view.getContext())).readMetainfo(book)) {
            Log.d("FBReaderViewManager", "setBook: readMetainfo success");
            try {
                if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                textWidget.setBook(book);
                
                // Force a default color profile and ensure high visibility
                textWidget.setColorProfileName("light");
                textWidget.setVisibility(View.VISIBLE);
                
                // Hide the black bar (footer progress) natively
                try {
                    ViewOptions.instance(view.getContext()).indicatorType.setValue(ViewOptions.ProgressIndicatorType.none);
                } catch (Exception e) {
                    Log.e("FBReaderViewManager", "Could not hide footer", e);
                }

                // Ensure no dimming from native side
                textWidget.setScreenBrightness(100, true);
                
                // Ensure layout is ready before invalidating to fix 0/0 page issue
                layout.post(new Runnable() {
                    @Override
                    public void run() {
                        int width = textWidget.getWidth();
                        Log.d("FBReaderViewManager", "setBook: Post-layout refresh. Dim: " + width + "x" + textWidget.getHeight());
                        
                        if (width == 0) {
                            Log.d("FBReaderViewManager", "setBook: Width is 0, forcing re-measure");
                            layout.measure(
                                View.MeasureSpec.makeMeasureSpec(layout.getWidth(), View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(layout.getHeight(), View.MeasureSpec.EXACTLY)
                            );
                            layout.layout(layout.getLeft(), layout.getTop(), layout.getRight(), layout.getBottom());
                        }

                        textWidget.clearTextCaches();
                        textWidget.bringToFront();
                        textWidget.requestLayout();
                        textWidget.invalidate();
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                    }
                });
            } catch (Exception e) {
                Log.e("FBReaderViewManager", "setBook: Error setting book", e);
                if (progressBar != null) progressBar.setVisibility(View.GONE);
            }
        } else {
            Log.d("FBReaderViewManager", "setBook: readMetainfo FAILED for " + value);
        }
    }


    @Override
    public void onLayoutChange(View view, int l, int t, int r, int b, int i4, int i5, int i6, int i7) {
        Rect rect = new Rect(l, t, r, b);
        rect.width();
        rect.height();
    }
}
