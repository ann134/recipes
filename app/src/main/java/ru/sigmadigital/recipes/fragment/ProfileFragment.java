package ru.sigmadigital.recipes.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.DialogFragment;
import de.hdodenhof.circleimageview.CircleImageView;
import ru.sigmadigital.recipes.App;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.activity.MainActivity;
import ru.sigmadigital.recipes.api.Network;
import ru.sigmadigital.recipes.api.asynctasks.SendProfilePhotoTask;
import ru.sigmadigital.recipes.api.asynctasks.SetProfileTask;
import ru.sigmadigital.recipes.model.User;
import ru.sigmadigital.recipes.model.request.ProfileRequest;
import ru.sigmadigital.recipes.model.response.DoneResponse;
import ru.sigmadigital.recipes.model.response.ProfileResponse;
import ru.sigmadigital.recipes.util.PicassoUtil;
import ru.sigmadigital.recipes.util.SettingHelper;


public class ProfileFragment extends BaseFragment implements View.OnClickListener {

    private OnProfileInfoChanger listener;

    private final int PHOTO_PROFILE = 12;
    private InputStream imageInputStream;

    private CircleImageView avatar;

    private EditText username;
    private Spinner genderSpinner;
    private EditText dateOfBirth;
    private EditText city;

    private Button saveButton;
    private ProgressBar loading;

    private SetProfileTask setProfileTask;
    private SendProfilePhotoTask sendProfilePhotoTask;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    public static ProfileFragment newInstance(OnProfileInfoChanger listener) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.setListener(listener);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        avatar = v.findViewById(R.id.avatar);
        username = v.findViewById(R.id.username);
        genderSpinner = v.findViewById(R.id.gender_spinner);
        dateOfBirth = v.findViewById(R.id.date_of_birth);
        city = v.findViewById(R.id.city);
        saveButton = v.findViewById(R.id.save_button);
        loading = v.findViewById(R.id.loading);

        initGenderAdapter();
        initDateTextWatcher();

        setUserData();

        avatar.setOnClickListener(this);
        v.findViewById(R.id.save_button).setOnClickListener(this);
        v.findViewById(R.id.change_password).setOnClickListener(this);
        return v;
    }

    private void initGenderAdapter() {
        List<String> unitsl = new ArrayList<>();
        unitsl.add(getString(R.string.female));
        unitsl.add(getString(R.string.male));
        if (getContext() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.item_spinner_gender, unitsl);
            adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
            genderSpinner.setAdapter(adapter);
        }
    }

    private void initDateTextWatcher() {
        dateOfBirth.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String string;

                if (count == 1 && start == 1) {
                    string = s + "/";
                    dateOfBirth.setText(string);
                    dateOfBirth.setSelection(dateOfBirth.getText().toString().length());
                }

                if (count == 1 && start == 4) {
                    string = s + "/";
                    dateOfBirth.setText(string);
                    dateOfBirth.setSelection(dateOfBirth.getText().toString().length());
                }

                if (before == 1 && start == 3) {
                    string = s.subSequence(0, dateOfBirth.getText().toString().length() - 1).toString();
                    dateOfBirth.setText(string);
                    dateOfBirth.setSelection(dateOfBirth.getText().toString().length());
                }

                if (before == 1 && start == 6) {
                    string = s.subSequence(0, dateOfBirth.getText().toString().length() - 1).toString();
                    dateOfBirth.setText(string);
                    dateOfBirth.setSelection(dateOfBirth.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setUserData() {
        PicassoUtil.getPicasso()
                .load(Network.BASE_URL + "api/profile/photo")
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(avatar);

        User curUser = SettingHelper.getUser();
        if (curUser != null) {
            username.setText(curUser.getName());
            city.setText(curUser.getCity());
            genderSpinner.setSelection(curUser.getGender());

            if (curUser.getBirthday() != null) {
                Date date = curUser.getBirthday();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                StringBuilder sb = new StringBuilder();
                if (calendar.get(Calendar.DAY_OF_MONTH) < 10) {
                    sb.append("0").append(calendar.get(Calendar.DAY_OF_MONTH));
                    sb.append("/");
                } else {
                    sb.append(calendar.get(Calendar.DAY_OF_MONTH));
                    sb.append("/");
                }

                if (calendar.get(Calendar.MONTH) + 1 < 10) {
                    sb.append("0").append(calendar.get(Calendar.MONTH) + 1);
                    sb.append("/");
                } else {
                    sb.append((calendar.get(Calendar.MONTH) + 1));
                    sb.append("/");
                }

                sb.append(calendar.get(Calendar.YEAR));

                dateOfBirth.setText(sb.toString());
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (setProfileTask != null) {
            setProfileTask.cancel(true);
        }
        if (sendProfilePhotoTask != null) {
            sendProfilePhotoTask.cancel(true);
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.avatar) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PHOTO_PROFILE);
        }

        if (v.getId() == R.id.save_button) {
            saveButton.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            sendProfile();
        }

        if (v.getId() == R.id.change_password) {
            ChangePasswordDialogFragment dialogFragment = new ChangePasswordDialogFragment();
            dialogFragment.setStyle(DialogFragment.STYLE_NO_FRAME, R.style.CustomDialog);
            if (getFragmentManager() != null) {
                dialogFragment.show(getFragmentManager(), "ChangePasswordDialogFragment");
            }
        }
    }


    private void sendProfile() {

        ProfileRequest profileRequest = new ProfileRequest();
        profileRequest.setName(username.getText().toString());
        profileRequest.setCity(city.getText().toString());
        profileRequest.setGender(genderSpinner.getSelectedItemPosition());

        if (!dateOfBirth.getText().toString().equals("")) {

            String[] date = dateOfBirth.getText().toString().split("/");

            if (date.length == 3 && (Integer.parseInt(date[0]) > 31 || Integer.parseInt(date[1]) > 12 || (Integer.parseInt(date[2]) > 2008 || Integer.parseInt(date[2]) < 1000))) {
                if (getFragmentManager() != null) {
                    InfoDialogFragment.newInstance(getString(R.string.birthday_error)).show(getFragmentManager(), "dialog");
                }
                saveButton.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                return;
            } else if (date.length > 0 && date.length < 3) {
                if (getFragmentManager() != null) {
                    InfoDialogFragment.newInstance(getString(R.string.birthday_error)).show(getFragmentManager(), "dialog");
                }
                saveButton.setVisibility(View.VISIBLE);
                loading.setVisibility(View.GONE);
                return;
            }

            Calendar calendar = Calendar.getInstance();
            calendar.clear();
            calendar.set(Calendar.YEAR, Integer.parseInt(date[2]));
            calendar.set(Calendar.MONTH, Integer.parseInt(date[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date[0]));

            profileRequest.setBdate(calendar.getTime());
        } else {
            profileRequest.setBdate(null);
        }


        setProfileTask = new SetProfileTask(profileRequest, new SetProfileTask.OnSetProfileListener() {
            @Override
            public void onSetProfile(ProfileResponse response) {
                if (response != null) {
                    User curUser = SettingHelper.getUser();
                    if (curUser != null) {
                        curUser.setBirthday(response.getBdate());
                        curUser.setName(response.getName());
                        curUser.setGender(response.getGender());
                        curUser.setCity(response.getCity());
                        SettingHelper.setUser(curUser);
                    }

                    if (listener != null){
                        listener.onInfoChange(response.getName(), response.getEmail());
                    }

                    if (imageInputStream != null) {
                        uploadImage();
                    } else {
                        closeProfile();
                    }
                } else {
                    saveButton.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.GONE);
                }
            }
        });
        setProfileTask.execute();
    }

    private void uploadImage() {
        sendProfilePhotoTask = new SendProfilePhotoTask(imageInputStream, new SendProfilePhotoTask.OnSendProfilePhotoListener() {
            @Override
            public void onSendPhoto(DoneResponse response) {
                if (response != null && response.isResult()) {
                    if (listener != null) {
                        listener.onPhotoChange();
                    }
                    closeProfile();
                } else {
                    closeProfile();
                }
            }
        });
        sendProfilePhotoTask.execute();

        PicassoUtil.deletePicasso();
    }


    private void closeProfile() {
        if (getActivity() != null) {
            getActivity().getSupportFragmentManager().popBackStack();
            ((MainActivity) getActivity()).getDrawer().openDrawer(GravityCompat.START);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PROFILE && resultCode == Activity.RESULT_OK) {
            if (data == null || data.getData() == null) {
                return;
            }

            try {
                InputStream inputStream = App.getAppContext().getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                avatar.setImageBitmap(bitmap);

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() * 400 / bitmap.getHeight(), 400, true);
                File file2 = new File(App.getAppContext().getFilesDir(), "Avatar.jpg");
                if (file2.exists())
                    file2.delete();
                FileOutputStream out2 = new FileOutputStream(file2);
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, out2);
                out2.flush();
                out2.close();

                imageInputStream = new FileInputStream(file2);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.content_main;
    }


    public void setListener(OnProfileInfoChanger listener) {
        this.listener = listener;
    }


    public interface OnProfileInfoChanger {
        void onInfoChange(String name, String email);

        void onPhotoChange();
    }
}
