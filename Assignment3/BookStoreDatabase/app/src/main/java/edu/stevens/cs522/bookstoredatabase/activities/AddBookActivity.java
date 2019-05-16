package edu.stevens.cs522.bookstoredatabase.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Collections;

import edu.stevens.cs522.bookstoredatabase.R;
import edu.stevens.cs522.bookstoredatabase.entities.Author;
import edu.stevens.cs522.bookstoredatabase.entities.Book;
import edu.stevens.cs522.bookstoredatabase.util.Utils;

import static android.app.Activity.RESULT_OK;


public class AddBookActivity extends AppCompatActivity {
	
	// Use this as the key to return the book details as a Parcelable extra in the result intent.
	public static final String BOOK_RESULT_KEY = "book_result";

	private int id;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_book);
		Intent intent=getIntent();
		id=intent.getIntExtra(BOOK_RESULT_KEY,3);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODOprovide ADD and CANCEL options
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.add_book_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TOD
		switch (item.getItemId())
		{
			case R.id.add_action :
				setResult(RESULT_OK,getIntent().putExtra(BOOK_RESULT_KEY,addBook()));
				break;
			case R.id.cancel_action:
				//finish();
				setResult(RESULT_CANCELED);
				break;
			default:
		}
		// ADD: return the book details to the BookStore activity
		
		// CANCEL: cancel the request

		/* cannot be used here:Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);*/
		finish();
		return false;
	}
	
	public Book addBook(){
		// TODOJust build a Book object with the search criteria and return that.
		int index=id;

		double intPrice=((int)(Math.random()*9+1)*1000)/100;
		String price=Double.toString(intPrice);
		EditText search_title =(EditText)findViewById(R.id.search_title);
		EditText search_author =(EditText)findViewById(R.id.search_author);
		EditText search_isbn =(EditText)findViewById(R.id.search_isbn);
		String str_search_title=search_title.getText().toString();
		String str_search_author=search_author.getText().toString();
		String str_search_isbn=search_isbn.getText().toString();

		Author[] authorName= Utils.parseAuthors(str_search_author);
		Book newBook=new Book(index+1,str_search_title,authorName,str_search_isbn,price);
		return newBook;
	}

}