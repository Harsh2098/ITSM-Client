package com.hmproductions.itsmclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.hmproductions.itsmclient.R;
import com.hmproductions.itsmclient.data.Ticket;

import java.util.List;

public class TicketRecyclerAdapter extends RecyclerView.Adapter<TicketRecyclerAdapter.TicketViewHolder> {

    private Context context;
    private List<Ticket> list;
    private OnTicketClickListener listener;

    public interface OnTicketClickListener {
        void onTicketClick(int position);
    }

    public TicketRecyclerAdapter(Context context, List<Ticket> list, OnTicketClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TicketViewHolder(LayoutInflater.from(context).inflate(R.layout.ticket_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        Ticket currentTicket = list.get(position);
        holder.numberTextView.setText(currentTicket.getNumber());
        holder.priorityTextView.setText(String.valueOf(currentTicket.getPriority()));
        holder.shortDescriptionTextView.setText(currentTicket.getShort_description());
        holder.categoryTextView.setText(currentTicket.getCategory());
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public void swapData(List<Ticket> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class TicketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView numberTextView, shortDescriptionTextView, priorityTextView, categoryTextView;

        TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            numberTextView = itemView.findViewById(R.id.numberTextView);
            shortDescriptionTextView = itemView.findViewById(R.id.shortDescriptionTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            priorityTextView = itemView.findViewById(R.id.priorityTextView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onTicketClick(getAdapterPosition());
        }
    }
}
