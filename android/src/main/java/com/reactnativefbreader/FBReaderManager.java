package com.reactnativefbreader;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.reactnativefbreader.impl.TextWidgetImpl;

import org.fbreader.book.Book;
import org.fbreader.format.BookException;
import org.fbreader.text.TextInterface;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.toc.TOCTree;
import org.fbreader.toc.TableOfContents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FBReaderManager extends ReactContextBaseJavaModule {
    public static final String REACT_CLASS = "FBReader";

    public FBReaderManager(@Nullable ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void tableOfContents(String path, Promise promise) {
        if (getCurrentActivity() != null) {
            TextWidget textWidget = new TextWidgetImpl(getCurrentActivity());
            final Book book = new Book(path.hashCode(), Collections.singletonList(path), null, null, null);
            if ((new TextInterface(getCurrentActivity())).readMetainfo(book)) {
                try {
                    textWidget.setBook(book);
                    TableOfContents toc = textWidget.tableOfContents();
                    if (toc != null && toc.root != null) {
                        promise.resolve(toJSONObject(toc.root));
                        return;
                    }
                } catch (BookException e) {
                }
            }
        }
        promise.resolve(Arguments.createMap());
    }

    private static WritableMap toJSONObject(TOCTree tree) {
        WritableMap map = Arguments.createMap();
        if (tree.Text != null) {
            map.putString("text", tree.Text);
        }

        if (tree.Reference != null) {
            map.putInt("ref", tree.Reference);
        }

        if (tree.hasChildren()) {
            List<TOCTree> children = tree.subtrees();
            WritableArray lst = Arguments.createArray();
            for (TOCTree child : children) {
                lst.pushMap(toJSONObject(child));
            }
            map.putArray("children", lst);
        }

        return map;
    }
}
