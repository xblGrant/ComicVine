package edu.psu.gsa5054.comicvine;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;

public class FavoriteDB extends SQLiteOpenHelper {

    interface onDBReadyListener {
        void onDBReady(SQLiteDatabase faveDB);
    }

    private static final int DATABASE_VERSION = 6;
    private static final String DATABASE_NAME = "fave.db";


    private static final String SQL_CREATE_FAVORITE =
            "CREATE TABLE FAVORITE ( " +
                    "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "CharacterID TEXT, " +
                    "UID TEXT ";



    private static final String SQL_DELETE_FAVORITE =
            "DROP TABLE IF EXISTS FAVORITE";

    private static FavoriteDB faveDb;
    private Context appContext;

    private FavoriteDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        appContext = context.getApplicationContext();
    }

    public static synchronized FavoriteDB getInstance(Context context) {
        if (faveDb == null){
            faveDb = new FavoriteDB(context.getApplicationContext());
        }
        return faveDb;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_FAVORITE);

        // No information to populate tables
        // TODO: Not fully connected to ComicVine API yet

        db.beginTransaction();
        ContentValues values = new ContentValues();
//        ...
        //values.put("UID", "test"/*string value*/);
//        ...
        //db.insert(/*table name*/"user", null, values);
//        ...
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_FAVORITE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void asyncWritableDatabase(onDBReadyListener listener){
        new OpenDbAsyncTask().execute(listener);
    }

    private static class OpenDbAsyncTask extends AsyncTask<onDBReadyListener, Void, SQLiteDatabase> {
        onDBReadyListener listener;

        @Override
        protected SQLiteDatabase doInBackground(onDBReadyListener... onDBReadyListeners) {
            listener = onDBReadyListeners[0];
            return FavoriteDB.faveDb.getWritableDatabase();
        }

        @Override
        protected void onPostExecute(SQLiteDatabase db) {
            listener.onDBReady(db);
        }
    }

}
