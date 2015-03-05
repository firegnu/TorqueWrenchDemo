package com.example.firegnu.torquewrenchdemo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
/**
 * Created by firegnu on 15-1-29.
 */
public class DataAnalysis {
    static HashMap<String, String> map = new HashMap<String, String>();

    /*public static void main(String args[]){
        String ReceiveData = "01 08 9E 03 03 02 0F 09 0A 0E 23 07 D1 00 01";
        map = toData(ReceiveData);
        Iterator<String> it = map.keySet().iterator();
        while(it.hasNext()) {
            String key = (String)it.next();
            System.out.print(key + ":" + map.get(key) + "\n");
        }
    }*/
    public static HashMap<String, String> toData(String data) {
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
        int monthNum = Integer.valueOf(array[7],16);
        String month = null;
        if(monthNum < 10){
            month = "0" + monthNum;
        }
        else{
            month = monthNum + "";
        }
        int dayNum = Integer.valueOf(array[8],16);
        String day = null;
        if(dayNum < 10){
            day = "0" + dayNum;
        }
        else{
            day = dayNum + "";
        }
        String date = year + "-" + month + "-" + day;
        map.put("日期", date);
        //时间
        int hour = Integer.valueOf(array[9],16);
        String Hour = hour + "";
        if(hour < 10){
            Hour = "0" + hour;
        }
        int minute = Integer.valueOf(array[10],16);
        String Minute = minute + "";
        if(minute < 10){
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
                result = "超上限";
            }
            else{
                result = "超下限";
            }
        }
        else{
            result = "合格";
        }
        map.put("结论", result);


        return map;
    }

    public static String strDeal(String str){
        if(str.length()<2){
            return "0" + str;
        }
        else if(str.length()>2){
            return str.substring(str.length()-2, str.length());
        }
        else {
            return str;
        }
    }

    public static String strDeal1(String str){
        if(str.length() == 1){
            return "000" + str;
        }
        else if(str.length() == 2){
            return "00" + str;
        }
        else if(str.length() == 3){
            return "0" + str;
        }
        else{
            return str;
        }
    }

    public static String adjustTime(){
        SimpleDateFormat formatter = new SimpleDateFormat ("yyyy MM dd HH mm ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        String array[] = str.split(" ");
        String year = strDeal(Integer.toHexString(Integer.parseInt(array[0])%2000));
        String month = strDeal(Integer.toHexString(Integer.parseInt(array[1])));
        String day = strDeal(Integer.toHexString(Integer.parseInt(array[2])));
        String hour = strDeal(Integer.toHexString(Integer.parseInt(array[3])));
        String minute = strDeal(Integer.toHexString(Integer.parseInt(array[4])));
        int checkSum = 221 + Integer.parseInt(array[0])%2000 + Integer.parseInt(array[1]) + Integer.parseInt(array[2]) + Integer.parseInt(array[3]) + Integer.parseInt(array[4]);
        String checkByte = strDeal(Integer.toHexString(checkSum));
        return "55aadd"+year+month+day+hour+minute+checkByte+"5a5a";
    }

    public static String sendTechnicsData(int num, float max, float min){
        String dataNum = strDeal(Integer.toHexString(num));
        int fIntMax = (int) max;
        BigDecimal max1 = new BigDecimal(Float.toString(max));
        BigDecimal max2 = new BigDecimal(Integer.toString(fIntMax));
        float fPointMax = max1.subtract(max2).floatValue();

        String max_fInt = Integer.toHexString(fIntMax);
        String strMax = null;
        if(fPointMax == 0.0){
            strMax = strDeal1(max_fInt) + "05";
        }
        else if(max_fInt.length() == 1 && fIntMax == 0){
            strMax = Integer.toHexString((int) fPointMax*16) + Integer.toHexString((int) (fPointMax - (int) fPointMax)*16) + Integer.toHexString((int) (fPointMax*16 - (int) fPointMax*16)*16) + Integer.toHexString((int) (fPointMax*16*16 - (int) fPointMax*16*16)*16) + "01";
        }
        else if(max_fInt.length() == 1 && fIntMax != 0){
            strMax = max_fInt + Integer.toHexString((int) fPointMax*16) + Integer.toHexString((int) (fPointMax - (int) fPointMax)*16) + Integer.toHexString((int) (fPointMax*16 - (int) fPointMax*16)*16) + "02";
        }
        else if(max_fInt.length() == 2){
            strMax = max_fInt + Integer.toHexString((int) fPointMax*16) + Integer.toHexString((int) (fPointMax - (int) fPointMax)*16) + "03";
        }
        else if(max_fInt.length() == 3){
            strMax = max_fInt + Integer.toHexString((int) fPointMax*16) + "04";
        }

        int fIntMin = (int) min;
        BigDecimal min1 = new BigDecimal(Float.toString(min));
        BigDecimal min2 = new BigDecimal(Integer.toString(fIntMin));
        float fPointMin = min1.subtract(min2).floatValue();

        String min_fInt = Integer.toHexString(fIntMin);
        String strMin = null;
        if(fPointMin == 0){
            strMin = strDeal1(min_fInt) + "05";
        }
        else if(min_fInt.length() == 1 && fIntMin == 0){
            strMin = Integer.toHexString((int) fPointMin*16) + Integer.toHexString((int) (fPointMin - (int) fPointMin)*16) + Integer.toHexString((int) (fPointMin*16 - (int) fPointMin*16)*16) + Integer.toHexString((int) (fPointMin*16*16 - (int) fPointMin*16*16)*16) + "01";
        }
        else if(min_fInt.length() == 1 && fIntMin != 0){
            strMin = min_fInt + Integer.toHexString((int) fPointMin*16) + Integer.toHexString((int) (fPointMin - (int) fPointMin)*16) + Integer.toHexString((int) (fPointMin*16 - (int) fPointMin*16)*16) + "02";
        }
        else if(min_fInt.length() == 2){
            strMin = min_fInt + Integer.toHexString((int) fPointMin*16) + Integer.toHexString((int) (fPointMin - (int) fPointMin)*16) + "03";
        }
        else if(min_fInt.length() == 3){
            strMin = min_fInt + Integer.toHexString((int) fPointMin*16) + "04";
        }

        int checkSum = 241 + num + Integer.valueOf(strMax.substring(0, 2),16) + Integer.valueOf(strMax.substring(2, 4),16) + Integer.valueOf(strMax.substring(4, 6),16) + Integer.valueOf(strMin.substring(0, 2),16) + Integer.valueOf(strMin.substring(2, 4),16) + Integer.valueOf(strMin.substring(4, 6),16);
        String checkByte = strDeal(Integer.toHexString(checkSum));

        return "55aaf1"+dataNum+strMax+strMin+checkByte+"5a5a";
    }
}
