package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.util.Utils;

public class Book implements Parcelable {
	
	// TODOModify this to implement the Parcelable interface.

	public long id;

	public String title;

	public Author[] authors;

	public String isbn;

	public String price;

	public Book(){
		this.id = 0;
		this.title = "";
		this.authors = new Author[1];
		this.isbn = "";
		this.price = "";
	}

	public Book(long id, String title, Author[]authors, String isbn, String price) {
		this.id = id;
		this.title = title;
		this.authors = authors;
		this.isbn = isbn;
		this.price = price;
	}

	public String getFirstAuthor() {
		if (authors != null && authors.length > 0) {
			return authors[0].toString();
		} else {
			return "";
		}
	}

	public Book GetThisBook()
	{
		return new Book(this.id,this.title,this.authors,this.isbn,this.price);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public Book(Parcel in) {
		// TODOinit from parcel
		id = in.readLong();
		title = in.readString();
		//authors=(Author[])in.readParcelableArray(Author.class.getClassLoader()); wrong too
		/*for(int i=0;i<in.readArray(Author.class.getClassLoader()).length;i++)
		{
			authors[i]=(Author)in.readParcelableArray(Author.class.getClassLoader())[i];//Unmarshalling unknown type code 43 at offset 144
		}*/
		/*Parcelable[] parcelableArray=in.readParcelableArray(Author.class.getClassLoader());
		if(parcelableArray!=null)
		{
			authors= Arrays.copyOf(parcelableArray,parcelableArray.length,Author[].class);
		}*/
		authors=in.createTypedArray(Author.CREATOR);
		isbn = in.readString();
		price = in.readString();
	}

	public void writeToParcel(Parcel parcel, int i) {
		// TODOsave state to parcel
		parcel.writeLong(id);
		parcel.writeString(title);
		//parcel.writeParcelableArray(authors,i);
		parcel.writeTypedArray(authors,i);
		parcel.writeString(isbn);
		parcel.writeString(price);
	}

	public static final Creator<Book> CREATOR = new Creator<Book>() {
		@Override
		public Book createFromParcel(Parcel in) {
			return new Book(in);
		}

		@Override
		public Book[] newArray(int size) {
			return new Book[size];
		}
	};

	public Book(Cursor cursor) {
		// TODOinit from cursor
		String authorsName="";

		this.id=Long.parseLong(BookContract.getId(cursor));
		this.title= BookContract.getTitle(cursor);

		String[]authorNames=BookContract.getAuthors(cursor);
		for(int i=0;i<authorNames.length;i++)
		{
			authorsName+=authorNames[i]+"\n";
		}
		this.authors= Utils.parseAuthors(authorsName);
		this.isbn=BookContract.getISBN(cursor);
		this.price=BookContract.getPRICE(cursor);
	}

	public ContentValues writeToProvider(ContentValues out) {
		// TODOwrite to ContentValues
		out.put(BookContract.ID,this.id);
		out.put(BookContract.TITLE,this.title);
		String authorsName="";
		for(int i=0;i<this.authors.length;i++)
		{
			authorsName+=authors[i].toString()+"\n";
		}
		out.put(BookContract.AUTHORS,authorsName);
		out.put(BookContract.ISBN,this.isbn);
		out.put(BookContract.PRICE,this.price);
		return out;
	}



}