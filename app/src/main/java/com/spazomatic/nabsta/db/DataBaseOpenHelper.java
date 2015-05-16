package com.spazomatic.nabsta.db;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by samuelsegal on 5/13/15.
 */
public class DataBaseOpenHelper extends SQLiteOpenHelper {
    private static final String LOG_TAG = DataBaseOpenHelper.class.getSimpleName();

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "Nabsta.db";
    private static File DATABASE_FILE;

    // This is an indicator if we need to copy the
    // database file.
    private boolean mInvalidDatabaseFile = false;
    private boolean mIsUpgraded = false;
    private Context mContext;

    /**
     * number of users of the database connection.
     * */
    private int mOpenConnections = 0;

    private static DataBaseOpenHelper mInstance;

    synchronized static public DataBaseOpenHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataBaseOpenHelper(context.getApplicationContext());
        }
        return mInstance;
    }

    private DataBaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.mContext = context;

        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            if (db != null) {
                db.close();
            }

            DATABASE_FILE = context.getDatabasePath(DATABASE_NAME);

            if (mInvalidDatabaseFile) {
                copyDatabase();
            }
            if (mIsUpgraded) {
                doUpgrade();
            }
        } catch (SQLiteException e) {
            Log.e(LOG_TAG, "Error constructing DatabaseOpenHelper: " + e.getClass() + " " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mInvalidDatabaseFile = true;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          int old_version, int new_version) {
        mInvalidDatabaseFile = true;
        mIsUpgraded = true;
    }

    /**
     * called if a database upgrade is needed
     */
    private void doUpgrade() {
        // implement the database upgrade here.
    }

    @Override
    public synchronized void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // increment the number of users of the database connection.
        mOpenConnections++;
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    /**
     * implementation to avoid closing the database connection while it is in
     * use by others.
     */
    @Override
    public synchronized void close() {
        mOpenConnections--;
        if (mOpenConnections == 0) {
            super.close();
        }
    }

    private void copyDatabase() {
        AssetManager assetManager = mContext.getResources().getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(DATABASE_NAME);
            out = new FileOutputStream(DATABASE_FILE);
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG,"Error copying database: " + e.getClass() + " " + e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG,"Error copying database: " + e.getClass() + " " + e.getMessage());
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG,"Error copying database: " + e.getClass() + " " + e.getMessage());
                }
            }
        }
        setDatabaseVersion();
        mInvalidDatabaseFile = false;
    }

    private void setDatabaseVersion() {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(DATABASE_FILE.getAbsolutePath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("PRAGMA user_version = " + VERSION);
        } catch (SQLiteException e ) {
            Log.e(LOG_TAG,"Error setting database version: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

}

