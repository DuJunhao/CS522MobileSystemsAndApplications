package edu.stevens.cs522.bookstoredatabase.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import edu.stevens.cs522.bookstoredatabase.R;
import edu.stevens.cs522.bookstoredatabase.entities.Book;


public class ViewBookActivity extends Activity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_KEY = "book";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_book);

		// TODOget book as parcelable intent extra and populate the UI with book details.
		Book book = getIntent().getParcelableExtra(BOOK_KEY);
		// Get the Intent that started this activity and extract the string
		Intent intent = getIntent();
		Book viewing_book=intent.getParcelableExtra(MainActivity.viewBook);

		TextView search_title =(TextView)findViewById(R.id.view_title);
		TextView search_author =(TextView)findViewById(R.id.view_author);
		TextView search_isbn =(TextView)findViewById(R.id.view_isbn);

		search_title.setText(viewing_book.title);
		for(int i=0;i<viewing_book.authors.length;i++)
		{
			search_author.setText(viewing_book.authors[i].toString());
		}
		search_isbn.setText(viewing_book.isbn);
	}

}