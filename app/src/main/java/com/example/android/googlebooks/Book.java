package com.example.android.googlebooks;

/**
 * This class defines a Book object states and methods.
 */
public class Book {

    //All important information from the book to be stored.
    private String mTitle;
    private String mPublisher;
    private String mAuthor;
    private int mNumberPages;
    private String mLanguage;

    //Public constructor is declared.
    public Book(String title, String publisher, String author, int numberPages,
                String language) {
        mTitle = title;
        mPublisher = publisher;
        mAuthor = author;
        mNumberPages = numberPages;
        mLanguage = language;
    }

    //Defined below are getter methods.
    public String getTitle() {return mTitle;}
    public String getPublisher() {return mPublisher;}
    public String getAuthor() {return mAuthor;}
    public int getNumberPages() {return mNumberPages;}
    public String getLanguage() {return mLanguage;}

}
