package edu.psu.gsa5054.comicvine;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class MainActivity extends AppCompatActivity {

    private long rowid;
    private SQLiteDatabase db;
    private SimpleCursorAdapter adapter;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        Button loginButton = (Button) findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = ((TextView) findViewById(R.id.emailEntry)).getText().toString();
                String password = ((TextView) findViewById(R.id.passwordEntry)).getText().toString();

                if(email!= null && !email.equals("") && password!= null && !password.equals("")){
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task){
                                        if(task.isSuccessful()){
                                            String UID = mAuth.getUid();
                                            //TODO add UID into local database.
                                            startActivity(new Intent(MainActivity.this, SearchScreen.class));
                                        }
                                        else{
                                            Toast.makeText(MainActivity.this, "Failed to create user account", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }else {
                    Toast.makeText(MainActivity.this, "Email field must not be blank", Toast.LENGTH_LONG).show();
                }
            }
        });

        Button signUpButton = (Button) findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
                Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
            }
        });

        FavoriteDB.getInstance(this).asyncWritableDatabase(new FavoriteDB.onDBReadyListener() {
            @Override
            public void onDBReady(SQLiteDatabase faveDB) {
                db = faveDB;
                dbAsyncLoadCursor(false);
                onCreateMainTextAdapter();
            }
        });



        if (!isNetworkConnected()) {
            new AlertDialog.Builder(this)
                    .setTitle("No Internet Connection")
                    .setMessage("Please connect to the internet and try again")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert).show();
        }
    }

    //END onCREATE ***************************************************************************

    //initialize the adapter that is bound to the TextView
    private void onCreateMainTextAdapter() {
        //initially set to null. it is not ready
        adapter = new SimpleCursorAdapter(this, R.layout.activity_main, null,
                new String[]{"UID"},
                new int[]{R.id.databaseTestTextView}, 0);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                TextView mainActivityTextView = (TextView) findViewById(R.id.databaseTestTextView);
                //should set the textView to "test"
                String test = cursor.getString(cursor.getColumnIndex("UID"));
                Log.i("testing Database", test);
                Log.i("testing null", "random string");
                mainActivityTextView.setText(cursor.getString(cursor.getColumnIndex("UID")));
                cursor.close();
                //possibly close db db.close()???
                return true;
            }
        });
    }

    //don't know if this is nessecary for just doing db.query and setting
    //a cursor to the result.
    @SuppressLint("StaticFieldLeak")
    private void dbAsyncLoadCursor(boolean scrollToEnd) {

        new AsyncTask<Boolean, Void, Cursor>() {
            boolean scrollToEnd;

            @Override
            protected Cursor doInBackground(Boolean... params) {
                scrollToEnd = params[0];
                String where = null;
                String[] projection = {"_id", "UID"};
                return db.query("USER", projection, where, null, null, null, null);
            }

            @Override
            protected void onPostExecute(Cursor cursor) {
                adapter.swapCursor(cursor);
            }
        }.execute(scrollToEnd);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
