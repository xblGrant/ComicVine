package edu.psu.gsa5054.comicvine;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button skipLoginButton = (Button) findViewById(R.id.skipSigninButton);
        skipLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this, SearchScreen.class));
            }
        });
    }
}
