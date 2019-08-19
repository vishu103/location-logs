package in.edureal.locationlogs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SingleLog extends AppCompatActivity {

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_log);
        initToolbar();
        getSupportActionBar().setTitle("Log ID - "+SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getInt("id",1));

        TextView datetime=(TextView) findViewById(R.id.datetime);
        TextView time24hour=(TextView) findViewById(R.id.time24hour);
        TextView coordinates=(TextView) findViewById(R.id.coordinates);
        TextView address=(TextView) findViewById(R.id.address);
        TextView reason=(TextView) findViewById(R.id.reason);
        TextView network=(TextView) findViewById(R.id.network);
        TextView battery=(TextView) findViewById(R.id.battery);
        TextView weather=(TextView) findViewById(R.id.weather);

        datetime.setText("Date - "+SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getString("logDate","")+"\nTime - "+SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getString("logTime12",""));
        time24hour.setText(SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getString("logTime24",""));
        coordinates.setText("Lat - "+SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getFloat("latitude",0.0f)+"\nLong - "+SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getFloat("longitude",0.0f));
        if(SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getString("address","").trim().length()==0){
            ((LinearLayout)findViewById(R.id.addressLayout)).setVisibility(View.GONE);
        }else{
            address.setText(SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getString("address",""));
        }

        if(SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getString("reason","").trim().length()==0){
            ((LinearLayout)findViewById(R.id.reasonLayout)).setVisibility(View.GONE);
        }else{
            reason.setText(SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getString("reason",""));
        }

        if(SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getString("mobile","").trim().length()==0){
            ((LinearLayout)findViewById(R.id.networkLayout)).setVisibility(View.GONE);
        }else{
            network.setText("Mobile - "+SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getString("mobile","")+"\nWiFi - "+SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getString("wifi",""));
        }

        if(SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getInt("batteryRate",0)==0){
            ((LinearLayout)findViewById(R.id.batteryLayout)).setVisibility(View.GONE);
        }else{
            battery.setText("Charged - "+SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getInt("batteryRate",0)+"%");
        }

        if(SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getFloat("temperature",0.0f)==0.0f){
            ((LinearLayout)findViewById(R.id.weatherLayout)).setVisibility(View.GONE);
        }else{
            weather.setText(SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getString("climate","")+"\nTemperature - "+SharedPreferenceSingleton.getInstance(this.getApplicationContext()).getSp().getFloat("temperature",0.0f)+"°C");
        }

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
        }else{
            Intent intent=new Intent(SingleLog.this, LogsActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(SingleLog.this, LogsActivity.class);
        startActivity(intent);
        finish();
    }

}
