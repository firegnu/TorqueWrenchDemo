package com.example.firegnu.torquewrenchdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ScanHistory extends Activity {

    private int year;
    private int month;
    private int day;
    private EditText showDate;
    private List<String> list = new ArrayList<String>();
    private Spinner spinnerPerson;
    private ArrayAdapter<String> adapterPerson;
    private String selectedPerson = "";

    private List<String> listPara = new ArrayList<String>();
    private Spinner spinnerPara;
    private ArrayAdapter<String> adapterPara;
    private String selectedPara = "";

    private List<String> listName = new ArrayList<String>();
    private Spinner spinnerName;
    private ArrayAdapter<String> adapterName;
    private String selectedName = "";

    public class TableCellView {
        public String dipanhao;
        public String jiancebuwei;
        public String gongweihao;
        public String shoujianniuju;
        public String shoujianshijian;
        public String shoujianjieguo;
        public String xiuzhengxiuju;
        public String xiuzhengshijian;
        public String xiuzhengjieguo;

        public TableCellView(String code1, String code2, String code3, String code4, String code5,
        String code6, String code7, String code8, String code9) {
            this.dipanhao = code1;
            this.jiancebuwei = code2;
            this.gongweihao = code3;
            this.shoujianniuju = code4;
            this.shoujianshijian = code5;
            this.shoujianjieguo = code6;
            this.xiuzhengxiuju = code7;
            this.xiuzhengshijian = code8;
            this.xiuzhengjieguo = code9;
        }

        public String getAt(int i) {
            if(i == 0) {
                return this.dipanhao;
            }
            else if(i == 1) {
                return this.jiancebuwei;
            }
            else if(i == 2) {
                return this.gongweihao;
            }
            else if(i == 3) {
                return this.shoujianniuju;
            }
            else if(i == 4) {
                return this.shoujianshijian;
            }
            else if(i == 5) {
                return this.shoujianjieguo;
            }
            else if(i == 6) {
                return this.xiuzhengxiuju;
            }
            else if(i == 7) {
                return this.xiuzhengshijian;
            }
            else{
                return this.xiuzhengjieguo;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_history);

        final ActionBar actionBar = getActionBar();
        setHasEmbeddedTabs(actionBar,false);
        setTitle("扭矩扳手工作正常");
        getActionBar().setIcon(R.drawable.userlogo);

        TableLayout bodyTable = (TableLayout)findViewById(R.id.bodyTable);
        TableCellView cellView = new TableCellView("E52581", "上反作用杆与中桥连接", "1号工位", "234",
                "09:30", "合格", "234", "09:30", "合格");
        for (int inx = 0; inx < 20; inx ++) {
            TableRow table_row = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);
            table_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: do your logic here
                    //Log.d("ListViewTableActivity", " on click ");
                }
            });
            for (int jnx = 0; jnx < 9; jnx++) {
                TextView table_row_text = (TextView) getLayoutInflater().inflate(R.layout.table_row_text, null);
                TableRow.LayoutParams lp = (TableRow.LayoutParams)table_row_text.getLayoutParams();
                if (lp == null) {
                    lp = new TableRow.LayoutParams(
                            TableRow.LayoutParams.WRAP_CONTENT,
                            TableRow.LayoutParams.WRAP_CONTENT);
                }
                lp.weight = 1;
                table_row_text.setLayoutParams(lp);
                String result = cellView.getAt(jnx);
                table_row_text.setText(result);
                table_row.addView(table_row_text);
            }
            bodyTable.addView(table_row);
        }

        Calendar mycalendar=Calendar.getInstance(Locale.CHINA);
        Date mydate=new Date();
        mycalendar.setTime(mydate);

        year = mycalendar.get(Calendar.YEAR);
        month = mycalendar.get(Calendar.MONTH);
        day = mycalendar.get(Calendar.DAY_OF_MONTH);

        ImageButton datePickerButton = (ImageButton) findViewById(R.id.dateselectorbutton);
        datePickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePicker = new DatePickerDialog(ScanHistory.this, Datelistener,year,month,day);
                datePicker.show();
            }
        });
        showDate = (EditText)findViewById(R.id.showdatetext);
        //////////////////////
        list.add("杨帆");
        list.add("韩柯");
        list.add("张翔");
        list.add("金鑫");
        list.add("无名");
        spinnerPerson = (Spinner)findViewById(R.id.spinnerperson);
        adapterPerson = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        adapterPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPerson.setAdapter(adapterPerson);

        spinnerPerson.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectedPerson = adapterPerson.getItem(arg2);
                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                selectedPerson = "";
                arg0.setVisibility(View.VISIBLE);
            }
        });

        spinnerPerson.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        spinnerPerson.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });
        ////////////////////////
        listPara.add("J6P684");
        listPara.add("M7A142");
        listPara.add("N8B253");
        listPara.add("O9C354");
        listPara.add("P0D475");
        spinnerPara = (Spinner)findViewById(R.id.spinnerpara);
        adapterPara = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listPara);
        adapterPara.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPara.setAdapter(adapterPara);

        spinnerPara.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectedPara = adapterPara.getItem(arg2);
                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                selectedPara = "";
                arg0.setVisibility(View.VISIBLE);
            }
        });

        spinnerPara.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        spinnerPara.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });
        ////////////////////////
        listName.add("左边轮胎");
        listName.add("右边轮胎");
        listName.add("左前轮胎");
        listName.add("左后轮胎");
        listName.add("右后轮胎");
        spinnerName = (Spinner)findViewById(R.id.spinnername);
        adapterName = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listName);
        adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerName.setAdapter(adapterName);

        spinnerName.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectedName = adapterName.getItem(arg2);
                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                selectedPara = "";
                arg0.setVisibility(View.VISIBLE);
            }
        });

        spinnerName.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        spinnerName.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });
        ////////////////////////
    }

    private DatePickerDialog.OnDateSetListener Datelistener = new DatePickerDialog.OnDateSetListener()
    {
        @Override
        public void onDateSet(DatePicker view, int myyear, int monthOfYear,int dayOfMonth) {
            year=myyear;
            month=monthOfYear;
            day=dayOfMonth;
            updateDate();
        }

        private void updateDate()
        {
            showDate.setText(year+"/"+(month+1)+"/"+day);
        }
    };

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
                Intent openMainActivity= new Intent(this, ScanChassisActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(openMainActivity);
                return true;
            case R.id.action_history_menu:
                return true;
            case R.id.action_setting_menu:
                Intent settingsActivity= new Intent(this, Settings.class);
                settingsActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(settingsActivity);
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
