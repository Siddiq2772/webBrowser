package com.example.webbrowser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity2 extends AppCompatActivity {
    private Button btn;
    private RadioGroup rg;
    private String search_engine = "https://google.com/search?q=";
    private EditText input_url;
    private RadioButton google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btn = findViewById(R.id.eng);
        rg = findViewById(R.id.rb);
        input_url = findViewById(R.id.url_input);
        google = findViewById(R.id.google);
        google.setChecked(true);

        input_url.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_DONE) {
                    Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                    intent.putExtra("search_engine", search_engine);
                    intent.putExtra("text", input_url.getText().toString());
                    startActivity(intent);
                    return true;
                }
                return false;
            }

        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rg.getVisibility() == View.VISIBLE)
                    rg.setVisibility(View.INVISIBLE);
                else
                    rg.setVisibility(View.VISIBLE);

            }

        });

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.google) {
                    search_engine = "https://google.com/search?q=";
                    rg.setVisibility(View.INVISIBLE);

                } else if (checkedId == R.id.brave) {
                    search_engine = "https://search.brave.com/search?q=";
                    rg.setVisibility(View.INVISIBLE);

                } else if (checkedId == R.id.duckduckgo) {
                    search_engine = "https://duckduckgo.com/?q=";
                    rg.setVisibility(View.INVISIBLE);

                } else if (checkedId == R.id.bing) {
                    search_engine = "https://bing.com/search?q=";
                    rg.setVisibility(View.INVISIBLE);

                }
                Log.d("TAG", "onCheckedChanged:" + search_engine);
            }
        });
    }
}