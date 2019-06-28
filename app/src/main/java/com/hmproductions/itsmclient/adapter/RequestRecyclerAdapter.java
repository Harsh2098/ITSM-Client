package com.hmproductions.itsmclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hmproductions.itsmclient.R;
import com.hmproductions.itsmclient.data.AlterRequest;

import java.util.List;

public class RequestRecyclerAdapter extends RecyclerView.Adapter<RequestRecyclerAdapter.RequestViewHolder> {

    private Context context;
    private List<AlterRequest> list;
    private OnRequestClickListener listener;

    public interface OnRequestClickListener {
        void onRequestClick(String requestId);
    }

    public RequestRecyclerAdapter(Context context, List<AlterRequest> list, OnRequestClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RequestViewHolder(
                LayoutInflater.from(context).inflate(R.layout.request_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        AlterRequest currentConfiguration = list.get(position);

        holder.requestTierTextView.setText(String.valueOf(currentConfiguration.getTier()));

        StringBuilder stringBuilder = new StringBuilder();
        for (String field : currentConfiguration.getFields()) {
            stringBuilder.append(field).append(", ");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length() - 1);
        holder.requestConfigTextView.setText(stringBuilder.toString());
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public void swapData(List<AlterRequest> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView requestTierTextView, requestConfigTextView;

        RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            requestTierTextView = itemView.findViewById(R.id.requestTierTextView);
            requestConfigTextView = itemView.findViewById(R.id.requestConfigTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRequestClick(list.get(getAdapterPosition()).getId());
        }
    }
}
