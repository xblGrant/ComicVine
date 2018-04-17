package edu.psu.gsa5054.comicvine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SelectedItem extends AppCompatActivity {

    private static String CHARACTER_NAME = "characterName", ALIASES = "aliases", DOB = "dateofbirth",
            COUNT_ISSUE_APPEARANCES = "countIssueAppearances", CREATORS = "creators", DECK = "deck",
            DESCRIPTION = "description", GENDER = "gender", CHARACTER_ID = "characterID", MOVIES = "movies",
            POWERS = "powers", REAL_NAME = "realName", TEAMS = "teams", IMAGE = "image", PUBLISHER_NAME = "publisherName",
            FIRST_APPEARANCE_ISSUE_NAME = "firstAppearanceIssueName", FIRST_APPEARANCE_ISSUE_NUM = "firstAppearanceIssueNum";
    private String characterName, aliases, birth, countIssueAppearances, creators, deck, description, gender,
            characterID, movies, powers, realName, teams, imageURL, publisherName, faiName, faiNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.selectedItemToolbar);
        setSupportActionBar(toolbar);

        onCreateGetIntentInformation();
        // TODO: populate fields with the information passed from the intent
//        onCreateViewFields();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.selecteditemmenu, menu);
        return true;
    }

    @Override //this is for appBar options at top of screen. When user selects action
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signoutMenuButton: {
                //user chose to signout. direct to activity_main and sign out if signed in.
                //TODO: check if signed in, if true (sign out, direct to main_activity) else (direct to main_activity)
                startActivity(new Intent(SelectedItem.this, MainActivity.class));
                Toast.makeText(SelectedItem.this, "Signed Out", Toast.LENGTH_SHORT).show();
                return true;
            }

            case R.id.favoritesMenuButton: {
                //user chose to go to favorites page. direct to activity_favorites.
                startActivity(new Intent(SelectedItem.this, Favorites.class));
                return true;
            }

            case R.id.searchMenuButton: {
                //user chose to go to search page. direct to activity_search_screen
                startActivity(new Intent (SelectedItem.this, SearchScreen.class));
                return true;
            }

            case R.id.addFavoriteButton: {
                //TODO: this should add the displayed item to the favorites list
                Toast.makeText(SelectedItem.this, "not implemented yet", Toast.LENGTH_SHORT).show();
                return true;
            }
        }
        return true;
    }

    public void onCreateGetIntentInformation() {
        Intent intent = getIntent();
        characterName = intent.getStringExtra(CHARACTER_NAME);
        aliases = intent.getStringExtra(ALIASES);
        birth = intent.getStringExtra(DOB);
        countIssueAppearances = intent.getStringExtra(COUNT_ISSUE_APPEARANCES);
        creators = intent.getStringExtra(CREATORS);
        deck = intent.getStringExtra(DECK);
        description = intent.getStringExtra(DESCRIPTION);
        gender = intent.getStringExtra(GENDER);
        characterID = intent.getStringExtra(CHARACTER_ID);
        movies = intent.getStringExtra(MOVIES);
        powers = intent.getStringExtra(POWERS);
        realName = intent.getStringExtra(REAL_NAME);
        teams = intent.getStringExtra(TEAMS);
        imageURL = intent.getStringExtra(IMAGE);
        publisherName = intent.getStringExtra(PUBLISHER_NAME);
        faiName = intent.getStringExtra(FIRST_APPEARANCE_ISSUE_NAME);
        faiNumber = intent.getStringExtra(FIRST_APPEARANCE_ISSUE_NUM);
    }
}
