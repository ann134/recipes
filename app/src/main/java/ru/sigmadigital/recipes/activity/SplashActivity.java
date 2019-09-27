package ru.sigmadigital.recipes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import java.util.List;

import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.asynctasks.UnitsTask;
import ru.sigmadigital.recipes.model.response.UnitsResponse;
import ru.sigmadigital.recipes.util.SettingHelper;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new UnitsTask(new UnitsTask.OnGetUnitsListener() {
            @Override
            public void onGetUnits(final List<UnitsResponse> units) {
                if (units != null)
                    SettingHelper.setUnits(units);
                startMainActivity();
            }
        }).execute();

    }

    private void startMainActivity() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 1000);
    }
}
