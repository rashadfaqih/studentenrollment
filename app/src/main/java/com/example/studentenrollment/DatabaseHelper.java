package com.example.studentenrollment;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "StudentEnrollment.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_STUDENTS = "Students";
    public static final String TABLE_SUBJECTS = "Subjects";
    public static final String TABLE_ENROLLMENT = "Enrollment";

    public static final String STUDENT_ID = "id";
    public static final String STUDENT_NAME = "name";
    public static final String STUDENT_EMAIL = "email";
    public static final String STUDENT_PASSWORD = "password";

    public static final String SUBJECT_ID = "id";
    public static final String SUBJECT_NAME = "name";
    public static final String SUBJECT_CREDITS = "credits";

    public static final String ENROLLMENT_ID = "id";
    public static final String ENROLLMENT_STUDENT_ID = "student_id";
    public static final String ENROLLMENT_SUBJECT_ID = "subject_id";
    public static final String ENROLLMENT_TOTAL_CREDITS = "total_credits";

    private static final String CREATE_STUDENT_TABLE =
            "CREATE TABLE " + TABLE_STUDENTS + " (" +
                    STUDENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    STUDENT_NAME + " TEXT, " +
                    STUDENT_EMAIL + " TEXT, " +
                    STUDENT_PASSWORD + " TEXT)";

    private static final String CREATE_SUBJECT_TABLE =
            "CREATE TABLE " + TABLE_SUBJECTS + " (" +
                    SUBJECT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SUBJECT_NAME + " TEXT, " +
                    SUBJECT_CREDITS + " INTEGER)";

    private static final String CREATE_ENROLLMENT_TABLE =
            "CREATE TABLE " + TABLE_ENROLLMENT + " (" +
                    ENROLLMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ENROLLMENT_STUDENT_ID + " INTEGER, " +
                    ENROLLMENT_SUBJECT_ID + " INTEGER, " +
                    ENROLLMENT_TOTAL_CREDITS + " INTEGER, " +
                    "FOREIGN KEY(" + ENROLLMENT_STUDENT_ID + ") REFERENCES " + TABLE_STUDENTS + "(" + STUDENT_ID + "), " +
                    "FOREIGN KEY(" + ENROLLMENT_SUBJECT_ID + ") REFERENCES " + TABLE_SUBJECTS + "(" + SUBJECT_ID + "))";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_SUBJECT_TABLE);
        db.execSQL(CREATE_ENROLLMENT_TABLE);
        insertDefaultSubjects(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STUDENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENROLLMENT);
        onCreate(db);
    }

    private void insertDefaultSubjects(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        String[] subjects = {
                "Mathematics", "Physics", "Chemistry", "Computer Science",
                "Biology", "English Literature", "History", "Philosophy",
                "Economics", "Statistics", "Programming", "Artificial Intelligence"
        };
        int[] credits = {3, 4, 3, 5, 4, 2, 3, 2, 3, 4, 5, 5};

        for (int i = 0; i < subjects.length; i++) {
            values.put(SUBJECT_NAME, subjects[i]);
            values.put(SUBJECT_CREDITS, credits[i]);
            db.insert(TABLE_SUBJECTS, null, values);
        }
    }
}