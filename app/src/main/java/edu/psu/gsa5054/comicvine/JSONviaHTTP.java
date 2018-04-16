package edu.psu.gsa5054.comicvine;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

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

public class JSONviaHTTP {
    private static final String TAG = "JSONViaHttp";
    private static final String baseURL = "https://comicvine.gamespot.com/api/";
    private static final String apiKey = "/?api_key=1b933662d46319e7bb1085f8a60f5a11519cc4f0";

    @SuppressLint("StaticFieldLeak")
    public static void getByCharacterName(final String queryName) {
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

                // TODO: populate listView
                for (Characters character: characters){
                    Log.i(TAG, character.getName() + " " + character.getPublisher().getName());
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
