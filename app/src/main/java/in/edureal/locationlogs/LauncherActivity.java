package in.edureal.locationlogs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);

        Glide.with(LauncherActivity.this).load(R.drawable.icon_500).into((ImageView) findViewById(R.id.logo));

        new CountDownTimer(3000,3000){

            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent intent=new Intent(LauncherActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }.start();

    }
}
