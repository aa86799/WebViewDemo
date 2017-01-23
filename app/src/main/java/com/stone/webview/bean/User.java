package com.stone.webview.bean;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 * time   : 23/01/2017 18 57
 */
public class User {

    private String name;
    private Context mContext;

    public User() {
    }

    public User(Context context, String name) {
        this.mContext = context;
        this.name = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JavascriptInterface //api 17之后需要加上
    public void showUser() {
        Toast.makeText(mContext, "current user is 【" + name + "】", Toast.LENGTH_SHORT).show();
    }
}
