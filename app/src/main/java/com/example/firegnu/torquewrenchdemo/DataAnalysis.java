package com.example.firegnu.torquewrenchdemo;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
/**
 * Created by firegnu on 15-1-29.
 */
public class DataAnalysis {
    static HashMap<String,String> map = new HashMap<String,String>();
    /*public static void main(String args[]){
        String ReceiveData = "01 08 9E 03 03 02 0F 09 0A 0E 23 07 D1 00 01";
        map = toData(ReceiveData);
        Iterator<String> it = map.keySet().iterator();
        while(it.hasNext()) {
            String key = (String)it.next();
            System.out.print(key + ":" + map.get(key) + "\n");
        }
    }*/
    public static HashMap<String, String> toData(String data){
        map.clear();
        String array[] = data.split(" ");
        //预置值编号
        int preValueNum = Integer.parseInt(array[0],16);
        map.put("预置值编号", String.valueOf(preValueNum));
        //单位
        int unit = Integer.valueOf(array[4]);
        String unitName = null;
        if(unit == 0){
            unitName = "N";
        }
        else if(unit == 3){
            unitName = "N.m";
        }
        map.put("单位", unitName);
        //精度范围
        int precision = Integer.valueOf(array[5],16);
        String precisionRange = "+/-" + precision + "%";
        map.put("精度范围", precisionRange);
        //日期
        int yearNum = Integer.valueOf(array[6],16);
        String year = null;
        if(yearNum < 10){
            year = "200" + yearNum;
        }
        else{
            year = "20" + yearNum;
        }
        int month = Integer.valueOf(array[7],16);
        int day = Integer.valueOf(array[8],16);
        String date = year + "." + month + "." + day;
        map.put("日期", date);
        //时间
        int hour = Integer.valueOf(array[9],16);
        String Hour = hour + "";
        if(hour < 10){
            Hour = "0" + hour;
        }
        int minute = Integer.valueOf(array[10],16);
        String Minute = minute + "";
        if(hour < 10){
            Minute = "0" + minute;
        }
        String time = Hour + ":" + Minute;
        map.put("时间", time);
        //预置值实测值差值
        float preValue = Integer.valueOf(array[1] + array[2],16);
        float peakValue = Integer.valueOf(array[11] + array[12],16);
        int pointPos = Integer.parseInt(array[3]);
        float preValue_f = 0;
        float peakValue_f = 0;
        float d_value = 0;
        String preValue_s = null;
        String peakValue_s = null;
        String d_value_s = null;
        if(pointPos == 1){
            preValue_f = preValue/10000;
            peakValue_f = peakValue/10000;
            DecimalFormat decimalFormat = new DecimalFormat(".0000");
            preValue_s = decimalFormat.format(preValue_f);
            peakValue_s = decimalFormat.format(peakValue_f);
            d_value = peakValue_f - preValue_f;
            d_value_s = decimalFormat.format(d_value);
        }
        else if(pointPos == 2){
            preValue_f = preValue/1000;
            peakValue_f = peakValue/1000;
            DecimalFormat decimalFormat = new DecimalFormat(".000");
            preValue_s = decimalFormat.format(preValue_f);
            peakValue_s = decimalFormat.format(peakValue_f);
            d_value = peakValue_f - preValue_f;
            d_value_s = decimalFormat.format(d_value);
        }
        else if(pointPos == 3){
            preValue_f = preValue/100;
            peakValue_f = peakValue/100;
            DecimalFormat decimalFormat = new DecimalFormat(".00");
            preValue_s = decimalFormat.format(preValue_f);
            peakValue_s = decimalFormat.format(peakValue_f);
            d_value = peakValue_f - preValue_f;
            d_value_s = decimalFormat.format(d_value);
        }
        else if(pointPos == 4){
            preValue_f = preValue/10;
            peakValue_f = peakValue/10;
            DecimalFormat decimalFormat = new DecimalFormat(".0");
            preValue_s = decimalFormat.format(preValue_f);
            peakValue_s = decimalFormat.format(peakValue_f);
            d_value = peakValue_f - preValue_f;
            d_value_s = decimalFormat.format(d_value);
        }
        else if(pointPos == 5){
            preValue_s = String.valueOf((int)preValue);
            peakValue_s = String.valueOf((int)peakValue);
            d_value_s = String.valueOf((int)peakValue - (int)preValue);
        }
        map.put("预置值", preValue_s);
        map.put("实测值", peakValue_s);
        map.put("差值", d_value_s);
        //峰值编号
        int peakValueNum = Integer.valueOf(array[13] + array[14],16);
        map.put("峰值编号", String.valueOf(peakValueNum));
        //误差，结论
        float error = Float.parseFloat(d_value_s)/Float.parseFloat(preValue_s)*100;
        DecimalFormat decimalFormat1 = new DecimalFormat("0.00");
        String Error = decimalFormat1.format(error) + "%";
        map.put("误差", Error);
        String result = null;
        if(error > precision || error < -precision){
            if(Float.parseFloat(d_value_s) > 0){
                result = "正超差";
            }
            else{
                result = "负超差";
            }
        }
        else{
            result = "合格";
        }
        map.put("结论", result);


        return map;
    }
}
