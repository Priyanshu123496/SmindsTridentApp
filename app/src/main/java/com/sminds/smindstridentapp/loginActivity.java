package com.sminds.smindstridentapp;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class loginActivity extends AppCompatActivity {

    private EditText etusername, etpassword;
    private Button btnlogin;
    private TextView tvreg;
    private ParseContent parseContent;
    private final int LoginTask = 1;
    private PreferenceHelper preferenceHelper;
    String response = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        getSupportActionBar().setTitle(R.string.app_name);
        getSupportActionBar().setSubtitle("The Non-ferrous People");
        getSupportActionBar().setLogo(R.drawable.ghloginlogo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        parseContent = new ParseContent(this);
        preferenceHelper = new PreferenceHelper(this);

        if (preferenceHelper.getIsLogin()) {
            Intent intent = new Intent(loginActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();
        }

        etusername = (EditText) findViewById(R.id.etusername);
        etpassword = (EditText) findViewById(R.id.etpassword);

        btnlogin = (Button) findViewById(R.id.btn);
        tvreg = (TextView) findViewById(R.id.tvreg);

        tvreg.setMovementMethod(LinkMovementMethod.getInstance());

        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    login();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void login() throws IOException, JSONException {

        if (!SmindUtils.isNetworkAvailable(loginActivity.this)) {
            Toast.makeText(loginActivity.this, "Internet is required!", Toast.LENGTH_SHORT).show();
            return;
        }
        SmindUtils.showSimpleProgressDialog(loginActivity.this);
        final HashMap<String, String> map = new HashMap<>();

        String username = etusername.getText().toString();
        String password = etpassword.getText().toString();

        if (username == "") {
            Toast.makeText(loginActivity.this, "Please enter user name", Toast.LENGTH_SHORT).show();
        } else if (password == "") {
            Toast.makeText(loginActivity.this, "Please enter password!", Toast.LENGTH_SHORT).show();
        } else {
            map.put(SmindConstants.Params.NAME, etusername.getText().toString());
            map.put(SmindConstants.Params.PASSWORD, etpassword.getText().toString());
            ExecutorService service = Executors.newSingleThreadExecutor();
            service.execute(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            //OnPreExecuteMethod
                        }
                    });
                    //doInBackground
                    try {
                        HttpRequest req = new HttpRequest(SmindConstants.ServiceType.LOGIN);
                        response = req.prepare(HttpRequest.Method.POST).withData(map).sendAndReadString();
                    } catch (Exception e) {
                        response = e.getMessage();
                    }
                    //doPostExecute
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            SmindUtils.removeSimpleProgressDialog();  //will remove progress dialog

                            switch (LoginTask) {
                                case LoginTask:

                                    if (parseContent.isSuccess(response)) {

                                        parseContent.saveInfo(response);
                                        Toast.makeText(loginActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(loginActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(loginActivity.this, parseContent.getErrorMessage(response), Toast.LENGTH_SHORT).show();
                                    }

                            }
                        }
                    });

                }

            });
        }

        }
    }
