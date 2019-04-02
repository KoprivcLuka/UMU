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

public class UrnikAdapter extends RecyclerView.Adapter<UrnikAdapter.MyViewHolder> {
    private List<List<Event>> list;
    List<String> types = new ArrayList<>();

    Context con;

    public UrnikAdapter() {
    }

    public UrnikAdapter(List<List<Event>> list) {
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
        ArrayList<String> toignore = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik"));

        ArrayList<Event> unignored = DobiFiltirane(list.get(i));

        //TODO UNIFY TA GAMAD KOLEGA
        if (unignored.size()== 1) {
            if (!toignore.contains(list.get(i).get(0).group.subGroup)) {
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


                Calendar cal = GregorianCalendar.getInstance();
                long newtime = d1.getTime() + (long) (list.get(i).get(0).duration * 60 * 1000);
                cal.setTimeInMillis(newtime);

                boolean found = false;
                // I = Zacetna ura
                // J = Naslednji element
                // K = Prekrivanje v elementu J
                if (i < list.size() - 1) {
                    for (int j = i + 1; j < list.size(); j++) {
                        //Začnem šteti na nasljednjem elementu
                        for (int k = 0; k < list.get(j).size(); k++) {
                            //Naslednji element lahko ima več otrok, minimalno 1
                            if (!toignore.contains(list.get(j).get(k).group.subGroup)) {
                                //Če element ni v kategoriji, ki jo ignoriramo smo našli naslednika
                                Date d2 = new Date();
                                try {
                                    d2 = parser.parse(list.get(j).get(k).startTime);
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
                        //če smo na koncu arraya in ni najden
                        if (j == list.size() - 1) {
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

                switch (types.indexOf(list.get(i).get(0).type)) {
                    case 0:
                        typecolor.setBackgroundColor(Color.parseColor("#5DBEE8"));
                        height.setBackgroundColor(Color.parseColor("#C7E8F7"));
                        break;

                    case 1:
                        typecolor.setBackgroundColor(Color.parseColor("#58CF60"));
                        height.setBackgroundColor(Color.parseColor("#D0F2D2"));
                        break;

                    case 2:
                        typecolor.setBackgroundColor(Color.parseColor("#E17756"));
                        height.setBackgroundColor(Color.parseColor("#F7DDD4"));
                        break;

                    case 3:
                        typecolor.setBackgroundColor(Color.parseColor("#DA7CD9"));
                        height.setBackgroundColor(Color.parseColor("#EEC4EE"));
                        break;


                    case 4:
                        typecolor.setBackgroundColor(Color.parseColor("#F1CE47"));
                        height.setBackgroundColor(Color.parseColor("#FFFFB6"));
                        break;


                    case 5:
                        typecolor.setBackgroundColor(Color.parseColor("#7D8FF7"));
                        height.setBackgroundColor(Color.parseColor("#C9D1FC"));
                        break;

                    default:
                        typecolor.setBackgroundColor(Color.parseColor("#45E49E"));
                        height.setBackgroundColor(Color.parseColor("#B8FBDB"));
                        break;


                }

                course.setText(list.get(i).get(0).course);
                grp.setText(list.get(i).get(0).type + " " + list.get(i).get(0).group.field + list.get(i).get(0).group.year + " " + list.get(i).get(0).group.subGroup);
                prof.setText(list.get(i).get(0).professor);
                loc.setText(list.get(i).get(0).room);


                myViewHolder.ureplac.addView(single);
            }

        } else { //TODO čekn kere dejansko ne ignoram
            for (int j = 0; j < list.get(i).size(); j++) {
                if (!toignore.contains(list.get(i).get(j).group.subGroup)) {
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
                    long newtime = d1.getTime() + (long) (list.get(i).get(j).duration * 60 * 1000);
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


                    switch (types.indexOf(list.get(i).get(j).type)) {
                        case 0:
                            typecolor.setBackgroundColor(Color.parseColor("#5DBEE8"));
                            height.setBackgroundColor(Color.parseColor("#C7E8F7"));

                            break;

                        case 1:
                            typecolor.setBackgroundColor(Color.parseColor("#58CF60"));
                            height.setBackgroundColor(Color.parseColor("#D0F2D2"));

                            break;

                        case 2:
                            typecolor.setBackgroundColor(Color.parseColor("#E17756"));
                            height.setBackgroundColor(Color.parseColor("#F7DDD4"));

                            break;

                        case 3:
                            typecolor.setBackgroundColor(Color.parseColor("#DA7CD9"));
                            height.setBackgroundColor(Color.parseColor("#EEC4EE"));

                            break;


                        case 4:
                            typecolor.setBackgroundColor(Color.parseColor("#F1CE47"));
                            height.setBackgroundColor(Color.parseColor("#FFFFB6"));


                            break;


                        case 5:
                            typecolor.setBackgroundColor(Color.parseColor("#7D8FF7"));
                            height.setBackgroundColor(Color.parseColor("#C9D1FC"));

                            break;

                        default:
                            typecolor.setBackgroundColor(Color.parseColor("#45E49E"));

                            break;


                    }

                    course.setText(list.get(i).get(j).course);
                    grp.setText(list.get(i).get(j).type + " " + list.get(i).get(j).group.field + list.get(i).get(j).group.year + " " + list.get(i).get(j).group.subGroup);
                    prof.setText(list.get(i).get(j).professor);
                    loc.setText(list.get(i).get(j).room);
                    myViewHolder.ureplac.addView(single);
                }
            }
        }


    }

    public ArrayList<Event> DobiFiltirane(List<Event> evs) {
        TinyDB tiny = new TinyDB(con.getApplicationContext());
        ArrayList<Event> toreturn = new ArrayList<>();
        //kategorije se ne osvezujejo
        ArrayList<String> toignore = tiny.getListString(tiny.getString("currpath") + tiny.getString("letnik"));
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
            scroller = v.findViewById(R.id.scroller);

        }
    }


}
