package com.fuu.lukak.fuu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UrnikAdapter extends RecyclerView.Adapter<UrnikAdapter.MyViewHolder> {
    private List<List<Event>> list;

    Context con;

    public UrnikAdapter() {
    }

    public UrnikAdapter(List<List<Event>> list) {
        this.list = list;

    }

    @NonNull
    @Override
    public UrnikAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.urnikadapter, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(v);
        con = viewGroup.getContext();

        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final UrnikAdapter.MyViewHolder myViewHolder, int i) {
        LayoutInflater inflater = (LayoutInflater) myViewHolder.ureplac.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TinyDB tiny = new TinyDB(con);
        ArrayList<String> toignore = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik"));



        //TODO pohandli prazn dan
        if (list.get(i).size() == 1) {
            if (!toignore.contains(list.get(i).get(0).group.subGroup)) {
                View single = inflater.inflate(R.layout.singlepredmet, null);
                TextView start = single.findViewById(R.id.Start);
                TextView end = single.findViewById(R.id.End);
                TextView course = single.findViewById(R.id.Course);
                TextView prof = single.findViewById(R.id.Prof);
                TextView loc = single.findViewById(R.id.Location);
                LinearLayout height = single.findViewById(R.id.heigtsetter);
                LinearLayout root = single.findViewById(R.id.root);

                start.setText(list.get(i).get(0).startTime);
                SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                Date d1 = new Date();

                try {
                    d1 = parser.parse(list.get(i).get(0).startTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (list.get(i).get(0).duration < 120) {
                    height.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (120 * 3)));
                } else {
                    height.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (list.get(i).get(0).duration * 3)));
                }

                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(d1.getTime() + (list.get(i).get(0).duration * 60 * 1000));
                int ura = cal.get(Calendar.HOUR_OF_DAY);
                String uraout = "";
                if(ura < 10 )
                {
                    uraout = "0" + ura;
                }
                else {uraout = ura+"";}

                if (cal.get(Calendar.MINUTE) == 0) {
                    end.setText(uraout + ":00");
                } else {
                    end.setText(uraout + ":" + cal.get(Calendar.MINUTE));
                }

                course.setText(list.get(i).get(0).course + "\n" + list.get(i).get(0).type + " " + list.get(i).get(0).group.subGroup);
                prof.setText(list.get(i).get(0).professor);
                loc.setText(list.get(i).get(0).room);


                myViewHolder.ureplac.addView(single);
            }

        } else {
            for (int j = 0; j < list.get(i).size(); j++) {
                if (!toignore.contains(list.get(i).get(j).group.subGroup)) {
                    View single = inflater.inflate(R.layout.singlepredmet, null);
                    TextView start = single.findViewById(R.id.Start);
                    TextView end = single.findViewById(R.id.End);
                    TextView course = single.findViewById(R.id.Course);
                    TextView prof = single.findViewById(R.id.Prof);
                    TextView loc = single.findViewById(R.id.Location);
                    LinearLayout height = single.findViewById(R.id.heigtsetter);
                    LinearLayout root = single.findViewById(R.id.root);

                    start.setText(list.get(i).get(j).startTime);
                    SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                    Date d1 = new Date();

                    try {
                        d1 = parser.parse(list.get(i).get(j).startTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    int pixels = (int) (200 * loc.getContext().getResources().getDisplayMetrics().density);
                    root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (list.get(i).get(j).duration < 120) {
                        height.setLayoutParams(new LinearLayout.LayoutParams(pixels, (120 * 3)));
                    } else {
                        height.setLayoutParams(new LinearLayout.LayoutParams(pixels, (list.get(i).get(j).duration * 3)));
                    }

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(d1.getTime() + (list.get(i).get(j).duration * 60 * 1000));
                    int ura = cal.get(Calendar.HOUR_OF_DAY);
                    String uraout = "";
                    if(ura < 10 )
                    {
                        uraout = "0" + ura;
                    }
                    else {uraout = ura+"";}

                    if (cal.get(Calendar.MINUTE) == 0) {
                        end.setText(uraout + ":00");
                    } else {
                        end.setText(uraout + ":" + cal.get(Calendar.MINUTE));
                    }

                    course.setText(list.get(i).get(j).course + "\n" + list.get(i).get(j).type + " " + list.get(i).get(j).group.subGroup);
                    prof.setText(list.get(i).get(j).professor);
                    loc.setText(list.get(i).get(j).room);
                    myViewHolder.ureplac.addView(single);
                }
            }
        }


    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        LinearLayout ureplac;
        HorizontalScrollView scroller;

        public MyViewHolder(View v) {
            super(v);
            ureplac = v.findViewById(R.id.UrePlac);
            scroller = v.findViewById(R.id.scroller);
        }
    }


}
