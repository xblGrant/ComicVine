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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

//this activity implements clearFavoritesDialogListener
public class Favorites extends AppCompatActivity implements clearFavoritesDialog.clearFavoritesDialogListener {

    private SQLiteDatabase db;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.favoritesToolbar);
        setSupportActionBar(toolbar);
        //getActionBar().setDisplayHomeAsUpEnabled(true); //this provides "UP" navigation. not working right now

        FavoriteDB.getInstance(this).asyncWritableDatabase(new FavoriteDB.onDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase faveDB) {
                db = faveDB;
                dbAsyncLoadCursor(false);
            }
        });

        onCreateFavoritesAdapter();
    }

    //initialize the adapter that is bound to the TextView
    // TODO: update to populate favorites list... listView
    private void onCreateFavoritesAdapter() {
        //initially set to null. it is not ready
        adapter = new SimpleCursorAdapter(this, R.layout.activity_favorites, null,
                new String[]{"UID"},
                new int[]{}, 0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                //
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
    private void dbAsyncLoadCursor(boolean scrollToEnd) {

        new AsyncTask<Boolean, Void, Cursor>() {
            boolean scrollToEnd;

            @Override
            protected Cursor doInBackground(Boolean... params) {
                // TODO: Query database for favorites.
                // TODO: query based on current user.
//                scrollToEnd = params[0];
//                String where = null;
//                String[] projection = {"_id", "UID"};
//                return db.query("USER", projection, where, null, null, null, null);
                return null;
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                adapter.swapCursor(cursor);
            }
        }.execute(scrollToEnd);
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
    public void eraseFavorites(){
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
