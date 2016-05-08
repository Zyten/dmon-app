package xyz.zyten.rdmon;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity{

    private static final String TAG = "HistoryActivity";
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("API History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initWebView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            WebView.class.getMethod("onResume").invoke(mWebView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            WebView.class.getMethod("onPause").invoke(mWebView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webView);
        //mWebView.setBackgroundColor(Color.TRANSPARENT);
        // WebViewの設定
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT > 7) {
            settings.setPluginState(WebSettings.PluginState.ON);
        } else {
            settings.setPluginState(WebSettings.PluginState.ON);
        }


        String html = "";
        html += "<html><body>";
        html += "<iframe width=\"315\" height=\"230\" src=\"https://api.thingspeak.com/channels/108012/charts/4?&results=15&dynamic=&width=320&title=API History\" frameborder=\"0\" allowfullscreen></iframe>";
        html += "</body></html>";

        mWebView.loadData(html, "text/html", null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id==android.R.id.home) {
            finish();}

        return super.onOptionsItemSelected(item);
    }
}



/*package xyz.zyten.rdmon;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by zyten on 2/5/2016.
 *//*
public class HistoryActivity extends AppCompatActivity{
*//*
    private static final String TAG = "HistoryActivity";
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Personal Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initWebView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            WebView.class.getMethod("onResume").invoke(mWebView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            WebView.class.getMethod("onPause").invoke(mWebView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webView);

        // WebViewの設定
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT > 7) {
            settings.setPluginState(WebSettings.PluginState.ON);
        } else {
            settings.setPluginState(WebSettings.PluginState.ON);
        }


        String html = "";
        html += "<html><body>";
        html += "<iframe width=\"350\" height=\"315\" src=\"https://api.thingspeak.com/channels/108012/charts/4?&results=15&dynamic=true&width=350&title=API History&bgcolor=#F5F6F6\" frameborder=\"0\" allowfullscreen></iframe>";
        html += "</body></html>";

        mWebView.loadData(html, "text/html", null);
    }
}
*/