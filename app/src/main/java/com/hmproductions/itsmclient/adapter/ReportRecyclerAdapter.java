package com.hmproductions.itsmclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hmproductions.itsmclient.R;
import com.hmproductions.itsmclient.data.ReportEntry;

import java.util.List;

public class ReportRecyclerAdapter extends RecyclerView.Adapter<ReportRecyclerAdapter.ReportViewHolder> {

    private List<ReportEntry> list;
    private Context context;
    private OnReportItemClickListener listener;

    public ReportRecyclerAdapter(List<ReportEntry> list, Context context, OnReportItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    public interface OnReportItemClickListener {
        void onReportEntryClick(int position);
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ReportViewHolder(LayoutInflater.from(context).inflate(R.layout.report_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        ReportEntry currentEntry = list.get(position);
        holder.reportValueTextView.setText(currentEntry.getValue());
        holder.reportKeyTextView.setText(currentEntry.getKey());
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public void swapData(List<ReportEntry> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    class ReportViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView reportKeyTextView, reportValueTextView;

        ReportViewHolder(@NonNull View itemView) {
            super(itemView);
            reportKeyTextView = itemView.findViewById(R.id.reportKeyTextView);
            reportValueTextView = itemView.findViewById(R.id.reportValueTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onReportEntryClick(getAdapterPosition());
        }
    }
}
