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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.activity.LoginActivity;
import ru.sigmadigital.recipes.activity.MainActivity;
import ru.sigmadigital.recipes.api.asynctasks.RegistrationTask;
import ru.sigmadigital.recipes.model.User;
import ru.sigmadigital.recipes.model.response.TokenResponse;
import ru.sigmadigital.recipes.util.HashUtil;
import ru.sigmadigital.recipes.util.SettingHelper;

public class RegistrationFragment extends BaseFragment implements View.OnClickListener {

    private EditText email;
    private EditText username;
    private EditText password;
    private EditText passwordAgain;

    private boolean isHide1 = true;
    private boolean isHide2 = true;

    private RegistrationTask registrationTask;

    public static RegistrationFragment newInstance() {
        return new RegistrationFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_registration, container, false);
        if (getActivity() != null) {
            ((LoginActivity) getActivity()).setToolbarVisibility(true);
            getActivity().setTitle(R.string.title_registration);
        }

        init(v);
        return v;
    }

    private void init(View v) {
        email = v.findViewById(R.id.email);
        username = v.findViewById(R.id.username);
        password = v.findViewById(R.id.password);
        passwordAgain = v.findViewById(R.id.password_again);

        View passwordHider1 = v.findViewById(R.id.v_password_hider1);
        View passwordHider2 = v.findViewById(R.id.v_password_hider2);

        addTextWatcher(password, passwordHider1);
        addTextWatcher(passwordAgain, passwordHider2);

        Button register = v.findViewById(R.id.button_registration);
        register.setOnClickListener(this);
        passwordHider1.setOnClickListener(this);
        passwordHider2.setOnClickListener(this);

    }


    private void addTextWatcher(final EditText editText, final View view) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().equals("")) {
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password, 0);
                    view.setVisibility(View.GONE);
                } else {
                    editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
                    view.setVisibility(View.VISIBLE);
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
        if (registrationTask != null) {
            registrationTask.cancel(true);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.v_password_hider1: {
                setInputType(isHide1, password);
                isHide1 = !isHide1;
                break;
            }

            case R.id.v_password_hider2: {
                setInputType(isHide2, passwordAgain);
                isHide2 = !isHide2;
                break;
            }

            case R.id.button_registration: {
                if (getFragmentManager() != null) {
                    if (email.getText().toString().equals("") || username.getText().toString().equals("") || password.getText().toString().equals("") || passwordAgain.toString().equals("")) {
                        InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.fields_error));
                        dialog.show(getFragmentManager(), "dialog");
                        return;
                    }

                    if (!isValidEmail(email.getText().toString())/*email.getText().toString().contains("@")*/) {
                        InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.email_error));
                        dialog.show(getFragmentManager(), "dialog");
                        return;
                    }

                    if (!password.getText().toString().equals(passwordAgain.getText().toString())) {
                        InfoDialogFragment dialog = InfoDialogFragment.newInstance(getString(R.string.passwords_error));
                        dialog.show(getFragmentManager(), "dialog");
                        return;
                    }
                }

                final User user = new User();
                user.setEmail(email.getText().toString());
                user.setName(username.getText().toString());
                user.setPassword(HashUtil.md5(password.getText().toString()));

                registrationTask = new RegistrationTask(user, new RegistrationTask.OnRegistrationListener() {
                    @Override
                    public void onRegistration(TokenResponse token) {
                        if (getActivity() != null && token != null) {
                            if (token.getToken() != null) {
                                token.setTime(System.currentTimeMillis());
                                SettingHelper.setToken(token);
                                SettingHelper.setUser(user);

                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                startActivity(intent);
                                if (getActivity() != null) getActivity().finish();
                            } else {
                                Toast.makeText(App.getAppContext(), R.string.sending_error, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(App.getAppContext(), R.string.sending_error, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                registrationTask.execute();
                break;
            }
        }
    }

    private void setInputType(boolean isHide, EditText editText) {
        if (isHide) {
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility, 0);
        } else {
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_visibility_off, 0);
        }

        editText.setSelection(editText.getText().length());
    }

    private static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.fragment_container;
    }
}
