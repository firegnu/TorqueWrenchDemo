package com.example.firegnu.torquewrenchdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


public class ScanChassisActivity extends Activity {
    MyDatabaseAdapter m_MyDatabaseAdapter;
    LinearLayout firstStepLayout;
    LinearLayout sencondStepLayout;
    LinearLayout thirdStepLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_chassis);
        m_MyDatabaseAdapter =MyDatabaseAdapter.getDatabaseAdapter(this);
        final ActionBar actionBar = getActionBar();
        setHasEmbeddedTabs(actionBar,false);
        setTitle("扭矩扳手工作正常");
        getActionBar().setIcon(R.drawable.userlogo);
        firstStepLayout = (LinearLayout)this.findViewById(R.id.firststeplayout);
        sencondStepLayout = (LinearLayout)this.findViewById(R.id.sencondsteplayout);
        thirdStepLayout = (LinearLayout)this.findViewById(R.id.thirdsteplayout);

        final Button firstStepButton = (Button) findViewById(R.id.firststepbutton);
        final Button sencondStepButton = (Button) findViewById(R.id.sencondstepbutton);
        final Button thirdStepButton = (Button) findViewById(R.id.thirdstepbutton);
        firstStepButton.setTextColor(getResources().getColor(R.color.cardview_initial_background));
        firstStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstStepLayout.setVisibility(View.VISIBLE);
                sencondStepLayout.setVisibility(View.GONE);
                thirdStepLayout.setVisibility(View.GONE);
                firstStepButton.setTextColor(getResources().getColor(R.color.cardview_initial_background));
                sencondStepButton.setTextColor(getResources().getColor(R.color.pureblack));
                thirdStepButton.setTextColor(getResources().getColor(R.color.pureblack));
            }
        });

        sencondStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sencondStepLayout.setVisibility(View.VISIBLE);
                firstStepLayout.setVisibility(View.GONE);
                thirdStepLayout.setVisibility(View.GONE);
                sencondStepButton.setTextColor(getResources().getColor(R.color.cardview_initial_background));
                firstStepButton.setTextColor(getResources().getColor(R.color.pureblack));
                thirdStepButton.setTextColor(getResources().getColor(R.color.pureblack));
            }
        });


        thirdStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                thirdStepLayout.setVisibility(View.VISIBLE);
                firstStepLayout.setVisibility(View.GONE);
                sencondStepLayout.setVisibility(View.GONE);
                thirdStepButton.setTextColor(getResources().getColor(R.color.cardview_initial_background));
                sencondStepButton.setTextColor(getResources().getColor(R.color.pureblack));
                firstStepButton.setTextColor(getResources().getColor(R.color.pureblack));
            }
        });

        ImageButton firstScanButton = (ImageButton) findViewById(R.id.firstscanbutton);
        firstScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE, PRODUCT_MODE");
                intent.putExtra("PROMPT_MESSAGE", "开始扫描底盘Vin码");
                startActivityForResult(intent, 0);
            }
        });

        ImageButton sencondScanButton = (ImageButton) findViewById(R.id.sencondscanbutton);
        sencondScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                intent.putExtra("SCAN_MODE", "QR_CODE_MODE, PRODUCT_MODE");
                intent.putExtra("PROMPT_MESSAGE", "开始扫描零件号");
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(requestCode == 0) {
            if(resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                if(firstStepLayout.getVisibility() == View.VISIBLE) {
                    EditText firstResult = (EditText)findViewById(R.id.firstscanresulttextview);
                    firstResult.setText(contents.toString());
                    ///
                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout firstStepLiearlayout = (LinearLayout)firstStepLayout.findViewById(R.id.firstchildlayout);
                    firstStepLiearlayout.removeAllViews();
                    //
                    String carMode = m_MyDatabaseAdapter.getInfoFromVinCode(contents.toString());//
                    if(!carMode.equals("")) {
                        EditText firstResultSuccessful = (EditText)findViewById(R.id.firstresultsuccesful);
                        firstResultSuccessful.setText("查询成功");
                        //carmodel
                        final LinearLayout lingjianModelLayout = (LinearLayout) layoutInflater.inflate(R.layout.firststepcarmodelname, null);
                        LinearLayout.LayoutParams lpCarModel = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        lpCarModel.setMargins(0, 0,
                                0, 10);
                        lingjianModelLayout.setLayoutParams(lpCarModel);
                        EditText firstCarModelResult = (EditText)lingjianModelLayout.findViewById(R.id.vincodecarmodelfirstedit);
                        firstCarModelResult.setText(carMode);
                        firstStepLiearlayout.addView(lingjianModelLayout);
                        //
                        List<String> partNoList = new ArrayList<String>();
                        Cursor cur = m_MyDatabaseAdapter.getPartListfromCarModel(carMode);
                        if (cur != null) {
                            if (cur.moveToFirst()) {
                                do {
                                    String partNo = cur.getString(0);
                                    partNoList.add(partNo);
                                } while (cur.moveToNext());
                            }
                            cur.close();
                        }
                        for(int i = 0; i < partNoList.size(); i++) {
                            final LinearLayout lingjianCodeLayout = (LinearLayout) layoutInflater.inflate(R.layout.dipan_lingjian_code, null);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            lp.setMargins(0, 0,
                                    0, 10);
                            lingjianCodeLayout.setLayoutParams(lp);
                            firstStepLiearlayout.addView(lingjianCodeLayout);
                            TextView lingjianName = (TextView)lingjianCodeLayout.findViewById(R.id.lingjianpartnotextview);
                            lingjianName.setText(partNoList.get(i).toString());
                        }
                    }
                    else {
                        EditText firstResultSuccessful = (EditText)findViewById(R.id.firstresultsuccesful);
                        firstResultSuccessful.setText("查询失败");
                    }
                }
                else if(sencondStepLayout.getVisibility() == View.VISIBLE) {
                    EditText sencondResult = (EditText)findViewById(R.id.sencondscanresulttextview);
                    sencondResult.setText(contents.toString());
                    /////
                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout thirdStepLiearlayout = (LinearLayout)thirdStepLayout.findViewById(R.id.thirdchildlayout);
                    thirdStepLiearlayout.removeAllViews();
                    EditText sencondResultSuccessful = (EditText)findViewById(R.id.sencondResultSuccessful);
                    //sencondstep layout add test data
                    Cursor cur = m_MyDatabaseAdapter.getPartInfoFromPartNo(contents.toString());//
                    Boolean bQuery = false;
                    String partName = "";
                    String boltType = "";
                    int boltNum = 0;
                    float stardardValue = 0;
                    float valueRange = 0;
                    float limitRange = 0;
                    String workmanShip = "";
                    //
                    final LinearLayout sencondResultLayout = (LinearLayout) layoutInflater.inflate(R.layout.sencondscanresult, null);
                    EditText sencondPartName = (EditText)sencondResultLayout.findViewById(R.id.sencondpartname);
                    sencondPartName.setText("");
                    EditText sencondBoldType = (EditText)sencondResultLayout.findViewById(R.id.boldtype);
                    sencondBoldType.setText("");
                    EditText sencondBoldNum = (EditText)sencondResultLayout.findViewById(R.id.boldnum);
                    sencondBoldNum.setText("");
                    EditText sencondStardardValue = (EditText)sencondResultLayout.findViewById(R.id.stardardvalue);
                    sencondStardardValue.setText("");
                    EditText sencondValueRange = (EditText)sencondResultLayout.findViewById(R.id.valueRange);
                    sencondValueRange.setText("");
                    EditText sencondLimitRange = (EditText)sencondResultLayout.findViewById(R.id.limitrange);
                    sencondLimitRange.setText("");
                    EditText sencondWorkManShip = (EditText)sencondResultLayout.findViewById(R.id.workmanship);
                    sencondWorkManShip.setText("");

                    if (cur != null) {
                        if (cur.moveToFirst()) {
                            do {
                                bQuery = true;
                                partName = cur.getString(0);
                                boltType = cur.getString(1);
                                boltNum = cur.getInt(2);
                                stardardValue = cur.getFloat(3);
                                valueRange = cur.getFloat(4);
                                limitRange = cur.getFloat(5);
                                workmanShip = cur.getString(6);
                            } while (cur.moveToNext());
                        }
                        cur.close();
                    }
                    if(bQuery) {
                        sencondResultSuccessful.setText("查询成功");
                        //
                        LinearLayout.LayoutParams lpsencondResult = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        lpsencondResult.setMargins(0, 0,
                                0, 10);
                        sencondResultLayout.setLayoutParams(lpsencondResult);
                        final LinearLayout sencondLayout = (LinearLayout)findViewById(R.id.sencondscanresult);
                        sencondLayout.addView(sencondResultLayout);
                        //
                        sencondPartName.setText(partName);
                        sencondBoldType.setText(boltType);
                        sencondBoldNum.setText(Integer.toString(boltNum));
                        sencondStardardValue.setText(Float.toString(stardardValue));
                        sencondValueRange.setText(Float.toString(valueRange));
                        sencondLimitRange.setText(Float.toString(limitRange));
                        sencondWorkManShip.setText(workmanShip);
                        for(int i = 0; i < boltNum; i++) {
                            final LinearLayout luosiLayout = (LinearLayout) layoutInflater.inflate(R.layout.test_torque_wrench, null);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            lp.setMargins(0, 0,
                                    10, 10);
                            luosiLayout.setLayoutParams(lp);
                            thirdStepLiearlayout.addView(luosiLayout);
                        }
                    }
                    else {
                        sencondResultSuccessful.setText("查询失败");
                    }
                    /**/
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scan_chassis, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_scan_menu:
                return true;
            case R.id.action_history_menu:
                Intent historyActivity= new Intent(this, ScanHistory.class);
                historyActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(historyActivity);
                return true;
            case R.id.action_setting_menu:
                Intent personalActivity= new Intent(this, Settings.class);
                personalActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(personalActivity);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void setHasEmbeddedTabs(Object inActionBar, final boolean inHasEmbeddedTabs)
    {
        // get the ActionBar class
        Class<?> actionBarClass = inActionBar.getClass();

        // if it is a Jelly Bean implementation (ActionBarImplJB), get the super class (ActionBarImplICS)
        if ("android.support.v7.app.ActionBarImplJB".equals(actionBarClass.getName()))
        {
            actionBarClass = actionBarClass.getSuperclass();
        }

        // if Android 4.3 >
        if ("android.support.v7.app.ActionBarImplJBMR2".equals(actionBarClass.getName())){
            actionBarClass = actionBarClass.getSuperclass().getSuperclass();
        }

        try
        {
            // try to get the mActionBar field, because the current ActionBar is probably just a wrapper Class
            // if this fails, no worries, this will be an instance of the native ActionBar class or from the ActionBarImplBase class
            final Field actionBarField = actionBarClass.getDeclaredField("mActionBar");
            actionBarField.setAccessible(true);
            inActionBar = actionBarField.get(inActionBar);
            actionBarClass = inActionBar.getClass();
        }
        catch (IllegalAccessException e) {}
        catch (IllegalArgumentException e) {}
        catch (NoSuchFieldException e) {}

        try
        {
            // now call the method setHasEmbeddedTabs, this will put the tabs inside the ActionBar
            // if this fails, you're on you own <img class="wp-smiley" alt=";-)" src="http://www.blogc.at/wp-includes/images/smilies/icon_wink.gif">
            final Method method = actionBarClass.getDeclaredMethod("setHasEmbeddedTabs", new Class[] { Boolean.TYPE });
            method.setAccessible(true);
            method.invoke(inActionBar, new Object[]{ inHasEmbeddedTabs });
        }
        catch (NoSuchMethodException e)        {}
        catch (InvocationTargetException e) {}
        catch (IllegalAccessException e) {}
        catch (IllegalArgumentException e) {}
    }
}
