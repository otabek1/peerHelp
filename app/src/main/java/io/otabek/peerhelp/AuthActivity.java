package io.otabek.peerhelp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AuthActivity extends AppCompatActivity {

    private String password, email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        final EditText emailEdit = findViewById(R.id.emailEditText);
        final EditText passwordEdit = findViewById(R.id.passwordEditText);
        Button signIn = findViewById(R.id.loginBtn);
        Button signUp = findViewById(R.id.registerBtn);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailEdit.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailEdit.getText().toString()).matches()){
                    emailEdit.requestFocus();
                    emailEdit.setError("Enter a valid email");
                }

                if (passwordEdit.getText().toString().isEmpty()){
                    passwordEdit.setError("Enter a password");
                    passwordEdit.requestFocus();
                }
            }
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
//            Intent
        } else {

            // No user is signed in
        }

    }
}