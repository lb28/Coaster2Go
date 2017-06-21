package de.uulm.dbis.coaster2go.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.WaitingTime;

/**
 * Created by Luis on 21.05.2017.
 */

public class WaitingTimeListAdapter extends RecyclerView.Adapter<WaitingTimeListAdapter.ViewHolder> {

    public enum SortMode {
        NAME, DATE, MINUTES
    }

    private List<WaitingTime> waitingTimeList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView waitingtimeName;
        public TextView waitingtimeDate;
        public TextView waitingtimeMinutes;

        public ViewHolder(View itemView) {
            super(itemView);

            waitingtimeName = (TextView) itemView.findViewById(R.id.waitingtime_name);
            waitingtimeDate = (TextView) itemView.findViewById(R.id.waitingtime_date);
            waitingtimeMinutes = (TextView) itemView.findViewById(R.id.waitingtime_minutes);
        }

    }

    public WaitingTimeListAdapter(List<WaitingTime> waitingTimeList) {
        this.waitingTimeList = waitingTimeList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.waitingtime_list_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(WaitingTimeListAdapter.ViewHolder viewHolder, int position) {
        // get the waiting time item based on position
        final WaitingTime waitingTime = waitingTimeList.get(position);

        Date ratingDate = waitingTime.getCreatedAt();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);
        String dateString = dateFormat.format(ratingDate);

        // fill the view based on the data
        viewHolder.waitingtimeName.setText(waitingTime.getDisplayName());
        viewHolder.waitingtimeDate.setText(dateString);
        viewHolder.waitingtimeMinutes.setText(waitingTime.getMinutes() + " min");
    }

    @Override
    public int getItemCount() {
        return waitingTimeList == null ? 0 : waitingTimeList.size();
    }

    public void changeSort(SortMode mode) {
        switch (mode) {
            case NAME:
                Collections.sort(waitingTimeList, new NameComparator());
                notifyDataSetChanged();
                break;
            case MINUTES:
                Collections.sort(waitingTimeList, new MinutesComparator());
                notifyDataSetChanged();
                break;
            case DATE:
                Collections.sort(waitingTimeList, new DateComparator());
                notifyDataSetChanged();
            default:
                break;
        }
    }

    private class NameComparator implements Comparator<WaitingTime> {
        @Override
        public int compare(WaitingTime w1, WaitingTime w2) {
            return w1.getDisplayName().compareTo(w2.getDisplayName());
        }
    }

    private class DateComparator implements Comparator<WaitingTime> {
        @Override
        public int compare(WaitingTime w1, WaitingTime w2) {
            return w2.getCreatedAt().compareTo(w1.getCreatedAt());
        }
    }

    private class MinutesComparator implements Comparator<WaitingTime> {
        @Override
        public int compare(WaitingTime w1, WaitingTime w2) {
            return Double.compare(w1.getMinutes(), w2.getMinutes());
        }
    }

    public void setWaitingTimeList(List<WaitingTime> waitingTimeList) {
        this.waitingTimeList = waitingTimeList;
    }

}
