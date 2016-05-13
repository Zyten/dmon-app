package xyz.zyten.rdmon;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class HistoryActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "HistoryActivity";
    private WebView mWebView = null;
    private LinearLayout mlLayoutRequestError = null;
    private Handler mhErrorLayoutHide;
    private final String URL = "https://api.thingspeak.com/channels/108012/charts/4?&results=15&dynamic=&width=320&title=API History";
    private boolean mbErrorOccured = false;
    private boolean mbReloadPressed = false;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("API History");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ((Button) findViewById(R.id.btnRetry)).setOnClickListener(this);
        mlLayoutRequestError = (LinearLayout) findViewById(R.id.lLayoutRequestError);
        mhErrorLayoutHide = getErrorLayoutHideHandler();
        initWebView();

    }

    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webView);

        WebSettings settings = mWebView.getSettings();
        mWebView.setWebViewClient(new MyWebViewClient());
        settings.setJavaScriptEnabled(true);

        mWebView.setWebChromeClient(getChromeClient());
        mWebView.loadUrl(URL);
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

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btnRetry) {
            if(MainActivity.InternetAvailable) {
                Log.i (TAG, "Internet Connected");
                if (!mbErrorOccured) {
                    return;
                }

                mbReloadPressed = true;
                mWebView.reload();
                mbErrorOccured = false;
            }
            else{
                Log.i ("Tag", "Internet Not Connected");
                Toast.makeText(HistoryActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (mbErrorOccured == false && mbReloadPressed) {
                hideErrorLayout();
                mbReloadPressed = false;
            }

            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            mbErrorOccured = true;
            showErrorLayout();
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    private WebChromeClient getChromeClient() {
        final ProgressDialog progressDialog = new ProgressDialog(HistoryActivity.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        return new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        };
    }

    private void showErrorLayout() {
        mlLayoutRequestError.setVisibility(View.VISIBLE);
    }

    private void hideErrorLayout() {
        mhErrorLayoutHide.sendEmptyMessageDelayed(10000, 200);
    }

    private Handler getErrorLayoutHideHandler() {
        return new Handler() {
            @Override
            public void handleMessage(Message msg) {
                mlLayoutRequestError.setVisibility(View.GONE);
                super.handleMessage(msg);
            }
        };
    }
}
