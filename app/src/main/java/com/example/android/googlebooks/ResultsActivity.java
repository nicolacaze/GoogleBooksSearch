package com.example.android.googlebooks;

import android.os.AsyncTask;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    //Log tag to be displayed for log messages.
    private static final String LOG_TAG = ResultsActivity.class.getSimpleName();
    private String userInput = SearchActivity.userQuery;
    //URL to query the Google Books dataset and include the user input from SearchActivity.
    private String gbRequestUrl = "https://www.googleapis.com/books/v1/volumes?q=" +
            userInput + "&maxResults=30";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_layout);

        RelativeLayout message = (RelativeLayout) findViewById(R.id.message);

        //Check if user input is empty. If so, display a message.
        if (userInput.isEmpty()) {
            message.setVisibility(View.VISIBLE);
        } else {

            // Kick off an {@link AsyncTask} to perform the network request
            BooksAsyncTask task = new BooksAsyncTask();
            task.execute();
        }
    }

    private void updateUi(ArrayList<Book> books) {

        ListView booksListView = (ListView) findViewById(R.id.list);

        //Set empty view message if no results are found.
        booksListView.setEmptyView(findViewById(R.id.empty_list_view));

        BookAdapter adapter = new BookAdapter(this, books);

        booksListView.setAdapter(adapter);
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and create
     * a ArrayList with the list of books matching the query in the response.
     */
    private class BooksAsyncTask extends AsyncTask<URL, Void, ArrayList<Book>> {

        @Override
        protected ArrayList<Book> doInBackground(URL... urls) {
            //Create a URL object.
            URL url = createUrl(gbRequestUrl);

            // Perform HTTP request to the URL and receive a JSON response back
            String JSONResponse = "";
            try {
                JSONResponse = makehttpRequest(url);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Problem making the http request to Google Books API", e);
            }

            // Extract relevant fields from the JSON response and create an {@link Book} object
            return extractFeaturesFromJson(JSONResponse);
        }

        /**
         * Update the screen with the given earthquake (which was the result of the
         * {@link BooksAsyncTask}).
         */
        @Override
        protected void onPostExecute(ArrayList<Book> books) {
            if (books == null) {
                return;
            }
            updateUi(books);
        }


        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String requestUrl) {
            URL url = null;
            try {
                url = new URL(requestUrl);
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Error with creating URL", e);
                return null;
            }
            return url;
        }

        /**
         * Make an HTTP request to the given URL and return a String as the response.
         */
        private String makehttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            //Handle the case when url returned from createUrl method is null.
            if (url == null) {
                return jsonResponse;
            }
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            int httpResponse;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /*In milliseconds*/);
                urlConnection.setConnectTimeout(15000 /*In milliseconds*/);
                urlConnection.connect();
                //We get the server response code to handle bad request.
                httpResponse = urlConnection.getResponseCode();
                if (httpResponse == 200) {
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e(LOG_TAG, "HTTP code response is: " + httpResponse);
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error making the http request", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        /**
         * Convert the {@link InputStream} into a String which contains the
         * whole JSON response from the server.
         */
        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }

        private ArrayList<Book> extractFeaturesFromJson(String booksJSON) {
            //If the JSON String is null or empty, return early.
            if (TextUtils.isEmpty(booksJSON)) {
                return null;
            }

            // Create an empty ArrayList that we can start adding books to.
            ArrayList<Book> books = new ArrayList<>();
            int pages;
            String publisher;
            String author;

            try {
                JSONObject baseJSONResponse = new JSONObject(booksJSON);
                if (baseJSONResponse.has("items")) {
                    JSONArray items = baseJSONResponse.getJSONArray("items");

                    //We loop through the array to get all results from the JSON.
                    for (int i = 0; i < items.length(); i++) {

                        JSONObject book = items.getJSONObject(i);
                        JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                        String title = volumeInfo.getString("title");

                    /*We check if authors are retrieved from data and loop through the JSONarray to
                    get all of them.*/
                        if (volumeInfo.has("authors")) {
                            JSONArray authors = volumeInfo.getJSONArray("authors");
                            author ="";
                            for (int y=0; y < authors.length(); y++) {
                                author += authors.getString(y) + " ";
                            }

                        } else {
                            author = "No author mentioned";
                        }

                        if (volumeInfo.has("publisher")) {
                            publisher = volumeInfo.getString("publisher");

                        } else {
                            publisher = "No publisher mentioned";
                        }

                    /*Check if pages count is retrieved from data. If not, we set pages to 0.
                    We then take care of formatting correctly TextView display in BookAdapter.*/
                        if (volumeInfo.has("pageCount")) {
                            pages = volumeInfo.getInt("pageCount");
                        } else {
                            pages = 0;
                        }

                        String language = volumeInfo.getString("language");

                        Book volume = new Book(title, publisher, author, pages, language);
                        books.add(volume);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "No book results.", Toast.LENGTH_SHORT).show();
                }
                return books;

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Problem parsing the books JSON result", e);
            }
           return null;
        }
    }
}
