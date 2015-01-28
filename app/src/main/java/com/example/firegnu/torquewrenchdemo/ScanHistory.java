package com.example.firegnu.torquewrenchdemo;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ScanHistory extends Activity {

    MyDatabaseAdapter m_MyDatabaseAdapter;

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

    private List<TableCellView> testResultList = new ArrayList<TableCellView>();

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
        public String userId;
        public String testDate;

        public TableCellView(String code1, String code2, String code3, String code4, String code5,
        String code6, String code7, String code8, String code9, String code10, String code11) {
            this.dipanhao = code1;
            this.jiancebuwei = code2;
            this.gongweihao = code3;
            this.shoujianniuju = code4;
            this.shoujianshijian = code5;
            this.shoujianjieguo = code6;
            this.xiuzhengxiuju = code7;
            this.xiuzhengshijian = code8;
            this.xiuzhengjieguo = code9;
            this.userId = code10;
            this.testDate = code11;
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
            else if(i == 8){
                return this.xiuzhengjieguo;
            }
            else if(i == 9) {
                return this.userId;
            }
            else {
                return this.testDate;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_history);
        m_MyDatabaseAdapter =MyDatabaseAdapter.getDatabaseAdapter(this);

        final ActionBar actionBar = getActionBar();
        setHasEmbeddedTabs(actionBar,false);
        setTitle("扭矩扳手工作正常");
        getActionBar().setIcon(R.drawable.userlogo);
        //get test result
        final ImageButton filterButton = (ImageButton) findViewById(R.id.filterbutton);
        Cursor cur = m_MyDatabaseAdapter.getTestResult();

        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    String carMode = cur.getString(0);
                    carMode = m_MyDatabaseAdapter.getCarModelFromVinCode(carMode);
                    String partCode = cur.getString(1);
                    String partName = m_MyDatabaseAdapter.getPartNameFromPartNo(partCode);
                    String partStation = cur.getString(2);
                    String torque = cur.getString(3);
                    String torqueTime = cur.getString(4);
                    //
                    SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String resultStr = "";
                    try {
                        Date dateObj = curFormater.parse(torqueTime);
                        SimpleDateFormat curFormaterStr = new SimpleDateFormat("HH:mm");
                        resultStr = curFormaterStr.format(dateObj);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    //
                    int torqueResult = cur.getInt(5);
                    String correcttedTorque = cur.getString(6);
                    String correcttedTorqueTime = cur.getString(7);
                    //
                    SimpleDateFormat curFormaterCorrecttedTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String resultStrCorrecttedTime = "";
                    try {
                        Date dateObj = curFormaterCorrecttedTime.parse(correcttedTorqueTime);
                        SimpleDateFormat curFormaterStrCorrecttedTime = new SimpleDateFormat("HH:mm");
                        resultStrCorrecttedTime = curFormaterStrCorrecttedTime.format(dateObj);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    //
                    int correcttedTorqueResult = cur.getInt(8);
                    String finalTorqueResult = "";
                    if(torqueResult == 0) {
                        finalTorqueResult = "合格";
                    }
                    else if(torqueResult == 1) {
                        finalTorqueResult = "超下限";
                    }
                    else if(torqueResult == 2) {
                        finalTorqueResult = "超上限";
                    }

                    String finalCorrecttedTorqueResult = "";
                    if(correcttedTorqueResult == 0) {
                        finalCorrecttedTorqueResult = "合格";
                    }
                    else if(correcttedTorqueResult == 1) {
                        finalCorrecttedTorqueResult = "超下限";
                    }
                    else if(correcttedTorqueResult == 2) {
                        finalCorrecttedTorqueResult = "超上限";
                    }
                    String userId = Integer.toString(cur.getInt(9));
                    String testDate = cur.getString(10);
                    TableCellView testResult = new TableCellView(carMode, partName, partStation, torque, resultStr,
                            finalTorqueResult, correcttedTorque, resultStrCorrecttedTime, finalCorrecttedTorqueResult, userId, testDate);
                    testResultList.add(testResult);
                } while (cur.moveToNext());
            }
            cur.close();
        }
        //
        TableLayout bodyTable = (TableLayout)findViewById(R.id.bodyTable);
        for (int inx = 0; inx < testResultList.size(); inx ++) {
            TableCellView cellView = testResultList.get(inx);
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
        list.add(DataHolder.getUserName());
        //if currentuser is superuser
        if(DataHolder.getUserType() == 0) {
            for (int inx = 0; inx < testResultList.size(); inx ++) {
                TableCellView cellView = testResultList.get(inx);
                String userName = m_MyDatabaseAdapter.getUserNameFromUserId(Integer.parseInt(cellView.getAt(9)));
                if(!list.contains(userName)) {
                    list.add(userName);
                }
            }
        }

        //////////////////////
        spinnerPerson = (Spinner)findViewById(R.id.spinnerperson);
        adapterPerson = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, list);
        adapterPerson.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPerson.setAdapter(adapterPerson);

        spinnerPerson.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectedPerson = adapterPerson.getItem(arg2);
                arg0.setVisibility(View.VISIBLE);
                filterButton.performClick();
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
        for (int inx = 0; inx < testResultList.size(); inx ++) {
            TableCellView cellView = testResultList.get(inx);
            String carModel = cellView.getAt(0);
            if(!listPara.contains(carModel)) {
                listPara.add(carModel);
            }
        }
        ////////////////////////
        spinnerPara = (Spinner)findViewById(R.id.spinnerpara);
        adapterPara = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listPara);
        adapterPara.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPara.setAdapter(adapterPara);

        spinnerPara.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectedPara = adapterPara.getItem(arg2);
                arg0.setVisibility(View.VISIBLE);
                filterButton.performClick();
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
        for (int inx = 0; inx < testResultList.size(); inx ++) {
            TableCellView cellView = testResultList.get(inx);
            String partName = cellView.getAt(1);
            if(!listName.contains(partName)) {
                listName.add(partName);
            }
        }
        ////////////////////////
        spinnerName = (Spinner)findViewById(R.id.spinnername);
        adapterName = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listName);
        adapterName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerName.setAdapter(adapterName);

        spinnerName.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                selectedName = adapterName.getItem(arg2);
                arg0.setVisibility(View.VISIBLE);
                filterButton.performClick();
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
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filterTestResult();
            }
        });

    }

    //filter testResult
    private void filterTestResult() {
        TableLayout bodyTable = (TableLayout)findViewById(R.id.bodyTable);
        bodyTable.removeAllViews();
        for (int inx = 0; inx < testResultList.size(); inx ++) {
            TableCellView cellView = testResultList.get(inx);
            TableRow table_row = (TableRow) getLayoutInflater().inflate(R.layout.table_row, null);
            table_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: do your logic here
                    //Log.d("ListViewTableActivity", " on click ");
                }
            });
            String userName = m_MyDatabaseAdapter.getUserNameFromUserId(Integer.parseInt(cellView.getAt(9)));
            if(!selectedPerson.equals("") && !selectedPerson.equals(userName)) {
                continue;
            }
            if(!selectedPara.equals("") && !selectedPara.equals(cellView.getAt(0))) {
                continue;
            }
            if(!selectedName.equals("") && !selectedName.equals(cellView.getAt(1))) {
                continue;
            }
            SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
            Date dateObj = null;
            try {
                dateObj = curFormater.parse(cellView.getAt(10));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dateObj.setHours(0);
            dateObj.setMinutes(0);
            dateObj.setSeconds(0);
            String resultStr = curFormater.format(dateObj);
            String datePickStr = showDate.getText().toString();
            if(!datePickStr.equals("") && !datePickStr.equals(resultStr)) {
                continue;
            }
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
            month += 1;
            String result = "";
            if(month < 10) {
                result = year+"-"+"0"+month+"-"+day;
            }
            else {
                result = year+"-"+month+"-"+day;
            }
            showDate.setText(result);
            filterTestResult();
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
