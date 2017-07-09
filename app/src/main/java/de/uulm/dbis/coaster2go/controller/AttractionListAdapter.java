package de.uulm.dbis.coaster2go.controller;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.pchmn.materialchips.R2;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.Attraction;

import static de.uulm.dbis.coaster2go.activities.AttractionOverviewActivity.ATTRACTION_TYPES;

/**
 * Created by Luis on 06.05.2017.
 */
public class AttractionListAdapter extends RecyclerView.Adapter<AttractionListAdapter.ViewHolder> {

    private static final String TAG = "AttractionListAdapter";

    public enum SortMode {
        NAME, RATING, WAIT_TIME
    }

    private SortMode currentSortMode;
    private String currentSearchString;
    private List<String> currentTypeFilters;

    private List<Attraction> attractionList;
    private List<Attraction> copyOfAttractionList;
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
        this.copyOfAttractionList = attractionList;
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
            viewHolder.attractionImage.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_theme_park));
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
            viewHolder.attractionWaitingBackground.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(66,232,72)));
            viewHolder.attractionWaitingTime.setTextColor(Color.WHITE); //Black or white text color for green background?
        } else if(attraction.getCurrentWaitingTime() > attraction.getAverageWaitingTime()*1.3 || attraction.getCurrentWaitingTime() >= 90){
            viewHolder.attractionWaitingBackground.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,74,58)));
            viewHolder.attractionWaitingTime.setTextColor(Color.WHITE);
        } else {
            viewHolder.attractionWaitingBackground.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255,248,72)));
            viewHolder.attractionWaitingTime.setTextColor(Color.BLACK);
        }

        viewHolder.attractionWaitingBackground.setCompatElevation(0);

        int lastYear = attraction.getLastUpdated().getYear();
        int lastMonth = attraction.getLastUpdated().getMonth();
        Date now = new Date();
        System.out.println(attraction.getLastUpdated());
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

    /**
     * Filters the attractions based on the search string AND the filtered types
     * @param searchString attraction names will all contain the searchString
     * @param filterTypes attraction types will contain
     */
    public void filterList(String searchString, List<String> filterTypes){
        List<String> attrTypes = new ArrayList<>();

        attractionList = copyOfAttractionList;
        List<Attraction> resultList = new ArrayList<>();
        for(Attraction a : attractionList){
            if(a.getName().toLowerCase().contains(searchString.toLowerCase())){
                // do we even need to check for attraction types?
                if (filterTypes.containsAll(ATTRACTION_TYPES)) {
                    // all types are valid --> found a match
                    resultList.add(a);
                } else {
                    // additional type filter --> see if the types match
                    attrTypes.addAll(Arrays.asList(TextUtils.split(a.getType(), ",")));
                    Log.d(TAG, "--- filterList: attrTypes: " + Arrays.toString(attrTypes.toArray()));
                    Log.d(TAG, "--- filterList: filterTypes: " + Arrays.toString(filterTypes.toArray()));
                    for (String type : attrTypes) {
                        // only add the attraction if it matches at least one type
                        // and is not already in the list
                        if (filterTypes.contains(type) && !resultList.contains(a)) {
                            // the attraction contains at least one of the types we filtered for
                            resultList.add(a);
                        }
                    }

                }
            }

            // clear the type list
            attrTypes.clear();
        }
        attractionList = resultList;

        currentSearchString = searchString;
        currentTypeFilters = filterTypes;

        notifyDataSetChanged();
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
        this.copyOfAttractionList = attractionList;

        // restore the last sort and filter state
        if (currentSortMode != null) {
            changeSort(currentSortMode);
        }
        if (currentSearchString != null && currentTypeFilters != null) {
            filterList(currentSearchString, currentTypeFilters);
        }
    }

    public int getPositionOf(Attraction attraction) {
        return attractionList.indexOf(attraction);
    }

}
