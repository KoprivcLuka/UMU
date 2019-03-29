package com.fuu.lukak.fuu;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class DayFragment extends android.support.v4.app.Fragment {

    private static final Calendar Day = Calendar.getInstance();


    private OnFragmentInteractionListener mListener;

    class SortByHour implements Comparator<Event> {
        public int compare(Event a, Event b) {
            SimpleDateFormat parser = new SimpleDateFormat("HH:mm");
            Date d1 = new Date();
            Date d2 = new Date();
            try {
                d1 = parser.parse(a.startTime);
                d2 = parser.parse(b.startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (d1.getTime() < d2.getTime()) return -1;
            else if (d1.getTime() == d1.getTime()) return 0;
            else return 1;
        }
    }

    class SortByDuration implements Comparator<Event> {
        public int compare(Event a, Event b) {

            if (a.duration < b.duration) return 1;
            else if (a.duration == b.duration) return 0;
            else return -1;
        }
    }

    public DayFragment() {
        // Required empty public constructor
    }


    public static DayFragment newInstance(Calendar Day) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putLong("Day", Day.getTimeInMillis());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Day.setTimeInMillis(getArguments().getLong("Day"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_day, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        ViewActivity main = (ViewActivity) getActivity();
        Calendar now = Calendar.getInstance();
        Calendar begining = Calendar.getInstance();

        if (begining.get(Calendar.MONTH) < 9) {
            begining.set(begining.get(Calendar.YEAR) - 1, 9, 1);
        } else {
            begining.set(begining.get(Calendar.YEAR), 9, 1);
        }
        int weeks = Math.round((float) (now.getTimeInMillis() - begining.getTimeInMillis()) / (1000 * 60 * 60 * 24 * 7)) + 1;
        ArrayList<Event> today = new ArrayList<>();
        for (int i = 0; i < main.res.size(); i++) {
            Event ev = main.res.get(i);
            if (ev.endWeek >= weeks && ev.beginWeek <= weeks && (ev.dayOfWeek + 1 == (Day.get(Calendar.DAY_OF_WEEK)) - 1)) {
                today.add(ev);
            }
        }

        //SAMO DA RADIII PUBECIII
        Collections.sort(today, new SortByHour());
        List<List<Event>> pouri = new ArrayList<>();
        List<Event> enaura = new ArrayList<>();

        for (int i = 0; i < today.size(); i++) {
            if (i == 0) {
                enaura.add(today.get(i));
            } else {
                if (!today.get(i - 1).startTime.equals(today.get(i).startTime)) {

                    pouri.add(enaura);
                    enaura = new ArrayList<>();
                    enaura.add(today.get(i));
                } else {
                    enaura.add(today.get(i));
                }
            }
        }
        pouri.add(enaura); // ja nič po uri so razbiti..zaj rabm sam nek clever nacin da view nardim

        RecyclerView recyclerView = view.findViewById(R.id.urnikrecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        recyclerView.setAdapter(new UrnikAdapter(pouri));

        for (int i = 0; i < pouri.size(); i++)
        {
            Collections.sort(pouri.get(i),new SortByDuration());
        }


    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
