package com.example.practiceliu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    final String fileName = pic1.substring(pic1.indexOf("_") + 1);
    String filePath = WpaApp.getDataPath() + WpaInfo.APP_DIR_DOWNLOAD + fileName;
    String fileType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (fileName != null) {
            String[] str = fileName.split("\\.");
            if (str.length > 1) {
                fileType = str[str.length - 1];
            }
        }
        if (Utilities.fileExists(filePath)) {
            openFileIntent(filePath + "/" + fileName, fileType);

            return;
        }
        downFile();
    }

    /**
     * 文件下载
     *
     * @param url
     */
    public void downFile(String url, String fileName, String filePath, String mType) {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("正在下载");
        progressDialog.setMessage("请稍后...");
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.show();
        progressDialog.setCancelable(false);
        DownloadUtil.get().download(url, filePath, fileName, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                openFileIntent(filePath + "/" + fileName, mType);

            }

            @Override
            public void onDownloading(int progress) {
                progressDialog.setProgress(progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                //下载异常进行相关提示操作
//                MessageBox.infoMsg(MainActivity.this, "请检查网络强度！");
            }
        });
    }
    private String getMap(String key) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("rar", "application/x-rar-compressed");
        map.put("zip", "application/zip");
        map.put("pdf", "application/pdf");
        map.put("doc", "application/msword");
        map.put("docx", "application/msword");
        map.put("xls", "application/vnd.ms-excel");
        map.put("xlsx", "application/vnd.ms-excel");
        map.put("ppt", "application/vnd.ms-powerpoint");
        map.put("html", "text/html");
        map.put("txt", "text/html");
        map.put("xml", "text/html");
        map.put("", "*/*");
        return map.get(key);
    }

    private void openFileIntent(String file, String type) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".fileProvider", new File(file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//给目标文件临时授权，必需添加
        } else {
            uri = Uri.fromFile(new File(file));
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            if ("doc".equals(type) || "xls".equals(type) || "xlsx".equals(type) || "docx".equals(type)) {
                intent.setDataAndType(uri, getMap(type));
                this.startActivity(intent);

            } else if ("jpg".equals(type) || "png".equals(type) || "jpeg".equals(type) || "bmp".equals(type)) {
                Intent intent1 = new Intent();
                intent1.putExtra(AppImageView.ARG_IMAGE_FILE, file);
                intent1.setClass(MainActivity.this, AppImageView.class);
                startActivity(intent1);
            } else {
                intent.setDataAndType(uri, getMap(type));
                this.startActivity(intent);
            }
        } catch (Exception e) {
//            MessageBox.infoMsg(MainActivity.this, "请安装查看 " + type + " 文件的工具！");

        }
    }
}
