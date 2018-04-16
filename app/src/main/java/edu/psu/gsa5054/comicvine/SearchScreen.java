package edu.psu.gsa5054.comicvine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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

    private ArrayAdapter<Characters> arrayAdapter;
    private List<Characters> searchResults;
    private static final String TAG = "SearchScreen";
    private static final String baseURL = "https://comicvine.gamespot.com/api/";
    private static final String apiKey = "/?api_key=1b933662d46319e7bb1085f8a60f5a11519cc4f0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchScreenToolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); /This provides "UP" navigation. Can't get it to work right now

        onCreateArrayAdapter();
        onCreateSearchButton();
    }

    private void onCreateArrayAdapter() {
        final ListView resultsView = findViewById(R.id.resultsListView);
        // TODO: create layout for list item
//        arrayAdapter = new ArrayAdapter<Characters>(this, R.layout./*?????*/, searchResults);
        resultsView.setAdapter(arrayAdapter);

        resultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // TODO: move to SearchItem Activity and carry character information forward
                String selectedCharacter = searchResults.get(position).getName();
                Toast.makeText(getApplicationContext(), "Character Selected: " + selectedCharacter, Toast.LENGTH_LONG).show();
            }
        });
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
                //TODO: check if signed in, if true (sign out, direct to main_activity) else (direct to main_activity)
                startActivity(new Intent(SearchScreen.this, MainActivity.class));
                Toast.makeText(SearchScreen.this, "Signed Out", Toast.LENGTH_SHORT).show();
                return true;
            }

            case R.id.favoritesMenuButton: {
                //user chose to go to favorites page. direct to activity_favorites.
                startActivity(new Intent(SearchScreen.this, Favorites.class));
                return true;
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

                for (Characters character: characters){
                    Log.i(TAG, character.getName() + " " + character.getImage().getThumb_url());
                }
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
        JSONObject result = new JSONObject(sResponse);
        JSONArray arrayResult = result.getJSONArray("results");

        Gson gson = new Gson();
        List<Characters> queryResult = new ArrayList<>();
        for (int i = 0; i < arrayResult.length(); i++){
            String results = arrayResult.get(i).toString();
            Characters characterInfo = gson.fromJson(results, Characters.class);
            queryResult.add(characterInfo);
        }

        return queryResult;
    }
}
