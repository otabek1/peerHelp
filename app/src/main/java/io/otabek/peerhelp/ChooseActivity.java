package io.otabek.peerhelp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);


    }

    public void post(View v) {
        startActivity(new Intent(ChooseActivity.this, PostSessionActivity.class));

    }

    public void explore(View v) {
        startActivity(new Intent(ChooseActivity.this, ExploreSessions.class));

    }

    public void mySession(View v){
        startActivity(new Intent(ChooseActivity.this,MySessionsActivity.class));
    }
}