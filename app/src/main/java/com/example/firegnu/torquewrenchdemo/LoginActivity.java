package com.example.firegnu.torquewrenchdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
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

        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight() - 2 * getActionBarHeight();
        int halfSizeHeightDp = px2dip(this, height);
        ImageView view = (ImageView)findViewById(R.id.userlogo);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(halfSizeHeightDp/4,halfSizeHeightDp/4);
        parms.setMargins(0, 0,
                0, 0);
        parms.gravity = Gravity.CENTER;
        view.setLayoutParams(parms);

        /*LinearLayout copyRightLabel = (LinearLayout)findViewById(R.id.copyrightLayout);
        LinearLayout.LayoutParams parmsCopyRight = (LinearLayout.LayoutParams)copyRightLabel.getLayoutParams();//new LinearLayout.LayoutParams(display.getWidth(), getActionBarHeight());
        copyRightLabel.height =*/

    }

    public static int px2dip(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    private int getActionBarHeight() {
        int actionBarHeight = getActionBar().getHeight();
        if (actionBarHeight != 0)
            return actionBarHeight;
        final TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
                actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        return actionBarHeight;
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
