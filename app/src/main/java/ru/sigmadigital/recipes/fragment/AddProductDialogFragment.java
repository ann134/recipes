package ru.sigmadigital.recipes.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.model.response.ProductResponse;
import ru.sigmadigital.recipes.model.response.UnitsResponse;
import ru.sigmadigital.recipes.util.SettingHelper;
import ru.sigmadigital.recipes.view.SpinnerTrigger;

public class AddProductDialogFragment extends DialogFragment implements View.OnClickListener {

    private List<UnitsResponse> units;

    private Context context;
    private DialogListener listener;

    private EditText name;
    private SpinnerTrigger unitSpinner;
    private EditText count;
    private TextView tvUnits;


    public interface DialogListener {
        void addProduct(ProductResponse product);
    }

    void setListener(Context context, DialogListener listener) {
        this.context = context;
        this.listener = listener;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_fragment_add_product, null);

        name = view.findViewById(R.id.product_name);
        unitSpinner = view.findViewById(R.id.unit_spinner);
        count = view.findViewById(R.id.count);
        tvUnits = view.findViewById(R.id.tv_units);
        view.findViewById(R.id.add_product).setOnClickListener(this);


        List<String> unitNames = new ArrayList<>();
        units = SettingHelper.getUnits();
        for (UnitsResponse unit : units) {
            unitNames.add(unit.getName());
        }
        if (!unitNames.isEmpty()) {
            unitNames.remove(0);
        }

        unitSpinner.setTextViewHint(tvUnits);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.item_spinner_unit, unitNames);
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown);
        unitSpinner.setAdapter(adapter);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        Dialog dialog = builder.create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        return dialog;
    }


    @Override
    public void onClick(View v) {
        String productName = name.getText().toString();
        String productCount = count.getText().toString();
        String productUnit = unitSpinner.getSelectedItem().toString();


        UnitsResponse unitResponse = new UnitsResponse();
        for (UnitsResponse unit : units) {
            if (unit.getName().equals(productUnit)) {
                unitResponse = unit;
                break;
            }
        }


        if (productName.equals("") || productCount.equals("")) {
            Toast.makeText(getActivity(), "Введите данные", Toast.LENGTH_SHORT).show();
        } else if (productUnit.equals("Ед.измерения")) {
            Toast.makeText(getActivity(), "Укажите еденицы измерения", Toast.LENGTH_SHORT).show();
        } else {
            ProductResponse product = new ProductResponse(productName, unitResponse, productCount);
            listener.addProduct(product);
            if (getActivity() != null) {
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
            dismiss();
        }
    }

}
