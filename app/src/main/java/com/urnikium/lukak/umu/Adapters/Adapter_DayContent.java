package com.urnikium.lukak.umu.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.urnikium.lukak.umu.Classes.Event;
import com.urnikium.lukak.umu.Classes.TinyDB;
import com.urnikium.lukak.umu.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class Adapter_DayContent extends RecyclerView.Adapter<Adapter_DayContent.MyViewHolder> {
    ArrayList<ArrayList<Event>> TodaysEvents;
    List<String> EventTypes = new ArrayList<>();
    ArrayList<String> IgnoredGroups = new ArrayList<>();

    public Adapter_DayContent(ArrayList<ArrayList<Event>> list) {
        this.TodaysEvents = list;
    }

    @NonNull
    @Override
    public Adapter_DayContent.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        TinyDB tiny = new TinyDB(viewGroup.getContext());
        EventTypes = tiny.getListString(tiny.getString("currpath") +
                tiny.getString("letnik") + "types");
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.urnikadapter, viewGroup, false);
        return new MyViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final Adapter_DayContent.MyViewHolder myViewHolder, int i) {
        LayoutInflater inflater = (LayoutInflater) myViewHolder.ureplac
                .getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TinyDB tiny = new TinyDB(myViewHolder.ureplac.getContext());
        IgnoredGroups = tiny.getListString(tiny.getString("currpath") +
                tiny.getString("letnik"));
        final ArrayList<Event> unignored = TodaysEvents.get(i);

        for (int j = 0; j < unignored.size(); j++) {
            final View EventBox = inflater.inflate(R.layout.singlepredmet, null);
            TextView StartText = EventBox.findViewById(R.id.Start);
            TextView EndText = EventBox.findViewById(R.id.End);
            TextView CourseText = EventBox.findViewById(R.id.Course);
            TextView ProfText = EventBox.findViewById(R.id.Prof);
            TextView LocationText = EventBox.findViewById(R.id.Location);
            RelativeLayout HeightSetter = EventBox.findViewById(R.id.heigtsetter);
            LinearLayout root = EventBox.findViewById(R.id.root);
         //   LinearLayout EventTypeColor = EventBox.findViewById(R.id.colortype);
            TextView GroupText = EventBox.findViewById(R.id.Grp);
            EventBox.setTag(unignored.get(j));
            String StartTime = unignored.get(j).startTime.split(":")[0];
            if (StartTime.length() == 1) {
                StartText.setText("0" + unignored.get(j).startTime);
            } else {
                StartText.setText(unignored.get(j).startTime);
            }
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date d1 = new Date();
            try {
                d1 = parser.parse(unignored.get(j).startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int pixels = (int) (200 * LocationText.getContext().getResources().getDisplayMetrics().density);
            if (unignored.size() == 1) {
                root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                if (unignored.get(j).duration < 120) {
                    HeightSetter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (120 * 3)));
                } else {
                    HeightSetter.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (unignored.get(j).duration * 3)));
                }
            } else {
                if(j != unignored.size() &&  j != 0)
                {
                    root.setPadding(0,root.getPaddingTop(),root.getPaddingRight(),root.getPaddingBottom());
                    root.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (unignored.get(j).duration < 120) {
                        HeightSetter.setLayoutParams(new LinearLayout.LayoutParams(pixels, (120 * 3)));
                    } else {
                        HeightSetter.setLayoutParams(new LinearLayout.LayoutParams(pixels, (TodaysEvents.get(i).get(j).duration * 3)));
                    }
                }

                else
                {
                    root.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (unignored.get(j).duration < 120) {
                        HeightSetter.setLayoutParams(new LinearLayout.LayoutParams(pixels, (120 * 3)));
                    } else {
                        HeightSetter.setLayoutParams(new LinearLayout.LayoutParams(pixels, (TodaysEvents.get(i).get(j).duration * 3)));
                    }
                }

            }
            Calendar cal = Calendar.getInstance();
            long NewTime = d1.getTime() + (long) (unignored.get(j).duration * 60 * 1000);
            cal.setTimeInMillis(NewTime);
            boolean found = false;
            if (i < TodaysEvents.size() - 1) {
                for (int h = i + 1; h < TodaysEvents.size(); h++) {
                    //Začnem šteti na nasljednjem elementu
                    for (int k = 0; k < TodaysEvents.get(h).size(); k++) {
                        //Naslednji element lahko ima več otrok, minimalno 1
                        if (!IgnoredGroups.contains(TodaysEvents.get(h).get(k).group.subGroup)) {
                            //Če element ni v kategoriji, ki jo ignoriramo smo našli naslednika
                            Date d2 = new Date();
                            try {
                                d2 = parser.parse(TodaysEvents.get(h).get(k).startTime);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //D2 = next startTime, Cal = EndTime
                            //Če ima naslednik isti začetni čas kot prejšni končni čas
                            if (!(cal.getTimeInMillis() == d2.getTime())) {
                                EndText.setText(unignored.get(j).endTime);
                            }
                            //V kateremkoli primeru smo našli naslednika - konec
                            found = true;
                            break;
                        }
                    }
                    //Ker smo našli naslednika ne prožim več zanke
                    if (found) {
                        break;
                    }
                    if (h == TodaysEvents.size() - 1) {

                        EndText.setText(unignored.get(j).endTime);

                    }
                }
            }
            //Ni naslednjega elementa, torej smo na zadnji uri
            else {
                EndText.setText(unignored.get(j).endTime);
            }

            int EventType = EventTypes.indexOf(unignored.get(j).type) % 5;

            switch (EventType) {
                case 0:
                  //  EventTypeColor.setBackgroundColor(Color.parseColor("#4f86c6"));
                    root.setBackgroundColor(Color.parseColor("#4f86c6"));
                    break;

                case 1:
                    //EventTypeColor.setBackgroundColor(Color.parseColor("#21a179"));
                    root.setBackgroundColor(Color.parseColor("#21a179"));
                    break;

                case 2:
                   // EventTypeColor.setBackgroundColor(Color.parseColor("#E17756"));
                    root.setBackgroundColor(Color.parseColor("#E17756"));
                    break;

                case 3:
                    //EventTypeColor.setBackgroundColor(Color.parseColor("#744fc6"));
                    root.setBackgroundColor(Color.parseColor("#744fc6"));
                    break;

                case 4:
                   // EventTypeColor.setBackgroundColor(Color.parseColor("#f3a712"));
                    root.setBackgroundColor(Color.parseColor("#f3a712"));
                    break;

                case 5:
                   // EventTypeColor.setBackgroundColor(Color.parseColor("#6320ee"));
                    root.setBackgroundColor(Color.parseColor("#6320ee"));
                    break;

                default:
                   // EventTypeColor.setBackgroundColor(Color.parseColor("#45E49E"));
                    root.setBackgroundColor(Color.parseColor("#45E49E"));
                    break;

            }

            CourseText.setText(unignored.get(j).course);
            GroupText.setText(unignored.get(j).type + " " + unignored.get(j).group.field +
                    unignored.get(j).group.year + " " + unignored.get(j).group.subGroup);
            ProfText.setText(unignored.get(j).professor);
            LocationText.setText(unignored.get(j).room);

            EventBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    View v = inflater.inflate(R.layout.popup_event, null);
                    Event saved = (Event) view.getTag();

                    TextView naziv = v.findViewById(R.id.textView);
                    TextView tip = v.findViewById(R.id.textView4);
                    TextView Program = v.findViewById(R.id.textView6);
                    TextView Skupina = v.findViewById(R.id.textView7);
                    TextView Profesor = v.findViewById(R.id.textView9);
                    TextView Location = v.findViewById(R.id.textView10);
                    TextView Start = v.findViewById(R.id.textView11);
                    TextView End = v.findViewById(R.id.textView12);

                    naziv.setText(saved.course);
                    tip.append(": " + saved.type);
                    Program.append(": " + saved.group.field);
                    Skupina.append(": " + saved.group.subGroup);
                    Profesor.append(": " + saved.professor);
                    Location.append(": " + saved.room);
                    Start.append(": " + saved.startTime);
                    End.append(": " + saved.endTime);


                    builder.setView(v);
                    builder.show();
                }
            });
            myViewHolder.ureplac.addView(EventBox);

        }

    }

    public ArrayList<Event> DobiFiltirane(List<Event> evs) {
        ArrayList<Event> toreturn = new ArrayList<>();
        for (Event ev : evs) {
            if (!IgnoredGroups.contains(ev.group.subGroup)) {
                toreturn.add(ev);
            }
        }

        return toreturn;
    }

    @Override
    public int getItemCount() {
        return TodaysEvents.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        LinearLayout ureplac;

        public MyViewHolder(View v) {
            super(v);
            ureplac = v.findViewById(R.id.UrePlac);


        }
    }


}




