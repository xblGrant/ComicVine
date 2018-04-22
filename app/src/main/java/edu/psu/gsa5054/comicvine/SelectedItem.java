package edu.psu.gsa5054.comicvine;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class SelectedItem extends AppCompatActivity {

    private static String CHARACTER_NAME = "characterName", COUNT_ISSUE_APPEARANCES = "countIssueAppearances",
            DECK = "deck", CHARACTER_ID = "characterID", IMAGE = "image", PUBLISHER_NAME = "publisherName",
            FIRST_APPEARANCE_ISSUE_NAME = "firstAppearanceIssueName", FIRST_APPEARANCE_ISSUE_NUM = "firstAppearanceIssueNum";
    private String characterName, characterID, countIssueAppearances, deck, imageURL, publisherName, faiName, faiNumber;

    private SQLiteDatabase db;
    private SharedPreferences sharedPreferences;
    Boolean dbReady = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.selectedItemToolbar);
        setSupportActionBar(toolbar);

        onCreateGetIntentInformation();
        onCreateViewFields();

        mAuth = FirebaseAuth.getInstance();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("olive_header",false)) {
            toolbar.setBackgroundColor(Color.GREEN);
        }

        FavoriteDB.getInstance(this).asyncWritableDatabase(new FavoriteDB.onDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase faveDB) {
                db = faveDB;
                dbReady = true;
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Toolbar toolbar = (Toolbar) findViewById(R.id.selectedItemToolbar);
        if (sharedPreferences.getBoolean("olive_header",false)) {
            toolbar.setBackgroundColor(Color.GREEN);
        }
        else {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
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
                startActivity(new Intent(SelectedItem.this, SearchScreen.class));
                return true;
            }

            case R.id.addFavoriteButton: {
                addToFavorites();
                return true;
            }
        }
        return true;
    }

    private void addToFavorites() {
        if (dbReady){
            try {
                db.beginTransaction();
                ContentValues values = new ContentValues();
                values.put("CharacterID", characterID/*string value*/);
                values.put("UID", mAuth.getUid());
                db.insertOrThrow(/*table name*/"FAVORITE", null, values);
                db.setTransactionSuccessful();
                db.endTransaction();

                Toast.makeText(SelectedItem.this, "Added to Favorites", Toast.LENGTH_LONG).show();
            } catch (SQLiteConstraintException e){
                Toast.makeText(SelectedItem.this, "Already Favorited", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(SelectedItem.this, "Try again shortly", Toast.LENGTH_LONG).show();
        }
    }

    private void onCreateGetIntentInformation() {
        Intent intent = getIntent();
        characterName = intent.getStringExtra(CHARACTER_NAME);
        characterID = intent.getStringExtra(CHARACTER_ID);
        countIssueAppearances = intent.getStringExtra(COUNT_ISSUE_APPEARANCES);
        deck = intent.getStringExtra(DECK);
        imageURL = intent.getStringExtra(IMAGE);
        publisherName = intent.getStringExtra(PUBLISHER_NAME);
        faiName = intent.getStringExtra(FIRST_APPEARANCE_ISSUE_NAME);
        faiNumber = intent.getStringExtra(FIRST_APPEARANCE_ISSUE_NUM);
    }

    private void onCreateViewFields() {
        ImageView characterImage = findViewById(R.id.selectedCharacterImage);
        Picasso.get().load(imageURL).resize(149, 199).into(characterImage);

        TextView selectedName = findViewById(R.id.characterNameTextView);
        String info = "Name: " + characterName;
        selectedName.setText(info);

        TextView selectedPublisher = findViewById(R.id.publisherTextView);
        if (publisherName != null) {
            info = "Publisher: " + publisherName;
            selectedPublisher.setText(info);
        } else {
            selectedPublisher.setText("");
        }

        TextView selectedFirstAppearance = findViewById(R.id.firstAppearanceTextView);
        if (faiName != null && faiNumber != null) {
            info = "First Appearance:\n" + faiName + " #" + faiNumber;
            selectedFirstAppearance.setText(info);
        } else {
            selectedFirstAppearance.setText("");
        }

        TextView selectedIssuesAppeared = findViewById(R.id.issuesAppearedTextView);
        if (countIssueAppearances != null) {
            info = "# Issues in: " + countIssueAppearances;
            selectedIssuesAppeared.setText(info);
        } else {
            selectedIssuesAppeared.setText("");
        }

        TextView selectedDescription = findViewById(R.id.characterSummaryTextView);
        if (deck != null) {
            info = "Summary: \n" + deck;
            selectedDescription.setText(info);
        } else {
            selectedDescription.setText("");
        }
    }
}
