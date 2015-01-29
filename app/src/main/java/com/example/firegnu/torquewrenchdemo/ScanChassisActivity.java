package com.example.firegnu.torquewrenchdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class ScanChassisActivity extends Activity {
    MyDatabaseAdapter m_MyDatabaseAdapter;
    LinearLayout firstStepLayout;
    LinearLayout sencondStepLayout;
    LinearLayout thirdStepLayout;

    private static final int REQUEST_ENABLE_BT = 1;
    private static final long SCAN_PERIOD = 10000;
    protected static final String TAG = "ScanChassisActivity";
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private List<BluetoothDevice> mDevices = new ArrayList<BluetoothDevice>();;
    private BluetoothLeService mBluetoothLeService;
    protected boolean mConnected;

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private final boolean D = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic mWriteCharacteristic;
    private ExpandableListView mGattServicesList;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

    private String address = "18:7A:93:04:A3:4A";

    private String resultDataStr1 = "";
    private String resultDataStr2 = "";
    private String resultDataStr3 = "";
    private String resultDataStr4 = "";

    int clickedIndex = -1;

    private View mProgressView;

    private String gVinCode = "";
    private String gCarMode = "";
    private String gPartCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_chassis);
        mProgressView = findViewById(R.id.login_progress);
        //bluetooth
        showProgress(true);
        initBlueTooth();
        autoLinkBlue();
        //
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

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            mProgressView.bringToFront();
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        }
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
                    String carMode = m_MyDatabaseAdapter.getInfoFromVinCode("'" + contents.toString() + "'");//'E52581'
                    gVinCode = contents.toString();
                    if(!carMode.equals("")) {
                        gCarMode = carMode;
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
                        Cursor cur = m_MyDatabaseAdapter.getPartListfromCarModel("'" + carMode + "'");
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
                    gPartCode = contents.toString();
                    /////
                    LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    LinearLayout thirdStepLiearlayout = (LinearLayout)thirdStepLayout.findViewById(R.id.thirdchildlayout);
                    thirdStepLiearlayout.removeAllViews();
                    EditText sencondResultSuccessful = (EditText)findViewById(R.id.sencondResultSuccessful);
                    //sencondstep layout add test data
                    //String partCode, String vinCode, String model
                    Cursor cur = m_MyDatabaseAdapter.getPartInfoFromPartNo("'" + contents.toString() + "'",
                            "'" + gVinCode + "'", "'" + gCarMode + "'");//
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
                    final EditText sencondPartName = (EditText)sencondResultLayout.findViewById(R.id.sencondpartname);
                    sencondPartName.setText("");
                    Spinner sencondBoldTypeSpinner = (Spinner)sencondResultLayout.findViewById(R.id.boldtype);
                    //sencondBoldType.setText("");
                    final EditText sencondBoldNum = (EditText)sencondResultLayout.findViewById(R.id.boldnum);
                    sencondBoldNum.setText("");
                    final EditText sencondStardardValue = (EditText)sencondResultLayout.findViewById(R.id.stardardvalue);
                    sencondStardardValue.setText("");
                    final EditText sencondValueRange = (EditText)sencondResultLayout.findViewById(R.id.valueRange);
                    sencondValueRange.setText("");
                    final EditText sencondLimitRange = (EditText)sencondResultLayout.findViewById(R.id.limitrange);
                    sencondLimitRange.setText("");
                    final EditText sencondWorkManShip = (EditText)sencondResultLayout.findViewById(R.id.workmanship);
                    sencondWorkManShip.setText("");

                    final List<String> listBoltType = new ArrayList<String>();
                    final List<String> partNameList = new ArrayList<String>();
                    final List<Integer> boltNumList = new ArrayList<Integer>();
                    final List<Float> stardardValueList = new ArrayList<Float>();
                    final List<Float> valueRangeList = new ArrayList<Float>();
                    final List<Float> limitRangeList = new ArrayList<Float>();
                    final List<String> workmanShipList = new ArrayList<String>();
                    if (cur != null) {
                        if (cur.moveToFirst()) {
                            do {
                                bQuery = true;
                                partName = cur.getString(3);
                                partNameList.add(partName);
                                boltType = cur.getString(2);
                                listBoltType.add(boltType);
                                boltNum = cur.getInt(8);
                                boltNumList.add(boltNum);
                                stardardValue = cur.getFloat(4);
                                stardardValueList.add(stardardValue);
                                valueRange = cur.getFloat(5);
                                valueRangeList.add(valueRange);
                                limitRange = cur.getFloat(6);
                                limitRangeList.add(limitRange);
                                workmanShip = cur.getString(7);
                                workmanShipList.add(workmanShip);
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
                        ////////////////
                        final ArrayAdapter<String> adapterBoltType = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listBoltType);
                        adapterBoltType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sencondBoldTypeSpinner.setAdapter(adapterBoltType);

                        sencondBoldTypeSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
                            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                                //selectedPerson = adapterBoltType.getItem(arg2);
                                sencondPartName.setText(partNameList.get(arg2));
                                sencondBoldNum.setText(Integer.toString(boltNumList.get(arg2)));
                                sencondStardardValue.setText(Float.toString(stardardValueList.get(arg2)));
                                sencondValueRange.setText(Float.toString(valueRangeList.get(arg2)));
                                sencondLimitRange.setText(Float.toString(limitRangeList.get(arg2)));
                                sencondWorkManShip.setText(workmanShipList.get(arg2));
                                arg0.setVisibility(View.VISIBLE);
                            }
                            public void onNothingSelected(AdapterView<?> arg0) {
                                // TODO Auto-generated method stub
                                //selectedPerson = "";
                                arg0.setVisibility(View.VISIBLE);
                            }
                        });

                        sencondBoldTypeSpinner.setOnTouchListener(new Spinner.OnTouchListener(){
                            public boolean onTouch(View v, MotionEvent event) {
                                // TODO Auto-generated method stub
                                return false;
                            }
                        });

                        sencondBoldTypeSpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
                            public void onFocusChange(View v, boolean hasFocus) {
                                // TODO Auto-generated method stub

                            }
                        });
                        ////////////////////
                        Cursor thirdCur = m_MyDatabaseAdapter.getGongweiCount("'" + gPartCode + "'", "'" + gVinCode + "'");
                        int gongweiCount = 0;
                        final List<String> listBoltNumList = new ArrayList<String>();
                        if (thirdCur != null) {
                            if (thirdCur.moveToFirst()) {
                                do {
                                    listBoltNumList.add(thirdCur.getString(0));
                                    gongweiCount++;
                                } while (thirdCur.moveToNext());
                            }
                            thirdCur.close();
                        }
                        for(int i = 0; i < gongweiCount; i++) {
                            final LinearLayout luosiLayout = (LinearLayout) layoutInflater.inflate(R.layout.test_torque_wrench, null);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                            lp.setMargins(0, 0,
                                    10, 10);
                            luosiLayout.setLayoutParams(lp);
                            luosiLayout.setTag(i);
                            thirdStepLiearlayout.addView(luosiLayout);
                            TextView gongweiNameTextView  = (TextView)luosiLayout.findViewById(R.id.gongweimingchengtextview);
                            //首次进入开始填写数据有则填没有则不填
                            Cursor testCorqueDataCur = m_MyDatabaseAdapter.getTestDataForGongwei("'" + listBoltNumList.get(i) + "'",
                                    "'" + gPartCode + "'", "'" + gVinCode + "'");
                            if (testCorqueDataCur != null) {
                                if (testCorqueDataCur.moveToFirst()) {
                                    do {
                                        String testTorque = testCorqueDataCur.getString(0);
                                        String testTime = testCorqueDataCur.getString(1);
                                        String testResult = testCorqueDataCur.getString(2);
                                        String correctedTorque = testCorqueDataCur.getString(3);
                                        String correctedTime = testCorqueDataCur.getString(4);
                                        String correctedResult = testCorqueDataCur.getString(5);

                                        EditText shoujianTorqueEdit = (EditText)luosiLayout.findViewById(R.id.shoucetextedit);
                                        shoujianTorqueEdit.setText(testTorque);
                                        EditText shoujianTorqueTimeEdit = (EditText)luosiLayout.findViewById(R.id.shoucetimetextedit);
                                        shoujianTorqueTimeEdit.setText(testTime);
                                        CheckBox shoujianCheckBox = (CheckBox)luosiLayout.findViewById(R.id.shoucecheckbox);
                                        shoujianCheckBox.setChecked(true);
                                        TextView shoujianResultTextView = (TextView)luosiLayout.findViewById(R.id.shouceresult);
                                        if(testResult.equals("0")) {
                                            shoujianResultTextView.setText("合格");
                                        }
                                        else if(testResult.equals("1")) {
                                            shoujianResultTextView.setText("超下限");
                                        }
                                        else if(testResult.equals("2")) {
                                            shoujianResultTextView.setText("超上限");
                                        }

                                        EditText xiuzhengTorqueEdit = (EditText)luosiLayout.findViewById(R.id.xiuzhengtextedit);
                                        xiuzhengTorqueEdit.setText(correctedTorque);
                                        EditText xiuzhengTorqueTimeEdit = (EditText)luosiLayout.findViewById(R.id.xiuzhengtimetextedit);
                                        xiuzhengTorqueTimeEdit.setText(correctedTime);
                                        CheckBox xiuzhengCheckBox = (CheckBox)luosiLayout.findViewById(R.id.xiuzhengcheckbox);
                                        xiuzhengCheckBox.setChecked(true);
                                        TextView xiuzhengResultTextView = (TextView)luosiLayout.findViewById(R.id.xiuzhengresult);
                                        if(correctedResult.equals("0")) {
                                            xiuzhengResultTextView.setText("合格");
                                        }
                                        else if(correctedResult.equals("1")) {
                                            xiuzhengResultTextView.setText("超下限");
                                        }
                                        else if(correctedResult.equals("2")) {
                                            xiuzhengResultTextView.setText("超上限");
                                        }
                                    } while (testCorqueDataCur.moveToNext());
                                }
                                testCorqueDataCur.close();
                            }
                            //
                            gongweiNameTextView.setText(listBoltNumList.get(i));
                            ////
                            ImageButton gongweiButton = (ImageButton)luosiLayout.findViewById(R.id.luoshuanbutton);
                            gongweiButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //其他的变灰
                                    LinearLayout luosiLayoutButton = (LinearLayout)view.getParent().getParent();
                                    clickedIndex = (int)luosiLayoutButton.getTag();
                                    LinearLayout thirdStepLiearlayout = (LinearLayout)thirdStepLayout.findViewById(R.id.thirdchildlayout);
                                    for(int i = 0; i < thirdStepLiearlayout.getChildCount(); i++) {
                                        LinearLayout luosiLayout = (LinearLayout)thirdStepLiearlayout.getChildAt(i);
                                        TextView textView0 = (TextView)luosiLayout.findViewById(R.id.gongweimingchengtextview);
                                        TextView textView1 = (TextView)luosiLayout.findViewById(R.id.textview1);
                                        TextView textView2 = (TextView)luosiLayout.findViewById(R.id.textview2);
                                        TextView textView3 = (TextView)luosiLayout.findViewById(R.id.textview3);
                                        TextView textView4 = (TextView)luosiLayout.findViewById(R.id.textview4);
                                        TextView textView5 = (TextView)luosiLayout.findViewById(R.id.shouceresult);
                                        TextView textView6 = (TextView)luosiLayout.findViewById(R.id.xiuzhengresult);


                                        EditText editText1 = (EditText)luosiLayout.findViewById(R.id.shoucetextedit);
                                        EditText editText2 = (EditText)luosiLayout.findViewById(R.id.shoucetimetextedit);
                                        EditText editText3 = (EditText)luosiLayout.findViewById(R.id.xiuzhengtextedit);
                                        EditText editText4 = (EditText)luosiLayout.findViewById(R.id.xiuzhengtimetextedit);
                                        if(i == clickedIndex) {
                                            textView0.setTextColor(getResources().getColor(R.color.pureblack));
                                            textView1.setTextColor(getResources().getColor(R.color.pureblack));
                                            textView2.setTextColor(getResources().getColor(R.color.pureblack));
                                            textView3.setTextColor(getResources().getColor(R.color.pureblack));
                                            textView4.setTextColor(getResources().getColor(R.color.pureblack));
                                            textView5.setTextColor(getResources().getColor(R.color.pureblack));
                                            textView6.setTextColor(getResources().getColor(R.color.pureblack));
                                            editText1.setTextColor(getResources().getColor(R.color.pureblack));
                                            editText2.setTextColor(getResources().getColor(R.color.pureblack));
                                            editText3.setTextColor(getResources().getColor(R.color.pureblack));
                                            editText4.setTextColor(getResources().getColor(R.color.pureblack));
                                        }
                                        else {
                                            textView0.setTextColor(getResources().getColor(R.color.hint));
                                            textView1.setTextColor(getResources().getColor(R.color.hint));
                                            textView2.setTextColor(getResources().getColor(R.color.hint));
                                            textView3.setTextColor(getResources().getColor(R.color.hint));
                                            textView4.setTextColor(getResources().getColor(R.color.hint));
                                            textView5.setTextColor(getResources().getColor(R.color.hint));
                                            textView6.setTextColor(getResources().getColor(R.color.hint));
                                            editText1.setTextColor(getResources().getColor(R.color.hint));
                                            editText2.setTextColor(getResources().getColor(R.color.hint));
                                            editText3.setTextColor(getResources().getColor(R.color.hint));
                                            editText4.setTextColor(getResources().getColor(R.color.hint));
                                        }
                                    }
                                }
                            });
                            /////
                        }
                        //首次进入第一个被点亮
                        if(boltNum > 0) {
                            LinearLayout luosiLayout = (LinearLayout)thirdStepLiearlayout.getChildAt(0);
                            ImageButton firstImageButton = (ImageButton)luosiLayout.findViewById(R.id.luoshuanbutton);
                            firstImageButton.performClick();
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

    ///////////////////////////////////////////////////////////bluetooth
    private void initBlueTooth() {
        mHandler = new Handler();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }

    /**
     * 蓝牙自动连接
     */
    //todo:当没有发现任何蓝牙设备时，线程无法停止
    private void autoLinkBlue() {

        android.util.Log.d(TAG, "autoLinkBlue");
        checkBlueIsSupported();

        new Thread(new Runnable() {

            @Override
            public void run() {
                scanDevice();

                for (int i = 0; i < 10; i++) {
                    if (mDevices.size() > 0)
                        break;
                    try {
                        android.util.Log.d(TAG, "scan " + (int) (i + 1) + " s");
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                android.util.Log.d(TAG, "stop scan");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);

                android.util.Log.d(TAG, "devices size:" + mDevices.size());
                // 没有发现设备
                if (mDevices.size() <= 0) {

                    return;
                }
                BluetoothDevice device = selectDevice();

                if (device != null) {
                    android.util.Log.d(TAG, device.getName() + " " + device.getAddress());
                    connect(device);
                }

            }
        }).start();
    }

    private void checkBlueIsSupported() {
        // Use this check to determine whether BLE is supported on the device.
        // Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT)
                    .show();
            finish();
        }

        // Initializes a Bluetooth adapter. For API level 18 and above, get a
        // reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported,
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    private void scanDevice() {

        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                android.util.Log.d(TAG, "stop scan!");
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
        }, SCAN_PERIOD);

        mBluetoothAdapter.startLeScan(mLeScanCallback);
        Log.d(TAG, "start scan!");
    }

    /**
     * 从搜索的设备列表中选择设备 根据设备的MAC地址进行选择
     */
    private BluetoothDevice selectDevice() {
        int size = mDevices.size();
        BluetoothDevice device = null;

        if (size > 0) {
            for (int i = 0; i < size; i++) {
                if(mDevices.get(i).getAddress().equalsIgnoreCase(address)) {
                    device = mDevices.get(i);
                    break;
                }
            }
        }

        return device;

    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            Log.d(TAG, "onServiceConnected");
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service)
                    .getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            // Automatically connects to the device upon successful start-up initialization.
//            mBluetoothLeService.connect(address);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private void connect(final BluetoothDevice device) {
        Log.d(TAG, "connect");
        boolean isconnected = mBluetoothLeService.connect(device.getAddress());
        if (isconnected)
            Log.d(TAG, "connected!");
        else
            Log.d(TAG, "not connected!");

    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            String stateMessage = null;

            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                showProgress(false);
                Log.d(TAG, "ACTION_GATT_CONNECTED");
                mConnected = true;
                stateMessage = "蓝牙设备已连接！";
                updateConnectionState(R.string.connected, stateMessage);
                invalidateOptionsMenu();
                setTitle("蓝牙设备连接正常");
                ////


            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                showProgress(false);
                Log.d(TAG, "ACTION_GATT_DISCONNECTED");
                stateMessage = "蓝牙设备断开连接！";
                mConnected = false;
                updateConnectionState(R.string.disconnected, stateMessage);
                setTitle("蓝牙设备断开连接");
                ///
                Dialog alertDialog = new AlertDialog.Builder(ScanChassisActivity.this).
                        setTitle("蓝牙连接").
                        setMessage("蓝牙失去连接，需要手动连接").
                        setIcon(R.drawable.ic_launcher).
                        setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                                //重新连接蓝牙
                                mBluetoothLeService.connect(address);
                            }
                        }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).
                        create();
                alertDialog.show();
                ///
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                Log.d(TAG, "ACTION_GATT_SERVICES_DISCOVERED");
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, "ACTION_DATA_AVAILABLE");
                rceiveData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void updateConnectionState(final int resourceId,
                                       final String stateMessage) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ScanChassisActivity.this, stateMessage,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    protected void rceiveData(String stringExtra) {
        Log.d(TAG, stringExtra);
        if (stringExtra != null) {
            /*Toast.makeText(ScanChassisActivity.this, stringExtra,
                    Toast.LENGTH_LONG).show();*/
            HashMap<String,String> map = new HashMap<String,String>();
            String ReceiveData = stringExtra;
            map = DataAnalysis.toData(ReceiveData);
            resultDataStr1 = map.get("时间");
            resultDataStr2 = map.get("实测值");
            resultDataStr3 = map.get("结论");
            resultDataStr4 = map.get("日期");
            Iterator<String> it = map.keySet().iterator();
            if(clickedIndex != -1) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                LinearLayout thirdStepLiearlayout = (LinearLayout)thirdStepLayout.findViewById(R.id.thirdchildlayout);
                for(int i = 0; i < thirdStepLiearlayout.getChildCount(); i++) {
                    if(i == clickedIndex) {
                        LinearLayout luosiLayout = (LinearLayout)thirdStepLiearlayout.getChildAt(i);
                        EditText shouceTextEdit = (EditText)luosiLayout.findViewById(R.id.shoucetextedit);
                        EditText shouceTimeTextEdit = (EditText)luosiLayout.findViewById(R.id.shoucetimetextedit);
                        CheckBox shouceCheckbox = (CheckBox)luosiLayout.findViewById(R.id.shoucecheckbox);
                        TextView shouceResult = (TextView)luosiLayout.findViewById(R.id.shouceresult);

                        EditText xiuzhengTextEdit = (EditText)luosiLayout.findViewById(R.id.xiuzhengtextedit);
                        EditText xiuzhengTimeTextEdit = (EditText)luosiLayout.findViewById(R.id.xiuzhengtimetextedit);
                        CheckBox xiuzhengCheckbox = (CheckBox)luosiLayout.findViewById(R.id.xiuzhengcheckbox);
                        TextView xiuzhengResult = (TextView)luosiLayout.findViewById(R.id.xiuzhengresult);

                        TextView gongweiNameTextView = (TextView)luosiLayout.findViewById(R.id.gongweimingchengtextview);
                        String gongweiName = gongweiNameTextView.getText().toString();
                        if(shouceTextEdit.getText().toString().equals("")) {
                            //首测为空直接插入表中首测数据
                            shouceTextEdit.setText(resultDataStr2);
                            shouceTimeTextEdit.setText(resultDataStr1);
                            shouceCheckbox.setChecked(true);
                            shouceResult.setText(resultDataStr3);
                            String resultTest = "";
                            if(resultDataStr3.equals("合格")) {
                                resultTest = "0";
                            }
                            else if(resultDataStr3.equals("超下限")) {
                                resultTest = "1";
                            }
                            else {
                                resultTest = "2";
                            }
                            String resultTime = resultDataStr4 + " " + resultDataStr1;
                            m_MyDatabaseAdapter.insertTestDataTableBlueTooth(gVinCode, gPartCode, gongweiName, resultTime,
                                    resultDataStr2, resultTime, resultTest, "", "", "", Integer.toString(DataHolder.getUserId()));
                        }
                        else {
                            //首测不为空，update表中数据，但是要update表中修正数据
                            xiuzhengTextEdit.setText(resultDataStr2);
                            xiuzhengTimeTextEdit.setText(resultDataStr1);
                            xiuzhengCheckbox.setChecked(true);
                            xiuzhengResult.setText(resultDataStr3);
                            String resultTest = "";
                            if(resultDataStr3.equals("合格")) {
                                resultTest = "0";
                            }
                            else if(resultDataStr3.equals("超下限")) {
                                resultTest = "1";
                            }
                            else {
                                resultTest = "2";
                            }
                            String resultTime = resultDataStr4 + " " + resultDataStr1;
                            m_MyDatabaseAdapter.updateTestDataTableBlueTooth(gVinCode, gPartCode, gongweiName, resultDataStr2,
                                    resultTime, resultTest, resultTime);
                        }
                    }
                }
                //移动到下一个
                clickedIndex++;
                if(clickedIndex <= thirdStepLiearlayout.getChildCount() - 1) {
                    LinearLayout luosiLayout = (LinearLayout)thirdStepLiearlayout.getChildAt(clickedIndex);
                    ImageButton nextImageButton = (ImageButton)luosiLayout.findViewById(R.id.luoshuanbutton);
                    nextImageButton.performClick();
                }
            }
        }
    }

    private void sendData(String str){
        Log.d(TAG, str);
        if(str==null){
            Toast.makeText(ScanChassisActivity.this, "没有任何数据！", Toast.LENGTH_LONG)
                    .show();
            return;
        }

        int idx = 0;
        int count = str.length() / 3;
        if (str.length() % 3 != 0) {
            count++;
        }
        byte[] buf = new byte[count];

        for (int i=0; i<str.length(); i+=3) {
            int end = i+2;
            if (end > str.length()) {
                end = str.length();
            }
            String s = str.substring(i, end);
            if (!Public.is_hex_char(s)) {
                Public.ShowAlert("Error", "Wrong data format!\n\nCorrect format:\n30 39 9D AA FF\n30,39,9D,AA,FF", ScanChassisActivity.this);
                return;
            }
            if (idx >= count) {
                break;
            }
            buf[idx++] = (byte)Integer.parseInt(s, 16);
        }
        mBluetoothLeService.writeCharacteristic(mWriteCharacteristic, buf);
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi,
                             final byte[] scanRecord) {
            Log.d(TAG, device.getName());
            mDevices.add(device);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device. If Bluetooth is not
        // currently enabled,
        // fire an intent to display a dialog asking the user to grant
        // permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(
                        BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        // if (mBluetoothLeService != null) {
        // final boolean result = mBluetoothLeService.connect(mDeviceAddress);
        // Log.d(TAG, "Connect request result=" + result);
        // }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ////unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {


            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);

                //++++++++++++++++++
                final int charaProp = gattCharacteristic.getProperties();

                Log.e("", charaProp + "===UUID:" + gattCharacteristic.getUuid().toString());

/*
                //if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                if (charaProp == 2) {
                    // If there is an active notification on a characteristic, clear
                    // it first so it doesn't update the data field on the user interface.
                    if (mNotifyCharacteristic != null) {
                        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                    }
                    mBluetoothLeService.readCharacteristic(gattCharacteristic);
                	Log.e("", "got READ characteristic ---- ");
                }
*/
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {

                    //判断一下UUID
                    if (gattCharacteristic.getUuid().toString().compareToIgnoreCase(SampleGattAttributes.ISSC_CHAR_RX_UUID) == 0) {
                        if (D) Log.e("", "got NOTIFY characteristic ---- ");
                        mNotifyCharacteristic = gattCharacteristic;
                        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);

                        //延时500ms，再发送一次，防止AMIC收不到
//                		try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//                		mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);
                    }
                }

                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE) > 0 ||
                        (charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) > 0)
                {
                    //判断一下UUID
                    if (gattCharacteristic.getUuid().toString().compareToIgnoreCase(SampleGattAttributes.ISSC_CHAR_TX_UUID) == 0) {
                        mWriteCharacteristic = gattCharacteristic;
                        if (D) Log.e("", "got WRITE characteristic ---- ");
                    }
                }

                //+++++++++++++++++
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
    }
    /**
     * 注册Intent的Action
     *
     * @return
     */
    private IntentFilter makeGattUpdateIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter
                .addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }
    //////////////////////////////////////////////////////////////////////
}
