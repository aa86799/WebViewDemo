package com.stone.webview;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * desc   :
 * author : stone
 * email  : aa86799@163.com
 * time   : 07/01/2017 18 56
 */
public class ApkDownloadThread extends Thread {
    private String mUrl;

    public ApkDownloadThread(String url) {
        this.mUrl = url;
    }

    @Override
    public void run() {
        try {
            URL url = new URL(mUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoInput(true);
//            conn.setDoOutput(true);

            System.out.println("download start");
            System.out.println(conn.getResponseCode());
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
                    && conn.getResponseCode() == 200) {
                byte[] buffer = new byte[5 * 1024];
                InputStream is = conn.getInputStream();
                File file = new File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        "test.apk");
                System.out.println(file.getAbsolutePath());
                FileOutputStream fos = new FileOutputStream(file);
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    System.out.println("cur file length---->" + file.length());
                }
                fos.flush();
                fos.close();
                System.out.println("download success");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
