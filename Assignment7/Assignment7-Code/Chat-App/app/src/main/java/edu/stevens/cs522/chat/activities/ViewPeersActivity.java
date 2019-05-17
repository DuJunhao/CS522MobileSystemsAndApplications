package edu.stevens.cs522.chat.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;


public class ViewPeersActivity extends Activity implements AdapterView.OnItemClickListener, QueryBuilder.IQueryListener<Peer> {

    /*
     * TODOSee ChatActivity for example of what to do, query peers database instead of messages database.
     */

    private PeerManager peerManager;

    private SimpleCursorAdapter peerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);

        // TODOinitialize peerAdapter with empty cursor (null)
        String[] from = new String[] { PeerContract.Id, PeerContract.NAME  };
        int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
        peerAdapter=new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                null, from, to,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );


        peerManager = new PeerManager(this);
        peerManager.getAllPeersAsync(this);

        ListView list = (ListView)findViewById(R.id.peerList);
        list.setAdapter(peerAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*if (cursor.moveToPosition(position)) {
                    Intent intent = new Intent(ViewPeersActivity.this, ViewPeerActivity.class);
                    Peer peer = new Peer(cursor);
                    intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
                    startActivity(intent);
                } else {
                    throw new IllegalStateException("Unable to move to position in cursor: "+position);
                }*/
                peerManager.getPeerAsync(position, new IContinue<Peer>() {
                    @Override
                    public void kontinue(Peer value) {
                        Intent intent = new Intent(ViewPeersActivity.this, ViewPeerActivity.class);
                        intent.putExtra(ViewPeerActivity.PEER_KEY, value);
                        startActivity(intent);
                    }
                });
            }
        });
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*
         * Clicking on a peer brings up details
         */
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            Intent intent = new Intent(this, ViewPeerActivity.class);
            Peer peer = new Peer(cursor);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: "+position);
        }
    }

    @Override
    public TypedCursor<Peer> handleResults(TypedCursor<Peer> results) {
        // TOD
        peerAdapter.changeCursor(results.getCursor());
        // AllBooksCursor=results.getCursor();
        peerAdapter.notifyDataSetChanged();
        return  results;
    }

    @Override
    public void closeResults() {
        // TOD
        peerAdapter.notifyDataSetChanged();
    }
}
