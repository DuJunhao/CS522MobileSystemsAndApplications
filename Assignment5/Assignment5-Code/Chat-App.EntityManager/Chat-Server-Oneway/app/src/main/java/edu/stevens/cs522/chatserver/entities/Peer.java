package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.util.DateUtils;
import edu.stevens.cs522.chatserver.util.InetAddressUtils;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable{

    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public InetAddress address;

    public int port;

    public  Peer()
    {
        this.id=0;
        this.name="";
        this.timestamp=null;
        this.address=null;
        this.port=0;
    }
    protected Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();

        timestamp= DateUtils.readDate(in);
        address= InetAddressUtils.readAddress(in);

        port = in.readInt();
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {
        @Override
        public Peer createFromParcel(Parcel in) {
            return new Peer(in);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);

        DateUtils.writeDate(parcel,timestamp);
        InetAddressUtils.writeAddress(parcel,address);

        parcel.writeInt(port);
    }

    // TODOadd operations for parcels (Parcelable), cursors and contentvalues
    public Peer(Cursor cursor) {
        // TODOinit from cursor
        this.id= Long.parseLong(PeerContract.getId(cursor));
        this.name=PeerContract.getName(cursor);
        this.timestamp=PeerContract.getTimestamp(cursor);
        this.address=PeerContract.getAddress(cursor);
        this.port=Integer.parseInt(PeerContract.getPort(cursor));
    }

    public ContentValues writeToProvider(ContentValues out) {
        // TODOwrite to ContentValues
        /*out.put(PeerContract.Id,this.id);
        out.put(PeerContract.NAME,this.name);
        DateUtils.putDate(out,"now",timestamp);
        InetAddressUtils.putAddress(out,"adress",this.address);
        out.put(PeerContract.PORT,Integer.toString(this.port));*/
        PeerContract.putId(out,Long.toString(this.id));
        PeerContract.putName(out,this.name);
        PeerContract.putTimestamp(out,this.timestamp);
        PeerContract.putAddress(out,this.address);
        PeerContract.putPort(out,Integer.toString(this.port));
        return out;
    }
}