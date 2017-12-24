package com.example.omar.timeTableV2.Activity.Adaptors;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.omar.timeTableV2.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class TimetableAdaptor extends RecyclerView.Adapter<TimetableAdaptor.ViewHolder>{

    private List<String>  sessionNames;
    private List<Integer> timeUntilNext;
    private List<Date>    startTimes;
    private List<Date>    endTimes;
    private int           sessionCount;

    private Context context;

    private SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.US);


    public TimetableAdaptor(List<String> sessionNames, List<Date> startTimes, List<Date> endTimes,
                            List<Integer> timeUntilNext){

        this.sessionNames = sessionNames;
        this.startTimes = startTimes;
        this.endTimes = endTimes;
        this.timeUntilNext = timeUntilNext;

        sessionCount = sessionNames.size();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View v = LayoutInflater.from(parent.getContext())
                               .inflate(R.layout.adaptor_timetable, parent, false);

        context = parent.getContext();

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position){

        setBottomMargin(holder.parent, position);
        setHeight(holder.parent, position);

        holder.sessionName.setText(sessionNames.get(position));
        holder.sessionStart.setText(sdf.format(startTimes.get(position)));
        holder.sessionEnd.setText(sdf.format(endTimes.get(position)));

    }


    @Override
    public int getItemCount(){

        return sessionCount;
    }


    private void setBottomMargin(View view, int position){

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();

        int leftMargin  = lp.leftMargin;
        int rightMargin = lp.rightMargin;
        int topMargin   = lp.topMargin;

        Resources r = context.getResources();

        int bottomMargin = timeUntilNext.get(position) == 0 ? 8 : timeUntilNext.get(position) * 2;
        bottomMargin = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomMargin, r.getDisplayMetrics());
        lp.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);


    }


    private void setHeight(View v, int position){

        long length = endTimes.get(position).getTime() - startTimes.get(position).getTime();
        length /= 60000;

        Resources r = context.getResources();
        length = length <= 10 ? 10 : length * 2;

        v.getLayoutParams().height = (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, length, r.getDisplayMetrics());
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        TextView sessionName, sessionStart, sessionEnd;
        CardView parent;


        ViewHolder(View itemView){

            super(itemView);

            sessionName = (TextView) itemView.findViewById(R.id.txt_session_name);
            sessionStart = (TextView) itemView.findViewById(R.id.txt_session_start);
            sessionEnd = (TextView) itemView.findViewById(R.id.txt_session_end);
            parent = (CardView) itemView.findViewById(R.id.timetable_parent);
        }
    }
}
