package io.otabek.peerhelp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostSessionActivity extends AppCompatActivity {

    private static final String TAG = "hackabull";
    public int selectedHours;
    EditText dateEditText;
    EditText timeEditText;
    EditText nameEditText;
    EditText detailsEditText;
    EditText linkEditText;
    Calendar myCalendar;
    int hour;
    int minute;
    Button postBtn;
    Map<String, Object> sessionMap = new HashMap<>();
    Boolean isTimeCorrect = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    int selectedMinutes;
    private String name, details, link;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_session);

        dateEditText = findViewById(R.id.sessionDateEditText);
        timeEditText = findViewById(R.id.sessionTimeEditText);
        nameEditText = findViewById(R.id.sessionNameEditText);
        detailsEditText = findViewById(R.id.sessionDetailsEditText);
        linkEditText = findViewById(R.id.sessionLinkEditText);
        postBtn = findViewById(R.id.postSessionBtn);

        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInputValid()) {
                    myCalendar.set(Calendar.HOUR, selectedHours);
                    Log.d(TAG, "onClick: " + selectedMinutes);
                    myCalendar.set(Calendar.MINUTE, selectedMinutes);
                    sessionMap.put("name", name);
                    sessionMap.put("details", details);
                    sessionMap.put("timestamp", myCalendar.getTime());
//                    Log.d(TAG, "onClick: " + myCalendar.getTime());
                    db.collection("sessions").add(sessionMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            Log.d(TAG, "onComplete: done");

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG, "onFailure: " + e.getMessage().toString());

                        }
                    });


                }
            }
        });


//    Calender editText set up
        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                view.setMaxDate(System.currentTimeMillis() - 1000);
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                updateLabel();
                Log.d(TAG, "onDateSet: " + dayOfMonth);
            }

        };


        dateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog da = new DatePickerDialog(PostSessionActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                Date newDate = myCalendar.getTime();

                da.getDatePicker().setMinDate(newDate.getTime() - (newDate.getTime() % (24 * 60 * 60 * 1000)));
                da.show();
            }
        });


        timeEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                minute = mcurrentTime.get(Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(PostSessionActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        if (selectedHour >= myCalendar.get(Calendar.HOUR_OF_DAY) && selectedMinute >= myCalendar.get(Calendar.MINUTE)) {
                            isTimeCorrect = true;
                            timeEditText.setText(selectedHour + ":" + selectedMinute);
                            selectedHours = selectedHour;
                            selectedMinutes = selectedMinute;
                        } else {
                            isTimeCorrect = false;
                            Toast.makeText(PostSessionActivity.this, "Cannot Select Past Time", Toast.LENGTH_SHORT).show();
                        }
//                        Log.d(TAG, "onTimeSet: " + selectedHour);
                    }
                }, hour, minute, false);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


    }


    //    Calendar EditText update
    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dateEditText.setText(sdf.format(myCalendar.getTime()));
    }

    private boolean isInputValid() {
        name = nameEditText.getText().toString();
        details = detailsEditText.getText().toString();
        link = linkEditText.getText().toString();

        if (name.isEmpty() || name.length() < 5) {
            nameEditText.requestFocus();
            nameEditText.setError("Name should be at least 5 characters");
            return false;
        }
        if (details.isEmpty() || details.length() < 5) {
            detailsEditText.requestFocus();
            detailsEditText.setError("Details should be at least 10 characters");
            return false;
        }
        if (!Patterns.WEB_URL.matcher(link).matches()) {
            linkEditText.requestFocus();
            linkEditText.setError("Link is invalid");
            return false;
        }


        return true;
    }
}