package edu.psu.gsa5054.comicvine;

import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class Favorites extends AppCompatActivity implements clearFavoritesDialog.clearFavoritesDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.favoritesToolbar);
        setSupportActionBar(toolbar);
        //getActionBar().setDisplayHomeAsUpEnabled(true); //this provides "UP" navigation. not working right now
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.favoritesmenu, menu);
        return true;
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

    //this is called when user selects "Erase Favorites" option in appBar
    public void eraseFavorites(){
        DialogFragment dialogFragment = new clearFavoritesDialog();
        dialogFragment.show(getFragmentManager(), "clearFavorites");
    }

    //TODO: this should make the current favorites be erased
    public void onPositiveClick(){
        Toast.makeText(this, "Not Yet Implemented", Toast.LENGTH_LONG).show();
    }
}
