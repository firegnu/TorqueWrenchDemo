package com.example.firegnu.torquewrenchdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Settings extends Activity {
    final Context context = this;
    MyDatabaseAdapter m_MyDatabaseAdapter;
    private View mProgressView;
    private List<Integer> idList = new ArrayList<Integer>();
    private List<String> nameList = new ArrayList<String>();
    private String newPassword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        m_MyDatabaseAdapter =MyDatabaseAdapter.getDatabaseAdapter(this);
        mProgressView = findViewById(R.id.update_progress);

        final ActionBar actionBar = getActionBar();
        setHasEmbeddedTabs(actionBar,false);
        setTitle(DataHolder.getUserName());
        actionBar.setDisplayShowHomeEnabled(false);
        //getActionBar().setIcon(R.drawable.userlogo);

        Button logoutButton = (Button) findViewById(R.id.logoutbutton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //此时将SharedPreferences中username和password清空
                SharedPreferences userName = getApplicationContext().getSharedPreferences("userName", 0);
                SharedPreferences.Editor editorUserName = userName.edit();
                editorUserName.putString("userName", "");
                editorUserName.apply();

                SharedPreferences userPassword = getApplicationContext().getSharedPreferences("userPassword", 0);
                SharedPreferences.Editor editorUserPassword = userPassword.edit();
                editorUserPassword.putString("userPassword", "");
                editorUserPassword.apply();

                logout();
            }
        });

        Button modifyPasswordButton = (Button) findViewById(R.id.modifyuserpasswordbutton);
        modifyPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modifyUserPassword();
            }
        });

        Button asyncDataButton = (Button) findViewById(R.id.immediateasyndatafromserver);
        asyncDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                asyncDataFromServer();
            }
        });

        int userType = DataHolder.getUserType();

        //normal user
        /*EditText saveGroupEdit = (EditText)findViewById(R.id.savegroupedit);
        ImageButton saveGroupButton = (ImageButton)findViewById(R.id.savegroupbutton);
        saveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGroup();
            }
        });*/
        Cursor cur = m_MyDatabaseAdapter.getAllGroupInfo();
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    int id = cur.getInt(0);
                    String name = cur.getString(1);
                    idList.add(id);
                    nameList.add(name);
                } while (cur.moveToNext());
            }
            cur.close();
        }
        ////////////////
        SharedPreferences settingsGroupName = getApplicationContext().getSharedPreferences("groupId", 0);
        int definedGroupId = 0;
        final int groupIdSelected = settingsGroupName.getInt("groupId", definedGroupId);
        Spinner groupSpinner = (Spinner)findViewById(R.id.spinnergroup);

        final ArrayAdapter<String> adapterBoltType = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, nameList);
        adapterBoltType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupSpinner.setAdapter(adapterBoltType);
        groupSpinner.setSelection(groupIdSelected);
        groupSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                //首先保存到SharedPreferences中
                SharedPreferences groupId = getApplicationContext().getSharedPreferences("groupId", 0);
                SharedPreferences.Editor editorBanshouName = groupId.edit();
                editorBanshouName.putInt("groupId", arg2);
                editorBanshouName.apply();
                if(arg2 != groupIdSelected) {
                    //String selectedPerson = adapterBoltType.getItem(arg2);
                    //此时将SharedPreferences中username和password清空
                    SharedPreferences userName = getApplicationContext().getSharedPreferences("userName", 0);
                    SharedPreferences.Editor editorUserName = userName.edit();
                    editorUserName.putString("userName", "");
                    editorUserName.apply();

                    SharedPreferences userPassword = getApplicationContext().getSharedPreferences("userPassword", 0);
                    SharedPreferences.Editor editorUserPassword = userPassword.edit();
                    editorUserPassword.putString("userPassword", "");
                    editorUserPassword.apply();
                    logout();
                }

                arg0.setVisibility(View.VISIBLE);
            }
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                //selectedPerson = "";
                arg0.setVisibility(View.VISIBLE);
            }
        });

        groupSpinner.setOnTouchListener(new Spinner.OnTouchListener(){
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        groupSpinner.setOnFocusChangeListener(new Spinner.OnFocusChangeListener(){
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub

            }
        });
        ////////////////////

        EditText saveBanshouEdit = (EditText)findViewById(R.id.savebanshouidedit);
        ImageButton saveBanshouButton = (ImageButton)findViewById(R.id.savebanshouidbutton);
        saveBanshouButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBanshouId();
            }
        });

        EditText banshouWatchTimeEdit = (EditText)findViewById(R.id.banshouwatchtimeedit);
        ImageButton banshouWatchTimeButton = (ImageButton)findViewById(R.id.banshouwatchtimebutton);
        banshouWatchTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBanshouWatchTime();
            }
        });

        EditText dataWatchTimeEdit = (EditText)findViewById(R.id.datawatchtimeedit);
        ImageButton dataWatchTimeButton = (ImageButton)findViewById(R.id.datawatchtimebutton);
        dataWatchTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveDataWatchTime();
            }
        });

        Switch autoRefreshDataSwitch = (Switch)findViewById(R.id.autorefreshdataswitch);
        autoRefreshDataSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                saveAutoRefreshSwitch(isChecked);
            }
        });
        if(userType == 1) {
            //saveGroupEdit.setEnabled(false);
            //saveGroupButton.setEnabled(false);
            groupSpinner.setEnabled(false);
            saveBanshouEdit.setEnabled(false);
            saveBanshouButton.setEnabled(false);
            banshouWatchTimeEdit.setEnabled(false);
            banshouWatchTimeButton.setEnabled(false);
            dataWatchTimeEdit.setEnabled(false);
            dataWatchTimeButton.setEnabled(false);
            autoRefreshDataSwitch.setEnabled(false);
        }
        else {
            //saveGroupEdit.setEnabled(true);
            //saveGroupButton.setEnabled(true);
            groupSpinner.setEnabled(true);
            saveBanshouEdit.setEnabled(true);
            saveBanshouButton.setEnabled(true);
            banshouWatchTimeEdit.setEnabled(true);
            banshouWatchTimeButton.setEnabled(true);
            dataWatchTimeEdit.setEnabled(true);
            dataWatchTimeButton.setEnabled(true);
            autoRefreshDataSwitch.setEnabled(true);
        }
        //get sharedPreferences data

        ////saveGroupEdit.setText(groupName);

        SharedPreferences settingsBanshouName = getApplicationContext().getSharedPreferences("banshouName", 0);
        String banshouName = settingsBanshouName.getString("banshouName", "");
        saveBanshouEdit.setText(banshouName);

        SharedPreferences settingsBanshouWatchTime = getApplicationContext().getSharedPreferences("banshouWatchTime", 0);
        String banshouWatchTime = settingsBanshouWatchTime.getString("banshouWatchTime", "");
        banshouWatchTimeEdit.setText(banshouWatchTime);

        SharedPreferences settingsDataWatchTime = getApplicationContext().getSharedPreferences("dataWatchTime", 0);
        String dataWatchTime = settingsDataWatchTime.getString("dataWatchTime", "");
        dataWatchTimeEdit.setText(dataWatchTime);

        SharedPreferences settingsAutoRefresh = getApplicationContext().getSharedPreferences("bAutoRefresh", 0);
        Boolean bDataRefresh = settingsAutoRefresh.getBoolean("bAutoRefresh", false);
        autoRefreshDataSwitch.setChecked(bDataRefresh);

        SharedPreferences settingsLastSyncTime = getApplicationContext().getSharedPreferences("lastAsyncTime", 0);
        String lastSyncTime = settingsLastSyncTime.getString("lastAsyncTime", "");
        TextView lastSyncTimeTextView = (TextView)findViewById(R.id.lastsynctimetextview);
        if(!lastSyncTime.equals("")) {
            lastSyncTimeTextView.setText("上次同步数据时间：" + lastSyncTime);
        }
        else {
            lastSyncTimeTextView.setText("你还未做过同步!");
        }
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

    public class TestData {
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
        public TestData() {
        }
    }

    public void asyncDataFromServer() {
        //0.首先把本地数据库中的数据上传,只上传检测数据(其他数据不上传)
        List<TestData> testDataList = new ArrayList<TestData>();
        Cursor cur = m_MyDatabaseAdapter.getLocalTestResult();
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    TestData testData = new TestData();
                    String vinCode = cur.getString(0);
                    String partCode = cur.getString(1);
                    String partStation = cur.getString(2);
                    String testDate = cur.getString(3);
                    String testTorque = cur.getString(4);
                    String testTime = cur.getString(5);
                    String testResult = cur.getString(6);
                    String correctedTorque = cur.getString(7);
                    String correctedTime = cur.getString(8);
                    String correctedResult = cur.getString(9);
                    String userID = cur.getString(10);
                    testData.dipanhao = vinCode;
                    testData.jiancebuwei = partCode;
                    testData.gongweihao = partStation;
                    testData.shoujianniuju = testTorque;
                    testData.shoujianshijian = testTime;
                    testData.shoujianjieguo = testResult;
                    testData.xiuzhengxiuju = correctedTorque;
                    testData.xiuzhengshijian = correctedTime;
                    testData.xiuzhengjieguo = correctedResult;
                    testData.userId = userID;
                    testData.testDate = testDate;
                    testDataList.add(testData);
                } while (cur.moveToNext());
            }
            cur.close();
        }
        new uploadLocalDataTasks(testDataList).execute();

    }

    private class uploadLocalDataTasks extends AsyncTask<Void, Void, String[]> {
        List<TestData> testDataList = new ArrayList<TestData>();
        Boolean bUpdate = true;

        public uploadLocalDataTasks(List<TestData> testDataList) {
            this.testDataList = testDataList;
        }

        public void postData() {
            String uri = DataHolder.getServerAddress() + "/mobile/uploadClientTestData/";
            HttpParams httpParams = new BasicHttpParams();
            httpParams.setParameter("charset", "UTF-8");
            HttpClient httpclient = new DefaultHttpClient(httpParams);
            HttpPost httppost = new HttpPost(uri);
            httppost.addHeader("charset", HTTP.UTF_8);
            try {
                // Add your data
                for(int i = 0; i < testDataList.size(); i++) {
                    TestData testData = testDataList.get(i);
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(11);
                    nameValuePairs.add(new BasicNameValuePair("vinCode", testData.dipanhao));
                    nameValuePairs.add(new BasicNameValuePair("partCode", testData.jiancebuwei));
                    nameValuePairs.add(new BasicNameValuePair("partStation", testData.gongweihao));
                    nameValuePairs.add(new BasicNameValuePair("testDate", testData.testDate));
                    nameValuePairs.add(new BasicNameValuePair("testTorque", testData.shoujianniuju));
                    nameValuePairs.add(new BasicNameValuePair("testTime", testData.shoujianshijian));
                    nameValuePairs.add(new BasicNameValuePair("testResult", testData.shoujianjieguo));
                    nameValuePairs.add(new BasicNameValuePair("correctedTorque", testData.xiuzhengxiuju));
                    nameValuePairs.add(new BasicNameValuePair("correctedTime", testData.xiuzhengshijian));
                    nameValuePairs.add(new BasicNameValuePair("correctedResult", testData.xiuzhengjieguo));
                    nameValuePairs.add(new BasicNameValuePair("userID", testData.userId));
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    // Execute HTTP Post Request
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    String line = EntityUtils.toString(entity);
                    System.out.println(line);
                    bUpdate = true;
                }
            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                bUpdate = false;
            } catch (IOException e) {
                // TODO Auto-generated catch block
                bUpdate = false;
            }
        }

        @Override
        protected String[] doInBackground(Void... params) {
            postData();
            return new String[0];
        }

        protected void onPostExecute(String[] result) {
            //1.从远程服务器下载服务其中的数据库所有数据
            if(bUpdate) {
                new DownloadDataTask().execute();
                super.onPostExecute(result);
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "与服务器同步失败", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                showProgress(false);
            }
        }

    }

    public class DownloadDataTask extends AsyncTask<Void, Void, String[]> {
        String serverData = "";
        @Override
        protected String[] doInBackground(Void... params) {
            BufferedReader in = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                String uri = DataHolder.getServerAddress() + "/mobile/getAllServerData";
                request.setURI(new URI(uri));
                HttpResponse response = client.execute(request);
                in = new BufferedReader
                        (new InputStreamReader(response.getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }
                in.close();
                String page = sb.toString();
                System.out.println(page);
                serverData = page;
                //
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        protected void onPostExecute(String[] result) {
            //3.将得到的远程服务中的数据填充如本地数据库
            JSONObject jsonObject = null;
            boolean bUpdate = false;
            try {
                jsonObject = new JSONObject(serverData);
                bUpdate = true;
                //group
                JSONArray serverGroupDataList = jsonObject.getJSONArray("serverGroupData");
                //2.next把本地数据全部清空
                m_MyDatabaseAdapter.clearAllDatabaseData();
                for(int i = 0; i < serverGroupDataList.length(); i++) {
                    JSONObject groupData = (JSONObject) serverGroupDataList.get(i);
                    String groupId = groupData.getString("id");
                    String groupName = groupData.getString("name");
                    m_MyDatabaseAdapter.insertGroupTable(groupId, groupName);
                }
                //user
                JSONArray serverUserDataList = jsonObject.getJSONArray("serverUser");
                for(int i = 0; i < serverUserDataList.length(); i++) {
                    JSONObject userData = (JSONObject) serverUserDataList.get(i);
                    String userId = userData.getString("id");
                    String userName = userData.getString("name");
                    String userPhoto = userData.getString("photo");
                    String userPassword = userData.getString("password");
                    String userType = userData.getString("type");
                    String userGroupId = userData.getString("groupID");
                    m_MyDatabaseAdapter.insertUserTable(userId, userName, userPhoto, userPassword,
                            userType, userGroupId);
                }
                //bomstruct
                JSONArray serverBomStructDataList = jsonObject.getJSONArray("serverBomStruct");
                for(int i = 0; i < serverBomStructDataList.length(); i++) {
                    JSONObject bomStructData = (JSONObject) serverBomStructDataList.get(i);
                    String model = bomStructData.getString("model");
                    String partNo = bomStructData.getString("partNo");
                    String boltType = bomStructData.getString("boltType");
                    String partName = bomStructData.getString("partName");
                    String standardValue = bomStructData.getString("standardValue");
                    String valueRange = bomStructData.getString("valueRange");
                    String limitRange = bomStructData.getString("limitRange");
                    String workmanship = bomStructData.getString("workmanship");
                    String boltNum = bomStructData.getString("boltNum");
                    m_MyDatabaseAdapter.insertBomStructTable(model, partNo, boltType, partName,
                            standardValue, valueRange, limitRange, workmanship, boltNum);
                }
                //bomdata
                JSONArray serverBomDataDataList = jsonObject.getJSONArray("serverBomData");
                for(int i = 0; i < serverBomDataDataList.length(); i++) {
                    JSONObject bomDataData = (JSONObject) serverBomDataDataList.get(i);
                    String id = bomDataData.getString("id");
                    String vinCode = bomDataData.getString("vinCode");
                    String partCode = bomDataData.getString("partCode");
                    String partStation = bomDataData.getString("partStation");
                    String model = bomDataData.getString("model");
                    String partNo = bomDataData.getString("partNo");
                    String boltType = bomDataData.getString("boltType");
                    m_MyDatabaseAdapter.insertBomDataTable(id, vinCode, partCode, partStation,
                            model, partNo, boltType);
                }
                //testdata不能下载testdata
                /*JSONArray serverTestDataDataList = jsonObject.getJSONArray("serverTestData");
                for(int i = 0; i < serverTestDataDataList.length(); i++) {
                    JSONObject bomTestDataData = (JSONObject) serverTestDataDataList.get(i);
                    String id = bomTestDataData.getString("id");
                    String vinCode = bomTestDataData.getString("vinCode");
                    String partCode = bomTestDataData.getString("partCode");
                    String partStation = bomTestDataData.getString("partStation");
                    String testDate = bomTestDataData.getString("testDate");
                    String testTorque = bomTestDataData.getString("testTorque");
                    String testTime = bomTestDataData.getString("testTime");
                    String testResult = bomTestDataData.getString("testResult");
                    String correctedTorque = bomTestDataData.getString("correctedTorque");
                    String correctedTime = bomTestDataData.getString("correctedTime");
                    String correctedResult = bomTestDataData.getString("correctedResult");
                    String userID = bomTestDataData.getString("userID");
                    m_MyDatabaseAdapter.insertTestDataTable(id, vinCode, partCode, partStation,
                            testDate, testTorque, testTime, testResult, correctedTorque,
                            correctedTime, correctedResult, userID);
                }*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //4.退出当前账号，重新登陆
            showProgress(false);
            if(bUpdate) {
                SharedPreferences lastAsyncTime = getApplicationContext().getSharedPreferences("lastAsyncTime", 0);
                SharedPreferences.Editor editorlastAsyncTime = lastAsyncTime.edit();
                Calendar currentDate = Calendar.getInstance();
                Date todayDate = currentDate.getTime();
                SimpleDateFormat curCommentFormaterStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String resultCommentStr = "";
                resultCommentStr = curCommentFormaterStr.format(todayDate);
                editorlastAsyncTime.putString("lastAsyncTime", resultCommentStr);
                editorlastAsyncTime.apply();
                TextView lastSyncTimeTextView = (TextView)findViewById(R.id.lastsynctimetextview);
                lastSyncTimeTextView.setText("上次同步数据时间：" + resultCommentStr);
                Switch syncSwitch = (Switch)findViewById(R.id.syncstausswitch);
                syncSwitch.setChecked(false);
                logout();
            }

            super.onPostExecute(result);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int testDataCount = m_MyDatabaseAdapter.getTestDateCount();
        Switch syncSwitch = (Switch)findViewById(R.id.syncstausswitch);
        if(testDataCount == 0) {
            syncSwitch.setChecked(false);
        }
        else {
            syncSwitch.setChecked(true);
        }
    }

    public void saveGroup() {
        /*EditText saveGroupEdit = (EditText)findViewById(R.id.savegroupedit);
        SharedPreferences groupName = getApplicationContext().getSharedPreferences("groupName", 0);
        SharedPreferences.Editor editorGroupName = groupName.edit();
        editorGroupName.putString("groupName", saveGroupEdit.getText().toString());
        editorGroupName.apply();*/
    }

    public void saveBanshouId() {
        EditText saveBanshouEdit = (EditText)findViewById(R.id.savebanshouidedit);
        SharedPreferences banshouName = getApplicationContext().getSharedPreferences("banshouName", 0);
        SharedPreferences.Editor editorBanshouName = banshouName.edit();
        editorBanshouName.putString("banshouName", saveBanshouEdit.getText().toString());
        editorBanshouName.apply();

        Toast toast = Toast.makeText(getApplicationContext(),
                "扳手ID修改成功!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void saveBanshouWatchTime() {
        EditText banshouWatchTimeEdit = (EditText)findViewById(R.id.banshouwatchtimeedit);
        SharedPreferences banshouWatchTime = getApplicationContext().getSharedPreferences("banshouWatchTime", 0);
        SharedPreferences.Editor editorBanshouWatchTime = banshouWatchTime.edit();
        editorBanshouWatchTime.putString("banshouWatchTime", banshouWatchTimeEdit.getText().toString());
        editorBanshouWatchTime.apply();

        Toast toast = Toast.makeText(getApplicationContext(),
                "扳手监测时间修改成功!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void saveDataWatchTime() {
        EditText dataWatchTimeEdit = (EditText)findViewById(R.id.datawatchtimeedit);
        SharedPreferences dataWatchTime = getApplicationContext().getSharedPreferences("dataWatchTime", 0);
        SharedPreferences.Editor editorDataWatchTime = dataWatchTime.edit();
        editorDataWatchTime.putString("dataWatchTime", dataWatchTimeEdit.getText().toString());
        editorDataWatchTime.apply();

        Toast toast = Toast.makeText(getApplicationContext(),
                "数据同步周期修改成功!", Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public void saveAutoRefreshSwitch(Boolean bChecked) {
        SharedPreferences bAutoRefresh = getApplicationContext().getSharedPreferences("bAutoRefresh", 0);
        SharedPreferences.Editor editorDataWatchTime = bAutoRefresh.edit();
        editorDataWatchTime.putBoolean("bAutoRefresh", bChecked);
        editorDataWatchTime.apply();
    }
    //////////////////////////////////////////////////////////////////////////////
    private class ModifyServerPassword extends AsyncTask<Void, Void, String[]> {

        public void postData() {
            // Create a new HttpClient and Post Header
            String uri = DataHolder.getServerAddress() + "/mobile/modifyUserPassword/";
            HttpParams httpParams = new BasicHttpParams();
            httpParams.setParameter("charset", "UTF-8");
            HttpClient httpclient = new DefaultHttpClient(httpParams);
            HttpPost httppost = new HttpPost(uri);
            httppost.addHeader("charset", HTTP.UTF_8);
            try {
                  List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                  nameValuePairs.add(new BasicNameValuePair("userId", Integer.toString(DataHolder.getUserId())));
                  nameValuePairs.add(new BasicNameValuePair("newPassword", newPassword));

                  httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, "UTF-8"));
                    // Execute HTTP Post Request
                  HttpResponse response = httpclient.execute(httppost);

                  HttpEntity entity = response.getEntity();
                  String line = EntityUtils.toString(entity);
                  System.out.println(line);

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
            } catch (IOException e) {
                // TODO Auto-generated catch block
            }
        }

        @Override
        protected String[] doInBackground(Void... params) {
            postData();
            return null;
        }

        protected void onPostExecute(String[] result) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "密码修改成功", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            super.onPostExecute(result);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////////
    public void modifyUserPassword() {
        EditText oldPasswordEdit = (EditText)findViewById(R.id.settingsoldpassword);
        EditText newPasswordEdit = (EditText)findViewById(R.id.settingsnewpassword);
        EditText reNewPasswordEdit = (EditText)findViewById(R.id.settingsrenewpassword);
        String oldPassword = oldPasswordEdit.getText().toString();
        newPassword = newPasswordEdit.getText().toString();
        String reNewPassword = reNewPasswordEdit.getText().toString();
        if(!oldPassword.equals(DataHolder.getUserPassword())) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "密码不匹配", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else if(!newPassword.equals(reNewPassword)) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "两次输入的密码不一致", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else if(newPassword.equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "密码不能为空", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else{
            //modify user password
            Boolean bModifyUserPassword = m_MyDatabaseAdapter.updateUserPassword(newPassword, DataHolder.getUserId());
            //修改prefrase中的password
            SharedPreferences userPassword = getApplicationContext().getSharedPreferences("userPassword", 0);
            SharedPreferences.Editor editorUserPassword = userPassword.edit();
            editorUserPassword.putString("userPassword", newPassword);
            editorUserPassword.apply();
            //
            if(bModifyUserPassword) {
                new ModifyServerPassword().execute();
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "密码修改失败", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
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
                Intent openMainActivity= new Intent(this, ScanChassisActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(openMainActivity);
                return true;
            case R.id.action_history_menu:
                Intent historyActivity= new Intent(this, ScanHistory.class);
                historyActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(historyActivity);
                return true;
            case R.id.action_setting_menu:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void logout() {
        Intent intent = new Intent(this, LoginActivity.class);
        //finish all tasks API11+
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        this.finish();
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
