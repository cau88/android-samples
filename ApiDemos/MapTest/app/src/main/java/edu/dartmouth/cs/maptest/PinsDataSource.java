package edu.dartmouth.cs.maptest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.util.Log;

//import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

public class PinsDataSource {

    // Database fields
    private MySQLiteHelper dbHelper;
    private String[] allColumns = { MySQLiteHelper.COLUMN_ID,
            MySQLiteHelper.COLUMN_PIN };

    private static final String TAG = "PINDB";

    PinsDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public Comment createPin(String pin) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_PIN, pin);
        long insertId = database.insert(MySQLiteHelper.TABLE_PINS, null,
                values);
        Cursor cursor = database.query(MySQLiteHelper.TABLE_PINS,
                allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Comment newComment = cursorToComment(cursor);

        // Log the comment stored
        Log.d(TAG, "comment = " + cursorToComment(cursor).toString()
                + " insert ID = " + insertId);

        cursor.close();
        database.close();
        dbHelper.close();
        return newComment;
    }

    public void deleteComment(Comment comment) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long id = comment.getId();
        Log.d(TAG, "delete comment = " + id);
        database.delete(MySQLiteHelper.TABLE_PINS, MySQLiteHelper.COLUMN_ID
                + " = " + id, null);
        database.close();
        dbHelper.close();
    }

    public void deleteAllComments() {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        System.out.println("Comment deleted all");
        Log.d(TAG, "delete all = ");
        database.delete(MySQLiteHelper.TABLE_PINS, null, null);
        database.close();
        dbHelper.close();
    }

    public List<Comment> getAllPins() {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        List<Comment> comments = new ArrayList<>();

        Cursor cursor = database.query(MySQLiteHelper.TABLE_PINS,
                allColumns, null, null, null, null, null);

        while (cursor.moveToNext()) {
            Comment comment = cursorToComment(cursor);
            Log.d(TAG, "get comment = " + cursorToComment(cursor).toString());
            comments.add(comment);
        }
        // Make sure to close the cursor
        cursor.close();
        database.close();
        dbHelper.close();
        return comments;
    }

    private Comment cursorToComment(Cursor cursor) {
        Comment comment = new Comment();
        comment.setId(cursor.getLong(0));
        comment.setComment(cursor.getString(1));
        return comment;
    }

}
