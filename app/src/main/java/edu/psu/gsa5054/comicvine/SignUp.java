package edu.psu.gsa5054.comicvine;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Button signUpButton = (Button) findViewById(R.id.confirmSignUpButton);

        mAuth = FirebaseAuth.getInstance();


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = ((TextView) findViewById(R.id.email)).getText().toString();
                String password1 = ((TextView) findViewById(R.id.password1)).getText().toString();
                String password2 = ((TextView) findViewById(R.id.password2)).getText().toString();

                if(email!= null && !email.equals("")){
                    if(password1.equals(password2)){

                        mAuth.createUserWithEmailAndPassword(email, password1)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task){
                                        if(task.isSuccessful()){
                                            String UID = mAuth.getUid();
                                            //TODO add UID into local database.
                                            startActivity(new Intent(SignUp.this, MainActivity.class));
                                        }
                                        else{
                                            Toast.makeText(SignUp.this, "Failed to create user account", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }
                    else{
                        Toast.makeText(SignUp.this, "Passwords must match", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(SignUp.this, "Email field must not be blank", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
