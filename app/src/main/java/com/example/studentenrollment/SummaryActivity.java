package com.example.studentenrollment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {

    private TextView tvStudentInfo, tvTotalCredits;
    private ListView lvEnrolledSubjects;
    private Button btnLogout;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        tvStudentInfo = findViewById(R.id.tvStudentInfo);
        tvTotalCredits = findViewById(R.id.tvTotalCredits);
        lvEnrolledSubjects = findViewById(R.id.lvEnrolledSubjects);
        btnLogout = findViewById(R.id.btnLogout);
        db = new DatabaseHelper(this);

        int studentId = getIntent().getIntExtra("studentId", -1);

        loadStudentInfo(studentId);
        loadEnrolledSubjects(studentId);

        btnLogout.setOnClickListener(v -> {
            SharedPreferences preferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            Intent intent = new Intent(SummaryActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadStudentInfo(int studentId) {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT name, email FROM Students WHERE id = ?", new String[]{String.valueOf(studentId)});

        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String email = cursor.getString(1);
            tvStudentInfo.setText("Name: " + name + "\nEmail: " + email);
        } else {
            Toast.makeText(this, "Student not found!", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
    }

    private void loadEnrolledSubjects(int studentId) {
        SQLiteDatabase database = db.getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT s.name, s.credits FROM Enrollment e " +
                        "INNER JOIN Subjects s ON e.subject_id = s.id " +
                        "WHERE e.student_id = ?",
                new String[]{String.valueOf(studentId)}
        );

        ArrayList<String> subjects = new ArrayList<>();
        int totalCredits = 0;

        while (cursor.moveToNext()) {
            String subjectName = cursor.getString(0);
            int credits = cursor.getInt(1);
            subjects.add(subjectName + " (" + credits + " credits)");
            totalCredits += credits;
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, subjects);
        lvEnrolledSubjects.setAdapter(adapter);

        tvTotalCredits.setText("Total Credits: " + totalCredits);
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Cannot go back from summary screen.", Toast.LENGTH_SHORT).show();
    }
}