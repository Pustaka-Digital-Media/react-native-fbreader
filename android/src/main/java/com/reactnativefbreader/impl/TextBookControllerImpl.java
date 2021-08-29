package com.reactnativefbreader.impl;

import android.content.Context;

import androidx.annotation.NonNull;

import org.fbreader.book.Book;
import org.fbreader.book.Bookmark;
import org.fbreader.book.HighlightingStyle;
import org.fbreader.config.ConfigInterface;
import org.fbreader.config.StringOption;
import org.fbreader.text.FixedPosition;
import org.fbreader.text.Position;
import org.fbreader.text.widget.TextBookController;

import java.util.Collections;
import java.util.List;

public class TextBookControllerImpl extends TextBookController {
    public TextBookControllerImpl(@NonNull Context context, @NonNull Book book) {
        super(context, book);
    }

    @Override
    public int defaultHighlightingStyleId() {
        return 0;
    }

    @Override
    public List<HighlightingStyle> highlightingStyles() {
        return Collections.emptyList();
    }

    @Override
    public void saveBookmark(Bookmark bookmark) {

    }

    @Override
    public void deleteBookmark(Bookmark bookmark) {

    }

    @Override
    public void iterateVisibleBookmarks(BookmarksConsumer bookmarksConsumer) {

    }

    @Override
    public List<Bookmark> hiddenBookmarks() {
        return Collections.emptyList();
    }

    @Override
    public Position position() {
        try {
            final String[] split = this.positionOption().getValue().split(";");
            return new FixedPosition(
                    Integer.parseInt(split[0]), Integer.parseInt(split[1]), Integer.parseInt(split[2])
            );
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void storePosition(Position position) {
        if (position != null) {
            this.positionOption().setValue(
                    position.getParagraphIndex() + ";" + position.getElementIndex() + ";" + position.getCharIndex()
            );
        }
    }

    @Override
    public void markHyperlinkAsVisited(String s) {

    }

    @Override
    public boolean isHyperlinkVisited(String s) {
        return false;
    }

    private StringOption positionOption() {
        return ConfigInterface.instance(this.applicationContext).stringOption(
                "book-position", String.valueOf(book.getId()), ""
        );
    }
}
