package edu.stevens.cs522.bookstoredatabase.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Author implements Parcelable{

	// TodoModify this to implement the Parcelable interface.

	// NOTE: middleInitial may be NULL!

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
		name=this.toString();
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
}
