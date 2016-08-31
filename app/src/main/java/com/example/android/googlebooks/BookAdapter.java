package com.example.android.googlebooks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nicolaslacaze on 22/08/16.
 */
public class BookAdapter extends ArrayAdapter<Book> {

    //Define public constructor.
    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.book_layout, parent, false);
        }

        Book currentBook = getItem(position);

        //Use of a ViewHolder object to easily access each view without the need for the look-up.
        ViewHolder holder = new ViewHolder();
        holder.bookTitle = (TextView) listItemView.findViewById(R.id.book_title) ;
        holder.bookAuthor = (TextView) listItemView.findViewById(R.id.book_author);
        holder.bookPublisher = (TextView) listItemView.findViewById(R.id.book_publisher);
        holder.bookPages = (TextView) listItemView.findViewById(R.id.book_pages);
        holder.bookLanguage = (TextView) listItemView.findViewById(R.id.book_language);

        //Setting correct retrieved values for each views.
        holder.bookTitle.setText(currentBook.getTitle());
        holder.bookAuthor.setText(currentBook.getAuthor());
        holder.bookPublisher.setText(currentBook.getPublisher());

        if (currentBook.getNumberPages() == 0) {
            holder.bookPages.setText(R.string.no_pages);
        } else {
            holder.bookPages.setText(String.valueOf(currentBook.getNumberPages()) + " pages");
        }
        holder.bookLanguage.setText("Language: " + currentBook.getLanguage());

        return listItemView;
    }
    //This class helps holding our set of views.
    private static class ViewHolder {
        TextView bookTitle;
        TextView bookAuthor;
        TextView bookPublisher;
        TextView bookPages;
        TextView bookLanguage;
    }
}


