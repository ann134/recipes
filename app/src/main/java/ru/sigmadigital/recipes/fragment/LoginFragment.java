package ru.sigmadigital.recipes.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.activity.LoginActivity;
import ru.sigmadigital.recipes.activity.MainActivity;
import ru.sigmadigital.recipes.api.asynctasks.AuthTask;
import ru.sigmadigital.recipes.model.User;
import ru.sigmadigital.recipes.model.response.TokenResponse;
import ru.sigmadigital.recipes.util.HashUtil;
import ru.sigmadigital.recipes.util.SettingHelper;


public class LoginFragment extends BaseFragment implements View.OnClickListener {

    private EditText email;
    private EditText password;

    private View passwordHider;
    private boolean isHide = true;

    private AuthTask authTask;


    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        if (getActivity() != null)
            ((LoginActivity) getActivity()).setToolbarVisibility(false);
        init(v);
        return v;
    }

    private void init(View v) {
        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);
        passwordHider = v.findViewById(R.id.password_hider);
        addTextWatcher();
        passwordHider.setOnClickListener(this);

        Button login = v.findViewById(R.id.login_button);
        TextView register = v.findViewById(R.id.register);
        register.setOnClickListener(this);
        login.setOnClickListener(this);

        register.measure(0, 0);       //must call measure!
        register.getMeasuredHeight(); //get height
        int w = register.getMeasuredWidth();  //get width
        v.findViewById(R.id.underline).setLayoutParams(new LinearLayout.LayoutParams(w, getResources().getDimensionPixelSize(R.dimen.underline_height)));
    }

    private void addTextWatcher() {
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (password.getText().toString().equals("")) {
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password, 0);
                    passwordHider.setVisibility(View.GONE);
                } else {
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    passwordHider.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (authTask != null)
            authTask.cancel(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.password_hider: {
                if (isHide) {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
                } else {
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                }

                password.setSelection(password.getText().length());
                isHide = !isHide;

                break;
            }

            case R.id.register: {
                loadFragment(RegistrationFragment.newInstance(), getString(R.string.title_registration), true);
                break;
            }

            case R.id.login_button: {
                final User user = new User(email.getText().toString(), HashUtil.md5(password.getText().toString()));

                authTask = new AuthTask(user, new AuthTask.OnAuthorizationListener() {
                    @Override
                    public void OnAuthorization(TokenResponse token) {
                        if (getActivity() != null && token != null && token.getToken() != null) {

                            token.setTime(System.currentTimeMillis());
                            SettingHelper.setToken(token);
                            SettingHelper.setUser(user);

                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);
                            getActivity().finish();

                        } else {
                            if (getFragmentManager() != null)
                                InfoDialogFragment.newInstance(getString(R.string.wrong_login_or_password)).show(getFragmentManager(), "dialog");
                        }
                    }
                });
                authTask.execute();
                break;
            }
        }
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.fragment_container;
    }
}
