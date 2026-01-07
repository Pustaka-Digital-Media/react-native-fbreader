package com.reactnativefbreader.impl;

import android.content.Context;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.reactnativefbreader.impl.opener.ExternalHyperlinkOpener;
import com.reactnativefbreader.impl.opener.InternalHyperlinkOpener;

import org.fbreader.book.Book;
import org.fbreader.text.widget.TextBookController;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.toc.TOCTree;
import org.fbreader.view.PageInText;
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

    @Override
    public void onContentUpdated() {
        super.onContentUpdated();
        WritableMap map = Arguments.createMap();
        PageInText page = pageInText();
        if (page != null) {
            map.putInt("page", page.pageNo);
            map.putInt("total", page.total);
        }
        if (currentTOCElement() != null) {
            WritableArray breadcrumbs = Arguments.createArray();
            storeBreadcrumbs(currentTOCElement(), breadcrumbs);
            map.putArray("chapter", breadcrumbs);
        }
        
        try {
            ((com.facebook.react.bridge.ReactContext) getContext())
                    .getJSModule(com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit("FBReaderViewContentUpdateEvent", map);
        } catch (Exception e) {
            // Context might not be ready yet
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        onContentUpdated();
    }


    private void storeBreadcrumbs(TOCTree tree, WritableArray storage) {
        WritableMap item = Arguments.createMap();
        if (tree.Text != null) {
            item.putString("name", tree.Text);
        }
        item.putInt("level", tree.Level);
        storage.pushMap(item);
        if (tree.Parent != null) {
            storeBreadcrumbs(tree.Parent, storage);
        }
    }
}
