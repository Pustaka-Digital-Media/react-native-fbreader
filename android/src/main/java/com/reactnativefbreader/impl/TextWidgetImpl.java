package com.reactnativefbreader.impl;

import android.content.Context;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.reactnativefbreader.impl.opener.ExternalHyperlinkOpener;
import com.reactnativefbreader.impl.opener.InternalHyperlinkOpener;

import org.fbreader.book.Book;
import org.fbreader.text.widget.TextBookController;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.view.PageInText;
import org.fbreader.widget.GestureListener;

public class TextWidgetImpl extends TextWidget {

    public interface TextWidgetListener {
        void onContentUpdated(WritableMap map);
    }

    private TextWidgetListener textWidgetListener;

    public TextWidgetImpl(Context context) {
        super(context);
        registerOpener(new InternalHyperlinkOpener(this));
        registerOpener(new ExternalHyperlinkOpener(this));
    }

    @Override
    protected TextBookController createController(Book book) {
        return new TextBookControllerImpl(getContext(), book);
    }

    @Override
    protected GestureListener createGestureListener() {
        return new GestureListenerExt(this);
    }

    @Override
    public void onContentUpdated() {
        super.onContentUpdated();
        TextWidgetListener textWidgetListener = getTextWidgetListener();
        if (textWidgetListener != null) {
            WritableMap map = Arguments.createMap();
            PageInText page = pageInText();
            map.putInt("page", page.pageNo);
            map.putInt("total", page.total);
            textWidgetListener.onContentUpdated(map);
        }
    }

    public TextWidgetListener getTextWidgetListener() {
        return textWidgetListener;
    }

    public void setTextWidgetListener(TextWidgetListener textWidgetListener) {
        this.textWidgetListener = textWidgetListener;
    }
}
