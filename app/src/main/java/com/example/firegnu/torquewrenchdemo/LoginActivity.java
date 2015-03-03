package com.example.firegnu.torquewrenchdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class LoginActivity extends Activity {

    final Context context = this;

    MyDatabaseAdapter m_MyDatabaseAdapter;
    Cursor cur = null;
    private List<Integer> idList = new ArrayList<Integer>();
    private List<String> nameList = new ArrayList<String>();
    private List<String> photoList = new ArrayList<String>();
    private List<String> passwordList = new ArrayList<String>();
    private List<Integer> typeList = new ArrayList<Integer>();
    private List<Integer> groupIdList = new ArrayList<Integer>();
    private int userIndex = 0;
    TextView userNameTextView;
    ImageView publisherPhotoView;
    private View mProgressView;
    //

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initAllDefaultParameter();
        m_MyDatabaseAdapter = MyDatabaseAdapter.getDatabaseAdapter(this);
        mProgressView = findViewById(R.id.login_progress);

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

        Button addServerAddressButton = (Button) findViewById(R.id.addserveraddress);
        addServerAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater li = LayoutInflater.from(context);
                View promptsView = li.inflate(R.layout.ipserverprompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);
                //
                SharedPreferences settings = getApplicationContext().getSharedPreferences("serveraddress", 0);
                String serverAddress = settings.getString("serveraddress", "");
                userInput.setText(serverAddress);
                //
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("确定",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        //set server address
                                        SharedPreferences settings = getApplicationContext().getSharedPreferences("serveraddress", 0);
                                        SharedPreferences.Editor editor = settings.edit();
                                        editor.putString("serveraddress", userInput.getText().toString());
                                        editor.apply();
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        Display display = getWindowManager().getDefaultDisplay();
        int height = display.getHeight() - 2 * getActionBarHeight();
        int halfSizeHeightDp = px2dip(this, height);
        ImageView view = (ImageView) findViewById(R.id.userlogo);
        LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(halfSizeHeightDp / 4, halfSizeHeightDp / 4);
        parms.setMargins(0, 0,
                0, 0);
        parms.gravity = Gravity.CENTER;
        view.setLayoutParams(parms);

        SharedPreferences settingsGroupName = getApplicationContext().getSharedPreferences("groupId", 0);
        int definedGroupId = 0;
        int groupIdSelected = settingsGroupName.getInt("groupId", definedGroupId);
        //初次进入默认选择第一组
        if(groupIdSelected == 0) {
            SharedPreferences groupId = getApplicationContext().getSharedPreferences("groupId", 0);
            SharedPreferences.Editor editorBanshouName = groupId.edit();
            editorBanshouName.putInt("groupId", 0);
            editorBanshouName.apply();
        }
        //
        cur = m_MyDatabaseAdapter.getAllUsersInfo();
        if (cur != null) {
            if (cur.moveToFirst()) {
                do {
                    int id = cur.getInt(0);
                    String name = cur.getString(1);
                    String photo = cur.getString(2);
                    String password = cur.getString(3);
                    int type = cur.getInt(4);
                    int groupID = cur.getInt(5);
                    if(groupID == (groupIdSelected + 1)) {
                        idList.add(id);
                        nameList.add(name);
                        photoList.add(photo);
                        passwordList.add(password);
                        typeList.add(type);
                        groupIdList.add(groupID);
                    }
                } while (cur.moveToNext());
            }
            cur.close();
        }
        SharedPreferences settings = getApplicationContext().getSharedPreferences("serveraddress", 0);
        String serverAddress = settings.getString("serveraddress", "");
        DataHolder.setServerAddress(serverAddress);
        userNameTextView = (TextView) findViewById(R.id.username);
        publisherPhotoView = (ImageView)findViewById(R.id.userlogo);
        if (nameList.size() == 0) {
            userNameTextView.setText("数据库为空");
            //loginButton.setEnabled(false);
            updateEmptyDatabase();
        } else {
            userNameTextView.setText(nameList.get(userIndex).toString());
            if(photoList.get(userIndex) != null) {
                Picasso.with(this).load("http://" + DataHolder.getServerAddress() + photoList.get(userIndex).toString()).into(publisherPhotoView);
            }
        }
        //
        SharedPreferences settingsLastSyncTime = getApplicationContext().getSharedPreferences("lastAsyncTime", 0);
        String lastSyncTime = settingsLastSyncTime.getString("lastAsyncTime", "");
        if(lastSyncTime.equals("")) {
            loginButton.setText("与服务器同步数据");
        }
    }

    //set default value
    public void initAllDefaultParameter() {
        SharedPreferences settingsGroupName = getApplicationContext().getSharedPreferences("groupName", 0);
        String groupName = settingsGroupName.getString("groupName", "");
        if(groupName.equals("")) {
            SharedPreferences.Editor editorGroupName = settingsGroupName.edit();
            editorGroupName.putString("groupName", "01班组");
            editorGroupName.apply();
        }

        SharedPreferences settingsBanshouName = getApplicationContext().getSharedPreferences("banshouName", 0);
        String banshouName = settingsBanshouName.getString("banshouName", "");
        if(banshouName.equals("")) {
            SharedPreferences.Editor editorBanshouName = settingsBanshouName.edit();
            editorBanshouName.putString("banshouName", "18:7A:93:04:A3:4A");
            editorBanshouName.apply();
        }

        SharedPreferences settingsBanshouWatchTime = getApplicationContext().getSharedPreferences("banshouWatchTime", 0);
        String banshouWatchTime = settingsBanshouWatchTime.getString("banshouWatchTime", "");
        if(banshouWatchTime.equals("")) {
            SharedPreferences.Editor editorBanshouWatchTime = settingsBanshouWatchTime.edit();
            editorBanshouWatchTime.putString("banshouWatchTime", "10");
            editorBanshouWatchTime.apply();
        }

        SharedPreferences settingsDataWatchTime = getApplicationContext().getSharedPreferences("dataWatchTime", 0);
        String dataWatchTime = settingsDataWatchTime.getString("dataWatchTime", "");
        if(dataWatchTime.equals("")) {
            SharedPreferences.Editor editorDataWatchTime = settingsDataWatchTime.edit();
            editorDataWatchTime.putString("dataWatchTime", "7");
            editorDataWatchTime.apply();
        }
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
        if (userIndex > 0) {
            userIndex -= 1;
            userNameTextView.setText(nameList.get(userIndex).toString());
            Picasso.with(this).load("http://" + DataHolder.getServerAddress() + photoList.get(userIndex).toString()).into(publisherPhotoView);
        }
    }

    public void nextUser() {
        if (userIndex < idList.size() - 1) {
            userIndex += 1;
            userNameTextView.setText(nameList.get(userIndex).toString());
            Picasso.with(this).load("http://" + DataHolder.getServerAddress() + photoList.get(userIndex).toString()).into(publisherPhotoView);
        }
    }

    public void updateEmptyDatabase() {

    }

    public void attempLogin() {

        SharedPreferences settings = getApplicationContext().getSharedPreferences("serveraddress", 0);
        String serverAddress = settings.getString("serveraddress", "");

        SharedPreferences settingsLastSyncTime = getApplicationContext().getSharedPreferences("lastAsyncTime", 0);
        String lastSyncTime = settingsLastSyncTime.getString("lastAsyncTime", "");
        boolean bSyncing = false;
        if(serverAddress.equals("")) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "服务器地址不能为空", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            bSyncing = true;
        }
        else {
            if(lastSyncTime.equals("")) {
                DataHolder.setServerAddress("http://" + serverAddress);
                showProgress(true);
                asyncDataFromServer();
                bSyncing = true;
            }
        }

        if(!bSyncing) {
            EditText passwordText = (EditText) findViewById(R.id.password);
            String password = passwordText.getText().toString();

            if (password.equals(passwordList.get(userIndex))) {
                DataHolder.setServerAddress("http://" + serverAddress);
                DataHolder.setUserName(nameList.get(userIndex));
                DataHolder.setUserId(idList.get(userIndex));
                DataHolder.setUserPassword(password);
                DataHolder.setUserType(typeList.get(userIndex));
                if(!lastSyncTime.equals("")) {
                    SharedPreferences settingsAutoRefresh = getApplicationContext().getSharedPreferences("bAutoRefresh", 0);
                    Boolean bDataRefresh = settingsAutoRefresh.getBoolean("bAutoRefresh", false);

                    SharedPreferences settingsDataWatchTime = getApplicationContext().getSharedPreferences("dataWatchTime", 0);
                    String dataWatchTime = settingsDataWatchTime.getString("dataWatchTime", "");
                    long dayOff = calculateDiffDay(lastSyncTime);
                    if(bDataRefresh && !dataWatchTime.equals("") && dayOff > Integer.parseInt(dataWatchTime)) {
                        showProgress(true);
                        asyncDataFromServer();
                    }
                    else {
                        Intent intent = new Intent(LoginActivity.this, ScanChassisActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "密码错误", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    //Sync Class
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
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "与服务器同步失败", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                showProgress(false);
            }
            super.onPostExecute(result);
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
                //testdata
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

            /*Intent intent = new Intent(LoginActivity.this, ScanChassisActivity.class);
            startActivity(intent);
            finish();*/
                logout();
            }
            else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "与服务器同步失败", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            super.onPostExecute(result);
        }
    }

    public long calculateDiffDay(String day1) {
        SimpleDateFormat curFormater = new SimpleDateFormat("yyyy-MM-dd");
        long dayDiff = 0;
        try {
            Calendar currentDate = Calendar.getInstance();
            currentDate.set(Calendar.MILLISECOND, 0);
            Date todayDate = currentDate.getTime();
            SimpleDateFormat curCommentFormaterStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String resultCommentStr = "";
            resultCommentStr = curCommentFormaterStr.format(todayDate);
            Date dateObj = curFormater.parse(resultCommentStr);
            dateObj.setHours(0);
            dateObj.setMinutes(0);
            dateObj.setSeconds(0);
            //
            Date dateObjBegin = curFormater.parse(day1);
            dateObjBegin.setHours(0);
            dateObjBegin.setMinutes(0);
            dateObjBegin.setSeconds(0);
            //
            long millsPerDay = 1000 * 60 * 60 * 24;
            if(( dateObj.getTime() - dateObjBegin.getTime() ) / millsPerDay < 0) {
                dayDiff = ( dateObj.getTime() - dateObjBegin.getTime() ) / millsPerDay - 1;
            }
            else {
                dayDiff = ( dateObj.getTime() - dateObjBegin.getTime() ) / millsPerDay + 1;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayDiff;
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
}
