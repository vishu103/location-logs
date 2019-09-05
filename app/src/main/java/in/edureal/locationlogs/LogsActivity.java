package in.edureal.locationlogs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LogsActivity extends AppCompatActivity {

    private SweetAlertDialog loader;

    RecyclerView logsRecycler;
    RecyclerView.Adapter adapter;

    private List<ListItem> listItems;

    private MySQLiteHelper helper;
    private SQLiteDatabase database;
    private int totalResults;

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
        getSupportActionBar().setTitle("Location Logs");
        Tools.setSystemBarColor(this, R.color.grey_5);
        Tools.setSystemBarLight(this);
    }

    private void setupRecycler(){
        loader.show();
        Cursor cursor = database.rawQuery("SELECT _id, latitude, longitude, logDate, logTime12, logTime24, address, reason, mobile, wifi, batteryRate, climate, temperature FROM logs ORDER BY _id DESC", new String[]{});
        if(cursor==null){
            loader.hide();
            Intent intent=new Intent(LogsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        int totalLogs=cursor.getCount();
        totalResults=totalLogs;
        if(totalLogs==0){
            loader.hide();
            Intent intent=new Intent(LogsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        cursor.moveToFirst();

        listItems.clear();
        while(totalLogs>=1){
            ListItem item=new ListItem(cursor.getInt(0),cursor.getDouble(1),cursor.getDouble(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(8),cursor.getString(9),cursor.getInt(10),cursor.getString(11),cursor.getDouble(12));
            listItems.add(item);
            totalLogs--;
            cursor.moveToNext();
        }

        adapter.notifyDataSetChanged();

        try{
            cursor.close();
        }catch(NullPointerException e) {
            Log.i("Error",e.toString());
        }

        loader.hide();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);
        initToolbar();

        loader=new SweetAlertDialog(LogsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        loader.setTitleText("Loading");
        loader.setCancelable(false);

        listItems=new ArrayList<>();
        logsRecycler=(RecyclerView) findViewById(R.id.logsRecycler);
        new ItemTouchHelper(itemSimpleCallback).attachToRecyclerView(logsRecycler);
        logsRecycler.setHasFixedSize(true);
        logsRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter=new MyAdapter(listItems, this,1);
        logsRecycler.setAdapter(adapter);

        helper=new MySQLiteHelper(this);
        database=helper.getWritableDatabase();

        setupRecycler();

        AdView adView=(AdView) findViewById(R.id.adView);
        AdRequest request=new AdRequest.Builder().build();
        adView.loadAd(request);

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
            Intent intent=new Intent(LogsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(LogsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        loader.dismiss();
    }

    ItemTouchHelper.SimpleCallback itemSimpleCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT){

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            adapter.notifyItemChanged(viewHolder.getAdapterPosition());
            if(direction == ItemTouchHelper.LEFT){
                String str="DELETE FROM logs WHERE _id="+listItems.get(viewHolder.getAdapterPosition()).getLogId();
                database.execSQL(str);
                totalResults--;
                if(totalResults==0){
                    Intent intent=new Intent(LogsActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    listItems.remove(viewHolder.getAdapterPosition());
                    adapter.notifyDataSetChanged();
                    Toast.makeText(LogsActivity.this, "Log deleted.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

}
