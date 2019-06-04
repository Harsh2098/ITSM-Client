package com.hmproductions.itsmclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hmproductions.itsmclient.R;
import com.hmproductions.itsmclient.data.ConfigurationField;

import java.util.List;

public class FieldRecyclerAdapter extends RecyclerView.Adapter<FieldRecyclerAdapter.FieldViewHolder> {

    private Context context;
    private List<ConfigurationField> list;

    public FieldRecyclerAdapter(Context context, List<ConfigurationField> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public FieldViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FieldViewHolder(
                LayoutInflater.from(context).inflate(R.layout.field_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FieldViewHolder holder, int position) {
        ConfigurationField currentField = list.get(position);
        holder.fieldCheckbox.setChecked(currentField.getChecked());
        holder.fieldCheckbox.setText(currentField.getField());
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public void swapData(List<ConfigurationField> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    public List<ConfigurationField> getUpdatedList() {
        return list;
    }

    class FieldViewHolder extends RecyclerView.ViewHolder {

        CheckBox fieldCheckbox;

        FieldViewHolder(@NonNull View itemView) {
            super(itemView);
            fieldCheckbox = itemView.findViewById(R.id.fieldCheckbox);

            fieldCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    list.get(getAdapterPosition()).setChecked(isChecked);
                }
            });
        }
    }
}
