package com.fuu.lukak.fuu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AuthenticationRequiredException;
import android.content.Context;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

public class UrnikAdapter extends RecyclerView.Adapter<UrnikAdapter.MyViewHolder> {
    private ArrayList<ArrayList<Event>> list;
    List<String> types = new ArrayList<>();
    ArrayList<String> toignore = new ArrayList<>();

    Context con;

    public UrnikAdapter() {
    }

    public UrnikAdapter(ArrayList<ArrayList<Event>> list) {
        this.list = list;

    }

    @NonNull
    @Override
    public UrnikAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        TinyDB tiny = new TinyDB(viewGroup.getContext());
        types = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik") + "types");
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.urnikadapter, viewGroup, false);
        MyViewHolder holder = new MyViewHolder(v);
        con = viewGroup.getContext();


        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull final UrnikAdapter.MyViewHolder myViewHolder, int i) {
        LayoutInflater inflater = (LayoutInflater) myViewHolder.ureplac.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        TinyDB tiny = new TinyDB(con);
        toignore = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik"));

        ArrayList<Event> unignored = DobiFiltirane(list.get(i));
        if (list.get(i).size() != 0) {
            for (int j = 0; j < unignored.size(); j++) {

                View single = inflater.inflate(R.layout.singlepredmet, null);
                TextView start = single.findViewById(R.id.Start);
                TextView end = single.findViewById(R.id.End);
                TextView course = single.findViewById(R.id.Course);
                TextView prof = single.findViewById(R.id.Prof);
                TextView loc = single.findViewById(R.id.Location);
                RelativeLayout height = single.findViewById(R.id.heigtsetter);
                LinearLayout root = single.findViewById(R.id.root);
                LinearLayout typecolor = single.findViewById(R.id.colortype);
                TextView grp = single.findViewById(R.id.Grp);

                start.setText(unignored.get(j).startTime);
                SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
                Date d1 = new Date();

                try {
                    d1 = parser.parse(unignored.get(j).startTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                int pixels = (int) (200 * loc.getContext().getResources().getDisplayMetrics().density);
                if (unignored.size() == 1) {

                    root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    if (unignored.get(j).duration < 120) {
                        height.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (120 * 3)));
                    } else {
                        height.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (unignored.get(j).duration * 3)));
                    }
                } else {
                    root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    if (unignored.get(j).duration < 120) {
                        height.setLayoutParams(new LinearLayout.LayoutParams(pixels, (120 * 3)));
                    } else {
                        height.setLayoutParams(new LinearLayout.LayoutParams(pixels, (list.get(i).get(j).duration * 3)));
                    }
                }

                //i mean tu je nek horsefuckery not ampak dela?
                Calendar cal = Calendar.getInstance();
                long newtime = d1.getTime() + (long) (unignored.get(j).duration * 60 * 1000);
                cal.setTimeInMillis(newtime);
                boolean found = false;
                if (i < list.size() - 1) {
                    for (int h = i + 1; h < list.size(); h++) {
                        //Začnem šteti na nasljednjem elementu
                        for (int k = 0; k < list.get(h).size(); k++) {
                            //Naslednji element lahko ima več otrok, minimalno 1
                            if (!toignore.contains(list.get(h).get(k).group.subGroup)) {
                                //Če element ni v kategoriji, ki jo ignoriramo smo našli naslednika
                                Date d2 = new Date();
                                try {
                                    d2 = parser.parse(list.get(h).get(k).startTime);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                //D2 = next startTime, Cal = EndTime
                                //Če ima naslednik isti začetni čas kot prejšni končni čas
                                if (!(cal.getTimeInMillis() == d2.getTime())) {
                                    int ura = cal.get(Calendar.HOUR_OF_DAY);
                                    String uraout = "";
                                    if (ura < 10) {
                                        uraout = "0" + ura;
                                    } else {
                                        uraout = ura + "";
                                    }
                                    if (cal.get(Calendar.MINUTE) == 0) {
                                        end.setText(uraout + ":00");
                                    } else {
                                        end.setText(uraout + ":" + cal.get(Calendar.MINUTE));
                                    }
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
                        if (h == list.size() - 1) {
                            int ura = cal.get(Calendar.HOUR_OF_DAY);
                            String uraout = "";
                            if (ura < 10) {
                                uraout = "0" + ura;
                            } else {
                                uraout = ura + "";
                            }
                            if (cal.get(Calendar.MINUTE) == 0) {
                                end.setText(uraout + ":00");
                            } else {
                                end.setText(uraout + ":" + cal.get(Calendar.MINUTE));
                            }
                        }
                    }

                }
                //Ni naslednjega elementa, torej smo na zadnji uri
                else {
                    int ura = cal.get(Calendar.HOUR_OF_DAY);
                    String uraout = "";
                    if (ura < 10) {
                        uraout = "0" + ura;
                    } else {
                        uraout = ura + "";
                    }

                    if (cal.get(Calendar.MINUTE) == 0) {
                        end.setText(uraout + ":00");
                    } else {
                        end.setText(uraout + ":" + cal.get(Calendar.MINUTE));
                    }
                }

                int typercolor = types.indexOf(unignored.get(j).type) % 5;

                switch (typercolor) {
                    case 0:
                        typecolor.setBackgroundColor(Color.parseColor("#4f86c6"));
                        //  height.setBackgroundColor(Color.parseColor("#C7E8F7"));

                        break;

                    case 1:
                        typecolor.setBackgroundColor(Color.parseColor("#21a179"));
                        // height.setBackgroundColor(Color.parseColor("#D0F2D2"));

                        break;

                    case 2:
                        typecolor.setBackgroundColor(Color.parseColor("#E17756"));
                        // height.setBackgroundColor(Color.parseColor("#F7DDD4"));

                        break;

                    case 3:
                        typecolor.setBackgroundColor(Color.parseColor("#744fc6"));
                        //  height.setBackgroundColor(Color.parseColor("#EEC4EE"));

                        break;


                    case 4:
                        typecolor.setBackgroundColor(Color.parseColor("#f3a712"));
                        //   height.setBackgroundColor(Color.parseColor("#FFFFB6"));


                        break;


                    case 5:
                        typecolor.setBackgroundColor(Color.parseColor("#6320ee"));
                        //  height.setBackgroundColor(Color.parseColor("#C9D1FC"));

                        break;

                    default:
                        typecolor.setBackgroundColor(Color.parseColor("#45E49E"));

                        break;


                }

                course.setText(unignored.get(j).course);
                grp.setText(unignored.get(j).type + " " + unignored.get(j).group.field + unignored.get(j).group.year + " " + unignored.get(j).group.subGroup);
                prof.setText(unignored.get(j).professor);
                loc.setText(unignored.get(j).room);
                myViewHolder.ureplac.addView(single);

            }
        }

      else
        {
            View motivacija = inflater.inflate(R.layout.motivacijskosporocilo, null);
            TextView sporocilo = motivacija.findViewById(R.id.textView4);
            ImageView slikca = motivacija.findViewById(R.id.imageView2);
            int pixels = (int) (400 * sporocilo.getContext().getResources().getDisplayMetrics().density);
            motivacija.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, pixels));

            List<String> sporocilca = new ArrayList<>();
            sporocilca.add("Prazno");
            Random rand = new Random();
            int cifr = rand.nextInt(sporocilca.size());

            sporocilo.setText(sporocilca.get(cifr%sporocilca.size()));
            myViewHolder.ureplac.addView(motivacija);
        }

    }

    public ArrayList<Event> DobiFiltirane(List<Event> evs) {
        ArrayList<Event> toreturn = new ArrayList<>();
        for (Event ev : evs) {
            if (!toignore.contains(ev.group.subGroup)) {
                toreturn.add(ev);
            }
        }

        return toreturn;
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
//            scroller = v.findViewById(R.id.scroller);

        }
    }


}
