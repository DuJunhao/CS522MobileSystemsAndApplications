package edu.stevens.cs522.bookstoredatabase.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import edu.stevens.cs522.bookstoredatabase.entities.Author;

import static android.R.attr.author;

/**
 * Created by dduggan.
 */

public class AuthorContract implements BaseColumns {

    public static final String FIRST_NAME = "first";

    public static final String MIDDLE_INITIAL = "initial";

    public static final String LAST_NAME = "last";

    public static final String BOOK_FK = "book_fk";

    public static final String WHOLE_NAME="whole_name";
    /*
     * FIRST_NAME column
     */

    private static int firstNameColumn = -1;
    private static int middleNameColumn= -1;
    private static int lastNameColumn=-1;
    private static int book_fkColumn=-1;
    private static int whole_nameColumn=-1;

    public static String getFirstName(Cursor cursor) {
        if (firstNameColumn < 0) {
            firstNameColumn =  cursor.getColumnIndexOrThrow(FIRST_NAME);;
        }
        return cursor.getString(firstNameColumn);
    }
    public static void putFirstName(ContentValues values, String firstName) {
        values.put(FIRST_NAME, firstName);
    }


    // TODOcomplete the definitions of the other operations
    public static String getMiddleInitial(Cursor cursor) {
        if (middleNameColumn < 0) {
            middleNameColumn =  cursor.getColumnIndexOrThrow(MIDDLE_INITIAL);;
        }
        return cursor.getString(middleNameColumn);
    }
    public static void putMiddleInitial(ContentValues values, String middleInitial) {
        values.put(MIDDLE_INITIAL, middleInitial);
    }


    public static String getLastName(Cursor cursor) {
        if (lastNameColumn < 0) {
            lastNameColumn =  cursor.getColumnIndexOrThrow(LAST_NAME);;
        }
        return cursor.getString(lastNameColumn);
    }
    public static void putLastName(ContentValues values, String lastName) {
        values.put(LAST_NAME, lastName);
    }

    public static String getBook_fk(Cursor cursor) {
        if (book_fkColumn < 0) {
            book_fkColumn =  cursor.getColumnIndexOrThrow(BOOK_FK);
        }
        return cursor.getString(book_fkColumn);
    }
    public static void putBook_fk(ContentValues values, String book_fk) {
        values.put(BOOK_FK, book_fk);
    }


    public static String getWhole_Name(Cursor cursor) {
        if (whole_nameColumn < 0) {
            whole_nameColumn =  cursor.getColumnIndexOrThrow(WHOLE_NAME);
        }
        return cursor.getString(whole_nameColumn);
    }
    public static void putWhole_Name(ContentValues values, String whole_name) {
        values.put(WHOLE_NAME, whole_name);
    }
}
