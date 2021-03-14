package io.otabek.peerhelp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MySessionsActivity extends AppCompatActivity {

    Dialog dialog;
    private String removedId;
    private static final String TAG = "hackabull";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    ArrayList<Session> mySessionList = new ArrayList<>();
    Session sessionToEdit;
    Boolean shouldEdit = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sessions);


        final SessionAdapter sessionAdapter = new SessionAdapter(mySessionList, new SessionAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Session item) {

                ViewDialog alert = new ViewDialog();
                alert.showDialog(MySessionsActivity.this, item);


            }
        });

        RecyclerView mySessionsRecycler = findViewById(R.id.mySessionsRecylcer);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mySessionsRecycler.setLayoutManager(layoutManager);
        mySessionsRecycler.setAdapter(sessionAdapter);

        db.collection("sessions").whereEqualTo("author", auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentChange documentChange : task.getResult().getDocumentChanges()) {
                        Session session = documentChange.getDocument().toObject(Session.class);
                        mySessionList.add(session);
                        sessionAdapter.notifyDataSetChanged();
                        showToast();

                    } 
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MySessionsActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void showToast() {
        if (mySessionList.isEmpty()) {
            Toast.makeText(this, "You do not have any sessions planned.", Toast.LENGTH_SHORT).show();
        }
    }


    public class ViewDialog {

        public void showDialog(Activity activity, final Session session) {
            dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.my_session_dialog);


            Button shareBtn = dialog.findViewById(R.id.shareBtn);
            Button cancelBtn = dialog.findViewById(R.id.cancelSessionBtn);
            final Button editBtn = dialog.findViewById(R.id.editBtn);

            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData(session);
                }
            });

            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData(session);
                    shouldEdit =true;
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
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSubject);
                    intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(intent, "Share the session"));
                }
            });

            dialog.setCanceledOnTouchOutside(true);
            dialog.show();

        }
    }


    public void remove() {
        if (removedId != null) {
            db.collection("sessions").document(removedId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(MySessionsActivity.this, "The session was deleted.", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MySessionsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void loadData(Session session) {
        db.collection("sessions").whereEqualTo("timestamp", session.timestamp).whereEqualTo("author", auth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult().getDocuments()) {
                                sessionToEdit = document.toObject(Session.class);
                                removedId = document.getId();
                                remove();
                                if (shouldEdit){
                                    editSession(sessionToEdit);
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MySessionsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void editSession(Session session){
        Intent postIntent = new Intent(MySessionsActivity.this,PostSessionActivity.class);
        postIntent.putExtra("name",session.name);
        postIntent.putExtra("details",session.details);
        postIntent.putExtra("link",session.link);
        postIntent.putExtra("timestamp",session.timestamp);

        startActivity(postIntent);
        remove();
        finish();


    }
}