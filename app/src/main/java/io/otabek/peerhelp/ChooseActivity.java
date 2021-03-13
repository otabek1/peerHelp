package io.otabek.peerhelp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ChooseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        Button postBtn = findViewById(R.id.postSessionBtn);
        Button exploreBtn = findViewById(R.id.explore);


    }

    public void post(View v) {
        startActivity(new Intent(ChooseActivity.this, PostSessionActivity.class));
        finish();
    }

    public void explore(View v) {
        startActivity(new Intent(ChooseActivity.this, ExploreSessions.class));
        finish();
    }
}