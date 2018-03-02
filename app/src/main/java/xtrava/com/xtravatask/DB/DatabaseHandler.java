package xtrava.com.xtravatask.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import xtrava.com.xtravatask.Model.TodoModel;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Cached_service";

    // Contacts table name
    private static final String TABLE_CONTACTS = "todo_list";

    // Contacts Table Columns names
    private static final String KEY_TITLE = "title";
    private static final String KEY_COMPLETED = "completed";
    private static final String KEY_ID = "id";
    private static final String KEY_URL = "url";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_CONTACTS + "("
                + KEY_TITLE + " TEXT,"
                + KEY_COMPLETED + " TEXT,"
                + KEY_ID + " TEXT,"
                + KEY_URL + " TEXT" + ");";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // If you need to add a column
        if (newVersion > oldVersion) {
            db.execSQL("ALTER TABLE foo ADD COLUMN new_column INTEGER DEFAULT 0");
        }
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */
    // Adding new contact
    public void addContact(TodoModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(KEY_TITLE, model.getTitle()); // Title
        values.put(KEY_COMPLETED, model.getCompleted()); // Completed
        values.put(KEY_ID, model.getId()); // ID
        values.put(KEY_URL, model.getUrl()); // URL

        // Inserting Row
        db.insert(TABLE_CONTACTS, null, values);
        Log.i("","inserted successfully");
        db.close(); // Closing database connection
    }

    // Getting All Contacts
    public List<TodoModel> getAllMissions() {
        List<TodoModel> missionList = new ArrayList<TodoModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                TodoModel contact = new TodoModel();
                contact.setTitle(cursor.getString(0));
                contact.setCompleted(cursor.getString(1));
                contact.setId(cursor.getString(2));
                contact.setUrl(cursor.getString(3));
                // Adding contact to list
                missionList.add(contact);
            } while (cursor.moveToNext());
        }
        db.close();
        // return contact list
        return missionList;
    }

    // Deleting single contact
    public void deleteContact(TodoModel contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CONTACTS, KEY_ID + " = ?", new String[]{String.valueOf(contact.getId())});
        db.close();
    }


}
