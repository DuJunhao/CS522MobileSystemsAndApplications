package edu.stevens.cs522.chatserver.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.databases.MessagesDbAdapter;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends Activity  {

    /*
     * TODOSee ChatServer for example of what to do, query peers database instead of messages database.
     */

    private ArrayList<Peer> allPeers;
    private SimpleCursorAdapter peersAdapter;
    private MessagesDbAdapter peersADbAdapter;
    private Cursor cursor=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        Intent intent = getIntent();
        allPeers= intent.getParcelableArrayListExtra(ChatServer.ViewPeers);
        // TODOopen the database using the database adapter
        peersADbAdapter=new MessagesDbAdapter(this);
        peersADbAdapter.open();
        // TODOquery the database using the database adapter, and manage the cursor on the messages thread
        cursor=peersADbAdapter.fetchAllPeers();
        // TODOuse SimpleCursorAdapter to display the messages received.
        if(cursor!=null)
        {
            this.startManagingCursor(cursor);
        }
        String[] from = new String[] { PeerContract.Id, PeerContract.NAME };
        int[] to = new int[] { android.R.id.text1, android.R.id.text2 };

        peersAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                cursor, from, to,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        ListView list = (ListView)findViewById(R.id.peerList);
        list.setAdapter(peersAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ViewPeersActivity.this, ViewPeerActivity.class);
                intent.putExtra(ViewPeerActivity.PEER_ID_KEY, allPeers.get(position));
                startActivity(intent);
            }
        });
    }


   /* @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        *//*
         * Clicking on a peer brings up details
         *//*
        Intent intent = new Intent(this, ViewPeerActivity.class);
        intent.putExtra(ViewPeerActivity.PEER_ID_KEY, allPeers.get(position));
        startActivity(intent);
    }*/
}
