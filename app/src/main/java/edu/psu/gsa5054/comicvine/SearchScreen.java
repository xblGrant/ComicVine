package edu.psu.gsa5054.comicvine;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import android.widget.Button;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import edu.psu.gsa5054.comicvine.ComicVine.*;

public class SearchScreen extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    private ArrayAdapter<Characters> arrayAdapter;
    private List<Characters> searchResults;
    private static final String TAG = "SearchScreen";
    private static final String baseURL = "https://comicvine.gamespot.com/api/";
    private static final String apiKey = "/?api_key=1b933662d46319e7bb1085f8a60f5a11519cc4f0";
    private static String CHARACTER_NAME = "characterName", COUNT_ISSUE_APPEARANCES = "countIssueAppearances",
            DECK = "deck", CHARACTER_ID = "characterID", IMAGE = "image", PUBLISHER_NAME = "publisherName",
            FIRST_APPEARANCE_ISSUE_NAME = "firstAppearanceIssueName", FIRST_APPEARANCE_ISSUE_NUM = "firstAppearanceIssueNum";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.searchScreenToolbar);
        setSupportActionBar(toolbar);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("olive_header",false)) {
            toolbar.setBackgroundColor(Color.GREEN);
        }

        onCreateArrayAdapter();
        onCreateSearchButton();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Toolbar toolbar = (Toolbar) findViewById(R.id.searchScreenToolbar);
        if (sharedPreferences.getBoolean("olive_header",false)) {
            toolbar.setBackgroundColor(Color.GREEN);
        }
        else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    private void onCreateArrayAdapter() {
        ListView resultsView = (ListView) findViewById(R.id.resultsListView);

        if (searchResults == null)
            searchResults = new ArrayList<>();

        arrayAdapter = new CharacterAdapter(this, R.layout.search_item, (ArrayList<Characters>) searchResults);
        resultsView.setAdapter(arrayAdapter);
    }

    private void onCreateSearchButton() {
        Button searchBtn = findViewById(R.id.searchSubmitButton);
        searchBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TextView searchTextView = findViewById(R.id.searchTextView);
                String searchQuery = searchTextView.getText().toString();
                getByCharacterName(searchQuery);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;
    }

    //END onCREATE ***************************************************************************

    @Override //this is for appBar options at top of screen. When user selects action
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signoutMenuButton: {
                //user chose to signout. direct to activity_main and sign out if signed in.
                mAuth.signOut();
                startActivity(new Intent(SearchScreen.this, MainActivity.class));
                return true;
            }

            case R.id.favoritesMenuButton: {
                //user chose to go to favorites page. direct to activity_favorites.
                startActivity(new Intent(SearchScreen.this, Favorites.class));
                return true;
            }

            case R.id.preferencesButton: {
                //user choses to go to preferences page
                startActivity(new Intent(SearchScreen.this, SettingsActivity.class));
            }
        }
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    public void getByCharacterName(final String queryName) {
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
            InfoHolder holder = null;
            final int location = position;

            if (row == null){
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new InfoHolder();
                holder.image = (ImageView) row.findViewById(R.id.characterImage);
                holder.name = (TextView) row.findViewById(R.id.characterName);
                holder.publisher = (TextView) row.findViewById(R.id.characterPublisher);

                row.setTag(holder);
            } else {
                holder = (InfoHolder) row.getTag();
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
                    Intent intent = new Intent(SearchScreen.this, SelectedItem.class);

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
