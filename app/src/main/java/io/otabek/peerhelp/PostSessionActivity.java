package io.otabek.peerhelp;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.auth.FirebaseAuth;
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
    FirebaseAuth auth = FirebaseAuth.getInstance();
    int selectedMinutes;
    private String name, details, link;
    Date date = new Date();

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


        Intent getIntent = getIntent();
        if (getIntent.getExtras() != null) {
            name = getIntent.getExtras().getString("name");
            details = getIntent.getExtras().getString("details");
            link = getIntent.getExtras().getString("link");
            String dtStart = getIntent.getExtras().getString("timestamp");
            Log.d(TAG, "sss"+dtStart+"hjh"+name);
            nameEditText.setText(name);
            detailsEditText.setText(details);
            linkEditText.setText(link);
            dateEditText.setText(date.toString());


        }


        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isInputValid();
                sendData(name, details, link, myCalendar.getTime());
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

    private void sendData(final String name, final String details, final String link, Date date) {
        if (isInputValid() && isTimeCorrect) {
            myCalendar.set(Calendar.HOUR, selectedHours);
            Log.d(TAG, "onClick: " + selectedMinutes);
            myCalendar.set(Calendar.MINUTE, selectedMinutes);
            sessionMap.put("name", name);
            sessionMap.put("details", details);
            sessionMap.put("link", link);
            sessionMap.put("timestamp", date);
            sessionMap.put("author", auth.getCurrentUser().getUid());
            db.collection("sessions").add(sessionMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    Log.d(TAG, "onComplete: done");
                    ViewDialog alert = new ViewDialog();
                    Session session = new Session(name, details, link, myCalendar.getTime());
                    alert.showDialog(PostSessionActivity.this, session);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Log.d(TAG, "onFailure: " + e.getMessage().toString());

                }
            });


        } else {
            Toast.makeText(PostSessionActivity.this, "Check your Input", Toast.LENGTH_SHORT).show();
        }
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

    public class ViewDialog {

        public void showDialog(Activity activity, final Session session) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.posted_dialog);


            Button shareBtn = dialog.findViewById(R.id.shareBtn);
            Button cancelBtn = dialog.findViewById(R.id.cancelBtn);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nameEditText.setText("");
                    detailsEditText.setText("");
                    linkEditText.setText("");
                    dateEditText.setText("");
                    timeEditText.setText("");
                    finish();
                }
            });

            shareBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Create an ACTION_SEND Intent*/
                    Intent intent = new Intent(android.content.Intent.ACTION_SEND);

                    String shareSubject = "Hey, Join this session with me";
                    String shareBody = session.name + "\n" + session.details + "\n" + session.timestamp + "\n" + session.link;

                    intent.setType("text/plain");
                    /*Applying information Subject and Body.*/
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    /*Fire!*/
                    startActivity(Intent.createChooser(intent, "Share the session"));
                }
            });

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();

        }
    }
}




