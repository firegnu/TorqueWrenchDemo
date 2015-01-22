package com.example.firegnu.torquewrenchdemo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ImageButton preUserButton = (ImageButton) findViewById(R.id.preuserbutton);
        preUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                preUser();
            }
        });

        ImageButton nextUserButton = (ImageButton) findViewById(R.id.nextuserbutton);
        nextUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextUser();
            }
        });

        Button loginButton = (Button) findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempLogin();
            }
        });
    }

    public void preUser() {

    }

    public void nextUser() {

    }

    public void attempLogin() {
        Intent intent = new Intent(LoginActivity.this, ScanChassisActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }
}
