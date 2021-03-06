package edu.stevens.cs522.bookstoredatabase.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.regex.Pattern;

import edu.stevens.cs522.bookstoredatabase.entities.Author;

/**
 * Created by dduggan.
 */

public class BookContract implements BaseColumns {
    public static final String Id = "BookId";

    public static final String TITLE = "title";

    public static final String AUTHORS = "authors";

    public static final String ISBN = "ISBN";

    public static final String PRICE = "price";

    /*
     * TITLE column
     */

    private static int titleColumn = -1;
    public static String getTitle(Cursor cursor) {
        if (titleColumn < 0) {
            titleColumn =  cursor.getColumnIndexOrThrow(TITLE);;
        }
        return cursor.getString(titleColumn);
    }

    public static void putTitle(ContentValues values, String title) {
        values.put(TITLE, title);
    }

    /*
     * Synthetic authors column
     */
    public static final char SEPARATOR_CHAR = '|';

    private static final Pattern SEPARATOR =
            Pattern.compile(Character.toString(SEPARATOR_CHAR), Pattern.LITERAL);

    public static String[] readStringArray(String in) {
        return SEPARATOR.split(in);
    }

    private static int authorColumn = -1;
    public static String[] getAuthors(Cursor cursor) {
        if (authorColumn < 0) {
            authorColumn =  cursor.getColumnIndexOrThrow(AUTHORS);;
        }
        return readStringArray(cursor.getString(authorColumn));
    }

    public static void putAuthors(ContentValues values, Author[] authors)
    {
        for(int i=0; i<authors.length;i++)
        {
            AuthorContract.putFirstName(values,authors[i].firstName);
            AuthorContract.putMiddleInitial(values,authors[i].middleInitial);
            AuthorContract.putLastName(values,authors[i].lastName);
        }
    }


    private static int ISBNColumn = -1;
    public static String getISBN(Cursor cursor) {
        if (ISBNColumn < 0) {
            ISBNColumn =  cursor.getColumnIndexOrThrow(ISBN);;
        }
        return cursor.getString(ISBNColumn);
    }

    public static void putISBN(ContentValues values, String isbn) {
        values.put(ISBN, isbn);
    }


    private static int PRICEColumn = -1;
    public static String getPRICE(Cursor cursor) {
        if (PRICEColumn < 0) {
            PRICEColumn =  cursor.getColumnIndexOrThrow(PRICE);;
        }
        return cursor.getString(PRICEColumn);
    }

    public static void putPRICE(ContentValues values, String price) {
        values.put(PRICE, price);
    }

    private static int IdColumn = -1;
    public static String getId(Cursor cursor) {
        if (IdColumn < 0) {
            IdColumn =  cursor.getColumnIndexOrThrow(_ID);;
        }
        return cursor.getString(IdColumn);
    }

    public static void putId(ContentValues values, String id) {
        values.put(Id, id);
    }

    // TODOcomplete definitions of other getter and setter operations


}
