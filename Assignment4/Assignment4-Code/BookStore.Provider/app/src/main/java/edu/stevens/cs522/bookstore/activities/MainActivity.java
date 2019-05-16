package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.providers.BookProvider;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
	
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;
	
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    private static final String Cart_State = "Cart_State";
    private static final String AllBooks = "NumberOfAllTheBooks";
    private static final int BOOKS_LOADER_ID = 1;
    private static final int AUTHORS_LOADER_ID=2;

    public static final String viewBook = "view_Book";

    private int numberOfBooks=0;
    private static int index=0;
    public static ListView lv;
    public  static LoaderManager lm;
    static final private int LOADER_ID = 1;

    BookAdapter bookAdapter;

    private ArrayList<Book> shoppingCart=new ArrayList<Book>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODOcheck if there is saved UI state, and if so, restore it (i.e. the cart contents)
        if (savedInstanceState != null) {
            shoppingCart=savedInstanceState.getParcelableArrayList(Cart_State);
            index=savedInstanceState.getInt(AllBooks);
        }
		// TODOSet the layout (use cart.xml layout)
        setContentView(R.layout.cart);
        // Use a custom cursor adapter to display an empty (null) cursor.
        Cursor cursor=getContentResolver().query(BookContract.CONTENT_URI,null,null,null,null);
        numberOfBooks=cursor.getCount();
        bookAdapter = new BookAdapter(this, cursor);
        lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(bookAdapter);
        if(cursor.getCount()!=0)
        {
            TextView cart_empty = (TextView)findViewById(android.R.id.empty);
            cart_empty.setText("");
        }
        // TODOset listeners for item selection and multi-choice CAB
        this.registerForContextMenu(lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        /*lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent viewBook_Intent=new Intent(MainActivity.this, ViewBookActivity.class);
                //viewBook_Intent.putExtra(viewBook,"");
                //startActivity(viewBook_Intent);
                Toast.makeText(MainActivity.this, "you chose item " + position, Toast.LENGTH_LONG).show();
               *//* if(view.getTag(R.id.TagIfClicked)!=null)
                {
                    view.setBackgroundColor(0x00000000);
                }
                else{
                    view.setTag(R.id.TagIfClicked,"true");
                    view.setBackgroundColor(0xFFFF0000);
                }
                bookAdapter.notifyDataSetChanged();*//*
               *//* Intent viewBook_Intent=new Intent(MainActivity.this, ViewBookActivity.class);
                Cursor bookCursor=getContentResolver().query(BookContract.CONTENT_URI(position),null,null,null,null);
                Book book=new Book(bookCursor);
                viewBook_Intent.putExtra(viewBook,book);
                startActivity(viewBook_Intent);*//*
            }
        });
*/
        lv.setSelected(true);
        lv.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                                            Set<Long> selected;
                                          @Override
                                          public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                                              // TODOinflate the menu for the CAB
                                              MenuInflater inflater = getMenuInflater();
                                              inflater.inflate(R.menu.books_cab, menu);
                                              selected = new HashSet<Long>();
                                              return true;
                                          }

                                          @Override
                                          public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                                              return false;
                                          }

                                          @Override
                                          public boolean onActionItemClicked(ActionMode actionMode, MenuItem item) {
                                              switch(item.getItemId()) {
                                                  case R.id.delete:
                                                      // TODOdelete the selected books
                                                      for(Long id:selected)
                                                      {
                                                          getContentResolver().delete(BookContract.CONTENT_URI(id),null,null);
                                                          numberOfBooks--;
                                                      }
                                                      bookAdapter.changeCursor(getContentResolver().query(BookContract.CONTENT_URI,null,null,null,null));
                                                      bookAdapter.notifyDataSetChanged();
                                                      return true;
                                                  case R.id.view:
                                                      for(Long id:selected)
                                                      {
                                                          Intent viewBook_Intent=new Intent(MainActivity.this, ViewBookActivity.class);
                                                          Cursor bookCursor=getContentResolver().query(BookContract.CONTENT_URI(id),null,null,null,null);
                                                          Book book=new Book(bookCursor);
                                                          viewBook_Intent.putExtra(viewBook,book);
                                                          startActivity(viewBook_Intent);
                                                      }

                                                  default:
                                                      return false;
                                              }
                                          }

                                          @Override
                                          public void onDestroyActionMode(ActionMode actionMode) {

                                          }

                                          @Override
                                          public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
                                              if (checked) {

                                                  selected.add(id);
                                              } else {
                                                  selected.remove(id);
                                              }
                                          }
                                      });

        // TODOuse loader manager to initiate a query of the database
        lm = getLoaderManager();
        lm.initLoader(BOOKS_LOADER_ID, null, this);
        lm.initLoader(AUTHORS_LOADER_ID, null, this);

    }


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODOinflate a menu with ADD and CHECKOUT options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bookstore_menu, menu);
        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // TODOADD provide the UI for adding a book
            case R.id.add:
                // Intent addIntent = new Intent(this, AddBookActivity.class);
                // startActivityForResult(addIntent, ADD_REQUEST);
                Intent addIntent = new Intent(this, AddBookActivity.class);
                addIntent.putExtra(AddBookActivity.BOOK_RESULT_KEY,index);
                startActivityForResult(addIntent, ADD_REQUEST);
                break;

            // TODOCHECKOUT provide the UI for checking out
            case R.id.checkout:
                Intent checkIntent=new Intent(this,CheckoutActivity.class);
                checkIntent.putExtra(CheckoutActivity.Check_Out_Result,numberOfBooks);
                startActivityForResult(checkIntent,CHECKOUT_REQUEST);
                break;
            default:
        }
        return false;
    }

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		// TODOHandle results from the Search and Checkout activities.

        // Use ADD_REQUEST and CHECKOUT_REQUEST codes to distinguish the cases.
        Log.v(TAG,"received request");
        switch(requestCode) {
            case ADD_REQUEST:
                // ADD: add the book that is returned to the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                Log.v(TAG,"request_code==ok");
                if (resultCode == RESULT_OK) {
                    Book result = intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                    shoppingCart.add(result);
                    numberOfBooks++;
                    index++;
                    ContentValues out = new ContentValues();
                    Uri myRowUri = getContentResolver().insert(BookContract.CONTENT_URI, result.writeToProvider(out));
                    Cursor cursor=getContentResolver().query(BookContract.CONTENT_URI,null,null,null,null);
                    if(cursor.getCount()>0)
                    {
                        TextView cart_empty = (TextView)findViewById(android.R.id.empty);
                        cart_empty.setText("");
                    }
                    bookAdapter.changeCursor(cursor);
                    bookAdapter.notifyDataSetChanged();
                }
                break;
            case CHECKOUT_REQUEST:
                // CHECKOUT: empty the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                if(resultCode==RESULT_CANCELED){
                    getContentResolver().delete(BookContract.CONTENT_URI, null, null);
                    shoppingCart.clear();
                    numberOfBooks=0;
                }
                bookAdapter.changeCursor(getContentResolver().query(BookContract.CONTENT_URI,null,null,null,null));
                bookAdapter.notifyDataSetChanged();
                TextView cart_empty = (TextView)findViewById(android.R.id.empty);
                cart_empty.setText(R.string.empty_cart);
                break;
        }

	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODOsave the shopping cart contents (which should be a list of parcelables).
        savedInstanceState.putParcelableArrayList(Cart_State,shoppingCart);
        savedInstanceState.putInt(AllBooks,index);
        super.onSaveInstanceState(savedInstanceState);
    }


    /*
     * Loader callbacks
     */

	@Override
	public Loader onCreateLoader(int id, Bundle args) {
        // TODOuse a CursorLoader to initiate a query on the database
        switch (id) {
            case BOOKS_LOADER_ID:
                return new CursorLoader(this, BookContract.CONTENT_URI, null, null, null, null);
            case AUTHORS_LOADER_ID:
                return new CursorLoader(this, AuthorContract.CONTENT_URI, null, null, null, null);
            default:
                return null; // An invalid id was passed in
        }
    }


    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        // TODOpopulate the UI with the result of querying the provider
        switch(loader.getId()) {
            case BOOKS_LOADER_ID:
                this.bookAdapter.swapCursor(cursor);
                break;
            case AUTHORS_LOADER_ID:
                this.bookAdapter.swapCursor(cursor);
                break;
        }
    }

    @Override
	public void onLoaderReset(Loader loader) {
        // TODOreset the UI when the cursor is empty
        this.bookAdapter.swapCursor(null);
	}


    /*
     * Selection of a book from the list view
     */

   /* @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODOquery for this book's details, and send to ViewBookActivity
        // ok to do on main thread for BookStoreWithContentProvider

    }*/


    /*
     * Handle multi-choice action mode for deletion of several books at once
     */

    /*public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        //  MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.context_menu, menu);
        menu.setHeaderTitle(R.string.Handle_Books);
        menu.setHeaderIcon(R.drawable.ic_menu_edit);
        menu.add(0,1,1,"View");
        menu.add(0,2,1,"Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case 1:
                Intent viewBook_Intent=new Intent(this, ViewBookActivity.class);
                Cursor bookCursor=getContentResolver().query(BookContract.CONTENT_URI((int)info.id),null,null,null,null);
                Book book=new Book(bookCursor);
                viewBook_Intent.putExtra(viewBook,book);
                startActivity(viewBook_Intent);
                break;
            case 2:

                getContentResolver().delete(BookContract.CONTENT_URI((int)info.id),null,null);
                numberOfBooks--;
                bookAdapter.changeCursor(getContentResolver().query(BookContract.CONTENT_URI,null,null,null,null));
                bookAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
    }*/
}