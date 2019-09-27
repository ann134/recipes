package ru.sigmadigital.recipes.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.api.asynctasks.ChangePasswordTask;
import ru.sigmadigital.recipes.model.User;
import ru.sigmadigital.recipes.model.request.UpdatePasswordRequest;
import ru.sigmadigital.recipes.model.response.DoneResponse;
import ru.sigmadigital.recipes.util.HashUtil;
import ru.sigmadigital.recipes.util.SettingHelper;

public class ChangePasswordDialogFragment extends DialogFragment implements View.OnClickListener {

    private EditText oldPassword;
    private EditText newPassword1;
    private EditText newPassword2;

    private boolean isHide1 = true;
    private boolean isHide2 = true;
    private boolean isHide3 = true;

    private ChangePasswordTask changePasswordTask;


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = View.inflate(App.getAppContext(), R.layout.dialog_fragment_change_password, null);

        oldPassword = view.findViewById(R.id.old_pass);
        newPassword1 = view.findViewById(R.id.new_pass1);
        newPassword2 = view.findViewById(R.id.new_pass2);

        View passwordHider1 = view.findViewById(R.id.password_hider1);
        View passwordHider2 = view.findViewById(R.id.password_hider2);
        View passwordHider3 = view.findViewById(R.id.password_hider3);

        addTextWatcher(oldPassword, passwordHider1);
        addTextWatcher(newPassword1, passwordHider2);
        addTextWatcher(newPassword2, passwordHider3);

        passwordHider1.setOnClickListener(this);
        passwordHider2.setOnClickListener(this);
        passwordHider3.setOnClickListener(this);

        view.findViewById(R.id.button).setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);

        Dialog dialog = builder.create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        return dialog;
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
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.password_hider1: {
                setInputType(isHide1, oldPassword);
                isHide1 = !isHide1;
                break;
            }

            case R.id.password_hider2: {
                setInputType(isHide2, newPassword1);
                isHide2 = !isHide2;
                break;
            }

            case R.id.password_hider3: {
                setInputType(isHide3, newPassword2);
                isHide3 = !isHide3;
                break;
            }

            case R.id.button:
                if (getActivity() != null) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }


                String old = oldPassword.getText().toString();
                final String new1 = newPassword1.getText().toString();
                String new2 = newPassword2.getText().toString();

                if (new1.equals(new2) && !old.equals("")) {
                    UpdatePasswordRequest request = new UpdatePasswordRequest(HashUtil.md5(old), HashUtil.md5(new1));
                    changePasswordTask = new ChangePasswordTask(request, new ChangePasswordTask.OnChangePasswordListener() {
                        @Override
                        public void onPasswordChange(DoneResponse response) {
                            if (response != null && response.isResult()) {
                                User user = SettingHelper.getUser();
                                if (user != null)
                                    user.setPassword(HashUtil.md5(new1));
                                SettingHelper.setUser(user);
                                dismiss();
                            } else {
                                if (getFragmentManager() != null)
                                    InfoDialogFragment.newInstance(getString(R.string.wrong_data)).show(getFragmentManager(), "dialog");
                            }
                        }
                    });
                    changePasswordTask.execute();
                } else {

                    if (getFragmentManager() != null)
                        InfoDialogFragment.newInstance(getString(R.string.wrong_data)).show(getFragmentManager(), "dialog");
                }
                break;
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


    @Override
    public void onPause() {
        super.onPause();
        if (changePasswordTask != null) {
            changePasswordTask.cancel(true);
        }
    }
}
