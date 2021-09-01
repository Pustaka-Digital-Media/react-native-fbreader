package com.reactnativefbreader;

import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;

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
import org.fbreader.toc.TableOfContents;
import org.fbreader.view.options.ColorProfile;

import java.util.Collections;

public class FBReaderViewManager extends SimpleViewManager<FrameLayout> {
    public static final String REACT_CLASS = "FBReaderView";

    private TextWidget textWidget;
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
        return frameLayout;
    }

    @ReactProp(name = "background")
    public void setBackground(View view, String value) {
        final ColorProfile profile = textWidget.colorProfile();
        profile.wallpaper.setValue(String.format("wallpapers/%s.jpg", value));
    }

    @ReactProp(name = "colorProfile")
    public void setColorProfile(View view, String value) {
        textWidget.setColorProfileName(value);
    }

    @ReactProp(name = "fontSize")
    public void setFontSize(View view, Integer value) {
        final BaseStyle baseStyle = textWidget.baseStyle();
        baseStyle.fontSize.setValue(value);
    }

    @ReactProp(name = "searchInText")
    public void setSearchInText(View view, String value) {
        if (value != null && !value.isEmpty()) {
            textWidget.searchInText(value);
        }
    }

    @ReactProp(name = "tocReference")
    public void setTocReference(View view, Integer ref) {
        if (ref != null) {
            textWidget.jumpTo(new FixedPosition(ref, 0, 0));
        }
    }

    @ReactProp(name = "book")
    public void setBook(View view, String value) {
        final Book book = new Book(value.hashCode(), Collections.singletonList(value), null, null, null);
        if ((new TextInterface(view.getContext())).readMetainfo(book)) {
            try {
                progressBar.setVisibility(View.VISIBLE);
                textWidget.setBook(book);
                progressBar.setVisibility(View.GONE);
            } catch (BookException e) { }
        }
    }

}
