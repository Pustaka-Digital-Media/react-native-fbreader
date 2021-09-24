package com.reactnativefbreader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.reactnativefbreader.impl.TextWidgetImpl;

import org.fbreader.book.Book;
import org.fbreader.format.BookException;
import org.fbreader.text.TextInterface;
import org.fbreader.text.widget.TextWidget;
import org.fbreader.toc.TOCTree;
import org.fbreader.toc.TableOfContents;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

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
            final Book book = new Book(path.hashCode(), Collections.singletonList(path), null, null, null);
            if ((new TextInterface(getCurrentActivity())).readMetainfo(book)) {
                try {
                    TextWidget textWidget = null;
                    TextWidget currentWidget = FBReaderViewManager.getCurrentWidget();
                    if (currentWidget != null) {
                        Book currentBook = currentWidget.book();
                        if (currentBook.getId() == book.getId()) {
                            textWidget = currentWidget;
                        }
                    }

                    if (textWidget == null){
                        textWidget = new TextWidgetImpl(getCurrentActivity());
                    }
                    textWidget.setBook(book);
                    TableOfContents toc = textWidget.tableOfContents();
                    HashMap<Integer, Integer> pageMap = textWidget.pageMap(toc);
                    if (toc != null && toc.root != null) {
                        promise.resolve(toJSONObject(toc.root, pageMap));
                        return;
                    }
                } catch (BookException e) {
                }
            }
        }
        promise.resolve(Arguments.createMap());
    }

    private static WritableMap toJSONObject(TOCTree tree, HashMap<Integer, Integer> pageMap) {
        WritableMap map = Arguments.createMap();
        if (tree.Text != null) {
            map.putString("text", tree.Text);
        }

        if (tree.Reference != null) {
            map.putInt("ref", tree.Reference);
            if (pageMap.containsKey(tree.Reference)) {
                map.putInt("page", pageMap.get(tree.Reference));
            }
        }

        if (tree.hasChildren()) {
            List<TOCTree> children = tree.subtrees();
            WritableArray lst = Arguments.createArray();
            for (TOCTree child : children) {
                lst.pushMap(toJSONObject(child, pageMap));
            }
            map.putArray("children", lst);
        }

        return map;
    }
}
