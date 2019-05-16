package edu.stevens.cs522.bookstore.activities;

import java.util.ArrayList;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.R;
import edu.stevens.cs522.bookstore.util.BooksAdapter;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";

	// Use this when logging errors and warnings.
	private static final String TAG = MainActivity.class.getCanonicalName();
	
	// These are request codes for subactivity request calls
	static final private int ADD_REQUEST = 1;

	static final private int CHECKOUT_REQUEST = ADD_REQUEST + 1;

    public static final String viewBook = "view_Book";

    static final private String Cart_State = "Cart_State";
	// There is a reason this must be an ArrayList instead of a List.
	private ArrayList<Book> shoppingCart=new ArrayList<Book>();
    private BooksAdapter cartAdapter;
    //ArrayList<String> items = new ArrayList<String>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// TODOcheck if there is saved UI state, and if so, restore it (i.e. the cart contents)
		if (savedInstanceState != null) {
            shoppingCart=savedInstanceState.getParcelableArrayList(Cart_State);
		}
		// TODOSet the layout (use cart.xml layout)
		setContentView(R.layout.cart);
		// TODOuse an array adapter to display the cart contents.
		ListView list = (ListView)findViewById(android.R.id.list);
        cartAdapter=new BooksAdapter(this,shoppingCart);
		list.setAdapter(cartAdapter);

        this.registerForContextMenu(list);
       /* list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent viewBook_Intent=new Intent(MainActivity.this, ViewBookActivity.class);
                viewBook_Intent.putExtra(viewBook,shoppingCart.get(position));
                startActivity(viewBook_Intent);
            }
        });*/

	}

    public void onCreateContextMenu(ContextMenu menu, View v,
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
       AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()){
            case 1:
                Intent viewBook_Intent=new Intent(this, ViewBookActivity.class);
                viewBook_Intent.putExtra(viewBook,shoppingCart.get((int)info.id ));
                startActivity(viewBook_Intent);
                break;
            case 2:
                shoppingCart.remove((int)info.id);
                cartAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);
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

            //  TODODD provide the UI for adding a book
            case R.id.add:
                Intent addIntent = new Intent(this, AddBookActivity.class);
               // addIntent.putExtra(AddBookActivity.BOOK_RESULT_KEY,item.getIntent());
                startActivityForResult(addIntent, ADD_REQUEST);
                break;

            //  TODOCHECKOUT provide the UI for checking out
            case R.id.checkout:
                Intent checkIntent=new Intent(this,CheckoutActivity.class);
                checkIntent.putExtra(CheckoutActivity.Check_Out_Result,shoppingCart.size());
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
                Log.v(TAG,"request_code==ok");
                if (resultCode == RESULT_OK) {
                    Book result = intent.getParcelableExtra(AddBookActivity.BOOK_RESULT_KEY);
                    shoppingCart.add(result);
                    if(shoppingCart.size()>0)
                    {
                        TextView cart_empty = (TextView)findViewById(android.R.id.empty);
                        cart_empty.setText("");
                    }
                   // items.add(result.title);
                    cartAdapter.notifyDataSetChanged();
                }
                break;
            case CHECKOUT_REQUEST:
                // CHECKOUT: empty the shopping cart.
                if(resultCode==RESULT_CANCELED){
                    shoppingCart.clear();
                    cartAdapter.clear();
                    cartAdapter.notifyDataSetChanged();
                }
                TextView cart_empty = (TextView)findViewById(android.R.id.empty);
                cart_empty.setText(R.string.empty_cart);
                break;

            default:
        }
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		// TODOsave the shopping cart contents (which should be a list of parcelables).
        savedInstanceState.putParcelableArrayList(Cart_State,shoppingCart);
        super.onSaveInstanceState(savedInstanceState);
	}
	
}