package edu.stevens.cs522.chat.rest;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import static android.app.Activity.RESULT_OK;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RequestService extends IntentService {

    private static final String TAG = RequestService.class.getCanonicalName();

    public static final String SERVICE_REQUEST_KEY = "edu.stevens.cs522.chat.rest.extra.REQUEST";
    public static final String Sync_REQUEST_KEY = "edu.stevens.cs522.chat.rest.extra.SyncREQUEST";
    public static final String RESULT_RECEIVER_KEY = "edu.stevens.cs522.chat.rest.extra.RECEIVER";

    private RequestProcessor processor;

    public RequestService() {
        super("RequestService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        processor = new RequestProcessor(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if(intent.getParcelableExtra(Sync_REQUEST_KEY)!=null)
        {
            Bundle bundle=intent.getParcelableExtra(Sync_REQUEST_KEY);
            Request request=bundle.getParcelable("test");
            ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER_KEY);
            if(request==null)
            {

            }
            else{
                Response response = processor.process(request);
            }

            if (receiver != null) {
                // TODOuse receiver to call back to activity
                receiver.send(RESULT_OK, null);
                //Intent intent1=new Intent(this,ChatActivity.class);
                //startActivity(intent1);
            } else {
                Log.i(TAG, "Missing receiver");
            }
        }
        else
        {
            Request request = intent.getParcelableExtra(SERVICE_REQUEST_KEY);
            ResultReceiver receiver = intent.getParcelableExtra(RESULT_RECEIVER_KEY);
            if(request==null)
            {

            }
            else{
                Response response = processor.process(request);
            }

            if (receiver != null) {
                // TODOuse receiver to call back to activity
                receiver.send(RESULT_OK, null);
                //Intent intent1=new Intent(this,ChatActivity.class);
                //startActivity(intent1);
            } else {
                Log.i(TAG, "Missing receiver");
            }
        }

    }

}
