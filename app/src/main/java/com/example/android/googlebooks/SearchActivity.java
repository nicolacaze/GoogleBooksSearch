package com.example.android.googlebooks;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {

    public static String userQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        final boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();



        ImageView searchButton = (ImageView) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Check if there is Internet connection before submitting the request.
                If null, user get a message returned.*/
                if (isConnected) {

                    //Get the user input keyword.
                    getQuery();

                    //Call an explicit intent to launch the results activity.
                    Intent resultsCall = new Intent(SearchActivity.this, ResultsActivity.class);
                    startActivity(resultsCall);

                } else {
                    Toast.makeText(getApplicationContext(), "No Internet connection.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**This method helps retrieve the user input and transform it in a formatted String.
     * @return userQuery
     */
    private String getQuery() {
        EditText userInput = (EditText) findViewById(R.id.user_input);
        userQuery = userInput.getText().toString();

        //Remove potential spaces from the keyword.
        userQuery = userQuery.replace(" ", "+");

        return userQuery;
    }
}
