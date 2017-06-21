package de.uulm.dbis.coaster2go.controller;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.Attraction;

/**
 * Created by Luis on 06.05.2017.
 */
public class AttractionListAdapter extends RecyclerView.Adapter<AttractionListAdapter.ViewHolder> {

    public enum SortMode {
        NAME, RATING, WAIT_TIME
    }

    // TODO use currentSortMode
    private SortMode currentSortMode;

    private List<Attraction> attractionList;
    private Context context;
    private final OnAttractionItemClickListener clickListener;
    private final OnAttractionItemLongClickListener longClickListener;
    private final int millisecondsOfADay = 86400000;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView attractionImage;
        TextView attractionName, attractionDate;
        RatingBar attractionRating;
        TextView attractionWaitingTime;
        FloatingActionButton attractionWaitingBackground;

        ViewHolder(View itemView) {
            super(itemView);

            attractionImage = (ImageView) itemView.findViewById(R.id.attrList_image);
            attractionName = (TextView) itemView.findViewById(R.id.attrList_name);
            attractionRating = (RatingBar) itemView.findViewById(R.id.attrList_rating);
            attractionWaitingTime = (TextView) itemView.findViewById(R.id.attrList_waitingTime);
            attractionWaitingBackground = (FloatingActionButton) itemView.findViewById(R.id.attrList_waitingBackground);
            attractionDate = (TextView) itemView.findViewById(R.id.attrList_today_date);
        }

    }

    public AttractionListAdapter(Context context, List<Attraction> attractionList,
                                 OnAttractionItemClickListener clickListener,
                                 OnAttractionItemLongClickListener longClickListener) {
        this.context = context;
        this.attractionList = attractionList;
        this.clickListener = clickListener;
        this.longClickListener = longClickListener;
    }

    @Override
    public AttractionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.attraction_list_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AttractionListAdapter.ViewHolder viewHolder, int position) {
        // get the attraction item based on position
        final Attraction attraction = attractionList.get(position);

        // fill the view based on the data
        if (attraction.getImage() == null || attraction.getImage().isEmpty()) {
            Picasso.with(context).load(R.drawable.ic_theme_park).
                    into(viewHolder.attractionImage);
        } else {
            Picasso.with(context).load(attraction.getImage()).into(viewHolder.attractionImage);
        }
        viewHolder.attractionName.setText(attraction.getName());
        viewHolder.attractionRating.setRating((float) attraction.getAverageReview());

        //If Latest WaitingTime is from Today use the last three ones else use the average of the last day with waiting times:
        if(!DateUtils.isToday(attraction.getLastUpdated().getTime())){
            attraction.setCurrentWaitingTime(attraction.getAverageTodayWaitingTime());
        }

        viewHolder.attractionWaitingTime.setText(attraction.getCurrentWaitingTime()+"");

        if(attraction.getCurrentWaitingTime() < attraction.getAverageWaitingTime()*0.7 || attraction.getCurrentWaitingTime() <= 10){
            viewHolder.attractionWaitingBackground.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
            viewHolder.attractionWaitingTime.setTextColor(Color.WHITE); //Black or white text color for green background?
        } else if(attraction.getCurrentWaitingTime() > attraction.getAverageWaitingTime()*1.3 || attraction.getCurrentWaitingTime() >= 90){
            viewHolder.attractionWaitingBackground.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
            viewHolder.attractionWaitingTime.setTextColor(Color.WHITE);
        } else {
            viewHolder.attractionWaitingBackground.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
            viewHolder.attractionWaitingTime.setTextColor(Color.BLACK);
        }

        viewHolder.attractionWaitingBackground.setCompatElevation(0);

        int lastYear = attraction.getLastUpdated().getYear();
        int lastMonth = attraction.getLastUpdated().getMonth();
        Date now = new Date();
        if((now.getYear() == lastYear) &&  (now.getMonth() == lastMonth)){
            if(DateUtils.isToday(attraction.getLastUpdated().getTime())){
                Date lastUpdated = attraction.getLastUpdated();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.GERMANY);
                String dateString = dateFormat.format(lastUpdated);
                viewHolder.attractionDate.setText("Heute "+dateString);
            }else if(now.getTime() - attraction.getLastUpdated().getTime() < 2*millisecondsOfADay){
                viewHolder.attractionDate.setText("Gestern");
            }else{
                Date lastUpdated = attraction.getLastUpdated();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.GERMANY);
                String dateString = dateFormat.format(lastUpdated);
                viewHolder.attractionDate.setText(dateString);
            }
        }else {
            Date lastUpdated = attraction.getLastUpdated();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.GERMANY);
            String dateString = dateFormat.format(lastUpdated);
            viewHolder.attractionDate.setText(dateString);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onAttractionItemClick(attraction);
            }
        });
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return longClickListener.onAttractionItemLongClick(attraction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attractionList == null ? 0 : attractionList.size();
    }

    public void changeSort(SortMode mode) {
        switch (mode) {
            case NAME:
                Collections.sort(attractionList, new NameComparator());
                notifyDataSetChanged();
                break;
            case RATING:
                Collections.sort(attractionList, new RatingComparator());
                notifyDataSetChanged();
                break;
            case WAIT_TIME:
                Collections.sort(attractionList, new WaitTimeComparator());
                notifyDataSetChanged();
                break;
            default:
                break;
        }

        // remember the new sort mode
        currentSortMode = mode;
    }

    private class RatingComparator implements Comparator<Attraction> {
        @Override
        public int compare(Attraction attraction1, Attraction attraction2) {
            // attraction 1 and 2 are reversed so the highest rating is on top
            return Double.compare(attraction2.getAverageReview(), attraction1.getAverageReview());
        }
    }

    private class NameComparator implements Comparator<Attraction> {
        @Override
        public int compare(Attraction attraction1, Attraction attraction2) {
            // park1 and park2 are reversed so the highest rating is on top
            return attraction1.getName().compareTo(attraction2.getName());
        }
    }

    private class WaitTimeComparator implements Comparator<Attraction> {
        @Override
        public int compare(Attraction a1, Attraction a2) {
            if(!DateUtils.isToday(a1.getLastUpdated().getTime())){
                a1.setCurrentWaitingTime(a1.getAverageTodayWaitingTime());
            }
            if(!DateUtils.isToday(a2.getLastUpdated().getTime())){
                a2.setCurrentWaitingTime(a2.getAverageTodayWaitingTime());
            }
            return Double.compare(a1.getCurrentWaitingTime(), a2.getCurrentWaitingTime());
        }
    }

    public void removeAt(int pos) {
        attractionList.remove(pos);
        notifyItemRemoved(pos);
    }

    // GETTERS & SETTERS

    public Context getContext() {
        return context;
    }

    public void setAttractionList(List<Attraction> attractionList) {
        this.attractionList = attractionList;
        if (currentSortMode != null) {
            changeSort(currentSortMode);
        }
    }

    public int getPositionOf(Attraction attraction) {
        return attractionList.indexOf(attraction);
    }

}
