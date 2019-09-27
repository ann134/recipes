package ru.sigmadigital.recipes.view;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class SpinnerTrigger extends androidx.appcompat.widget.AppCompatSpinner {

    private View textViewHint;



    public SpinnerTrigger(Context context) {
        super(context);
    }

    public SpinnerTrigger(Context context, int mode) {
        super(context, mode);
    }

    public SpinnerTrigger(Context context, AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }

    public SpinnerTrigger(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public SpinnerTrigger(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SpinnerTrigger(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public View getTextViewHint() {
        return textViewHint;
    }

    public void setTextViewHint(View textViewHint) {
        this.textViewHint = textViewHint;
    }

    @Override
    public void setSelection(int position, boolean animate) {
        ignoreOldSelectionByReflection();
        super.setSelection(position, animate);
    }

    private void ignoreOldSelectionByReflection() {
        try {
            if(textViewHint != null){
                hideTextView(textViewHint);
            }
        } catch (Exception e) {
            Log.d("Exception Private", "ex", e);
        }
    }

    @Override
    public void setSelection(int position) {
        ignoreOldSelectionByReflection();
        super.setSelection(position);
    }

    public void hideTextView(View view){
        view.setVisibility(GONE);
    }

}
