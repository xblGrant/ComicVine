package edu.psu.gsa5054.comicvine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.psu.gsa5054.comicvine.ComicVine.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//this activity implements clearFavoritesDialogListener
public class Favorites extends AppCompatActivity implements clearFavoritesDialog.clearFavoritesDialogListener {

    private Cursor cursor;
    private SQLiteDatabase db;
    private List<Characters> searchResults;
    private static final String TAG = "SearchScreen";
    private static final String baseURL = "https://comicvine.gamespot.com/api/";
    private static final String apiKey = "/?api_key=1b933662d46319e7bb1085f8a60f5a11519cc4f0";
    private static String CHARACTER_NAME = "characterName", COUNT_ISSUE_APPEARANCES = "countIssueAppearances",
            DECK = "deck", CHARACTER_ID = "characterID", IMAGE = "image", PUBLISHER_NAME = "publisherName",
            FIRST_APPEARANCE_ISSUE_NAME = "firstAppearanceIssueName", FIRST_APPEARANCE_ISSUE_NUM = "firstAppearanceIssueNum";

    Boolean dbReady = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.favoritesToolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

        FavoriteDB.getInstance(this).asyncWritableDatabase(new FavoriteDB.onDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase faveDB) {
                db = faveDB;
                dbReady = true;
                onCreateArrayAdapter();
            }
        });
    }

    //initialize the adapter that is bound to the TextView
    private void onCreateArrayAdapter() {
        ArrayAdapter<Characters> arrayAdapter;
        ListView resultsView = (ListView) findViewById(R.id.resultsListView);

        getFavoritesByID();

        arrayAdapter = new CharacterAdapter(this, R.layout.search_item, (ArrayList<Characters>) searchResults);
        resultsView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favoritesmenu, menu);
        return true;
    }

    //END onCREATE ***************************************************************************

    @SuppressLint("StaticFieldLeak")
    private void getFavoritesData() {

        new AsyncTask<Void, Void, Cursor>() {
            String userID;

            @Override
            protected Cursor doInBackground(Void... params) {
                userID = mAuth.getUid();
                String where = "UID = " + userID;
                String[] projection = {"_id", "UID", "CharacterID"};
                return db.query("FAVORITE", projection, where, null, null, null, null);
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                Favorites.this.cursor = cursor;
            }

        }.execute();
    }

    @Override //this is for appBar options at top of screen. When user selects action
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signoutMenuButton: {
                //user chose to signout. direct to activity_main and sign out if signed in.
                //TODO: check if signed in, if true (sign out, direct to main_activity) else (direct to main_activity)
                startActivity(new Intent(Favorites.this, MainActivity.class));
                Toast.makeText(Favorites.this, "Signed Out", Toast.LENGTH_SHORT).show();
                return true;
            }

            case R.id.searchMenuButton: {
                //user chose to go to favorites page. direct to activity_favorites.
                startActivity(new Intent(Favorites.this, SearchScreen.class));
                return true;
            }

            case R.id.clearFavoritesButton: {
                //user chose to clear favorites. AlertDialog to verify clearing favorites
                eraseFavorites();
            }
        }
        return true;
    }

    // TODO: implement to erase favorites from database
    @SuppressLint("StaticFieldLeak")
    public void eraseFavorites(){

        new AsyncTask<Void, Void, Cursor>() {
            String userID;

            @Override
            protected Cursor doInBackground(Void... params) {
                // TODO: Delete entries for specific UID.
//                userID = mAuth.getUid();
//                String where = "UID = " + userID;
//                String[] projection = {"_id", "UID", "CharacterID"};
//                return db.query("FAVORITE", projection, where, null, null, null, null);
                return null;
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                Favorites.this.cursor = cursor;
            }
        };

        // TODO: does this go here?
        //this creates the new dialog
        DialogFragment dialogFragment = new clearFavoritesDialog();
        //this causes the dialog to be shown to user
        dialogFragment.show(getFragmentManager(), "clearFavorites");
    }

    //TODO: this should make the current favorites be erased
    //this is from the clearFavoritesDialogListener interface
    public void onPositiveClick(){
        Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_LONG).show();
    }

    @SuppressLint("StaticFieldLeak")
    public void getFavoritesByID(final String queryName) {
        new AsyncTask<Void, Void, List<Characters>>() {

            @Override
            public List<Characters> doInBackground(Void... params) {

                String queryParam = queryName;
                StringBuilder buffedUp = new StringBuilder();
                String[] parts = queryName.split(" ");
                int length = parts.length;
                if (length > 1) {
                    for (int i = 0; i < length - 1; i++) {
                        buffedUp.append(parts[i] + "_");
                    }
                    buffedUp.append(parts[length - 1]);
                    queryParam = buffedUp.toString();
                }

                String filter = "&filter=name%3A" + queryParam + "&format=JSON";
                String sURL = baseURL + "characters" + apiKey + filter;

                List<Characters> characters = null;

                try {
                    URL url;
                    HttpURLConnection urlConnection;

                    try {
                        url = new URL(sURL);
                        urlConnection = (HttpURLConnection) url.openConnection();
                    } catch (IOException e) {
                        Log.e(TAG, "Error opening connection for site: " + e.getMessage());
                        return null;
                    }

                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        characters = parseResponse(in);
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading response from site: " + e.getMessage());
                    } catch (JSONException e) {
                        Log.e(TAG, "Malformed JSON returned from site: " + e.getMessage());
                    } finally {
                        urlConnection.disconnect();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error trying to traverse que" +
                            "ry results: " + e.getMessage());
                }
                return characters;
            }

            @Override
            public void onPostExecute(List<Characters> characters) {
                searchResults = characters;
                onCreateArrayAdapter();
            }
        }.execute();
    }

    private static List<Characters> parseResponse(BufferedReader in) throws IOException, JSONException {
        StringBuilder response = new StringBuilder(2048);
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line);
        }
        String sResponse = response.toString();
        JSONObject results = new JSONObject(sResponse);
        JSONArray arrayResult = results.getJSONArray("results");

        if (arrayResult != null) {
            Gson gson = new Gson();
            List<Characters> queryResult = new ArrayList<>();
            for (int i = 0; i < arrayResult.length(); i++) {
                String result = arrayResult.get(i).toString();
                Characters characterInfo = gson.fromJson(result, Characters.class);
                queryResult.add(characterInfo);
            }

            return queryResult;
        }

        return null;
    }

    public class CharacterAdapter extends ArrayAdapter<Characters> {
        Context context;
        int layoutResourceId;
        ArrayList<Characters> data;

        public CharacterAdapter(Context context, int resource, ArrayList<Characters> objects) {
            super(context, resource, objects);
            this.layoutResourceId = resource;
            this.context = context;
            this.data = objects;
        }

        @Override @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent){
            View row = convertView;
            CharacterAdapter.InfoHolder holder = null;
            final int location = position;

            if (row == null){
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new CharacterAdapter.InfoHolder();
                holder.image = (ImageView) row.findViewById(R.id.characterImage);
                holder.name = (TextView) row.findViewById(R.id.characterName);
                holder.publisher = (TextView) row.findViewById(R.id.characterPublisher);

                row.setTag(holder);
            } else {
                holder = (CharacterAdapter.InfoHolder) row.getTag();
            }

            Characters item = data.get(position);

            Image image = item.getImage();
            String imageUrl = ((image == null)
                    ? "https://comicvine.gamespot.com/api/image/scale_avatar/6373148-blank.png"
                    : image.getThumb_url());

            Publisher publisher = item.getPublisher();
            String publisherName = ((publisher == null) ? "" : publisher.getName());

            Picasso.get()
                    .load(imageUrl)
                    .resize(200,200)
                    .into(holder.image);
            holder.name.setText(item.getName());
            holder.publisher.setText(publisherName);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Characters selected = searchResults.get(location);
                    Intent intent = new Intent(Favorites.this, SelectedItem.class);

                    intent.putExtra(CHARACTER_NAME, selected.getName());
                    intent.putExtra(COUNT_ISSUE_APPEARANCES, selected.getCount_of_issue_appearances());
                    intent.putExtra(DECK, selected.getDeck());
                    intent.putExtra(CHARACTER_ID, selected.getId());

                    Image image = selected.getImage();
                    String imageURL = ((image == null)
                            ? "https://comicvine.gamespot.com/api/image/scale_avatar/6373148-blank.png"
                            : image.getMedium_url());
                    intent.putExtra(IMAGE, imageURL);

                    Publisher publisher = selected.getPublisher();
                    String publisherName = ((publisher == null) ? "" : publisher.getName());
                    intent.putExtra(PUBLISHER_NAME, publisherName);

                    FirstAppearanceIssue fai = selected.getFirst_appeared_in_issue();
                    String faiName = ((fai == null) ? "" : fai.getName());
                    String faiNum = ((fai == null) ? "" : fai.getIssue_number());
                    intent.putExtra(FIRST_APPEARANCE_ISSUE_NAME, faiName);
                    intent.putExtra(FIRST_APPEARANCE_ISSUE_NUM, faiNum);

                    startActivity(intent);
                }
            });

            return row;
        }

        private class InfoHolder {
            public ImageView image;
            public TextView name;
            public TextView publisher;
        }
    }
}
