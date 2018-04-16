package edu.psu.gsa5054.comicvine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SearchScreen extends AppCompatActivity {

    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.searchScreenToolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true); /This provides "UP" navigation. Can't get it to work right now

        TextView searchTextField = (TextView) findViewById(R.id.searchTextView);

        onCreateSetupSearchListView();


    }

    private void onCreateSearchListAdapter() {

    }

    private void onCreateSetupSearchListView() {
        ListView listView = findViewById(R.id.searchListView);
//        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: move to SearchItem Activity
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
}
