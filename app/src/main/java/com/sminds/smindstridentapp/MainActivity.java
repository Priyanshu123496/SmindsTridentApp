package com.sminds.smindstridentapp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView etpartyname, etcompo;
    private TextView etreferencerow;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;

    private static LinearLayout parentLinearLayout;
    private static LinearLayout main_order_layout;
    private String partyname;
    private String pono;
    private String podate;
    private String msonote;

    private RadioButton rbtsalestype, rbtorderby, rbtform, rbtthicknessunit, rbtwidthunit, rbtlengthunit, rbtquantityunit, rbthardness;
    private RadioGroup rgrpsalestype, rgrpform, rgrpthickness_unit, rgrpwidth_unit, rgrplength_unit, rgrpquantity_unit, rgrphardness;
    private static String[] s = new String[2];

    private static List<String> contacts_array, compo_array;
    private static PreferenceHelper preferenceHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setSubtitle(R.string.app_subtitle);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }

        parentLinearLayout = findViewById(R.id.parent_linear_layout);
        main_order_layout = findViewById(R.id.main_order_layout);

        preferenceHelper = new PreferenceHelper(this);

        parentLinearLayout.setOnClickListener(v -> hideKeyboard());

        setDefaults();
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }


    public void setDefaults() {
        ExecutorService service = Executors.newSingleThreadExecutor();
        contacts_array = new ArrayList<>();
        compo_array = new ArrayList<>();

        service.execute(() -> {
            runOnUiThread(() -> {
                //OnPreExecuteMethod
            });
            //doInBackground
            try {
                String urlWebService_contacts = SmindConstants.ServiceType.PARAM_CUSTOMER_LIST;
                String urlWebService_compo = SmindConstants.ServiceType.PARAM_COMPO_LIST;

                String inputStr;
                StringBuilder responseStrBuilder = new StringBuilder();
                // 23-08-2023 Priyanshu : setting up URL for Party name (contacts)
                URL currentUrl = new URL(urlWebService_contacts);
                HttpURLConnection urlConnection = (HttpURLConnection) currentUrl.openConnection();
                urlConnection.setDoInput(true);
                BufferedInputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr).append(",");
                }
                responseStrBuilder.deleteCharAt(responseStrBuilder.length() - 1);
                s[0] = responseStrBuilder.toString().trim();
                responseStrBuilder.setLength(0);


                // 23-08-2023 Priyanshu : setting up URL for Composition
                currentUrl = new URL(urlWebService_compo);
                urlConnection = (HttpURLConnection) currentUrl.openConnection();
                urlConnection.setDoInput(true);
                in = new BufferedInputStream(urlConnection.getInputStream());
                streamReader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
                while ((inputStr = streamReader.readLine()) != null) {
                    responseStrBuilder.append(inputStr).append(",");
                }
                responseStrBuilder.deleteCharAt(responseStrBuilder.length() - 1);
                s[1] = responseStrBuilder.toString().trim();
                responseStrBuilder.setLength(0);
//                Toast.makeText(MainActivity.this, "Entered in maint activity try function", Toast.LENGTH_LONG).show();

            } catch (MalformedURLException | UnsupportedEncodingException e) {
                Log.e("MainActivity", "Error in setDefaults", e);
            } catch (UnknownHostException e) {
                Log.e("MainActivity", "Error in setDefaults", e);
            } catch (IOException e) {
                Log.e("MainActivity", "Error in setDefaults", e);
            } catch (Exception e) {
                Log.e("MainActivity", "Error in setDefaults", e);
            }
            //doPostExecute
            runOnUiThread(() -> {
                ArrayAdapter<String> adapter_contacts;
                JSONArray jsonArr_contacts, jsonArr_compo;
                try {
                    jsonArr_contacts = new JSONArray(s[0]);
                    jsonArr_compo = new JSONArray(s[1]);
                    // 23-08-2023 Priyanshu : iterating through Contact array

                    for (int i = 0; i < jsonArr_contacts.length(); i++) {
                        JSONObject jsonObj = jsonArr_contacts.getJSONObject(i);
                        String contacts = jsonObj.getString("party_name");
                        contacts_array.add(contacts);
                    }

                    final View activity_main_view = getLayoutInflater().inflate(R.layout.activity_main, null);
                    adapter_contacts = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, contacts_array);
                    etpartyname = findViewById(R.id.partyname);
                    etpartyname.setThreshold(2);
                    etpartyname.setAdapter(adapter_contacts);

                    rgrpsalestype = activity_main_view.findViewById(R.id.radiogroup_sales_type);
                    int radio_salestype_selectedId = rgrpsalestype.getCheckedRadioButtonId();

                    rbtsalestype = findViewById(radio_salestype_selectedId);
                    // Find the EditText by its ID
                    EditText poDateEditText = findViewById(R.id.po_date_text);

                    // Get the current date
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                    String currentDate = dateFormat.format(calendar.getTime());

                    // Set the default date to the EditText
                    poDateEditText.setText(currentDate);

                    // 24-08-23 Priyanshu : iterating through Compo array
                    for (int i = 0; i < jsonArr_compo.length(); i++) {
                        JSONObject jsonObj = jsonArr_compo.getJSONObject(i);
                        String compo = jsonObj.getString("compo_name");
                        compo_array.add(compo);

                    }

                    final View editOrderFormView = getLayoutInflater().inflate(R.layout.order_form, null);

                    rgrpform = editOrderFormView.findViewById(R.id.radiogroup_form_type);
                    int radio_form_selectedId = rgrpform.getCheckedRadioButtonId();

                    rbtform = editOrderFormView.findViewById(radio_form_selectedId);

                    rgrpthickness_unit = editOrderFormView.findViewById(R.id.radiogroup_thickness_unit);
                    int radio_thickness_unit_selectedId = rgrpthickness_unit.getCheckedRadioButtonId();

                    rbtthicknessunit = editOrderFormView.findViewById(radio_thickness_unit_selectedId);

                    rgrpwidth_unit = editOrderFormView.findViewById(R.id.radiogroup_width_unit);
                    int radio_width_unit_selectedId = rgrpwidth_unit.getCheckedRadioButtonId();

                    rbtwidthunit = editOrderFormView.findViewById(radio_width_unit_selectedId);

                    rgrplength_unit = editOrderFormView.findViewById(R.id.radiogroup_length_unit);
                    int radio_length_unit_selectedId = rgrplength_unit.getCheckedRadioButtonId();

                    rbtlengthunit = editOrderFormView.findViewById(radio_length_unit_selectedId);

                    rgrpquantity_unit = editOrderFormView.findViewById(R.id.radiogroup_quantity_unit);
                    int radio_quantity_unit_selectedId = rgrpquantity_unit.getCheckedRadioButtonId();

                    rbtquantityunit = editOrderFormView.findViewById(radio_quantity_unit_selectedId);

                    rgrphardness = editOrderFormView.findViewById(R.id.radiogroup_hardness);
                    int radio_hardness_selectedId = rgrphardness.getCheckedRadioButtonId(); // Correct declaration
                    rbthardness = editOrderFormView.findViewById(radio_hardness_selectedId);


                    etreferencerow = findViewById(R.id.reference_row);
                    String default_sales_order = getString(R.string.compo_default) + "|"+ rbtform.getText() + "|0.7|" + rbtthicknessunit.getText() + "|350|" + rbtwidthunit.getText() + "|0|" + rbtlengthunit.getText() + "|100|" + rbtquantityunit.getText() +"|600|" + rbthardness.getText() +"|0|0|NA|NA";
                    String default_sales_order_with_space = default_sales_order.replace("|"," | ");
                    etreferencerow.setText(default_sales_order_with_space);
                } catch (JSONException e) {
                    Log.e("MainActivity", "Error parsing JSON in setDefaults", e);
                }
            });

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.btn) {
            preferenceHelper.putIsLogin(false);
            Intent intent = new Intent(MainActivity.this, loginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            MainActivity.this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public int isContactInList(String partyname) {
        Iterator<String> it = contacts_array.iterator();

        int contact_in_list = 0;
        while (it.hasNext()) {
            //Toast.makeText(MainActivity.this, it.next().toString(), Toast.LENGTH_LONG).show();
            if (partyname.compareTo(it.next()) == 0) {
                contact_in_list = 1;
                break;
            }
        }
        if (contact_in_list == 0) {
            Toast.makeText(MainActivity.this, "Please select contact from the list.", Toast.LENGTH_LONG).show();
            return 0;
        } else
            return 1;
    }

    // 23-08-2023 Priyanshu goradia : adding Compo in list function.
    public int isCompoInList(String compo) {
        Iterator<String> it = compo_array.iterator();

        int compo_in_list = 0;
        while (it.hasNext()) {
            //Toast.makeText(MainActivity.this, it.next().toString(), Toast.LENGTH_LONG).show();
            if (compo.compareTo(it.next()) == 0) {
                compo_in_list = 1;
                break;
            }
        }
        if (compo_in_list == 0) {
            Toast.makeText(MainActivity.this, "Please select Composition from the list.", Toast.LENGTH_LONG).show();
            return 0;
        } else
            return 1;
    }

    // 23-08-2023 : adding Quantity zero check.
    public int isValueZero(String quantity) {
        if (Float.parseFloat(quantity) == 0)
            return 1;
        else
            return 0;
    }

    // 23-08-2023 : adding Quantity zero check.
    public int isHardnessTolCorrect(String min_hardness_value, String max_hardness_value) {
        if (Float.parseFloat(min_hardness_value) != 0 && Float.parseFloat(max_hardness_value) != 0) {

            if (Float.parseFloat(min_hardness_value) < 45 || Float.parseFloat(min_hardness_value) > 250) {
                Toast.makeText(MainActivity.this, "Please enter Min Hardness value between 45 and 250.", Toast.LENGTH_LONG).show();
                return 1;
            } else if (Float.parseFloat(max_hardness_value) < 45 || Float.parseFloat(max_hardness_value) > 250) {
                Toast.makeText(MainActivity.this, "Please enter Max Hardness value between 45 and 250.", Toast.LENGTH_LONG).show();
                return 1;
            } else if (Float.parseFloat(max_hardness_value) <= Float.parseFloat(min_hardness_value)) {
                Toast.makeText(MainActivity.this, "Please enter Max Hardness must be greate than Min Hardness.", Toast.LENGTH_LONG).show();
                return 1;
            }
            else
                return 0;
        }
        else if ( Float.parseFloat(min_hardness_value) != 0 && Float.parseFloat(max_hardness_value) == 0 ){
                if (Float.parseFloat(min_hardness_value) < 45 || Float.parseFloat(min_hardness_value) > 250) {
                    Toast.makeText(MainActivity.this, "Please enter Min Hardness value between 45 and 250.", Toast.LENGTH_LONG).show();
                    return 1;
                }
                else
                    return 0;
        }
        else if ( Float.parseFloat(max_hardness_value) != 0 && Float.parseFloat(min_hardness_value) == 0 ){
            if (Float.parseFloat(max_hardness_value) < 45 || Float.parseFloat(max_hardness_value) > 250) {
                Toast.makeText(MainActivity.this, "Please enter Max Hardness value between 45 and 250.", Toast.LENGTH_LONG).show();
                return 1;
            }
            else
                return 0;
        }
        else
            return 0;
    }

    //29-08-2023 Priyanshu Goradia : Function to check Thickness value is correct
    public int isThicknessCorrect(String thickness_value, String thickness_unit_selected){
        if (Float.parseFloat(thickness_value) > 0) {
            if (thickness_unit_selected.equals(getString(R.string.unit_type_1)) && (Float.parseFloat(thickness_value) < 0.05 || Float.parseFloat(thickness_value) > 20)) {
                Toast.makeText(MainActivity.this, "Please enter Thickness value between 0.05 mm & 20mm.", Toast.LENGTH_LONG).show();
                return 1;
            } else if (thickness_unit_selected.equals(getString(R.string.unit_type_2)) && (Float.parseFloat(thickness_value) <= 0 || Float.parseFloat(thickness_value) > 1)) {
                Toast.makeText(MainActivity.this, "Please enter Thickness value between 0.01 inch & 1 inch.", Toast.LENGTH_LONG).show();
                return 1;
            } else if (thickness_unit_selected.equals(getString(R.string.unit_type_3)) && (Float.parseFloat(thickness_value) < 1 || Float.parseFloat(thickness_value) > 40)) {
                Toast.makeText(MainActivity.this, "Please enter Thickness value between 1 gauge & 40 gauge.", Toast.LENGTH_LONG).show();
                return 1;
            } else
                return 0;
        }
        else {
            Toast.makeText(MainActivity.this, "Please enter Thickness value greater than 0.", Toast.LENGTH_LONG).show();
            return 1;
        }
    }

    //29-08-2023 Priyanshu Goradia : Function to check Width value is correct
    public int isWidthCorrect(String width_value, String width_unit_selected){
        if (Float.parseFloat(width_value) > 0) {
            if (width_unit_selected.equals(getString(R.string.unit_type_1)) && (Float.parseFloat(width_value) < 6 || Float.parseFloat(width_value) > 650)) {
                Toast.makeText(MainActivity.this, "Please enter Width value between 6mm & 650mm.", Toast.LENGTH_LONG).show();
                return 1;
            } else if (width_unit_selected.equals(getString(R.string.unit_type_2)) && (Float.parseFloat(width_value) <= 0 || Float.parseFloat(width_value) > 26)) {
                Toast.makeText(MainActivity.this, "Please enter Width value between 0.01 inch & 26 inch.", Toast.LENGTH_LONG).show();
                return 1;
            } else if (width_unit_selected.equals(getString(R.string.unit_type_4)) && (Float.parseFloat(width_value) <= 0 || Float.parseFloat(width_value) > 3)) {
                Toast.makeText(MainActivity.this, "Please enter Width value between 0.01 feet & 3 feet.", Toast.LENGTH_LONG).show();
                return 1;
            } else
                return 0;
        }
        else {
            Toast.makeText(MainActivity.this, "Please enter Width value greater than 0.", Toast.LENGTH_LONG).show();
            return 1;
        }
    }


    //29-08-2023 Priyanshu Goradia : Function to check Length value is correct
    public int isLengthCorrect(String length_value, String length_unit_selected){
        if ( length_unit_selected.equals(getString(R.string.unit_type_1)) && (Float.parseFloat(length_value) < 25 || Float.parseFloat(length_value) > 4000) ) {
            Toast.makeText(MainActivity.this, "Please enter Length value between 25mm & 4000mm.", Toast.LENGTH_LONG).show();
            return 1;
        }
        else if (length_unit_selected.equals(getString(R.string.unit_type_2)) && (Float.parseFloat(length_value) < 1 || Float.parseFloat(length_value) > 160) ) {
            Toast.makeText(MainActivity.this, "Please enter Length value between 1 inch & 160 inch.", Toast.LENGTH_LONG).show();
            return 1;
        }
        else if (length_unit_selected.equals(getString(R.string.unit_type_4)) && (Float.parseFloat(length_value) <= 0 || Float.parseFloat(length_value) > 13) ) {
            Toast.makeText(MainActivity.this, "Please enter Length value between 0.01 feet & 13 feet.", Toast.LENGTH_LONG).show();
            return 1;
        }
        else
            return 0;
    }

    //29-08-2023 Priyanshu Goradia : Function to check Length value is correct
    public int isRateCorrect(String rate_value, String sales_type_selected){
        if ( Float.parseFloat(rate_value) <= 0  ) {
            Toast.makeText(MainActivity.this, "Please enter Rate more than INR. 0.", Toast.LENGTH_LONG).show();
            return 1;
        }
        else if (sales_type_selected.equals(getString(R.string.sale_type_2)) && (Float.parseFloat(rate_value) < 1 || Float.parseFloat(rate_value) > 200) ) {
            Toast.makeText(MainActivity.this, "Please enter JobWork Rate between INR. 1 & 200.", Toast.LENGTH_LONG).show();
            return 1;
        }
        else
            return 0;
    }

    public void onDelete(View v) {

        main_order_layout.removeView((View) v.getParent());
    }

    public void onDuplicateSO(View v) {
        LinearLayout row_view = (LinearLayout) v.getParent();

        dialogBuilder = new AlertDialog.Builder(this);
        ArrayAdapter<String> adapter_compo;
        final View editOrderFormView = getLayoutInflater().inflate(R.layout.order_form,null);


        etreferencerow = (TextView) row_view.getChildAt(2);
        String default_sales_order_with_space = etreferencerow.getText().toString();
        String default_sales_order=default_sales_order_with_space.replace(" | ", "|");
        String[] selected_sales_order= default_sales_order.split("\\|");

        // 29-08-2023 Priyanshu Goradia: Assigning the form ids to variables.
        etcompo = editOrderFormView.findViewById(R.id.compo);
        rgrpform = editOrderFormView.findViewById(R.id.radiogroup_form_type);
        EditText etthickness = editOrderFormView.findViewById(R.id.number_thickness_text);
        rgrpthickness_unit = editOrderFormView.findViewById(R.id.radiogroup_thickness_unit);
        EditText etwidth = editOrderFormView.findViewById(R.id.number_width_text);
        rgrpwidth_unit = editOrderFormView.findViewById(R.id.radiogroup_width_unit);
        EditText etlength = editOrderFormView.findViewById(R.id.number_length_text);
        rgrplength_unit = editOrderFormView.findViewById(R.id.radiogroup_length_unit);
        EditText etquantity = editOrderFormView.findViewById(R.id.number_quantity_text);
        rgrpquantity_unit = editOrderFormView.findViewById(R.id.radiogroup_quantity_unit);
        EditText etrate = editOrderFormView.findViewById(R.id.number_rate_text);
        rgrphardness = editOrderFormView.findViewById(R.id.radiogroup_hardness);
        EditText ethardnessmin = editOrderFormView.findViewById(R.id.number_hardness_min_text);
        EditText ethardnessmax = editOrderFormView.findViewById(R.id.number_hardness_max_text);
        EditText etsoitemcode = editOrderFormView.findViewById(R.id.text_so_item_code_text);
        EditText etsonote = editOrderFormView.findViewById(R.id.text_so_note_text);
        Button btsave = editOrderFormView.findViewById(R.id.save_inquiry);
        Button btcancel = editOrderFormView.findViewById(R.id.cancel_inquiry);


        // 23-08-2023 Priyanshu Goradia : Loading the data to variables
        adapter_compo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, compo_array);
        etcompo.setThreshold(2);
        etcompo.setAdapter(adapter_compo);
        etcompo.setText(selected_sales_order[0]);
        checkSelectedRadioButton (rgrpform,selected_sales_order[1]);
        etthickness.setText(selected_sales_order[2]);
        checkSelectedRadioButton (rgrpthickness_unit,selected_sales_order[3]);
        etwidth.setText(selected_sales_order[4]);
        checkSelectedRadioButton (rgrpwidth_unit,selected_sales_order[5]);
        etlength.setText(selected_sales_order[6]);
        checkSelectedRadioButton (rgrplength_unit,selected_sales_order[7]);
        etquantity.setText(selected_sales_order[8]);
        checkSelectedRadioButton (rgrpquantity_unit,selected_sales_order[9]);
        etrate.setText(selected_sales_order[10]);
        checkSelectedRadioButton (rgrphardness,selected_sales_order[11]);
        ethardnessmin.setText(selected_sales_order[12]);
        ethardnessmax.setText(selected_sales_order[13]);
        etsoitemcode.setText(selected_sales_order[14]);
        etsonote.setText(selected_sales_order[15]);


        dialogBuilder.setView(editOrderFormView);
        dialog = dialogBuilder.create();
        dialog.show();



        btsave.setOnClickListener(order_form_v -> {
            //public void onClick(View order_form){

                // 23-08-2023 Priyanshu Goradia : checking for Composition is from the list.
                String selected_compo = etcompo.getText().toString().trim();
                if(isCompoInList(selected_compo) == 0)
                {
                    Toast.makeText(MainActivity.this, "Please select a Composition from list", Toast.LENGTH_LONG).show();
                    return;
                }

                // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
                int radio_form_selectedId = rgrpform.getCheckedRadioButtonId();
                rbtform = editOrderFormView.findViewById(radio_form_selectedId);

                // 29-08-2023 Priyanshu Goradia : Checking if thickness is correct
                String thickness_value = etthickness.getText().toString().trim();
                // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
                int radio_thicknessunit_selectedId = rgrpthickness_unit.getCheckedRadioButtonId();
                rbtthicknessunit = editOrderFormView.findViewById(radio_thicknessunit_selectedId);
                if (thickness_value.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter Thickness more than 0", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(isThicknessCorrect(thickness_value,rbtthicknessunit.getText().toString()) == 1)
                {
                    return;
                }

                // 29-08-2023 Priyanshu Goradia : Checking if Width is correct
                String width_value = etwidth.getText().toString().trim();
                // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
                int radio_widthunit_selectedId = rgrpwidth_unit.getCheckedRadioButtonId();
                rbtwidthunit = editOrderFormView.findViewById(radio_widthunit_selectedId);
                if (width_value.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter Width more than 0.", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(isWidthCorrect(width_value,rbtwidthunit.getText().toString()) == 1)
                {
                    return;
                }

                // 23-08-2023 Priyanshu Goradia : checking if Sheet is selected and length is 0.
                int selected_form_id = rbtform.getId();

                // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
                int radio_lengthunit_selectedId = rgrplength_unit.getCheckedRadioButtonId();
                rbtlengthunit = editOrderFormView.findViewById(radio_lengthunit_selectedId);

                String length_value = etlength.getText().toString().trim();
                if (length_value.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter 0 or more value in Length", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (selected_form_id == R.id.radio_form_type_2) {
                    if(isLengthCorrect(length_value,rbtlengthunit.getText().toString()) == 1){
                        return;
                    }
                }
                else if (selected_form_id == R.id.radio_form_type_3 && Float.parseFloat(length_value) != 0){
                    if(isLengthCorrect(length_value,rbtlengthunit.getText().toString()) == 1){
                        return;
                    }
                }

                // 29-08-2023 Priyanshu Goradia : checking if Quantity is 0.
                String quantity_value = etquantity.getText().toString().trim();
                if (quantity_value.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter Quantity more than 0.", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(isValueZero(quantity_value) == 1)
                {
                    Toast.makeText(MainActivity.this, "Please enter Quantity greater than zero", Toast.LENGTH_LONG).show();
                    return;
                }

                // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
                int radio_quantityunit_selectedId = rgrpquantity_unit.getCheckedRadioButtonId();
                rbtquantityunit = editOrderFormView.findViewById(radio_quantityunit_selectedId);

                // 23-08-2023 Priyanshu Goradia : checking if Rate is 0.
                rgrpsalestype = findViewById(R.id.radiogroup_sales_type);
                int radio_salestype_selectedId = rgrpsalestype.getCheckedRadioButtonId();
                rbtsalestype = findViewById(radio_salestype_selectedId);
                String rate_value = etrate.getText().toString().trim();
                if (rate_value.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter Rate more than 0.", Toast.LENGTH_LONG).show();
                    return;
                }
                else if(isRateCorrect(rate_value,rbtsalestype.getText().toString()) == 1)
                {
                    //Toast.makeText(MainActivity.this, "Please enter Rate greater than zero", Toast.LENGTH_LONG).show();
                    return;
                }


                // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
                int radio_hardnessunit_selectedId = rgrphardness.getCheckedRadioButtonId();
                rbthardness = editOrderFormView.findViewById(radio_hardnessunit_selectedId);

                // 29-08-2023 Priyanshu Goradia : checking if hardness values are correct.
                String min_hardness_value = ethardnessmin.getText().toString().trim();
                String max_hardness_value = ethardnessmax.getText().toString().trim();
                if (min_hardness_value.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter 0 or more value in Min Hardness.", Toast.LENGTH_LONG).show();
                    return;
                }
                else if (max_hardness_value.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter 0 or more value in Max Hardness.", Toast.LENGTH_LONG).show();
                    return;
                }
                else {
                    if(isHardnessTolCorrect(min_hardness_value,max_hardness_value) == 1)
                    {
                        return;
                    }
                }

                String order = etcompo.getText()+"|"+rbtform.getText().toString()+"|"+etthickness.getText()+"|"+rbtthicknessunit.getText().toString()+"|"+etwidth.getText()+"|"+rbtwidthunit.getText().toString()+"|"+etlength.getText()+"|"+rbtlengthunit.getText().toString()+"|"+etquantity.getText()+"|"+rbtquantityunit.getText().toString()+"|"+etrate.getText()+"|"+rbthardness.getText().toString()+"|"+ethardnessmin.getText()+"|"+ethardnessmax.getText()+"|"+etsoitemcode.getText()+"|"+etsonote.getText();
                LinearLayout new_row_added = (LinearLayout) main_order_layout.getChildAt(main_order_layout.getChildCount()-1);
                TextView duplicate_row_text = (TextView) new_row_added.getChildAt(2);
                String order_with_space = order.replace("|"," | ");
                duplicate_row_text.setText(order_with_space);
                dialog.dismiss();
        });

        btcancel.setOnClickListener(v1 -> dialog.dismiss());
    }

    public void onSaveOrder(View v) {
        LinearLayout row;
        TextView row_text;

        String[] sales_order_array = new String[main_order_layout.getChildCount()];

        partyname = etpartyname.getText().toString().trim();

        // Validate party name
        if (isContactInList(partyname) == 0) {
            return;
        }

        rgrpsalestype = findViewById(R.id.radiogroup_sales_type);
        int radio_salestype_selectedId = rgrpsalestype.getCheckedRadioButtonId();
        rbtsalestype = findViewById(radio_salestype_selectedId);

        RadioGroup rgrporderby = findViewById(R.id.radiogroup_order_from);
        int radio_orderby_selectedId = rgrporderby.getCheckedRadioButtonId();
        rbtorderby = findViewById(radio_orderby_selectedId);

        EditText etpono = findViewById(R.id.po_text);
        pono = etpono.getText().toString().trim();

        EditText etpodate = findViewById(R.id.po_date_text);
        podate = etpodate.getText().toString().trim();

        EditText etmsonote = findViewById(R.id.mso_notes_text);
        msonote = etmsonote.getText().toString().trim();

        // Collect sales order rows
        for (int i = 0; i < main_order_layout.getChildCount(); i++) {
            row = (LinearLayout) main_order_layout.getChildAt(i);
            row_text = (TextView) row.getChildAt(2);
            sales_order_array[i] = row_text.getText().toString();
        }

        // Thread execution starts here
        ExecutorService service = Executors.newSingleThreadExecutor();

        service.execute(() -> {
            runOnUiThread(() -> {
                // Pre-execute UI work if needed
            });

            // Build WhatsApp text message
            StringBuilder paramlist = new StringBuilder("\nORDER ACKNOWLEDGEMENT \n");
            paramlist.append("\nParty Name: ").append(partyname);
            paramlist.append("\nSales Type: ").append(rbtsalestype.getText().toString());

            if (!pono.isEmpty()) {
                paramlist.append("\nPO_No: ").append(pono);
            }

            paramlist.append("\nPo Date: ").append(podate);
            paramlist.append("\nPO Source: ").append(rbtorderby.getText().toString());

            if (!msonote.isEmpty()) {
                paramlist.append("\nNote: ").append(msonote);
            }

            paramlist.append("\n\nWe acknowledge the receipt of your following order: \n");

            // Convert each sales order row into WhatsApp-readable format
            for (int i = 0; i < sales_order_array.length; i++) {
                paramlist.append("\n").append(i + 1).append(") ");

                String[] elements = sales_order_array[i].split("\\|", -1);

                String temp_compo = elements[0].trim();
                String temp_form = elements[1].trim();
                float min_hardness = Float.parseFloat(elements[12].trim());
                float max_hardness = Float.parseFloat(elements[13].trim());
                String temp_soitemcode = elements[14].trim().toUpperCase();
                String temp_sonote = elements[15].trim().toUpperCase();

                if (temp_compo.contains("CuZn")) {
                    paramlist.append("Brass (").append(temp_compo).append(") ");
                } else if (temp_compo.contains("Cop")) {
                    paramlist.append("Copper (").append(temp_compo).append(") ");
                } else if (temp_compo.contains("CuSn")) {
                    paramlist.append("Bronze (").append(temp_compo).append(") ");
                } else {
                    paramlist.append(temp_compo).append(" ");
                }

                paramlist.append(temp_form).append(" - ");
                paramlist.append(elements[2].trim()).append(elements[3].trim()).append(" X ");
                paramlist.append(elements[4].trim()).append(elements[5].trim());

                if (!temp_form.contains("Coil")) {
                    paramlist.append(" X ").append(elements[6].trim()).append(elements[7].trim()).append(" ");
                }

                paramlist.append(" - ");
                paramlist.append(elements[8].trim()).append(elements[9].trim()).append(" ");

                paramlist.append(elements[11].trim());

                if (min_hardness != 0) {
                    if (max_hardness != 0) {
                        paramlist.append("(").append(min_hardness).append(" - ").append(max_hardness).append(")");
                    } else {
                        paramlist.append("(min ").append(min_hardness).append(")");
                    }
                } else if (max_hardness != 0) {
                    paramlist.append("(max ").append(max_hardness).append(")");
                }

                paramlist.append(" - INR.").append(elements[10].trim()).append("/- ");

                if (!temp_soitemcode.equals("NA")) {
                    paramlist.append("ItemCode:-").append(temp_soitemcode).append(" ");
                }

                if (!temp_sonote.equals("NA")) {
                    paramlist.append("(").append(temp_sonote).append(")");
                }

                paramlist.append("\n");
            }

            paramlist.append("\nIf any changes, do let us know.\n");

            // ----------------------------
            // ✅ NEW WHATSAPP SHARE BLOCK
            // ----------------------------
            try {
                Bitmap imgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ghwhatsapplogo);

                File imageFile = new File(getCacheDir(), "image.png");
                FileOutputStream fos = new FileOutputStream(imageFile);
                imgBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                Uri imgBitmapUri = FileProvider.getUriForFile(
                        MainActivity.this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        imageFile
                );

                // Run intent creation and start on UI thread
                runOnUiThread(() -> {
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    sendIntent.setType("image/*"); // Generic image type
                    sendIntent.putExtra(Intent.EXTRA_STREAM, imgBitmapUri);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, paramlist.toString());

                    // ClipData is good for Android 10+ permissions
                    sendIntent.setClipData(ClipData.newUri(getContentResolver(), "Order Image", imgBitmapUri));

                    Intent chooser = Intent.createChooser(sendIntent, "Share Order Acknowledgement");
                    startActivity(chooser);
                });


            } catch (Exception e) {
                runOnUiThread(() ->
                        Toast.makeText(MainActivity.this, "Not able to load image or send via WhatsApp.", Toast.LENGTH_LONG).show()
                );
                Log.e("MainActivity", "WhatsApp share error", e);
            }

            // ---------------------------------------------------
            //  NOTE: Your database save logic continues below...

            // ---------------------------------------------------

            // (Your DB connection + POST code remains unchanged)

        });
    }

    public void pickdate(View v) {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        EditText etpodate = findViewById(R.id.po_date_text);

        // Create a DatePickerDialog and set the maximum date
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> etpodate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year), mYear, mMonth, mDay);

        // Set the maximum date to the current date
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

        datePickerDialog.show();
    }

    public void onClearOrder(View v) {
        MainActivity.this.finishAffinity();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    // 29-08-2023 Priyanshu Goradia : Created this funtion to check the selected Radio button in the Radio group
    public void checkSelectedRadioButton(RadioGroup radioGroup,String desiredTextValue ) {

        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            View radioButton = radioGroup.getChildAt(i);
            if (radioButton instanceof RadioButton) {
                RadioButton radioButtonView = (RadioButton) radioButton;
                if (radioButtonView.getText().toString().equals(desiredTextValue)) {
                    radioButtonView.setChecked(true);
                    break; // Stop the loop once the desired radio button is found and checked
                }
            }
        }
    }


    public void onEditSO(View v) {
        LinearLayout row_view = (LinearLayout) v.getParent();

        dialogBuilder = new AlertDialog.Builder(this);
        ArrayAdapter<String> adapter_compo;
        final View editOrderFormView = getLayoutInflater().inflate(R.layout.order_form,null);

        etreferencerow = (TextView) row_view.getChildAt(2);
        String default_sales_order_with_space = etreferencerow.getText().toString();
        String  default_sales_order = default_sales_order_with_space.replace(" | ", "|");
        String[] selected_sales_order= default_sales_order.split("\\|");

        // 29-08-2023 Priyanshu Goradia: Assigning the form ids to variables.
        etcompo = editOrderFormView.findViewById(R.id.compo);
        rgrpform = editOrderFormView.findViewById(R.id.radiogroup_form_type);
        EditText etthickness = editOrderFormView.findViewById(R.id.number_thickness_text);
        rgrpthickness_unit = editOrderFormView.findViewById(R.id.radiogroup_thickness_unit);
        EditText etwidth = editOrderFormView.findViewById(R.id.number_width_text);
        rgrpwidth_unit = editOrderFormView.findViewById(R.id.radiogroup_width_unit);
        EditText etlength = editOrderFormView.findViewById(R.id.number_length_text);
        rgrplength_unit = editOrderFormView.findViewById(R.id.radiogroup_length_unit);
        EditText etquantity = editOrderFormView.findViewById(R.id.number_quantity_text);
        rgrpquantity_unit = editOrderFormView.findViewById(R.id.radiogroup_quantity_unit);
        EditText etrate = editOrderFormView.findViewById(R.id.number_rate_text);
        rgrphardness = editOrderFormView.findViewById(R.id.radiogroup_hardness);
        EditText ethardnessmin = editOrderFormView.findViewById(R.id.number_hardness_min_text);
        EditText ethardnessmax = editOrderFormView.findViewById(R.id.number_hardness_max_text);
        EditText etsoitemcode = editOrderFormView.findViewById(R.id.text_so_item_code_text);
        EditText etsonote = editOrderFormView.findViewById(R.id.text_so_note_text);
        Button btsave = editOrderFormView.findViewById(R.id.save_inquiry);
        Button btcancel = editOrderFormView.findViewById(R.id.cancel_inquiry);


        // 23-08-2023 Priyanshu Goradia : Loading the data to variables
        adapter_compo = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, compo_array);
        etcompo.setThreshold(2);
        etcompo.setAdapter(adapter_compo);
        etcompo.setText(selected_sales_order[0]);
        checkSelectedRadioButton (rgrpform,selected_sales_order[1]);
        etthickness.setText(selected_sales_order[2]);
        checkSelectedRadioButton (rgrpthickness_unit,selected_sales_order[3]);
        etwidth.setText(selected_sales_order[4]);
        checkSelectedRadioButton (rgrpwidth_unit,selected_sales_order[5]);
        etlength.setText(selected_sales_order[6]);
        checkSelectedRadioButton (rgrplength_unit,selected_sales_order[7]);
        etquantity.setText(selected_sales_order[8]);
        checkSelectedRadioButton (rgrpquantity_unit,selected_sales_order[9]);
        etrate.setText(selected_sales_order[10]);
        checkSelectedRadioButton (rgrphardness,selected_sales_order[11]);
        ethardnessmin.setText(selected_sales_order[12]);
        ethardnessmax.setText(selected_sales_order[13]);
        etsoitemcode.setText(selected_sales_order[14]);
        etsonote.setText(selected_sales_order[15]);

        dialogBuilder.setView(editOrderFormView);
        dialog = dialogBuilder.create();
        dialog.show();

        btsave.setOnClickListener(order_form_v -> {
            // 23-08-2023 Priyanshu Goradia : checking for Composition is from the list.
            String selected_compo = etcompo.getText().toString().trim();
            if(isCompoInList(selected_compo) == 0)
            {
                Toast.makeText(MainActivity.this, "Please select a Composition from list", Toast.LENGTH_LONG).show();
                return;
            }

            // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
            int radio_form_selectedId = rgrpform.getCheckedRadioButtonId();
            rbtform = editOrderFormView.findViewById(radio_form_selectedId);

            // 29-08-2023 Priyanshu Goradia : Checking if thickness is correct
            String thickness_value = etthickness.getText().toString().trim();
            // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
            int radio_thicknessunit_selectedId = rgrpthickness_unit.getCheckedRadioButtonId();
            rbtthicknessunit = editOrderFormView.findViewById(radio_thicknessunit_selectedId);
            if (thickness_value.isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter Thickness more than 0", Toast.LENGTH_LONG).show();
                return;
            }
            else if(isThicknessCorrect(thickness_value,rbtthicknessunit.getText().toString()) == 1)
            {
                return;
            }

            // 29-08-2023 Priyanshu Goradia : Checking if Width is correct
            String width_value = etwidth.getText().toString().trim();
            // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
            int radio_widthunit_selectedId = rgrpwidth_unit.getCheckedRadioButtonId();
            rbtwidthunit = editOrderFormView.findViewById(radio_widthunit_selectedId);
            if (width_value.isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter Width more than 0.", Toast.LENGTH_LONG).show();
                return;
            }
            else if(isWidthCorrect(width_value,rbtwidthunit.getText().toString()) == 1)
            {
                return;
            }

            // 23-08-2023 Priyanshu Goradia : checking if Sheet is selected and length is 0.
            int selected_form_id = rbtform.getId();

            // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
            int radio_lengthunit_selectedId = rgrplength_unit.getCheckedRadioButtonId();
            rbtlengthunit = editOrderFormView.findViewById(radio_lengthunit_selectedId);

            String length_value = etlength.getText().toString().trim();
            if (length_value.isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter 0 or more value in Length", Toast.LENGTH_LONG).show();
                return;
            }
            else if (selected_form_id == R.id.radio_form_type_2) {
                if(isLengthCorrect(length_value,rbtlengthunit.getText().toString()) == 1){
                    return;
                }
            }
            else if (selected_form_id == R.id.radio_form_type_3 && Float.parseFloat(length_value) != 0){
                if(isLengthCorrect(length_value,rbtlengthunit.getText().toString()) == 1){
                    return;
                }
            }

            // 29-08-2023 Priyanshu Goradia : checking if Quantity is 0.
            String quantity_value = etquantity.getText().toString().trim();
            if (quantity_value.isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter Quantity more than 0.", Toast.LENGTH_LONG).show();
                return;
            }
            else if(isValueZero(quantity_value) == 1)
            {
                Toast.makeText(MainActivity.this, "Please enter Quantity greater than zero", Toast.LENGTH_LONG).show();
                return;
            }

            // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
            int radio_quantityunit_selectedId = rgrpquantity_unit.getCheckedRadioButtonId();
            rbtquantityunit = editOrderFormView.findViewById(radio_quantityunit_selectedId);

            // 23-08-2023 Priyanshu Goradia : checking if Rate is 0.
            rgrpsalestype = findViewById(R.id.radiogroup_sales_type);
            int radio_salestype_selectedId = rgrpsalestype.getCheckedRadioButtonId();
            rbtsalestype = findViewById(radio_salestype_selectedId);
            String rate_value = etrate.getText().toString().trim();
            if (rate_value.isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter Rate more than 0.", Toast.LENGTH_LONG).show();
                return;
            }
            else if(isRateCorrect(rate_value,rbtsalestype.getText().toString()) == 1)
            {
                //Toast.makeText(MainActivity.this, "Please enter Rate greater than zero", Toast.LENGTH_LONG).show();
                return;
            }


            // 29-08-2023 Priyanshu Goradia : Getting Selected Radio button id.
            int radio_hardnessunit_selectedId = rgrphardness.getCheckedRadioButtonId();
            rbthardness = editOrderFormView.findViewById(radio_hardnessunit_selectedId);

            // 29-08-2023 Priyanshu Goradia : checking if hardness values are correct.
            String min_hardness_value = ethardnessmin.getText().toString().trim();
            String max_hardness_value = ethardnessmax.getText().toString().trim();
            if (min_hardness_value.isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter 0 or more value in Min Hardness.", Toast.LENGTH_LONG).show();
                return;
            }
            else if (max_hardness_value.isEmpty()){
                Toast.makeText(MainActivity.this, "Please enter 0 or more value in Max Hardness.", Toast.LENGTH_LONG).show();
                return;
            }
            else {
                if(isHardnessTolCorrect(min_hardness_value,max_hardness_value) == 1)
                {
                    return;
                }
            }

            String order = etcompo.getText()+"|"+rbtform.getText().toString()+"|"+etthickness.getText()+"|"+rbtthicknessunit.getText().toString()+"|"+etwidth.getText()+"|"+rbtwidthunit.getText().toString()+"|"+etlength.getText()+"|"+rbtlengthunit.getText().toString()+"|"+etquantity.getText()+"|"+rbtquantityunit.getText().toString()+"|"+etrate.getText()+"|"+rbthardness.getText().toString()+"|"+ethardnessmin.getText()+"|"+ethardnessmax.getText()+"|"+etsoitemcode.getText()+"|"+etsonote.getText();
            LinearLayout new_row_added = (LinearLayout) main_order_layout.getChildAt(main_order_layout.getChildCount()-1);
            TextView duplicate_row_text = (TextView) new_row_added.getChildAt(2);
            String order_with_space = order.replace("|"," | ");
            duplicate_row_text.setText(order_with_space);
            dialog.dismiss();
        });

        btcancel.setOnClickListener(v1 -> dialog.dismiss());
    }
}
