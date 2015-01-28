package com.example.firegnu.torquewrenchdemo;

/**
 * Created by firegnu on 15-1-26.
 */
public class Log {

    public static int d(Object object,String msg){
        String tag=object.getClass().getSimpleName();
        return android.util.Log.d(tag, msg);
    }

    public static int v(Object object,String msg){
        String tag=object.getClass().getSimpleName();
        return android.util.Log.v(tag, msg);
    }

    public static int i(Object object,String msg){
        String tag=object.getClass().getSimpleName();
        return android.util.Log.i(tag, msg);
    }

    public static int w(Object object,String msg){
        String tag=object.getClass().getSimpleName();
        return android.util.Log.w(tag, msg);
    }

    public static int e(Object object,String msg){
        String tag=object.getClass().getSimpleName();
        return android.util.Log.e(tag, msg);
    }
}

