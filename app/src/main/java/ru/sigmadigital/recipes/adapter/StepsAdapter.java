package ru.sigmadigital.recipes.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import ru.sigmadigital.recipes.R;
import ru.sigmadigital.recipes.model.response.StepResponse;


public class StepsAdapter extends RecyclerView.Adapter<StepsAdapter.StepHolder> implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    private List<StepResponse> steps = new ArrayList<>();

    public StepsAdapter() {
    }

    public StepsAdapter(List<StepResponse> steps) {
        this.steps = steps;
    }


    @NonNull
    @Override
    public StepHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        @SuppressLint("InflateParams") View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_step, null);
        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new StepHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull StepHolder stepHolder, final int i) {
        stepHolder.stepNumber.setText(String.valueOf(i + 1));
        stepHolder.tvStepDesc.setText(steps.get(i).getAction());
        updateLines(stepHolder, i);
    }

    private void updateLines(@NonNull StepHolder stepHolder, int i) {
        stepHolder.lineDown.setVisibility(View.VISIBLE);
        stepHolder.lineUp.setVisibility(View.VISIBLE);

        if (i == 0) {
            stepHolder.lineUp.setVisibility(View.INVISIBLE);
        }

        if (i == getItemCount() - 1) {
            stepHolder.lineDown.setVisibility(View.INVISIBLE);
        }
    }


    public void addStep(String step) {
        steps.add(new StepResponse(step, getItemCount()));
        notifyDataSetChanged();
    }

    private void removeStep(int step) {
        steps.remove(step);
        notifyDataSetChanged();
    }


    public List<StepResponse> getSteps() {
        for (int i = 0; i < getItemCount(); i++) {
            steps.get(i).setPosition(i);
        }
        return steps;
    }

    /*public void setSteps(List<StepResponse> steps) {
        this.steps = steps;
        notifyDataSetChanged();
    }*/

    @Override
    public int getItemCount() {
        return steps.size();
    }


    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(steps, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(steps, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemSwiped(final RecyclerView.ViewHolder viewHolder, final int position) {
        removeStep(position);
    }


    public class StepHolder extends RecyclerView.ViewHolder implements SimpleItemTouchHelperCallback.ItemTouchHelperViewHolder {
        private View lineUp;
        private View lineDown;
        private TextView stepNumber;
        private TextView tvStepDesc;

        private StepHolder(@NonNull View itemView) {
            super(itemView);
            lineDown = itemView.findViewById(R.id.line_down);
            lineUp = itemView.findViewById(R.id.line_up);
            stepNumber = itemView.findViewById(R.id.tv_step_number);
            tvStepDesc = itemView.findViewById(R.id.tv_step_desc);
        }

        @Override
        public void onItemSelected() {
            lineUp.setVisibility(View.INVISIBLE);
            lineDown.setVisibility(View.INVISIBLE);
            stepNumber.setText("");
        }

        @Override
        public void onItemClear() {
            notifyItemRangeChanged(0, steps.size());
        }
    }
}
