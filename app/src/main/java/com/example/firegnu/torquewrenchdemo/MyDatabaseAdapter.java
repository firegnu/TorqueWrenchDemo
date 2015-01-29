package com.example.firegnu.torquewrenchdemo;

/**
 * Created by firegnu on 15-1-26.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.IOException;

/**
 *
 * 数据增删改查
 *
 * @author renlq
 *
 */
public class MyDatabaseAdapter {

    // 数据库名称
    private static final String DB_NAME = "torquewrench.sqlite";
    public static final String T_GROUP = "_group";
    public static final String T_BOMDATA = "bomdata";
    public static final String T_BOMSTRUCT = "bomstruct";
    public static final String T_TESTDATA = "testdata";
    public static final String T_USER = "user";

    private static SQLiteDatabase mSqLiteDatabase = null;
    private Context mContext = null;

    private static MyDatabaseAdapter m_MyDatabaseAdapter;

    public static MyDatabaseAdapter getDatabaseAdapter(Context context){
        if (m_MyDatabaseAdapter==null){
            m_MyDatabaseAdapter = new MyDatabaseAdapter();
        }
        //mContext = context;
        if (mSqLiteDatabase == null){
            open(context);
        }
        return m_MyDatabaseAdapter;
    }
    public MyDatabaseAdapter() {
    }
    public MyDatabaseAdapter(Context context) {
        mContext = context;
    }
    // 打开数据库
    public static void open(Context context) throws SQLException {
        DataBaseHelper myDbHelper = new DataBaseHelper(context, DB_NAME);
        try {
            if (mSqLiteDatabase != null){
                mSqLiteDatabase.close();
            }else
                mSqLiteDatabase = myDbHelper.openOrCreateDataBase("");
        } catch (IOException e) {
            mSqLiteDatabase = null;
            e.printStackTrace();
        }
    }
    // 打开数据库
    public void open() throws SQLException {
        DataBaseHelper myDbHelper = new DataBaseHelper(mContext, DB_NAME);
        try {
            if (mSqLiteDatabase != null){
                mSqLiteDatabase.close();
            }else
                mSqLiteDatabase = myDbHelper.openOrCreateDataBase("");
        } catch (IOException e) {
            mSqLiteDatabase = null;
            e.printStackTrace();
        }
    }

    // 关闭数据库
    public void close() {
        mSqLiteDatabase.close();
    }

    //get all users infomations
    public Cursor getAllUsersInfo() {
        Cursor cursor = mSqLiteDatabase.rawQuery("select * from " + T_USER, null);
        return cursor;
    }

    //modify user password
    public boolean updateUserPassword(String newPassword, int userId) {
        ContentValues con = new ContentValues();
        con.put("password", newPassword);
        return mSqLiteDatabase.update(T_USER, con, "id=" + userId, null) > 0;
    }

    //通过扫描底盘VIN码得到相关数据
    public String getInfoFromVinCode(String vinCode) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select distinct model from " + T_BOMDATA + " where vinCode=" + vinCode, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }
        return "";
    }

    //根据所属车辆型号查询其包含的零件名称
    public Cursor getPartListfromCarModel(String carModel) {
        Cursor cur = mSqLiteDatabase.rawQuery("select distinct partName from " + T_BOMSTRUCT + " where model=" + carModel, null);
        return cur;
    }

    //根据扫描得到的零件号得到该零件的相关信息
    public Cursor getPartInfoFromPartNo(String partCode, String vinCode, String model) {
        //return cursor;
        //String sqlStr = "select distinct partNo from " + T_BOMDATA + " where partCode = " + "'E52581-P001'" + " and vinCode = " +"'E52581'";
        String sqlStr = "select distinct partNo from " + T_BOMDATA + " where partCode = " + partCode + " and vinCode = " +vinCode;
        Cursor cur = mSqLiteDatabase.rawQuery(sqlStr, null);
        String partNoCur = "";
        if (cur != null) {
            if (cur.moveToFirst()) {
                partNoCur = cur.getString(0);
            }
        }
        //String sqlStr1 = "select * from " + T_BOMSTRUCT + " where partNo = " + "'SF-lj-ZQ' " + "and model = " + "'J6P6*4'";
        String sqlStr1 = "select * from " + T_BOMSTRUCT + " where partNo = " + "'" + partNoCur + "'" + " and model = " + model;
        Cursor curSor = mSqLiteDatabase.rawQuery(sqlStr1, null);
        return curSor;
    }

    //得到检测数据
    public Cursor getTestResult() {
        Cursor cursor = mSqLiteDatabase.rawQuery("select vinCode, partCode, partStation, testTorque, testTime, testResult, correctedTorque, correctedTime, correctedResult, userID, testDate  from " + T_TESTDATA, null);
        return cursor;
    }

    //通过底盘VIN码得到车辆型号
    public String getCarModelFromVinCode(String vinCode) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select model from " + T_BOMDATA + " where vinCode=" + vinCode, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }
        return "";
    }

    //通过零件编号得到零件型号
    public String getPartNoFromCode(String partCode) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select partNo from " + T_BOMDATA + " where partCode = " + partCode, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }
        return "";
    }

    //通过零件型号得到零件名称
    public String getPartNameFromPartNo(String partCode) {
        String partNo = "'" + getPartNoFromCode(partCode) + "'";
        Cursor cursor = mSqLiteDatabase.rawQuery("select partName from " + T_BOMSTRUCT + " where partNo=" + partNo, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }
        return "";
    }

    //通过UserID得到用户名称
    public String getUserNameFromUserId(int userId) {
        Cursor cursor = mSqLiteDatabase.rawQuery("select name from " + T_USER + " where id=" + userId, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        }
        return "";
    }

    //上传功能得到所有本地的检测数据
    public Cursor getLocalTestResult() {
        Cursor cursor = mSqLiteDatabase.rawQuery("select vinCode, partCode, partStation, testDate ,testTorque, testTime, testResult, correctedTorque, correctedTime, correctedResult, userID  from " + T_TESTDATA, null);
        return cursor;
    }

    //清空本地数据库所有数据
    public boolean clearAllDatabaseData() {
        mSqLiteDatabase.delete(T_GROUP, null, null);
        mSqLiteDatabase.delete(T_BOMDATA, null, null);
        mSqLiteDatabase.delete(T_BOMSTRUCT, null, null);
        mSqLiteDatabase.delete(T_TESTDATA, null, null);
        mSqLiteDatabase.delete(T_USER, null, null);
        return true;
    }

    //insert data to _group table
    public long insertGroupTable(String groupId, String name) {
        ContentValues init = new ContentValues();
        init.put("id", groupId);
        init.put("name", name);
        return mSqLiteDatabase.insert(T_GROUP, null, init);
    }

    //insert data to user table
    public long insertUserTable(String id, String name, String photo, String password, String type, String groupID) {
        ContentValues init = new ContentValues();
        init.put("id", id);
        init.put("name", name);
        init.put("photo", photo);
        init.put("password", password);
        init.put("type", type);
        init.put("groupID", groupID);
        return mSqLiteDatabase.insert(T_USER, null, init);
    }

    //insert data to bomstruct table
    public long insertBomStructTable(String model, String partNo, String boltType, String partName,
                                     String standardValue, String valueRange, String limitRange, String workmanship, String boltNum) {
        ContentValues init = new ContentValues();
        init.put("model", model);
        init.put("partNo", partNo);
        init.put("boltType", boltType);
        init.put("partName", partName);
        init.put("standardValue", standardValue);
        init.put("valueRange", valueRange);
        init.put("limitRange", limitRange);
        init.put("workmanship", workmanship);
        init.put("boltNum", boltNum);
        return mSqLiteDatabase.insert(T_BOMSTRUCT, null, init);
    }

    //insert data to bomdata table
    public long insertBomDataTable(String id, String vinCode, String partCode, String partStation,
                                   String model, String partNo, String boltType) {
        ContentValues init = new ContentValues();
        init.put("id", id);
        init.put("vinCode", vinCode);
        init.put("partCode", partCode);
        init.put("partStation", partStation);
        init.put("model", model);
        init.put("partNo", partNo);
        init.put("boltType", boltType);
        return mSqLiteDatabase.insert(T_BOMDATA, null, init);
    }

    //insert data to testdata table
    public long insertTestDataTable(String id, String vinCode, String partCode, String partStation,
                                   String testDate, String testTorque, String testTime, String testResult, String correctedTorque,
                                   String correctedTime, String correctedResult, String userID) {
        ContentValues init = new ContentValues();
        init.put("id", id);
        init.put("vinCode", vinCode);
        init.put("partCode", partCode);
        init.put("partStation", partStation);
        init.put("testDate", testDate);
        init.put("testTorque", testTorque);
        init.put("testTime", testTime);
        init.put("testResult", testResult);
        init.put("correctedTorque", correctedTorque);
        init.put("correctedTime", correctedTime);
        init.put("correctedResult", correctedResult);
        init.put("userID", userID);
        return mSqLiteDatabase.insert(T_TESTDATA, null, init);
    }

    public long insertTestDataTableBlueTooth(String vinCode, String partCode, String partStation,
                                             String testDate, String testTorque, String testTime, String testResult, String correctedTorque,
                                             String correctedTime, String correctedResult, String userID)
    {
        ContentValues init = new ContentValues();
        init.put("vinCode", vinCode);
        init.put("partCode", partCode);
        init.put("partStation", partStation);
        init.put("testDate", testDate);
        init.put("testTorque", testTorque);
        init.put("testTime", testTime);
        init.put("testResult", testResult);
        init.put("correctedTorque", correctedTorque);
        init.put("correctedTime", correctedTime);
        init.put("correctedResult", correctedResult);
        init.put("userID", userID);
        return mSqLiteDatabase.insert(T_TESTDATA, null, init);
    }

    public boolean updateTestDataTableBlueTooth(String vinCode, String partCode, String partStation,
                                                String correctedTestDate, String correctedTestTorque, String correctedTestTime,
                                                String resultTime) {
        ContentValues con = new ContentValues();
        con.put("testDate", resultTime);
        con.put("correctedTorque", correctedTestDate);
        con.put("correctedTime", correctedTestTorque);
        con.put("correctedResult", correctedTestTime);
        String sqlStr = "vinCode=" + "'" +vinCode + "'" + " and partCode = " + "'" +partCode + "'" + " and partStation = " + "'" +partStation + "'";
        return mSqLiteDatabase.update(T_TESTDATA, con, sqlStr, null) > 0;

    }



    public Cursor getGongweiCount(String partCode, String vinCode) {
        //select partStation, boltType from torquewrench.bomdata where partCode = 'E52581-P001' and vinCode = 'E52581';
        //String sqlStr = "select partStation from " + T_BOMDATA + " where partCode = " + "'E52581-P001' and vinCode = " + "'E52581' " + "order by id";
        String sqlStr = "select partStation from " + T_BOMDATA + " where partCode = " + partCode + " and vinCode = " + vinCode + " order by id";
        Cursor cursor = mSqLiteDatabase.rawQuery(sqlStr, null);
        return cursor;
    }
    //从testdata表中查找相关数据
    public Cursor getTestDataForGongwei(String gongweiName, String partCode, String vinCode) {
        String sqlStr = "select testTorque, testTime, testResult, correctedTorque, correctedTime, correctedResult from " +
                T_TESTDATA + " where vinCode = " + vinCode + " and partCode = " + partCode + " and partStation = " + gongweiName;
        Cursor cursor = mSqLiteDatabase.rawQuery(sqlStr, null);
        return cursor;

    }
}
