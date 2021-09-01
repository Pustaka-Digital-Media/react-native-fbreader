package com.reactnativefbreader.impl;

import android.content.Context;

import com.facebook.react.uimanager.ThemedReactContext;
import com.reactnativefbreader.impl.opener.ExternalHyperlinkOpener;
import com.reactnativefbreader.impl.opener.InternalHyperlinkOpener;

import org.fbreader.book.Book;
import org.fbreader.text.widget.TextBookController;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.widget.GestureListener;

public class TextWidgetImpl extends TextWidget {

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
}
