package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class ViewPeerActivity extends Activity {

    public static final String PEER_ID_KEY = "peer_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peer);

        //TODOModify the list to display the content of the peer
        Intent intent = getIntent();
        Peer peer=intent.getParcelableExtra(PEER_ID_KEY);
        TextView name =(TextView)findViewById(R.id.view_user_name);
        TextView timestamp =(TextView)findViewById(R.id.view_timestamp);
        TextView address =(TextView)findViewById(R.id.view_address);
        TextView port =(TextView)findViewById(R.id.view_port);

        port.setText(Integer.toString(peer.port));
        name.setText(peer.name);
        timestamp.setText(peer.timestamp.toString());
        address.setText(peer.address.toString());

    }

}
