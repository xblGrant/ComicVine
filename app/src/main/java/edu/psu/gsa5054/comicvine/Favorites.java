package edu.psu.gsa5054.comicvine;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

//this activity implements clearFavoritesDialogListener
public class Favorites extends AppCompatActivity implements clearFavoritesDialog.clearFavoritesDialogListener {

    private SQLiteDatabase db;
    private SimpleCursorAdapter adapter;

    Boolean dbReady = false;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.favoritesToolbar);
        setSupportActionBar(toolbar);
        //getActionBar().setDisplayHomeAsUpEnabled(true); //this provides "UP" navigation. not working right now

        mAuth = FirebaseAuth.getInstance();
        onCreateFavoritesAdapter();

        FavoriteDB.getInstance(this).asyncWritableDatabase(new FavoriteDB.onDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase faveDB) {
                db = faveDB;
                dbReady = true;
                dbAsyncLoadCursor();
            }
        });


    }

    //initialize the adapter that is bound to the TextView
    private void onCreateFavoritesAdapter() {
        //initially set to null. it is not ready
        adapter = new SimpleCursorAdapter(this, R.layout.activity_favorites, null,
                new String[]{"UID"},
                new int[]{}, 0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                // TODO: get favorites query from database.
                // following will be similar to SearchScreen
                // TODO: for each item in the resulting query, call ComicVine with characterID
                // TODO: with character information populate listview and set OnClickListener
                // TODO: use OnClickListener to start a new Intent to open SelectedItem

                // TODO: in SelectedItem, toggle btwn Add to Favorites / Remove from Favorites ???

//                TextView mainActivityTextView = (TextView) findViewById(R.id.databaseTestTextView);
//                //should set the textView to "test"
//                String test = cursor.getString(cursor.getColumnIndex("UID"));
//                Log.i("testing Database", test);
//                Log.i("testing null", "random string");
//                mainActivityTextView.setText(cursor.getString(cursor.getColumnIndex("UID")));
//                cursor.close();
//                //possibly close db db.close()???
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favoritesmenu, menu);
        return true;
    }

    //END onCREATE ***************************************************************************

    @SuppressLint("StaticFieldLeak")
    private void dbAsyncLoadCursor() {

        new AsyncTask<Void, Void, Cursor>() {
            String userID;

            @Override
            protected Cursor doInBackground(Void... params) {
                // TODO: Query database for favorites.
                // TODO: query based on current user.
                userID = mAuth.getUid();
                String where = "UID = " + userID;
                String[] projection = {"_id", "UID", "CharacterID"};
                return db.query("FAVORITE", projection, where, null, null, null, null);
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                adapter.swapCursor(cursor);
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
                adapter.swapCursor(cursor);
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
}
