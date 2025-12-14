package com.sminds.smindstridentapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class DisplayMessageActivity extends AppCompatActivity {

    private static String message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        Intent intent = getIntent();
        Bundle b = getIntent().getExtras();
        //String message = intent.getStringExtra(DisplayActivity.EXTRA_MESSAGE);
        message = b.getString("message");

        // Capture the layout's TextView and set the string as its text
        TextView textView = findViewById(R.id.tvmessage);
        textView.setText(message);
    }

    public void BackToMain(View arg0) {
        DisplayMessageActivity.this.finishAffinity();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        ///nagashree Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        ///nagashree startActivity(intent);

    }

}
