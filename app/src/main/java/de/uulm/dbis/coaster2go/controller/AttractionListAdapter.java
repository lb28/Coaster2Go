package de.uulm.dbis.coaster2go.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.Attraction;
import de.uulm.dbis.coaster2go.data.Park;

/**
 * Created by Luis on 06.05.2017.
 */
public class AttractionListAdapter extends RecyclerView.Adapter<AttractionListAdapter.ViewHolder> {

    public static final String SORT_MODE_RATING = "SORT_MODE_RATING";
    public static final String SORT_MODE_ABC = "SORT_MODE_ABC";

    // TODO implement SortedList?
    // private SortedList<Park> parkList;

    private List<Attraction> attractionList;
    private Context context;
    private final OnAttractionItemClickListener clickListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView attractionImage;
        public TextView attractionName;
        public RatingBar attractionRating;
        public TextView attractionWaitingTime;

        public ViewHolder(View itemView) {
            super(itemView);

            attractionImage = (ImageView) itemView.findViewById(R.id.attrList_image);
            attractionName = (TextView) itemView.findViewById(R.id.attrList_name);
            attractionRating = (RatingBar) itemView.findViewById(R.id.attrList_rating);
            attractionWaitingTime = (TextView) itemView.findViewById(R.id.attrList_waitingTime);
        }

    }

    public AttractionListAdapter(Context context, List<Attraction> attractionList,
                                 OnAttractionItemClickListener clickListener) {
        this.context = context;
        this.attractionList = attractionList;
        this.clickListener = clickListener;
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
        Picasso.with(context).load(attraction.getImage()).into(viewHolder.attractionImage);
        viewHolder.attractionName.setText(attraction.getName());
        viewHolder.attractionRating.setRating((float) attraction.getAverageReview());
        viewHolder.attractionWaitingTime.setText(attraction.getCurrentWaitingTime() + " min");

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onAttractionItemClick(attraction);
            }
        });
    }

    @Override
    public int getItemCount() {
        return attractionList == null ? 0 : attractionList.size();
    }

    public void changeSort(String mode) {
        switch (mode) {
            case SORT_MODE_ABC:
                Collections.sort(attractionList, new AbcComparator());
                notifyDataSetChanged();
                break;
            case SORT_MODE_RATING:
                Collections.sort(attractionList, new RatingComparator());
                notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    private class RatingComparator implements Comparator<Attraction> {
        @Override
        public int compare(Attraction attraction1, Attraction attraction2) {
            // attraction 1 and 2 are reversed so the highest rating is on top
            return Double.compare(attraction2.getAverageReview(), attraction1.getAverageReview());
        }
    }

    private class AbcComparator implements Comparator<Attraction> {
        @Override
        public int compare(Attraction attraction1, Attraction attraction2) {
            // park1 and park2 are reversed so the highest rating is on top
            return attraction1.getName().compareTo(attraction2.getName());
        }
    }

    // GETTERS & SETTERS

    public Context getContext() {
        return context;
    }

    public void setAttractionList(List<Attraction> attractionList) {
        this.attractionList = attractionList;
    }
}
