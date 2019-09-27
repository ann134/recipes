package ru.sigmadigital.recipes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.fragment.LoginFragment;
import ru.sigmadigital.recipes.util.SettingHelper;

public class LoginActivity extends BaseActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        toolbar = findViewById(R.id.my_action_bar);
        LinearLayout btnBackLayout = findViewById(R.id.button_back_layout);
        setSupportActionBar(toolbar);
        btnBackLayout.setVisibility(View.VISIBLE);
        btnBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (SettingHelper.getUser() != null) {
            startMainActivity();
        } else {
            loadFragment(LoginFragment.newInstance(), getResources().getString(R.string.app_name), true);
        }
    }

    public void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void setToolbarVisibility (boolean visible){
        toolbar.setVisibility(visible? View.VISIBLE: View.GONE);
    }

    @Override
    public void setTitle(int titleId) {
        super.setTitle(titleId);
        TextView title = findViewById(R.id.toolbar_title);
        title.setText(titleId);
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.fragment_container;
    }
}
