package com.fuu.lukak.fuu;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DayListAdapter extends RecyclerView.Adapter<DayListAdapter.MyViewHolder> {
    private List<Date> list;
    final String[] Dnevi = {"NED", "PON", "TOR", "SRE", "ČET", "PET", "SOB"};

    public DayListAdapter(List<Date> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public DayListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recycle_view_month, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DayListAdapter.MyViewHolder myViewHolder, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(list.get(i).getTime());
        String sklamfi = Dnevi[cal.get(Calendar.DAY_OF_WEEK) -1] + " , " + cal.get(Calendar.DAY_OF_MONTH) + "." +(cal.get(Calendar.MONTH)+1);

        myViewHolder.DateText.setText(sklamfi);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView DateText;

        public MyViewHolder(View v) {
            super(v);
            DateText = v.findViewById(R.id.datetext);
        }
    }

}
