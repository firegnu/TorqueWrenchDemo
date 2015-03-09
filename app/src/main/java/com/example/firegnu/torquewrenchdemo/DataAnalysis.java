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
        //日期时间
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
        String date = year + "-" + month + "-" + day;
        String time = Hour + ":" + Minute + ":00";
        map.put("日期", date);
        map.put("时间", time);
        //预置值实测值差值
        float preValue = Integer.valueOf(array[1] + array[2],16);
        String _peakValue16 = Integer.valueOf(array[11] + array[12],16).toString();
        Integer _peakValue10 = Integer.valueOf(_peakValue16,10);
        String _peakValue2 = Integer.toBinaryString(_peakValue10);
        if(_peakValue2.length() < 16) {
            String bu = "";
            for(int i = 0;i<16-_peakValue2.length();i++){
                bu = "0" + bu;
            }
            _peakValue2 = bu + _peakValue2;
        }
        //System.out.print(_peakValue2 + "\n");
        //System.out.print(_peakValue2.substring(0, 1) + "\n");
        float peakValue = 0;
        if(_peakValue2.substring(0, 1).equalsIgnoreCase("0")){
            peakValue = Integer.valueOf(array[11] + array[12],16);
        }
        else if(_peakValue2.substring(0, 1).equalsIgnoreCase("1")){
            String result_new = "";
            for(int i=0;i < _peakValue2.length();i++){
                String temp = "";
                if(_peakValue2.substring(i, i+1).equalsIgnoreCase("0")){
                    temp = "1";
                }
                else if(_peakValue2.substring(i, i+1).equalsIgnoreCase("1")){
                    temp = "0";
                }
                result_new = result_new + temp;
            }
            //System.out.print(result_new + "\n");
            Integer a = Integer.parseInt(Integer.valueOf(result_new,2).toString()) + 1;
            //System.out.print(a + "\n");
            String b = Integer.toHexString(a);
            peakValue = Integer.valueOf(b,16);
            //System.out.print(peakValue + "\n");
        }
        //int pointPos = Integer.parseInt(array[3]);
        float preValue_f = 0;
        float peakValue_f = 0;
        float d_value = 0;
        String preValue_s = null;
        String peakValue_s = null;
        String d_value_s = null;
//		if(pointPos == 1){
//			preValue_f = preValue/10000;
//			peakValue_f = peakValue/10000;
//			DecimalFormat decimalFormat = new DecimalFormat(".0000");
//			preValue_s = decimalFormat.format(preValue_f);
//			peakValue_s = decimalFormat.format(peakValue_f);
//			d_value = peakValue_f - preValue_f;
//			d_value_s = decimalFormat.format(d_value);
//		}
//		else if(pointPos == 2){
//			preValue_f = preValue/1000;
//			peakValue_f = peakValue/1000;
//			DecimalFormat decimalFormat = new DecimalFormat(".000");
//			preValue_s = decimalFormat.format(preValue_f);
//			peakValue_s = decimalFormat.format(peakValue_f);
//			d_value = peakValue_f - preValue_f;
//			d_value_s = decimalFormat.format(d_value);
//		}
//		else if(pointPos == 3){
//			preValue_f = preValue/100;
//			peakValue_f = peakValue/100;
//			DecimalFormat decimalFormat = new DecimalFormat(".00");
//			preValue_s = decimalFormat.format(preValue_f);
//			peakValue_s = decimalFormat.format(peakValue_f);
//			d_value = peakValue_f - preValue_f;
//			d_value_s = decimalFormat.format(d_value);
//		}
//		else if(pointPos == 4){
//			preValue_f = preValue/10;
//			peakValue_f = peakValue/10;
//			DecimalFormat decimalFormat = new DecimalFormat(".0");
//			preValue_s = decimalFormat.format(preValue_f);
//			peakValue_s = decimalFormat.format(peakValue_f);
//			d_value = peakValue_f - preValue_f;
//			d_value_s = decimalFormat.format(d_value);
//		}
//		else if(pointPos == 5){
        preValue_s = String.valueOf((int)preValue);
        peakValue_s = String.valueOf((int)peakValue);
        d_value_s = String.valueOf((int)peakValue - (int)preValue);
//		}
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
        String year =  " " + strDeal(Integer.toHexString(Integer.parseInt(array[0])%2000)).toUpperCase();
        String month =  " " + strDeal(Integer.toHexString(Integer.parseInt(array[1]))).toUpperCase();
        String day =  " " + strDeal(Integer.toHexString(Integer.parseInt(array[2]))).toUpperCase();
        String hour =  " " + strDeal(Integer.toHexString(Integer.parseInt(array[3]))).toUpperCase();
        String minute =  " " + strDeal(Integer.toHexString(Integer.parseInt(array[4]))).toUpperCase();
        int checkSum = 221 + Integer.parseInt(array[0])%2000 + Integer.parseInt(array[1]) + Integer.parseInt(array[2]) + Integer.parseInt(array[3]) + Integer.parseInt(array[4]);
        String checkByte = " " + strDeal(Integer.toHexString(checkSum)).toUpperCase();
        return "55 AA DD"+year+month+day+hour+minute+checkByte+" 5A 5A";
    }

    public static String sendTechnicsData(int num, double max, double min){
        String dataNum = " " + strDeal(Integer.toHexString(num)).toUpperCase();
        int fIntMax = (int) max;

        String max_fInt = Integer.toHexString(fIntMax).toUpperCase();
        String strMax = null;
        strMax = strDeal1(max_fInt) + "05";

        int fIntMin;
        if(min == (int) min){
            fIntMin = (int) min;
        }
        else{
            fIntMin = (int) (min + 1);
        }

        String min_fInt = Integer.toHexString(fIntMin).toUpperCase();
        String strMin = null;
        strMin = strDeal1(min_fInt) + "05";

        int checkSum = 241 + num + Integer.valueOf(strMax.substring(0, 2),16) + Integer.valueOf(strMax.substring(2, 4),16) + Integer.valueOf(strMax.substring(4, 6),16) + Integer.valueOf(strMin.substring(0, 2),16) + Integer.valueOf(strMin.substring(2, 4),16) + Integer.valueOf(strMin.substring(4, 6),16);
        String checkByte = " " + strDeal(Integer.toHexString(checkSum)).toUpperCase();

        return "55 AA F1"+dataNum+" "+strMax.substring(0, 2)+" "+strMax.substring(2, 4)+" "+strMax.substring(4, 6)+" "+strMin.substring(0, 2)+" "+strMin.substring(2, 4)+" "+strMin.substring(4, 6)+checkByte+" 5A 5A";
    }
}
