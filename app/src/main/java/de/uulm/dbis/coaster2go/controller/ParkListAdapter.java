package de.uulm.dbis.coaster2go.controller;

import android.content.Context;
import android.location.Location;
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
import de.uulm.dbis.coaster2go.data.Park;

/**
 * Created by Luis on 06.05.2017.
 */
public class ParkListAdapter extends RecyclerView.Adapter<ParkListAdapter.ViewHolder> {

    public enum SortMode {
        RATING, NAME, DISTANCE
    }

    // TODO implement SortedList?
    // private SortedList<Park> parkList;

    private List<Park> parkList;
    private Context context;
    private final OnParkItemClickListener clickListener;
    private Location lastLocation;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView parkImage;
        public TextView parkName;
        public RatingBar parkRating;
        public TextView parkLocation;
        public TextView parkDistance;

        public ViewHolder(View itemView) {
            super(itemView);

            parkImage = (ImageView) itemView.findViewById(R.id.parkList_image);
            parkName = (TextView) itemView.findViewById(R.id.parkList_name);
            parkRating = (RatingBar) itemView.findViewById(R.id.parkList_rating);
            parkLocation = (TextView) itemView.findViewById(R.id.parkList_location);
            parkDistance = (TextView) itemView.findViewById(R.id.parkList_distance);
        }

    }

    public ParkListAdapter(Context context, List<Park> parkList,
                           OnParkItemClickListener clickListener) {
        this.context = context;
        this.parkList = parkList;
        this.clickListener = clickListener;
    }

    @Override
    public ParkListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.park_list_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ParkListAdapter.ViewHolder viewHolder, int position) {
        // get the park item based on position
        final Park park = parkList.get(position);

        // fill the view based on the data
        if (park.getImage() != null) {
            Picasso.with(context).load(park.getImage()).into(viewHolder.parkImage);
        }
        viewHolder.parkName.setText(park.getName());
        viewHolder.parkRating.setRating((float) park.getAverageReview());
        viewHolder.parkLocation.setText(park.getLocation());
        Location parkLocationLatLng = new Location("");
        parkLocationLatLng.setLatitude(park.getLat());
        parkLocationLatLng.setLongitude(park.getLon());
        if (lastLocation == null) {
            viewHolder.parkDistance.setVisibility(View.INVISIBLE);
        } else {
            Location parkLoc = new Location("");
            parkLoc.setLongitude(park.getLon());
            parkLoc.setLatitude(park.getLat());

            float distance = lastLocation.distanceTo(parkLoc);

            viewHolder.parkDistance.setText(buildDistanceString(distance));

        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickListener.onParkItemClick(park);
            }
        });
    }

    /**
     * builds an appropriate string based on the distance
     * @param distanceMeters the distance in meters
     * @return a string showing the distance in m or km
     */
    private String buildDistanceString(float distanceMeters) {
        if (distanceMeters > 1000) {
            int distanceKm = Math.round(distanceMeters/1000);
            return distanceKm + " km";
        } else {
            return Math.round(distanceMeters) + " m";
        }
    }

    @Override
    public int getItemCount() {
        return parkList == null ? 0 : parkList.size();
    }

    public void changeSort(SortMode mode) {
        switch (mode) {
            case NAME:
                Collections.sort(parkList, new AbcComparator());
                notifyDataSetChanged();
                break;
            case RATING:
                Collections.sort(parkList, new RatingComparator());
                notifyDataSetChanged();
                break;
            case DISTANCE:
                Collections.sort(parkList, new DistanceComparator());
                notifyDataSetChanged();
            default:
                break;
        }
    }

    private class RatingComparator implements Comparator<Park> {
        @Override
        public int compare(Park park1, Park park2) {
            // park1 and park2 are reversed so the highest rating is on top
            return Double.compare(park2.getAverageReview(), park1.getAverageReview());
        }
    }

    private class AbcComparator implements Comparator<Park> {
        @Override
        public int compare(Park park1, Park park2) {
            return park1.getName().compareTo(park2.getName());
        }
    }

    private class DistanceComparator implements Comparator<Park> {
        @Override
        public int compare(Park park1, Park park2) {
            if (lastLocation == null) return 0;
            Location locPark1 = new Location("");
            locPark1.setLatitude(park1.getLat());
            locPark1.setLongitude(park1.getLon());
            Location locPark2 = new Location("");
            locPark2.setLatitude(park2.getLat());
            locPark2.setLongitude(park2.getLon());

            return Float.compare(lastLocation.distanceTo(locPark1),
                    lastLocation.distanceTo(locPark2));
        }
    }

    // GETTERS & SETTERS

    public Context getContext() {
        return context;
    }

    public void setParkList(List<Park> parkList) {
        this.parkList = parkList;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

}
