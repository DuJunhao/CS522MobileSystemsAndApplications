package edu.stevens.cs522.chat.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.chat.async.AsyncContentResolver;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.IEntityCreator;
import edu.stevens.cs522.chat.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chat.contracts.BaseContract;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.providers.ChatProvider;


/**
 * Created by dduggan.
 */

public class MessageManager extends Manager<ChatMessage> {

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<ChatMessage> creator = new IEntityCreator<ChatMessage>() {
        @Override
        public ChatMessage create(Cursor cursor) {
            return new ChatMessage(cursor);
        }
    };

    private AsyncContentResolver contentResolver;
    private Context CONTEXT;

    public MessageManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
        this.CONTEXT=context;
    }

    public void getAllMessagesAsync(IQueryListener<ChatMessage> listener) {
        // TODOuse QueryBuilder to complete this
        executeQuery(MessageContract.CONTENT_URI,listener);
    }

    public void persistAsync(final ChatMessage message) {
        // TOD
        ContentValues values = new ContentValues();
        message.writeToProvider(values);
        values.put(ChatProvider.PeerForeignKey,message.timestamp.getTime());
        contentResolver.insertAsync(MessageContract.CONTENT_URI, values,
                new IContinue<Uri>() {
                    public void kontinue(Uri uri) {
                        long id = BaseContract.getId(uri);
                    }
                });
    }

}
