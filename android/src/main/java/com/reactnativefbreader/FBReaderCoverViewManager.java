package com.reactnativefbreader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import org.fbreader.book.Book;
import org.fbreader.format.BookException;
import org.fbreader.image.StreamImage;
import org.fbreader.text.TextInterface;
import org.fbreader.text.info.CoverHelper;

import java.util.Collections;

public class FBReaderCoverViewManager extends SimpleViewManager<ImageView> {
    public static final String REACT_CLASS = "FBReaderCoverView";

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    protected ImageView createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new ImageView(reactContext);
    }

    @ReactProp(name = "book")
    public void setBook(ImageView imageView, String value) {
        final Book book = new Book(value.hashCode(), Collections.singletonList(value), null, null, null);
        if ((new TextInterface(imageView.getContext())).readMetainfo(book)) {
            Bitmap bitmap = null;
            final StreamImage image = CoverHelper.instance(imageView.getContext()).getCover(book);
            if (image != null) {
                try {
                    bitmap = BitmapFactory.decodeStream(image.inputStream());
                } catch (Exception e) {
                    // ignore
                }
            }
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
