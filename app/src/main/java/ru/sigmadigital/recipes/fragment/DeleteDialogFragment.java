package ru.sigmadigital.recipes.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import ru.sigmadigital.recipes.R;

public class DeleteDialogFragment extends DialogFragment {

    public static final int PRESS_OK = 10;

    private String questionText;
    private OnActionClickListener listener;

    public interface OnActionClickListener {
        void onActionClick(int action);
    }

    public static DeleteDialogFragment newInstance(String questionText, OnActionClickListener listener) {
        Bundle bundle = new Bundle();
        bundle.putString("questionText", questionText);
        DeleteDialogFragment dialog = new DeleteDialogFragment();
        dialog.setArguments(bundle);
        dialog.setListener(listener);
        return dialog;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null && getArguments().containsKey("questionText")) {
            questionText = getArguments().getString("questionText");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_fragment_delete, null);
        TextView tvQuestion = view.findViewById(R.id.question_text);
        tvQuestion.setText(questionText);
        Button btnOK = view.findViewById(R.id.btn_ok);
        Button btnCancel = view.findViewById(R.id.btn_cancel);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onActionClick(PRESS_OK);
                dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        setCancelable(false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        Dialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public void setListener(OnActionClickListener listener) {
        this.listener = listener;
    }
}
