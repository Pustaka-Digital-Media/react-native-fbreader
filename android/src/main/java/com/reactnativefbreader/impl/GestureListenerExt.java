package com.reactnativefbreader.impl;

import android.graphics.Point;

import org.fbreader.text.widget.TextWidget;
import org.fbreader.util.PageIndex;

public class GestureListenerExt extends GestureListenerImpl {
    public GestureListenerExt(TextWidget widget) {
        super(widget);
    }

    @Override
    protected boolean onFingerSingleTap(Point pt) {
        if (super.onFingerSingleTap(pt)) {
            return true;
        }

        if (pt.x <= this.widget().getWidth() / 3) {
            this.widget().startAnimatedScrolling(PageIndex.previous);
        } else if (pt.x >= 2 * this.widget().getWidth() / 3) {
            this.widget().startAnimatedScrolling(PageIndex.next);
        }
        return true;
    }
    @Override
    public boolean isDoubleTapSupported() {
        return false;
    }
}
