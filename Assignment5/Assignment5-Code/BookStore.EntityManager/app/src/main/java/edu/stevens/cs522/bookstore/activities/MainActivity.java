package edu.stevens.cs522.bookstore.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.async.AsyncContentResolver;
import edu.stevens.cs522.bookstore.async.IContinue;
import edu.stevens.cs522.bookstore.async.IEntityCreator;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.managers.BookManager;
import edu.stevens.cs522.bookstore.managers.TypedCursor;
import edu.stevens.cs522.bookstore.util.BookAdapter;

public class MainActivity extends Activity implements IQueryListener {
	
	// Use this when logging errors and warnings.
	@SuppressWarnings("unused")
	private static final String TAG = MainActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;

    public static final String viewBook = "view_Book";

    private static final String Cart_State = "Cart_State";
    private static final String AllBooks = "NumberOfAllTheBooks";
	@SuppressWarnings("unused")
	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    private static BookManager bookManager;
    private static BookAdapter bookAdapter;

    public static final int BOOKS_LOADER_ID = 1;
    public static final int AUTHORS_LOADER_ID=2;

   // public static IQueryListener<Book> bookListener;
    public static IContinue<Book> bookContinue;

    private static Cursor AllBooksCursor;

    private int numberOfBooks=0;
    private static int index=0;
    public static ListView lv;
    public  static LoaderManager lm;
    static final private int LOADER_ID = 1;
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
        bookAdapter = new BookAdapter(this, null);
        ListView lv = (ListView) findViewById(android.R.id.list);
        lv.setAdapter(bookAdapter);
        // TODOSet listeners for item selection and multi-choice CAB
       /* bookListener=new IQueryListener<Book>() {
            @Override
            public TypedCursor<Book> handleResults(TypedCursor<Book> results) {
                AllBooksCursor=results.getCursor();
                bookAdapter.changeCursor(results.getCursor());
                bookAdapter.notifyDataSetChanged();
                return results;
            }

            @Override
            public void closeResults() {
                bookAdapter.notifyDataSetChanged();
            }
        };*/

        bookContinue=new IContinue<Book>() {
            @Override
            public Book kontinue(Book value) {
                Intent viewBook_Intent=new Intent(MainActivity.this, ViewBookActivity.class);
                viewBook_Intent.putExtra(viewBook,value);
                startActivity(viewBook_Intent);
                return value;
            }
        };
        //Cursor cursor=getContentResolver().query(BookContract.CONTENT_URI,null,null,null,null);
      //  Cursor cursor=null;
        /*TypedCursor<Book> result=new TypedCursor<Book>(cursor, new IEntityCreator<Book>() {
            @Override
            public Book create(Cursor cursor) {
                return new Book(cursor);
            }
        });*/
        // Initialize the book manager and query for all books
        bookManager = new BookManager(this);
        bookManager.getAllBooksAsync(this);
       // result=bookListener.handleResults(result); //TODOREMEMBER TO CHECK WHERE THE FUNCTION WILL GO
       // cursor=result.getCursor();
        //Cursor cursor1=result.getCursor();

       // bookAdapter.changeCursor(result.getCursor());

        this.registerForContextMenu(lv);
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
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
                        /*for(Long id:selected)
                        {
                            //getContentResolver().delete(BookContract.CONTENT_URI(id),null,null);//TODO:Use the LoaderManager

                            numberOfBooks--;
                        }*/
                        bookManager.deleteBooksAsync(selected);
                        numberOfBooks=numberOfBooks-selected.size();
                        bookManager.getAllBooksAsync(MainActivity.this);
                        //bookAdapter.changeCursor(getContentResolver().query(BookContract.CONTENT_URI,null,null,null,null));
                        //bookAdapter.notifyDataSetChanged();
                        return true;
                    case R.id.view:
                        for(Long id:selected)
                        {
                            //Intent viewBook_Intent=new Intent(MainActivity.this, ViewBookActivity.class);
                           // Cursor bookCursor=getContentResolver().query(BookContract.CONTENT_URI(id),null,null,null,null);
                            bookManager.getBookAsync(id,bookContinue);
                            //viewBook_Intent.putExtra(viewBook,book);
                            //startActivity(viewBook_Intent);
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

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent viewBook_Intent=new Intent(MainActivity.this, ViewBookActivity.class);
                //viewBook_Intent.putExtra(viewBook,"");
                //startActivity(viewBook_Intent);
                Toast.makeText(MainActivity.this, "you chose item " + position, Toast.LENGTH_LONG).show();
                /*if(view.getTag(R.id.TagIfClicked)!=null)
                {
                    view.setBackgroundColor(0x00000000);
                }
                else{
                    view.setTag(R.id.TagIfClicked,"true");
                    view.setBackgroundColor(0xFFFF0000);
                }*/
                // bookAdapter.notifyDataSetChanged();
               // Intent viewBook_Intent=new Intent(MainActivity.this, ViewBookActivity.class);
                bookManager.getBookAsync(id,bookContinue);

                //Cursor bookCursor=getContentResolver().query(BookContract.CONTENT_URI(position),null,null,null,null);
                //Book book=new Book(bookCursor);
                //viewBook_Intent.putExtra(viewBook,book);
                //startActivity(viewBook_Intent);
            }
        });
        //AsyncContentResolver asyncResolver=bookManager.getAsyncResolver();
        //asyncResolver.
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
                Log.v(TAG,"request_code==ok");
                // ADD: add the book that is returned to the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                if (resultCode == RESULT_OK) {
                    Book result = intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                    shoppingCart.add(result);
                    numberOfBooks++;
                    index++;
                    //ContentValues out = new ContentValues();
                    bookManager.persistAsync(result);
                    //ContentResolver contentResolver=getContentResolver();
                   // Uri myRowUri = contentResolver.insert(BookContract.CONTENT_URI, result.writeToProvider(out));
/*                    bookManager.getAllBooksAsync(bookListener);
                    Cursor cursor2=null;

                    TypedCursor<Book>newContents=new TypedCursor<Book>(cursor2, new IEntityCreator<Book>() {
                        @Override
                        public Book create(Cursor cursor) {
                            return new Book(cursor);
                        }
                    });
                    bookListener.handleResults(newContents);
                    cursor2=newContents.getCursor();*/
                   // Cursor cursor=contentResolver.query(BookContract.CONTENT_URI,null,null,null,null);
                    //cursor.getCount();//TODOcomment this line
                    //bookManager = new BookManager(MainActivity.this);
                   // bookManager.getAllBooksAsync(this);
                    //this.getContentResolver().notifyChange(BookContract.CONTENT_URI(result.id),null);
                    /*bookAdapter.changeCursor(cursor);
                    bookAdapter.notifyDataSetChanged();*/
                    //contentResolver.notifyChange(myRowUri,null);
                }
                break;
            case CHECKOUT_REQUEST:
                // CHECKOUT: empty the shopping cart.
                // It is okay to do this on the main thread for BookStoreWithContentProvider
                if(resultCode==RESULT_CANCELED){
                    /*AsyncContentResolver asyncContentResolver=new AsyncContentResolver(getContentResolver());
                    asyncContentResolver.deleteAsync(BookContract.CONTENT_URI,null,null);*/
                    //Cursor cursor=getContentResolver().query(BookContract.CONTENT_URI,null,null,null,null);
                   // bookManager.getAllBooksAsync(this);
                    HashSet<Long>deletingBooks=new HashSet<Long>();
                    for(int i=0;i<AllBooksCursor.getCount();i++)
                    {
                        Book thisBook=new Book(AllBooksCursor);
                        deletingBooks.add(thisBook.id);
                        AllBooksCursor.moveToPosition(i);
                    }
                    bookManager.deleteBooksAsync(deletingBooks);
                   // bookManager.deleteBooksAsync();
                    shoppingCart.clear();
                    numberOfBooks=0;
                }
               // bookAdapter.changeCursor(getContentResolver().query(BookContract.CONTENT_URI,null,null,null,null));
                //bookAdapter.notifyDataSetChanged();
                //TextView cart_empty = (TextView)findViewById(android.R.id.empty);
                //cart_empty.setText(R.string.empty_cart);
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
     * TODOQuery listener callbacks
     */

    @Override
    public TypedCursor handleResults(TypedCursor results) {
        // TODOupdate the adapter
        bookAdapter.changeCursor(results.getCursor());
        AllBooksCursor=results.getCursor();
        bookAdapter.notifyDataSetChanged();
        return  results;
    }

    @Override
    public void closeResults() {
        // TODOupdate the adapter
        bookAdapter.notifyDataSetChanged();
    }


    /*
     * Selection of a book from the list view
     */

    /*@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODOquery for this book's details, and send to ViewBookActivity
        // ok to do on main thread for BookStoreWithContentProvider

    }
*/

    /*
     * Handle multi-choice action mode for deletion of several books at once
     */
}