package in.edureal.locationlogs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity{

    private SweetAlertDialog loader;

    private EditText visitReason;
    private boolean includeReason;
    private SwitchCompat networkSwitcher;
    private boolean includeNetwork;
    private SwitchCompat batterySwitcher;
    private boolean includeBattery;
    private SwitchCompat addressSwitcher;
    private boolean includeAddress;
    private SwitchCompat weatherSwitcher;
    private boolean includeWeather;

    private Double latitude;
    private Double longitude;

    private int batteryLevel;
    private String mobile="";
    private String wifi="";
    private String reason;
    private String address;

    private String time12hour;
    private String time24hour;

    private String date;

    private String climate;
    private double temperature;

    private MySQLiteHelper helper;
    private SQLiteDatabase database;
    private int numOfLogs;

    private double kelvinToCelcius(double temp){
        return (temp-273.15);
    }

    private int getBatteryLevel(){
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, iFilter);

        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        return (int) ((level / (float) scale) * 100);
    }

    public void createLog(View view){
        address="";
        time12hour="";
        time24hour="";
        climate="";
        temperature=0.0;
        date="";

        DateFormat df = new SimpleDateFormat("dd MMMM YYYY");
        Date dateobj = new Date();
        date=df.format(dateobj);
        df = new SimpleDateFormat("h:m a");
        time12hour=df.format(dateobj);
        df = new SimpleDateFormat("H:m");
        time24hour=df.format(dateobj);

        reason=visitReason.getText().toString().trim();
        if(reason.length()==0){
            includeReason=false;
        }else{
            includeReason=true;
        }

        mobile="";
        wifi="";
        if(includeNetwork){
            ConnectivityManager connect = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connect.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED){
                mobile="Turned On";
            }else{
                mobile="Turned Off";
            }

            if(connect.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
                wifi="Turned On";
            }else{
                wifi="Turned Off";
            }

        }

        batteryLevel=0;
        if(includeBattery){
            batteryLevel=getBatteryLevel();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setCustomImage(R.drawable.ic_location)
                        .setTitleText("Permission Required")
                        .setContentText("Permission to access your location is required to continue.")
                        .setConfirmText("Settings")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                sweetAlertDialog.dismiss();
                                startActivity(intent);
                            }
                        })
                        .setCancelText("Cancel")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .show();

            }else{
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }


        } else{

            getUserLocation();

        }
    }

    private void showError(String str){
        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(str)
                .show();
    }

    private void finalStep(){
        helper.insertData(database,latitude,longitude,date,time12hour,time24hour,address,reason,mobile,wifi,batteryLevel,climate,temperature);
        visitReason.setText("");
        new SweetAlertDialog(this, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Yipee...")
                .setContentText("Your log was successfully created. Click on 'Open my Location Log' to check it out.")
                .show();
    }

    public void viewMyLogs(View view){
        //Cursor cursor = database.rawQuery("SELECT _id, latitude, longitude, logDate, logTime12, logTime24, address, reason, mobile, wifi, batteryRate, climate, temperature FROM logs ORDER BY _id DESC", new String[]{});
        //numOfLogs=cursor.getCount();
        //cursor.moveToFirst();

        //while(numOfLogs>=1){

            //System.out.println("_id - "+cursor.getInt(0));
            //System.out.println("latitude - "+cursor.getDouble(1));
            //System.out.println("longitude - "+cursor.getDouble(2));
            //System.out.println("logDate - "+cursor.getString(3));
            //System.out.println("logTime12 - "+cursor.getString(4));
            //System.out.println("logTime24 - "+cursor.getString(5));
            //System.out.println("address - "+cursor.getString(6));
            //System.out.println("reason - "+cursor.getString(7));
            //System.out.println("mobile - "+cursor.getString(8));
            //System.out.println("wifi - "+cursor.getString(9));
            //System.out.println("batteryRate - "+cursor.getInt(10));
            //System.out.println("climate - "+cursor.getString(11));
            //System.out.println("temperature - "+cursor.getDouble(12));

            //System.out.println();
            //System.out.println();
            //System.out.println();
            //System.out.println();

            //numOfLogs--;
        //}

        Cursor cursor = database.rawQuery("SELECT _id FROM logs", new String[]{});
        if(cursor!=null && cursor.getCount()>=0){
            numOfLogs=cursor.getCount();
        }else{
            numOfLogs=0;
        }

        try{
            cursor.close();
        }catch(NullPointerException e) {
            Log.i("Error",e.toString());
        }

        if(numOfLogs==0){
            showError("You don't have any logs.");
        }else{
            Intent intent=new Intent(MainActivity.this, LogsActivity.class);
            startActivity(intent);
        }

    }

    private void getWeather(){

        StringRequest stringRequest = new StringRequest(0, "https://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&appid=44ef9267da8ab535d8a498af41aaa8d4",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{

                            JSONObject main=new JSONObject(response);
                            JSONArray weatherArr=main.getJSONArray("weather");
                            JSONObject weatherObj=weatherArr.getJSONObject(0);
                            climate=weatherObj.getString("main")+" - "+weatherObj.getString("description");
                            temperature=kelvinToCelcius(main.getJSONObject("main").getDouble("temp"));
                            loader.hide();
                            finalStep();

                        }catch(JSONException e){
                            loader.hide();
                            showError("Weather API request limit reached. Please try again later.");
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    loader.hide();
                    showError("There is no internet connection. Please try again later.");
                }else if(error instanceof TimeoutError){
                    loader.hide();
                    showError("Your request has timed out. Please try again later.");
                }else if(error instanceof AuthFailureError) {
                    loader.hide();
                    showError("There was an Authentication Failure while performing the request. Please try again later.");
                }else if(error instanceof ServerError) {
                    loader.hide();
                    showError("Server responded with a error response. Please try again later.");
                }else if(error instanceof NetworkError) {
                    loader.hide();
                    showError("There was network error while performing the request. Please try again later.");
                }else if(error instanceof ParseError) {
                    loader.hide();
                    showError("Server response could not be parsed. Please try again later.");
                }else{
                    loader.hide();
                    showError("There was a problem. Please try again later.");
                }
            }
        });

        stringRequest.setTag("API");
        VollySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void getUserLocation(){
        loader.show();
        LocationListener listener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitude=location.getLatitude();
                longitude=location.getLongitude();
                if(includeAddress){
                    StringRequest stringRequest = new StringRequest(0, "https://us1.locationiq.com/v1/reverse.php?key=19b214da76793e&lat="+latitude+"&lon="+longitude+"&format=json",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try{

                                        JSONObject obj=new JSONObject(response);
                                        address=obj.getString("display_name");
                                        if(obj.has("error")){
                                            loader.hide();
                                            showError("Geocoding request limit reached. Please try again later.");
                                        }else{

                                            if(includeWeather){
                                                getWeather();
                                            }else{
                                                loader.hide();
                                                finalStep();
                                            }

                                        }

                                    }catch(JSONException e){
                                        loader.hide();
                                        showError("There was a problem. Please try again later.");
                                    }

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error instanceof NoConnectionError) {
                                loader.hide();
                                showError("There is no internet connection. Please try again later.");
                            }else if(error instanceof TimeoutError){
                                loader.hide();
                                showError("Your request has timed out. Please try again later.");
                            }else if(error instanceof AuthFailureError) {
                                loader.hide();
                                showError("There was an Authentication Failure while performing the request. Please try again later.");
                            }else if(error instanceof ServerError) {
                                loader.hide();
                                showError("Server responded with a error response. Please try again later.");
                            }else if(error instanceof NetworkError) {
                                loader.hide();
                                showError("There was network error while performing the request. Please try again later.");
                            }else if(error instanceof ParseError) {
                                loader.hide();
                                showError("Server response could not be parsed. Please try again later.");
                            }else{
                                loader.hide();
                                showError("There was a problem. Please try again later.");
                            }
                        }
                    });

                    stringRequest.setTag("API");

                    VollySingleton.getInstance(MainActivity.this).addToRequestQueue(stringRequest);
                }else{
                    if(includeWeather){
                        getWeather();
                    }else{
                        loader.hide();
                        finalStep();
                    }
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
                loader.hide();
                showError("Please enable your GPS.");
            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                loader.hide();
                showError("Please enable your GPS.");
            }
        };

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        final Looper looper = null;
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            try{

                if(locationManager.isProviderEnabled("gps")){
                    locationManager.requestSingleUpdate(criteria, listener, looper);
                }else{
                    loader.hide();
                    showError("Please enable your GPS.");
                }

            }catch(NullPointerException e){
                loader.hide();
                showError("There was a problem getting your location.");
            }
        }else{
            loader.hide();
            showError("There was a problem getting your location.");
        }

    }

    public void githubSources(){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_about);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.tv_version)).setText("Version " + BuildConfig.VERSION_NAME);

        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        ((Button) dialog.findViewById(R.id.bt_openGitHub)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/vishu103/"));
                startActivity(browserIntent);
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Location Logs");
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();

        loader=new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        loader.setTitleText("Loading");
        loader.setCancelable(false);

        visitReason=(EditText) findViewById(R.id.visitReason);
        includeReason=false;
        networkSwitcher=(SwitchCompat) findViewById(R.id.networkSwitcher);
        includeNetwork=true;
        batterySwitcher=(SwitchCompat) findViewById(R.id.batterySwitcher);
        includeBattery=true;
        addressSwitcher=(SwitchCompat) findViewById(R.id.addressSwitcher);
        includeAddress=false;
        weatherSwitcher=(SwitchCompat) findViewById(R.id.weatherSwitcher);
        includeWeather=false;
        latitude=0.0d;
        longitude=0.0d;
        helper=new MySQLiteHelper(this);
        database=helper.getWritableDatabase();

        networkSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    includeNetwork=true;
                }else{
                    includeNetwork=false;
                }
            }
        });

        batterySwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    includeBattery=true;
                }else{
                    includeBattery=false;
                }
            }
        });

        addressSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    includeAddress=true;
                }else{
                    includeAddress=false;
                }
            }
        });

        weatherSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    includeWeather=true;
                }else{
                    includeWeather=false;
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.developer_info){
            githubSources();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(VollySingleton.getInstance(this.getApplicationContext()).getRequestQueue()!=null){
            VollySingleton.getInstance(this.getApplicationContext()).getRequestQueue().cancelAll("API");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loader.dismiss();
    }
}
