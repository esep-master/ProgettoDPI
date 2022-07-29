package it.bleb.dpi.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import it.bleb.blebandroid.Blebricks;
import it.bleb.blebandroid.utils.Logger;
import it.bleb.dpi.BuildConfig;
import it.bleb.dpi.R;
import it.bleb.dpi.utils.Prefs;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();

        TextView txtVersion = findViewById(R.id.txt_version);
        txtVersion.setText(String.format(Locale.US, "%d", BuildConfig.VERSION_CODE));

        ImageView imgLogo = findViewById(R.id.img_logo);

        //sempre true
        if (BuildConfig.DEBUG) {
            Logger.setLogLevel(3);
            if (Prefs.GetIsCTrace(getApplicationContext())) {
                Blebricks.AskPermissions(SplashActivity.this, new Blebricks.OnAskPermissionsListener() {
                    @Override
                    public void OnAskPermissionsDone() {
                        if (!Blebricks.IsInit())
                            Blebricks.Init(getApplicationContext());
                        //una volta concessi i permessi all'app andiamo su LoginActivity
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        }

        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Disable auto turning on GPS if your coming back from other activities
        if (Blebricks.IsInit())
            Blebricks.DisableAutoEnableGpsWhenTurnedOff();
    }
}
