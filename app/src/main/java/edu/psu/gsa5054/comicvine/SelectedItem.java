package edu.psu.gsa5054.comicvine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class SelectedItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.selectedItemToolbar);
        setSupportActionBar(toolbar);
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
}
