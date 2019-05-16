package edu.stevens.cs522.bookstoredatabase.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import edu.stevens.cs522.bookstoredatabase.R;


public class CheckoutActivity extends AppCompatActivity {

	public static final String Check_Out_Result = "checkout_result";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.checkout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		// TODOdisplay ORDER and CANCEL options.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.checkout_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// TOD
		switch (item.getItemId())
		{
			case R.id.order_action :
				Intent intent=getIntent();
				int Amount=intent.getIntExtra(CheckoutActivity.Check_Out_Result,3);
				Toast toast=Toast.makeText(CheckoutActivity.this,"You have ordered "+Integer.toString(Amount)+" books",Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				break;
			case R.id.cancel_action_2:
				//finish();
				setResult(RESULT_CANCELED);
				//overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
				break;
			default:
		}
		
		// ORDER: display a toast message of how many books have been ordered and return
		
		// CANCEL: just return with REQUEST_CANCELED as the result code

		//Intent intent = new Intent(this, MainActivity.class);
		//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//startActivity(intent);
		finish();
		return false;
	}
	
}