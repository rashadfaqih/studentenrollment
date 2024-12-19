package com.example.studentenrollment;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class EnrollmentActivity extends AppCompatActivity {

    private TextView tvTotalCredits;
    private ListView lvSubjects;
    private Button btnEnroll, btnViewSummary;
    private DatabaseHelper db;

    private ArrayList<String> selectedSubjects = new ArrayList<>();
    private int totalCredits = 0;
    private final int MAX_CREDITS = 24;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        tvTotalCredits = findViewById(R.id.tvTotalCredits);
        lvSubjects = findViewById(R.id.lvSubjects);
        btnEnroll = findViewById(R.id.btnEnroll);
        btnViewSummary = findViewById(R.id.btnViewSummary);
        db = new DatabaseHelper(this);

        loadSubjects();

        lvSubjects.setOnItemClickListener((parent, view, position, id) -> {
            String subject = (String) parent.getItemAtPosition(position);
            int credits = getSubjectCredits(subject);

            if (selectedSubjects.contains(subject)) {
                Toast.makeText(this, "Subject already selected!", Toast.LENGTH_SHORT).show();
            } else if (totalCredits + credits > MAX_CREDITS) {
                Toast.makeText(this, "Credit limit exceeded! Maximum is 24 credits.", Toast.LENGTH_SHORT).show();
            } else {
                selectedSubjects.add(subject);
                totalCredits += credits;
                tvTotalCredits.setText("Total Credits: " + totalCredits);
            }
        });

        btnEnroll.setOnClickListener(v -> {
            int studentId = getIntent().getIntExtra("studentId", -1);

            if (isAlreadyEnrolled(studentId)) {
                Toast.makeText(this, "You have already enrolled!", Toast.LENGTH_SHORT).show();
            } else if (selectedSubjects.isEmpty()) {
                Toast.makeText(this, "Please select at least one subject!", Toast.LENGTH_SHORT).show();
            } else if (totalCredits > MAX_CREDITS) {
                Toast.makeText(this, "Credit limit exceeded!", Toast.LENGTH_SHORT).show();
            } else {
                enrollSubjects(studentId);
            }
        });

        btnViewSummary.setOnClickListener(v -> {
            Intent intent = new Intent(EnrollmentActivity.this, SummaryActivity.class);
            intent.putExtra("studentId", getIntent().getIntExtra("studentId", -1));
            startActivity(intent);
        });
    }

    private void loadSubjects() {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT name FROM Subjects", null);

        ArrayList<String> subjects = new ArrayList<>();
        while (cursor.moveToNext()) {
            subjects.add(cursor.getString(0));
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subjects);
        lvSubjects.setAdapter(adapter);
    }

    private int getSubjectCredits(String subject) {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT credits FROM Subjects WHERE name = ?", new String[]{subject});
        int credits = 0;
        if (cursor.moveToFirst()) {
            credits = cursor.getInt(0);
        }
        cursor.close();
        return credits;
    }

    private void enrollSubjects(int studentId) {
        SQLiteDatabase database = db.getWritableDatabase();

        for (String subject : selectedSubjects) {
            ContentValues values = new ContentValues();
            values.put("student_id", studentId);
            values.put("subject_id", getSubjectId(subject));
            values.put("total_credits", totalCredits);
            database.insert("Enrollment", null, values);
        }

        Toast.makeText(this, "Enrollment successful!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(EnrollmentActivity.this, SummaryActivity.class);
        intent.putExtra("studentId", studentId);
        startActivity(intent);
        finish();
    }

    private boolean isAlreadyEnrolled(int studentId) {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT COUNT(*) FROM Enrollment WHERE student_id = ?",
                new String[]{String.valueOf(studentId)}
        );

        boolean enrolled = false;
        if (cursor.moveToFirst()) {
            enrolled = cursor.getInt(0) > 0;
        }
        cursor.close();
        return enrolled;
    }

    private int getSubjectId(String subject) {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT id FROM Subjects WHERE name = ?", new String[]{subject});
        int id = 0;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}