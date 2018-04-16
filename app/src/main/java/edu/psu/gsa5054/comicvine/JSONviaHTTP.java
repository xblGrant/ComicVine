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

public class JSONviaHTTP {
    private static final String TAG = "JSONViaHttp";

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

                String resource = "characters";
                String apiKey = "/?api_key=1b933662d46319e7bb1085f8a60f5a11519cc4f0";
                String filter = "&filter=name%3A" + queryParam + "&format=JSON";
                String sURL = "https://comicvine.gamespot.com/api/" + resource + apiKey + filter;

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

        List<Characters> queryResult = new ArrayList<>();
        for (int i = 0; i < arrayResult.length(); i++){
            String results = arrayResult.get(i).toString();

            Gson gson = new Gson();
            Characters characterInfo = gson.fromJson(results, Characters.class);

            queryResult.add(characterInfo);
        }

        return queryResult;
    }

    private class Characters {
        String aliases, api_detail_url, birth, character_enemies, character_friends,
                count_of_issue_appearances, creators, date_added, date_last_updated, deck,
                description, gender, id, issue_credits, issues_died_in, movies, name,
                powers, real_name, site_detail_url, story_arc_credits, team_enemies,
                team_friends, teams, volume_credits;
        Image image;
        Origin origin;
        Publisher publisher;
        FirstAppearanceIssue first_appeared_in_issue;

        public String getAliases() {
            return aliases;
        }
        public void setAliases(String aliases) {
            this.aliases = aliases;
        }
        public String getApi_detail_url() {
            return api_detail_url;
        }
        public void setApi_detail_url(String api_detail_url) {
            this.api_detail_url = api_detail_url;
        }
        public String getBirth() {
            return birth;
        }
        public void setBirth(String birth) {
            this.birth = birth;
        }
        public String getCharacter_enemies() {
            return character_enemies;
        }
        public void setCharacter_enemies(String character_enemies) {
            this.character_enemies = character_enemies;
        }
        public String getCharacter_friends() {
            return character_friends;
        }
        public void setCharacter_friends(String character_friends) {
            this.character_friends = character_friends;
        }
        public String getCount_of_issue_appearances() {
            return count_of_issue_appearances;
        }
        public void setCount_of_issue_appearances(String count_of_issue_appearances) {
            this.count_of_issue_appearances = count_of_issue_appearances;
        }
        public String getCreators() {
            return creators;
        }
        public void setCreators(String creators) {
            this.creators = creators;
        }
        public String getDate_added() {
            return date_added;
        }
        public void setDate_added(String date_added) {
            this.date_added = date_added;
        }
        public String getDate_last_updated() {
            return date_last_updated;
        }
        public void setDate_last_updated(String date_last_updated) {
            this.date_last_updated = date_last_updated;
        }
        public String getDeck() {
            return deck;
        }
        public void setDeck(String deck) {
            this.deck = deck;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public FirstAppearanceIssue getFirst_appeared_in_issue() {
            return first_appeared_in_issue;
        }
        public void setFirst_appeared_in_issue(FirstAppearanceIssue first_appeared_in_issue) {
            this.first_appeared_in_issue = first_appeared_in_issue;
        }
        public String getGender() {
            return gender;
        }
        public void setGender(String gender) {
            this.gender = gender;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public Image getImage() {
            return image;
        }
        public void setImage(Image image) {
            this.image = image;
        }
        public String getIssue_credits() {
            return issue_credits;
        }
        public void setIssue_credits(String issue_credits) {
            this.issue_credits = issue_credits;
        }
        public String getIssues_died_in() {
            return issues_died_in;
        }
        public void setIssues_died_in(String issues_died_in) {
            this.issues_died_in = issues_died_in;
        }
        public String getMovies() {
            return movies;
        }
        public void setMovies(String movies) {
            this.movies = movies;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Origin getOrigin() {
            return origin;
        }
        public void setOrigin(Origin origin) {
            this.origin = origin;
        }
        public String getPowers() {
            return powers;
        }
        public void setPowers(String powers) {
            this.powers = powers;
        }
        public Publisher getPublisher() {
            return publisher;
        }
        public void setPublisher(Publisher publisher) {
            this.publisher = publisher;
        }
        public String getReal_name() {
            return real_name;
        }
        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }
        public String getSite_detail_url() {
            return site_detail_url;
        }
        public void setSite_detail_url(String site_detail_url) {
            this.site_detail_url = site_detail_url;
        }
        public String getStory_arc_credits() {
            return story_arc_credits;
        }
        public void setStory_arc_credits(String story_arc_credits) {
            this.story_arc_credits = story_arc_credits;
        }
        public String getTeam_enemies() {
            return team_enemies;
        }
        public void setTeam_enemies(String team_enemies) {
            this.team_enemies = team_enemies;
        }
        public String getTeam_friends() {
            return team_friends;
        }
        public void setTeam_friends(String team_friends) {
            this.team_friends = team_friends;
        }
        public String getTeams() {
            return teams;
        }
        public void setTeams(String teams) {
            this.teams = teams;
        }
        public String getVolume_credits() {
            return volume_credits;
        }
        public void setVolume_credits(String volume_credits) {
            this.volume_credits = volume_credits;
        }
    }

    private class Image {
        String icon_url, medium_url, screen_url, screen_large_url, small_url, super_url,
            thumb_url, tiny_url, original_url, image_tags;

        public String getIcon_url() {
            return icon_url;
        }
        public void setIcon_url(String icon_url) {
            this.icon_url = icon_url;
        }
        public String getMedium_url() {
            return medium_url;
        }
        public void setMedium_url(String medium_url) {
            this.medium_url = medium_url;
        }
        public String getScreen_url() {
            return screen_url;
        }
        public void setScreen_url(String screen_url) {
            this.screen_url = screen_url;
        }
        public String getScreen_large_url() {
            return screen_large_url;
        }
        public void setScreen_large_url(String screen_large_url) {
            this.screen_large_url = screen_large_url;
        }
        public String getSmall_url() {
            return small_url;
        }
        public void setSmall_url(String small_url) {
            this.small_url = small_url;
        }
        public String getSuper_url() {
            return super_url;
        }
        public void setSuper_url(String super_url) {
            this.super_url = super_url;
        }
        public String getThumb_url() {
            return thumb_url;
        }
        public void setThumb_url(String thumb_url) {
            this.thumb_url = thumb_url;
        }
        public String getTiny_url() {
            return tiny_url;
        }
        public void setTiny_url(String tiny_url) {
            this.tiny_url = tiny_url;
        }
        public String getOriginal_url() {
            return original_url;
        }
        public void setOriginal_url(String original_url) {
            this.original_url = original_url;
        }
        public String getImage_tags() {
            return image_tags;
        }
        public void setImage_tags(String image_tags) {
            this.image_tags = image_tags;
        }
    }

    private class Origin {
        String api_detail_url, id, name;

        public String getApi_detail_url() {
            return api_detail_url;
        }
        public void setApi_detail_url(String api_detail_url) {
            this.api_detail_url = api_detail_url;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    private class Publisher {
        String api_detail_url, id, name;

        public String getApi_detail_url() {
            return api_detail_url;
        }
        public void setApi_detail_url(String api_detail_url) {
            this.api_detail_url = api_detail_url;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    private class FirstAppearanceIssue {
        String api_detail_url, id, name, issue_number;

        public String getApi_detail_url() {
            return api_detail_url;
        }
        public void setApi_detail_url(String api_detail_url) {
            this.api_detail_url = api_detail_url;
        }
        public String getId() {
            return id;
        }
        public void setId(String id) {
            this.id = id;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getIssue_number() {
            return issue_number;
        }
        public void setIssue_number(String issue_number) {
            this.issue_number = issue_number;
        }
    }
}
