package com.sminds.smindstridentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderFormActivity extends AppCompatActivity {

    // 30/08/2021 Priyanshu : declaring all parameter variables

    private AutoCompleteTextView etcompo;

    private Context context;

    private static LinearLayout parentLinearLayout;

    private String compo;
    private String form;
    private String thickness;
    private String thickness_unit;
    private String width;
    private String width_unit;
    private String length;
    private String length_unit;
    private String quantity;
    private String quantity_unit;
    private String rate;
    private String hardness;
    private String hardness_min;
    private String hardness_max;
    private String soitem_code;
    private String so_note;
    private RadioGroup rgrpsalestype,rgrporderby,rgrpform,rgrpthickness_unit,rgrpwidth_unit,rgrplength_unit,rgrpquantity_unit,rgrphardness;
    private RadioButton rbtsalestype,rbtorderby,rbtform,rbtthicknessunit,rbtwidthunit,rbtlengthunit,rbtquantityunit,rbthardness;
    private EditText etthickness,etwidth,etlength,etquantity,etrate,ethardnessmin,ethardnessmax,etsoitemcode,etsonote;
    private List<String> compo_array;


    private PreferenceHelper preferenceHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_form);
        parentLinearLayout = (LinearLayout) findViewById(R.id.parent_linear_layout_order);
        preferenceHelper = new PreferenceHelper(this);
        context = getApplicationContext();

        parentLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
            }
        });

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.execute(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {

                        compo_array = new ArrayList<>();

                    }
                });

                //doInBackground
                URL currentUrl;

                String urlWebService_compo = SmindConstants.ServiceType.PARAM_COMPO_LIST;

                String[] s = new String[1];
                StringBuilder responseStrBuilder = new StringBuilder();
                try {

                    HttpURLConnection urlConnection;
                    InputStream in;
                    BufferedReader streamReader;
                    String inputStr;

                    // 30/08/2021 Priyanshu : setting up URL for Product
                    currentUrl = new URL(urlWebService_compo);
                    urlConnection = (HttpURLConnection) currentUrl.openConnection();
                    urlConnection.setDoInput(true);
                    in = new BufferedInputStream(urlConnection.getInputStream());
                    streamReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                    while ((inputStr = streamReader.readLine()) != null) {
                        responseStrBuilder.append(inputStr + ",");
                    }
                    responseStrBuilder.deleteCharAt(responseStrBuilder.length() - 1);
                    s[0]=responseStrBuilder.toString().trim();

                    responseStrBuilder.setLength(0);

                }catch (MalformedURLException e) {
                    e.printStackTrace();
                }catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }catch (Exception  e){
                    e.printStackTrace();
                }
                //doPostExecute
                runOnUiThread(new Runnable() {
                    ArrayAdapter<String> adapter_compo;

                    @Override
                    public void run() {
                        JSONArray jsonArr_compo, jsonArr_contacts;
                        //
                        try {
                            jsonArr_compo = new JSONArray(s[0]);

                            // 30/08/2021 Priyanshu : iterating through Product array
                            for (int i = 0; i < jsonArr_compo.length(); i++) {
                                JSONObject jsonObj = jsonArr_compo.getJSONObject(i);
                                String product = jsonObj.getString("compo_name");
                                compo_array.add(product);
                            }

                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }

                        // 30/08/2021 Priyanshu : creating adapter for Product
                        adapter_compo = new ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, compo_array);
                        etcompo = (AutoCompleteTextView) findViewById(R.id.compo);
                        etcompo.setThreshold(2);
                        etcompo.setAdapter(adapter_compo);

                    }
                });

            }

        });

    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }


    // 31/10/2021 Nagashree  : To check if Product from the list
    public int isCompoInList() {
        LinearLayout inner = (LinearLayout) parentLinearLayout.getChildAt(0);
        Iterator it = compo_array.iterator();

        if (inner.getChildAt(1) instanceof AutoCompleteTextView) {
            int compo_in_list = 0;
            etcompo = (AutoCompleteTextView) inner.getChildAt(1);
            String compo = etcompo.getText().toString().trim();
            while (it.hasNext()) {
                //Toast.makeText(MainActivity.this, it.next().toString(), Toast.LENGTH_LONG).show();
                if (compo.compareTo(it.next().toString()) == 0) {
                    compo_in_list = 1;
                    break;
                }
            }
            if (compo_in_list == 0) {
                Toast.makeText(OrderFormActivity.this, "Please select Priyanshu -1 Composition from the list.", Toast.LENGTH_LONG).show();
                return 0;
            } else
                return 1;
        }
        return 1;
    }

    public void onDelete(View v) {
        parentLinearLayout.removeView((View) v.getParent());
    }


    public void InsertRow(View arg0) {

        final View editOrderFormView = getLayoutInflater().inflate(R.layout.order_form,null);

        if(isCompoInList() == 0)
        {
            Toast.makeText(OrderFormActivity.this, "Please check Composition values.", Toast.LENGTH_LONG).show();
            return;
        }
        else {

            LinearLayout inner = (LinearLayout) parentLinearLayout.getChildAt(0);
            // 24/08/2023 Priyanshu : Consolidationg all the Composition values to one
            if (inner.getChildAt(1) instanceof AutoCompleteTextView) {
                etcompo = (AutoCompleteTextView) inner.getChildAt(0);
                compo = etcompo.getText().toString().trim();
                Log.d("TAG", etcompo.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Form values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(1);

            if (inner.getChildAt(1) instanceof RadioGroup) {
                rbtform = (RadioButton) inner.getChildAt(0);
                form = rbtform.getText().toString().trim();
                Log.d("TAG", rbtform.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Thickness values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(2);

            if (inner.getChildAt(1) instanceof EditText) {
                etthickness = (EditText) inner.getChildAt(2);
                thickness = etthickness.getText().toString().trim();
                Log.d("TAG", etthickness.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Thickness unit values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(3);

            if (inner.getChildAt(1) instanceof RadioGroup) {
                rbtthicknessunit = (RadioButton) inner.getChildAt(0);
                thickness_unit = rbtthicknessunit.getText().toString().trim();
                Log.d("TAG", rbtthicknessunit.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Width values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(4);

            if (inner.getChildAt(1) instanceof EditText) {
                etwidth = (EditText) inner.getChildAt(2);
                width = etwidth.getText().toString().trim();
                Log.d("TAG", etwidth.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Width unit values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(5);

            if (inner.getChildAt(1) instanceof RadioGroup) {
                rbtwidthunit = (RadioButton) inner.getChildAt(0);
                width_unit = rbtwidthunit.getText().toString().trim();
                Log.d("TAG", rbtwidthunit.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Length values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(6);

            if (inner.getChildAt(1) instanceof EditText) {
                etlength = (EditText) inner.getChildAt(2);
                length = etlength.getText().toString().trim();
                Log.d("TAG", etlength.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Length unit values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(7);

            if (inner.getChildAt(1) instanceof RadioGroup) {
                rbtlengthunit = (RadioButton) inner.getChildAt(0);
                length_unit = rbtlengthunit.getText().toString().trim();
                Log.d("TAG", rbtlengthunit.getText().toString());
            }


            // 24/08/2023 Priyanshu : Consolidationg all the Quantity values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(8);

            if (inner.getChildAt(1) instanceof EditText) {
                etquantity = (EditText) inner.getChildAt(2);
                quantity = etquantity.getText().toString().trim();
                Log.d("TAG", etquantity.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Quantity unit values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(9);

            if (inner.getChildAt(1) instanceof RadioGroup) {
                rbtquantityunit = (RadioButton) inner.getChildAt(0);
                quantity_unit = rbtquantityunit.getText().toString().trim();
                Log.d("TAG", rbtquantityunit.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Rate values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(10);
            // 30/08/2021 Priyanshu : Consolidationg all the Rate values to one
            if (inner.getChildAt(1) instanceof EditText) {
                etrate = (EditText) inner.getChildAt(3);
                rate = etrate.getText().toString().trim();
                Log.d("TAG", etrate.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Hardness unit values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(11);

            if (inner.getChildAt(1) instanceof RadioGroup) {
                rbthardness = (RadioButton) inner.getChildAt(0);
                hardness = rbthardness.getText().toString().trim();
                Log.d("TAG", rbthardness.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Hardness Min values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(12);

            if (inner.getChildAt(1) instanceof EditText) {
                ethardnessmin = (EditText) inner.getChildAt(3);
                hardness_min = ethardnessmin.getText().toString().trim();
                Log.d("TAG", ethardnessmax.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Hardness Max values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(13);

            if (inner.getChildAt(1) instanceof EditText) {
                ethardnessmax = (EditText) inner.getChildAt(3);
                hardness_max = ethardnessmax.getText().toString().trim();
                Log.d("TAG", ethardnessmax.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the Item Code values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(14);

            if (inner.getChildAt(1) instanceof EditText) {
                etsoitemcode = (EditText) inner.getChildAt(3);
                soitem_code = etsoitemcode.getText().toString().trim();
                Log.d("TAG", etsoitemcode.getText().toString());
            }

            // 24/08/2023 Priyanshu : Consolidationg all the So Note values to one
            inner = (LinearLayout) parentLinearLayout.getChildAt(15);

            if (inner.getChildAt(1) instanceof EditText) {
                etsonote = (EditText) inner.getChildAt(3);
                so_note = etsonote.getText().toString().trim();
                Log.d("TAG", etsonote.getText().toString());
            }

        }
        buildBundle(compo,form,thickness,thickness_unit, width,width_unit, length,length_unit, quantity, quantity_unit,rate, hardness,hardness_min,hardness_max,soitem_code,so_note);

    }

    private void buildBundle(String compo, String form,String thickness,String thickness_unit,String  width,String width_unit,String  length,String length_unit,String  quantity,String  quantity_unit,String rate,String  hardness,String hardness_min,String hardness_max,String soitem_code,String so_note)
    {
        //Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);

        //Create a bundle object
        Bundle b = new Bundle();

        //Inserts a String value into the mapping of this Bundle
        //Toast.makeText(MainActivity.this, name,Toast.LENGTH_LONG ).show();
        b.putString("compo", compo);
        b.putString("form", form);
        b.putString("head", thickness);
        b.putString("thickness", thickness_unit);
        b.putString("width", width);
        b.putString("width_unit", width_unit);
        b.putString("length", length);
        b.putString("length_unit", length_unit);
        b.putString("quantity", quantity);
        b.putString("quantity_unit", quantity_unit);
        b.putString("rate", rate);
        b.putString("hardness", hardness);
        b.putString("quantity", quantity);
        b.putString("hardness_min", hardness_min);
        b.putString("hardness_max", hardness_max);
        b.putString("soitem_code", soitem_code);
        b.putString("so_note", so_note);
        //Add the bundle to the intent.
        //intent.putExtras(b);

        //start the DisplayActivity
        //startActivity(intent);

    }

}