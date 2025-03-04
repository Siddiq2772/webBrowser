package com.example.webbrowser;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Patterns;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.ValueCallback;
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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    //components initialization
    private WebView webView ;
    private ImageView cleanUrl ;
    private EditText input_url;
    private ProgressBar progressBar ;
    private ImageView reload ;
    private ImageView more ;
    private TextToSpeech tts;
    private String text;
    String search_engine;




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
        //on light mode whether device in light or not
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        webView = findViewById(R.id.web_view);
        cleanUrl = findViewById(R.id.clear_icon);
        input_url = findViewById(R.id.url_input);
        progressBar = findViewById(R.id.progress_bar);
        reload= findViewById(R.id.reload_icon);
        more = findViewById(R.id.more_icon);


        Intent intent = getIntent();
        search_engine= intent.getStringExtra("search_engine");
        text = intent.getStringExtra("text");




        WebSettings webSettings = webView.getSettings();

        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);

        lodMyUrl(text);

        webView.setWebViewClient(new myWebViewClient());
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
                input_url.setText(webView.getUrl().toString());
                if(newProgress==100)
                    progressBar.setVisibility(View.INVISIBLE);

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
        //tts implementation
        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.US); // Set language (e.g., US, UK)

                    if (result == TextToSpeech.LANG_NOT_SUPPORTED || result == TextToSpeech.LANG_MISSING_DATA) {
                        Toast.makeText(MainActivity.this, "Language not supported", Toast.LENGTH_SHORT).show();
                    } else {

                    }
                } else {
                    Toast.makeText(MainActivity.this, "TTS initialization failed", Toast.LENGTH_SHORT).show();
                }
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
                    else if (item.getItemId() == R.id.tts) {
                        Toast.makeText(MainActivity.this, "Processing large text, this may take a moment...", Toast.LENGTH_LONG).show();
                        if (tts.isSpeaking())
                                tts.stop();
                        if (text.length() > 4000) {
                            String[] parts = text.split("(?<=\\G.{4000})");
                            for (String part : parts) {
                                tts.speak(part, TextToSpeech.QUEUE_ADD, null, null);
                                text ="";
                            }
                        } else {
                            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
                            text ="";

                        }

                    }
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
            webView.loadUrl(search_engine+url);

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
            // JavaScript to extract text
            String js = "document.body.innerText";

            webView.evaluateJavascript(js, new ValueCallback<String>() {
                @Override
                public void onReceiveValue(String value) {
                    // Remove "\n" inside words but keep those separating sentences.
                    text = value.replaceAll("([a-zA-Z0-9])\\\\n([a-zA-Z0-9])", "$1$2");
                    System.out.println("Extracted Text: " + text);
                }
            });

            super.onPageFinished(view, url);
            progressBar.setVisibility(View.INVISIBLE);

        }
    }


    @Override
    public void onBackPressed() {
        if(webView.canGoBack())
            webView.goBack();
        else
            super.onBackPressed();

    }





}