package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.R;


public class ViewBookActivity extends Activity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_KEY = "book";

	private ArrayList<String> authorsList=new ArrayList<String>();
	private ArrayAdapter<String> authorsAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_book);
		authorsAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,authorsList);
		// TODOget book as parcelable intent extra and populate the UI with book details.
		Book book = getIntent().getParcelableExtra(BOOK_KEY);
		// Get the Intent that started this activity and extract the string
		Intent intent = getIntent();
		Book viewing_book=intent.getParcelableExtra(MainActivity.viewBook);
		TextView search_title =(TextView)findViewById(R.id.view_title);

		ListView search_authors =(ListView)findViewById(R.id.view_authors);
		search_authors.setAdapter(authorsAdapter);

		TextView search_isbn =(TextView)findViewById(R.id.view_isbn);
		search_title.setText(viewing_book.title);
		for(int i=0;i<viewing_book.authors.length;i++)
		{
			authorsList.add(viewing_book.authors[i].toString());
			authorsAdapter.notifyDataSetChanged();
		}
		search_isbn.setText(viewing_book.isbn);
	}

}