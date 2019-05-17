package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;

import static android.R.attr.author;

public class Author implements Parcelable {
	
	// TODOModify this to implement the Parcelable interface.

	public String firstName;

	public String middleInitial;

	public String lastName;

	public long book_fk;

	public String name;

	public Author(String firstName,String middleInitial,String lastName){
		this.firstName=firstName;
		this.middleInitial=middleInitial;
		this.lastName=lastName;
		this.book_fk=0;
		name=this.toString();
	}

	public Author(String authorText) {
		this.name = authorText;
		this.book_fk=0;
	}

	public Author(){
		this.firstName="";
		this.middleInitial="";
		this.lastName="";
		book_fk=0;
		name="";
	}
	protected Author(Parcel in) {
		firstName = in.readString();
		middleInitial = in.readString();
		lastName = in.readString();
		book_fk=in.readLong();
		name=in.readString();
	}

	public static final Creator<Author> CREATOR = new Creator<Author>() {
		@Override
		public Author createFromParcel(Parcel in) {
			return new Author(in);
		}

		@Override
		public Author[] newArray(int size) {
			return new Author[size];
		}
	};

	public String toString() {
		StringBuffer sb = new StringBuffer();
		if (firstName != null && !"".equals(firstName)) {
			sb.append(firstName);
			sb.append(' ');
		}
		if (middleInitial != null && !"".equals(middleInitial)) {
			sb.append(middleInitial);
			sb.append(' ');
		}
		if (lastName != null && !"".equals(lastName)) {
			sb.append(lastName);
		}
		return sb.toString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(firstName);
		parcel.writeString(middleInitial);
		parcel.writeString(lastName);
		parcel.writeLong(book_fk);
		parcel.writeString(this.toString());
	}

	public ContentValues writeToProvider(ContentValues out) {
		// TODOwrite to ContentValues
		out.put(AuthorContract.ID,this.book_fk);
		out.put(AuthorContract.FIRST_NAME,this.firstName);
		out.put(AuthorContract.MIDDLE_INITIAL,this.middleInitial);
		out.put(AuthorContract.LAST_NAME,this.lastName);
		out.put(AuthorContract.BOOK_FK,this.book_fk);
		out.put(AuthorContract.WHOLE_NAME,this.name);
		return out;
	}
}
