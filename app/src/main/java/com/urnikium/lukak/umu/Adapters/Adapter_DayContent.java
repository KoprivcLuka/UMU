package com.urnikium.lukak.umu.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.urnikium.lukak.umu.Classes.Event;
import com.urnikium.lukak.umu.Classes.TinyDB;
import com.urnikium.lukak.umu.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


public class Adapter_DayContent extends RecyclerView.Adapter<Adapter_DayContent.MyViewHolder> {
    ArrayList<ArrayList<Event>> TodaysEvents;
    List<String> EventTypes = new ArrayList<>();
    ArrayList<String> IgnoredGroups = new ArrayList<>();
    Boolean Today = true;
    Boolean Past = true;
    final Date Date;


    public Adapter_DayContent(ArrayList<ArrayList<Event>> list, Date Date) {
        Calendar tod = Calendar.getInstance();
        this.Today = false;
        this.Past = false;
        this.Date = Date;
        Date.setHours(0);
        Date.setMinutes(0);

        Date date8PM = getDateTo8PM(Date); //Date is in the past After Eight

        if (tod.getTimeInMillis() > date8PM.getTime()) {
            this.Past = true;
        }

        tod.setTimeInMillis(Date.getTime());
        Calendar calendar = Calendar.getInstance();

        if (tod.get(Calendar.DATE) == calendar.get(Calendar.DATE) && tod.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && tod.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            this.Today = true;
        }

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
            View Endline = EventBox.findViewById(R.id.EndLine);
            TextView CourseText = EventBox.findViewById(R.id.Course);
            TextView ProfText = EventBox.findViewById(R.id.Prof);
            TextView LocationText = EventBox.findViewById(R.id.Location);
            LinearLayout root = EventBox.findViewById(R.id.root);
            TextView GroupText = EventBox.findViewById(R.id.Grp);
            TextView PathAndTypeText = EventBox.findViewById(R.id.pth);
            View ColorIndicator = EventBox.findViewById(R.id.colorIndicator);
            EventBox.setTag(unignored.get(j));
            String StartTime = unignored.get(j).getStartTime().split(":")[0];
            if (StartTime.length() == 1) {
                StartText.setText("0" + unignored.get(j).getStartTime());
            } else {
                StartText.setText(unignored.get(j).getStartTime());
            }
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date d1 = new Date();
            try {
                d1 = parser.parse(unignored.get(j).getEndTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }


            int orientation = myViewHolder.ureplac.getContext().getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (unignored.size() == 1) {
                    root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                } else {
                    if (j != unignored.size() && j != 0) {
                        root.setPadding(root.getPaddingLeft(), root.getPaddingTop(), 0, root.getPaddingBottom());
                        root.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    } else {
                        root.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    }
                }

            } else {
                if (unignored.size() == 1) {

                    root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                } else {

                    root.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }
            }


            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(d1.getTime());
            boolean found = false;
            if (i < TodaysEvents.size() - 1) {
                for (int h = i + 1; h < TodaysEvents.size(); h++) {
                    //Začnem šteti na nasljednjem elementu
                    for (int k = 0; k < TodaysEvents.get(h).size(); k++) {
                        //Naslednji element lahko ima več otrok, minimalno 1
                        if (!IgnoredGroups.contains(TodaysEvents.get(h).get(k).getGroup().getSubGroup())) {
                            //Če element ni v kategoriji, ki jo ignoriramo smo našli naslednika
                            Date d2 = new Date();
                            try {
                                d2 = parser.parse(TodaysEvents.get(h).get(k).getStartTime());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //D2 = next startTime, Cal = EndTime
                            //Če ima naslednik isti začetni čas kot prejšni končni čas
                            if (!(cal.getTimeInMillis() == d2.getTime())) {
                                EndText.setText(unignored.get(j).getEndTime());
                                Endline.setVisibility(View.VISIBLE);
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
                        EndText.setText(unignored.get(j).getEndTime());

                    }
                }
            }
            //Ni naslednjega elementa, torej smo na zadnji uri
            else {
                EndText.setText(unignored.get(j).getEndTime());
            }

            if (i != 0 && TodaysEvents.get(i - 1).get(0).getEndTime().equals(TodaysEvents.get(i).get(0).getStartTime())) {
                if (Endline.getVisibility() == View.VISIBLE) {
                    ColorIndicator.setBackground(root.getContext().getDrawable(R.drawable.tableft_rounded_bottom));
                } else {
                    if (i == TodaysEvents.size() - 1) {
                        ColorIndicator.setBackground(root.getContext().getDrawable(R.drawable.tableft_rounded_bottom));
                    } else {
                        ColorIndicator.setBackground(root.getContext().getDrawable(R.drawable.tableft_straight));
                    }
                }
            } else {
                if (Endline.getVisibility() == View.VISIBLE) {
                    ColorIndicator.setBackground(root.getContext().getDrawable(R.drawable.tableft_rounded_single));
                } else {
                    if (i == TodaysEvents.size() - 1) {
                        ColorIndicator.setBackground(root.getContext().getDrawable(R.drawable.tableft_rounded_single));
                    } else {
                        ColorIndicator.setBackground(root.getContext().getDrawable(R.drawable.tableft_rounded_top));
                    }
                }
            }


            int EventType = EventTypes.indexOf(unignored.get(j).getType()) % 5;

            switch (EventType) {
                case 0:
                    ColorIndicator.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#90C147")));
                    break;

                case 1:
                    ColorIndicator.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#21a179")));
                    break;

                case 2:
                    ColorIndicator.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E17756")));
                    break;

                case 3:
                    ColorIndicator.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#744fc6")));
                    break;

                case 4:
                    ColorIndicator.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f3a712")));
                    break;

                case 5:
                    ColorIndicator.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#6320ee")));
                    break;

                default:
                    ColorIndicator.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#45E49E")));
                    break;

            }

            CourseText.setText(unignored.get(j).getCourse());
            GroupText.setText(unignored.get(j).getGroup().getField() + " " +
                    unignored.get(j).getGroup().getYear() + ". letnik");
            PathAndTypeText.setText("(" + unignored.get(j).getType() + ") " + unignored.get(j).getGroup().getSubGroup());
            ProfText.setText(unignored.get(j).getProfessor());
            LocationText.setText(unignored.get(j).getRoom());

            EventBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    View v = inflater.inflate(R.layout.popup_event, null);
                    final Event saved = (Event) view.getTag();
                    final TextView naziv = v.findViewById(R.id.textView);
                    TextView tip = v.findViewById(R.id.textView4);
                    TextView Program = v.findViewById(R.id.textView6);
                    TextView Skupina = v.findViewById(R.id.textView7);
                    TextView Profesor = v.findViewById(R.id.textView9);
                    TextView Location = v.findViewById(R.id.textView10);
                    TextView Start = v.findViewById(R.id.textView11);
                    TextView End = v.findViewById(R.id.textView12);

                    naziv.setText(saved.getCourse());
                    tip.append(": " + saved.getType());
                    Program.append(": " + saved.getGroup().getField());
                    Skupina.append(": " + saved.getGroup().getSubGroup());
                    Profesor.append(": " + saved.getProfessor());
                    Location.append(": " + saved.getRoom());
                    Start.append(": " + saved.getStartTime());
                    End.append(": " + saved.getEndTime());

                    Button Export = v.findViewById(R.id.exportButton);
                    Export.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_INSERT);
                            intent.setType("vnd.android.cursor.item/event");
                            SimpleDateFormat format1 = new SimpleDateFormat("hh:mm");
                            try {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(Date.getTime());

                                cal.set(Calendar.HOUR_OF_DAY, format1.parse(saved.getStartTime()).getHours());
                                cal.set(Calendar.MINUTE, format1.parse(saved.getStartTime()).getMinutes());
                                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, cal.getTimeInMillis());

                                cal.set(Calendar.HOUR_OF_DAY, format1.parse(saved.getEndTime()).getHours());
                                cal.set(Calendar.MINUTE, format1.parse(saved.getEndTime()).getMinutes());
                                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, cal.getTimeInMillis());

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false);
                            intent.putExtra(CalendarContract.Events.TITLE, saved.getCourse());
                            intent.putExtra(CalendarContract.Events.DESCRIPTION, saved.getGroup().getSubGroup() + "\n" + saved.getProfessor());
                            intent.putExtra(CalendarContract.Events.EVENT_LOCATION, saved.getRoom());
                            v.getContext().startActivity(intent);
                        }
                    });


                    builder.setView(v);
                    builder.show();
                }
            });
            if (Past) {
                EventBox.setAlpha(.3f);
            }
            if (Today) {// pogoji : ce je dogodek mimo ali ce je dogodek v teku
                try {
                    Calendar now = Calendar.getInstance();
                    Calendar start = Calendar.getInstance();
                    start.setTime((parser.parse(unignored.get(j).getStartTime())));
                    start.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
                    Calendar end = Calendar.getInstance();
                    end.setTime((parser.parse(unignored.get(j).getEndTime())));
                    end.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE));
                    if (end.before(now)) {
                        EventBox.setAlpha(.3f);
                    }

                    /*else if (now.before(end) && now.after(start)) {
                        HeighSetter.setBackgroundColor(HeighSetter.getContext().getResources().getColor(R.color.InProgress));
                        StartText.setBackgroundColor(HeighSetter.getContext().getResources().getColor(R.color.InProgress));
                        EndText.setBackgroundColor(HeighSetter.getContext().getResources().getColor(R.color.InProgress));
                    } */


                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
            myViewHolder.ureplac.addView(EventBox);

        }

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

    private Date getDateTo8PM(Date date) {
        Calendar cal = Calendar.getInstance();

        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 55);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }


}




