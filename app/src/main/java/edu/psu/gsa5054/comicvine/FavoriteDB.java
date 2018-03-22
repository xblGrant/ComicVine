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

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "fave.db";

    private static final String SQL_CREATE_PUBLISHER =
            "CREATE TABLE publisher ( " +
                    "_id INTEGER PRIMARY KEY, " +
                    "name TEXT)";
    private static final String SQL_CREATE_CHARACTER =
            "CREATE TABLE character ( " +
                    "_id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "publisher INTEGER, " +
                    "FOREIGN KEY (publisher) REFERENCES publisher(id))";
    private static final String SQL_CREATE_EPISODE =
            "CREATE TABLE episode ( " +
                    "_id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "series TEXT, " +
                    "episode_number TEXT)";
    private static final String SQL_CREATE_ISSUE =
            "CREATE TABLE issue ( " +
                    "_id INTEGER PRIMARY KEY, " +
                    "name TEXT)";
    private static final String SQL_CREATE_MOVIE =
            "CREATE TABLE movie ( " +
                    "_id INTEGER PRIMARY KEY, " +
                    "name TEXT)";
    private static final String SQL_CREATE_SERIES =
            "CREATE TABLE series ( " +
                    "_id INTEGER PRIMARY KEY, " +
                    "name TEXT)";
    private static final String SQL_CREATE_TEAM =
            "CREATE TABLE team ( " +
                    "_id INTEGER PRIMARY KEY, " +
                    "name TEXT)";
    private static final String SQL_CREATE_VOLUME =
            "CREATE TABLE team ( " +
                    "_id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "publisher INTEGER, " +
                    "FOREIGN KEY (publisher) REFERENCES publisher(id))";

    private static final String SQL_DELETE_PUBLISHER =
            "DROP TABLE IF EXISTS publisher";
    private static final String SQL_DELETE_CHARACTER =
            "DROP TABLE IF EXISTS character";
    private static final String SQL_DELETE_EPISODE =
            "DROP TABLE IF EXISTS episode";
    private static final String SQL_DELETE_ISSUE =
            "DROP TABLE IF EXISTS issue";
    private static final String SQL_DELETE_MOVIE =
            "DROP TABLE IF EXISTS movie";
    private static final String SQL_DELETE_SERIES =
            "DROP TABLE IF EXISTS series";
    private static final String SQL_DELETE_TEAM =
            "DROP TABLE IF EXISTS team";
    private static final String SQL_DELETE_VOLUME =
            "DROP TABLE IF EXISTS volume";

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
        db.execSQL(SQL_CREATE_PUBLISHER);
        // db.execSQL(SQL_CREATE_CHARACTER);
        // db.execSQL(SQL_CREATE_EPISODE);
        // db.execSQL(SQL_CREATE_ISSUE);
        // db.execSQL(SQL_CREATE_MOVIE);
        // db.execSQL(SQL_CREATE_SERIES);
        // db.execSQL(SQL_CREATE_TEAM);
        // db.execSQL(SQL_CREATE_VOLUME);

        // No information to populate tables
        // TODO: Not fully connected to ComicVine API yet

        db.beginTransaction();
        ContentValues values = new ContentValues();
//        ...
        values.put("_id", 1/*integer value*/);
        values.put("name", "test"/*string value*/);
//        ...
        db.insert(/*table name*/"publisher", null, values);
//        ...
        db.setTransactionSuccessful();
        db.endTransaction();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_PUBLISHER);
        // db.execSQL(SQL_DELETE_CHARACTER);
//        db.execSQL(SQL_DELETE_EPISODE);
//        db.execSQL(SQL_DELETE_ISSUE);
//        db.execSQL(SQL_DELETE_MOVIE);
//        db.execSQL(SQL_DELETE_SERIES);
//        db.execSQL(SQL_DELETE_TEAM);
//        db.execSQL(SQL_DELETE_VOLUME);
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
