package com.hmproductions.itsmclient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.hmproductions.itsmclient.R;
import com.hmproductions.itsmclient.data.CoreData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GraphRecyclerAdapter extends RecyclerView.Adapter<GraphRecyclerAdapter.GraphViewHolder> {

    private Context context;
    private List<CoreData> list;

    public GraphRecyclerAdapter(Context context, List<CoreData> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public GraphViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GraphViewHolder(LayoutInflater.from(context).inflate(R.layout.graph_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull GraphViewHolder holder, int position) {
        CoreData currentData = list.get(position);

        holder.fieldNameTextView.setText(currentData.getFieldName());

        List<BarEntry> entries = new ArrayList<>();
        HashMap<Integer, String> labelMap = new HashMap<>();

        if (currentData.getIntValues().size() > 0) {
            HashMap<Integer, Integer> valuesCountMap = new HashMap<>();
            for (Integer value : currentData.getIntValues()) {
                if (valuesCountMap.containsKey(value))
                    valuesCountMap.put(value, valuesCountMap.get(value) + 1);
                else
                    valuesCountMap.put(value, 1);
            }


            for (HashMap.Entry<Integer, Integer> entry : valuesCountMap.entrySet()) {
                labelMap.put(entry.getKey(), String.valueOf(entry.getKey()));
                entries.add(new BarEntry(entry.getKey(), entry.getValue()));
            }
        } else {
            HashMap<String, Integer> valuesCountMap = new HashMap<>();
            for (String value : currentData.getStringValues()) {
                if (valuesCountMap.containsKey(value))
                    valuesCountMap.put(value, valuesCountMap.get(value) + 1);
                else
                    valuesCountMap.put(value, 1);
            }

            int i = 1;
            for (HashMap.Entry<String, Integer> entry : valuesCountMap.entrySet()) {
                labelMap.put(i, entry.getKey());
                entries.add(new BarEntry(i++, entry.getValue()));
            }
        }

        BarDataSet barDataSet = new BarDataSet(entries, currentData.getFieldName());
        barDataSet.setValueTextSize(14);
        BarData barData = new BarData(barDataSet);

        holder.barChart.setData(barData);

        holder.barChart.getXAxis().setValueFormatter(new CustomXAxisFormatter(labelMap));
        holder.barChart.getXAxis().setDrawGridLines(false);
        holder.barChart.getXAxis().setGranularity(1f);
        holder.barChart.getXAxis().setTextSize(14);
        holder.barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        holder.barChart.getAxisRight().setEnabled(false);
        holder.barChart.getAxisLeft().setTextSize(14);
        holder.barChart.getLegend().setEnabled(false);
        holder.barChart.setExtraOffsets(12, 12, 12, 12);
        holder.barChart.setContentDescription("");

        holder.barChart.invalidate();
        holder.barChart.animateY(500);
    }

    @Override
    public int getItemCount() {
        if (list == null) return 0;
        return list.size();
    }

    public void swapData(List<CoreData> newList) {
        list = newList;
        notifyDataSetChanged();
    }

    class GraphViewHolder extends RecyclerView.ViewHolder {

        BarChart barChart;
        TextView fieldNameTextView;

        GraphViewHolder(@NonNull View itemView) {
            super(itemView);
            barChart = itemView.findViewById(R.id.barChart);
            fieldNameTextView = itemView.findViewById(R.id.fieldNameTextView);
        }
    }

    private class CustomXAxisFormatter extends ValueFormatter {

        private final HashMap<Integer, String> labelMap;

        CustomXAxisFormatter(HashMap<Integer, String> labelMap) {
            this.labelMap = labelMap;
        }

        @Override
        public String getFormattedValue(float value) {
            String str = labelMap.get((int) value);
            return str.substring(0, 1).toUpperCase() + str.substring(1);
        }
    }
}
