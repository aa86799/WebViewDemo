package com.stone.webview;

import android.net.http.AndroidHttpClient;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 * time   : 07/01/2017 23 06
 */

public class HttpCookieThread extends Thread {

    private String mUrl;
    private Handler mHandler;

    public HttpCookieThread(String url, Handler handler) {
        this.mUrl = url;
        this.mHandler = handler;
    }

    /*
    cookie的中文意思是小点心，在IE里它通常用来在用户电脑上产生一个小文件，用来存放经常需要用到的东西。
    例如，你登陆一个网站，希望一个月内不在需要输入用户密码，于是这个网站就会产生一个cookie存放在你的电脑上，
    每次你访问这个网站的时候，它就会从cookie里读取你的用户和密码。

    cookie是保存在客户端的,当浏览器保存了cookie后,下次访问的时候,会自动填写一些信息(用户名,密码什么的),所以会自动登录
    服务端也会保存cookie,原理和session一样,一般是保存在内存中

    一套完整的web是在进入主页的时候验证cookie，如果验证不通过就跳转到登录界面去登录
    String cookie = CookieManager.getInstance().getCookie("url");
     */

    @Override
    public void run() {
//        sendCookieByHttpURLConnection();

        sendCookieByHttpHttpClient();
    }

    private void sendCookieByHttpURLConnection() {
        try {
            URL url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            String content = "username=admin&password=admin";
            OutputStream os = conn.getOutputStream();
            os.write(content.toString().getBytes("utf-8"));
            os.close();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String responseCookie = conn.getHeaderField("Set-Cookie");// 取到所用的Cookie
            String sessionIdString = "";
            if (responseCookie != null) {
                sessionIdString = responseCookie.substring(0, responseCookie.indexOf(";"));
                System.out.println(sessionIdString);
            }
// 输出内容
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                line = br.readLine();
            }
// access
            URL url1 = new URL("网页的登录后的页面");
            HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
            connection1.setRequestProperty("Cookie", responseCookie);// 给服务器送登录后的cookie
            BufferedReader br1 = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
            String line1 = br1.readLine();
            while (line1 != null) {
                // TODO:操作
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendCookieByHttpHttpClient() {
        try {
            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost(mUrl);

            List<NameValuePair> list = new ArrayList<>();
            list.add(new BasicNameValuePair("name", "stone"));
            list.add(new BasicNameValuePair("age", "18"));

            post.setEntity(new UrlEncodedFormEntity(list));

            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                AbstractHttpClient absClient = (AbstractHttpClient) client;
                List<Cookie> cookies = absClient.getCookieStore().getCookies();
                for (Cookie cookie : cookies) {
                    System.out.println("name = " + cookie.getName() + ", value = " + cookie.getValue());
                    System.out.println("cookie -----" + cookie);

                    Message.obtain(mHandler, 0, cookie).sendToTarget();
                }
            }
        } catch (Exception e) {

        }




    }
}
