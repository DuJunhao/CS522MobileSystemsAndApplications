package edu.stevens.cs522.bookstore.util;


import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Author;

/**
 * Created by dduggan.
 */

public class BookAdapter extends ResourceCursorAdapter {

    protected final static int ROW_LAYOUT = android.R.layout.simple_list_item_2;

    public BookAdapter(Context context, Cursor cursor) {
        super(context, ROW_LAYOUT, cursor, 0);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // TOD
        TextView titleLine = (TextView) view.findViewById(android.R.id.text1);
        TextView authorLine = (TextView) view.findViewById(android.R.id.text2);
        // etc
        String title= BookContract.getTitle(cursor);
        String[]authorsName=BookContract.getAuthors(cursor);
        Author[]authors=Utils.parseAuthors(authorsName[0]);
        //String id=BookContract.getId(cursor);
        String firstAuthorName=authors[0].toString()+"et al";

        titleLine.setText(title);
        authorLine.setText(firstAuthorName);

    }

    public void bindView(View view, Context context, Cursor BookCursor,Cursor AuthorCursor) {
        // TOD
        TextView titleLine = (TextView) view.findViewById(android.R.id.text1);
        TextView authorLine = (TextView) view.findViewById(android.R.id.text2);
        // etc
        String title=BookContract.getTitle(BookCursor);
        String []authors=BookContract.getAuthors(AuthorCursor);
        String firstAuthorName=authors[0]+"et al";

        titleLine.setText(title);
        authorLine.setText(firstAuthorName);
    }

}
