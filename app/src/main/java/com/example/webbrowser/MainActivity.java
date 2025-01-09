package com.example.webbrowser;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    //components initialization
    private WebView webView ;
    private ImageView cleanUrl ;
    private EditText input_url;
    private ProgressBar progressBar ;
    private ImageView reload ;
    private ImageView more ;

    @Override
    public void onBackPressed() {
        if(webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        webView = findViewById(R.id.web_view);
        cleanUrl = findViewById(R.id.clear_icon);
        input_url = findViewById(R.id.url_input);
        progressBar = findViewById(R.id.progress_bar);
        reload= findViewById(R.id.reload_icon);
        more = findViewById(R.id.more_icon);

        //run google in webview
        webView.loadUrl("https://www.google.com");

        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                input_url.setText(webView.getUrl().toString());
            }

        });


        input_url.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO||actionId == EditorInfo.IME_ACTION_DONE) {
                    lodMyUrl(input_url.getText().toString());
                    return true;
            }
                return false;
            }

        });
        cleanUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_url.setText("");
            }
        });

        reload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.reload();
            }
        });

        more.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId()==R.id.copy_link){

                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("URL", webView.getUrl());
                        clipboard.setPrimaryClip(clip);
                    }
                    else if(item.getItemId()==R.id.share_link)
                    {
                        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
                        sendIntent.setType("text/plain");
                        startActivity(Intent.createChooser(sendIntent, "Share URL"));
                    }
                    else if(item.getItemId()==R.id.forward)
                        webView.goForward();
                    else
                        return false;
                    return true;

                }
            });
            popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
            popup.show();
        });









    }




    void lodMyUrl(String url){
        boolean match = Patterns.WEB_URL.matcher(url.trim()).matches();
        if(match)
            webView.loadUrl("https://"+url);
        else
            webView.loadUrl("https://www.google.com/search?q="+url);

    }
    class myWebViewClient extends WebViewClient{
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return false;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }




}