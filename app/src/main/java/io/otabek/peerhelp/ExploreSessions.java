package io.otabek.peerhelp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ExploreSessions extends AppCompatActivity {

    private static final String TAG = "hackabull";
    ArrayList<Session> sessionsList = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_sessions);

        final SessionAdapter sessionAdapter = new SessionAdapter(sessionsList, new SessionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Session item) {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(ExploreSessions.this, item);

            }
        });

        RecyclerView sessionsRecycler = findViewById(R.id.sessionsRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        sessionsRecycler.setLayoutManager(layoutManager);
        sessionsRecycler.setAdapter(sessionAdapter);


        db.collection("sessions").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        Session session = document.toObject(Session.class);
                        sessionsList.add(session);
                        sessionAdapter.notifyDataSetChanged();

                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public class ViewDialog {

        public void showDialog(Activity activity, final Session session) {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.custom_dialog);

            Button joinMeetingBtn = dialog.findViewById(R.id.joinMeetingBtn);
            Button addCalenderBtn = dialog.findViewById(R.id.addCalendarBtn);
            Button shareBtn = dialog.findViewById(R.id.shareBtn);

            joinMeetingBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String url = session.link;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);

                }
            });

            addCalenderBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    intent.putExtra(CalendarContract.Events.TITLE, session.name);
                    intent.putExtra(CalendarContract.Events.DTSTART, session.timestamp.getTime());

                    intent.putExtra(CalendarContract.Events.DESCRIPTION, session.details);
                    startActivity(intent);
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