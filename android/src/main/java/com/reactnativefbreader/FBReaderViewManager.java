package com.reactnativefbreader;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.reactnativefbreader.impl.GestureListenerExt;
import com.reactnativefbreader.impl.TextBookControllerImpl;

import org.fbreader.book.Book;
import org.fbreader.format.BookException;
import org.fbreader.format.BookFormatException;
import org.fbreader.text.TextInterface;
import org.fbreader.text.view.style.BaseStyle;
import org.fbreader.text.widget.TextBookController;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.view.options.ColorProfile;
import org.fbreader.widget.GestureListener;

import java.util.Collections;

public class FBReaderViewManager extends SimpleViewManager<View> {
    public static final String REACT_CLASS = "FBReaderView";


    @Override
    @NonNull
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    @NonNull
    public TextWidget createViewInstance(ThemedReactContext reactContext) {
        return new TextWidget(reactContext) {
            @Override
            protected TextBookController createController(Book book) {
                return new TextBookControllerImpl(getContext(), book);
            }

            @Override
            protected GestureListener createGestureListener() {
                return new GestureListenerExt(this);
            }
        };
    }

    @ReactProp(name = "background")
    public void setBackground(TextWidget view, String value) {
        final ColorProfile profile = view.colorProfile();
        profile.wallpaper.setValue(String.format("wallpapers/%s.jpg", value));
    }

    @ReactProp(name = "textColor")
    public void setTextColor(TextWidget view, String value) {
        final ColorProfile profile = view.colorProfile();
        profile.regularText.setValue(Color.parseColor(value));
    }

    @ReactProp(name = "fontSize")
    public void setFontSize(TextWidget view, Integer value) {
        final BaseStyle baseStyle = view.baseStyle();
        baseStyle.fontSize.setValue(value);
    }

    @ReactProp(name = "book")
    public void setBook(TextWidget view, String value) {
        final Book book = new Book(1L, Collections.singletonList(value), null, null, null);
        if (!(new TextInterface(view.getContext())).readMetainfo(book)) {

        } else {
            try {
                view.setBook(book);
            } catch (BookException e) {

            }
        }
    }


}
