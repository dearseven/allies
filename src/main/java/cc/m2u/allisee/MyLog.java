package cc.m2u.allisee;

/**
 * Created by apple on 2016/12/25.
 */

public class MyLog {
    public static void i(Class clz,String str){
        android.util.Log.i("allisee", clz.getName());
        android.util.Log.i("allisee", str);
    }

}
