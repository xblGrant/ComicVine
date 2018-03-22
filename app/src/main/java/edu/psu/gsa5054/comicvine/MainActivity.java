package edu.psu.gsa5054.comicvine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private long rowid;
    private SQLiteDatabase db;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button skipLoginButton = (Button) findViewById(R.id.skipSigninButton);
        skipLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SearchScreen.class));
                Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
            }
        });

        onCreateMainTextAdapter();

        FavoriteDB.getInstance(this).asyncWritableDatabase(new FavoriteDB.onDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase faveDB) {
                db = faveDB;
              dbAsyncLoadCursor(false);
            }
        });


    }

    //initialize the adapter that is bound to the TextView
    private void onCreateMainTextAdapter(){
        //initially set to null. it is not ready
        adapter = new SimpleCursorAdapter(this, R.layout.activity_main, null,
                new String[]{"name"},
                new int[]{R.id.databaseTestTextView}, 0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {
                TextView mainActivityTextView = (TextView) findViewById(R.id.databaseTestTextView);
                mainActivityTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                return true;
            }
        });
    }

    //don't know if this is nessecary for just doing db.query and setting
    //a cursor to the result.
    @SuppressLint("StaticFieldLeak")
    private void dbAsyncLoadCursor(boolean scrollToEnd){

        new AsyncTask<Boolean, Void, Cursor>(){
            boolean scrollToEnd;
            @Override
            protected Cursor doInBackground(Boolean... params){
                scrollToEnd = params[0];
                String where = null;
                String[] projection = {"name"};
                return db.query("publisher", projection, where, null, null, null, null);

            }

            @Override
            protected void onPostExecute(Cursor cursor){
                adapter.swapCursor(cursor);
            }
        }.execute(scrollToEnd);
    }



}
