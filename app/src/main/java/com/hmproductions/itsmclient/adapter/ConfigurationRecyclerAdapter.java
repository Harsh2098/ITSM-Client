package com.hmproductions.itsmclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hmproductions.itsmclient.R;
import com.hmproductions.itsmclient.data.Configuration;

import java.util.List;

public class ConfigurationRecyclerAdapter extends RecyclerView.Adapter<ConfigurationRecyclerAdapter.ConfigurationViewHolder> {

    private Context context;
    private List<Configuration> list;
    private OnConfigurationClickListener listener;

    public interface OnConfigurationClickListener {
        void onConfigurationClick(int position);
    }

    public ConfigurationRecyclerAdapter(Context context, List<Configuration> list, OnConfigurationClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConfigurationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConfigurationViewHolder(
                LayoutInflater.from(context).inflate(R.layout.configuration_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigurationViewHolder holder, int position) {
        Configuration currentConfiguration = list.get(position);

        holder.tierTextView.setText(String.valueOf(currentConfiguration.getTier()));

        StringBuilder stringBuilder = new StringBuilder();
        for(String field : currentConfiguration.getFields()) {
            stringBuilder.append(field).append(", ");
        }
        stringBuilder.delete(stringBuilder.length()-2, stringBuilder.length()-1);
        holder.configTextView.setText(stringBuilder.toString());
    }

    @Override
    public int getItemCount() {
        if(list == null) return 0;
        return list.size();
    }

    public void swapData(List<Configuration> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    class ConfigurationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tierTextView, configTextView;

        ConfigurationViewHolder(@NonNull View itemView) {
            super(itemView);
            tierTextView = itemView.findViewById(R.id.tierTextView);
            configTextView = itemView.findViewById(R.id.configTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onConfigurationClick(getAdapterPosition());
        }
    }
}
