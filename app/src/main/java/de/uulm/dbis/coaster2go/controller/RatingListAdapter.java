package de.uulm.dbis.coaster2go.controller;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RatingBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.uulm.dbis.coaster2go.R;
import de.uulm.dbis.coaster2go.data.Park;
import de.uulm.dbis.coaster2go.data.Review;

/**
 * Created by Luis on 19.05.2017.
 */
public class RatingListAdapter extends RecyclerView.Adapter<RatingListAdapter.ViewHolder> implements Filterable {

    public enum SortMode {
        NAME, DATE, RATING
    }
    private List<Review> ratingsList;

    @Override
    public Filter getFilter() {
        return null;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView ratingName;
        TextView ratingDate;
        RatingBar ratingRating;
        TextView ratingDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            ratingName = (TextView) itemView.findViewById(R.id.ratingList_username);
            ratingDate = (TextView) itemView.findViewById(R.id.ratingList_date);
            ratingRating = (RatingBar) itemView.findViewById(R.id.ratingList_rating);
            ratingDescription = (TextView) itemView.findViewById(R.id.ratingList_descrText);
        }

    }

    public RatingListAdapter(List<Review> ratingsList) {
        this.ratingsList = ratingsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = inflater.inflate(R.layout.rating_list_item, parent, false);

        // Return a new holder instance
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RatingListAdapter.ViewHolder viewHolder, int position) {
        // get the review item based on position
        final Review review = ratingsList.get(position);

        // fill the view based on the data
        viewHolder.ratingName.setText(review.getDisplayName());
        viewHolder.ratingRating.setRating(review.getNumberOfStars());
        viewHolder.ratingDescription.setText(review.getComment());
        Date ratingDate = review.getCreatedAt();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.GERMANY);
        String dateString = dateFormat.format(ratingDate);
        viewHolder.ratingDate.setText(dateString);
    }

    @Override
    public int getItemCount() {
        return ratingsList == null ? 0 : ratingsList.size();
    }

    public void changeSort(SortMode mode) {
        switch (mode) {
            case NAME:
                Collections.sort(ratingsList, new NameComparator());
                notifyDataSetChanged();
                break;
            case RATING:
                Collections.sort(ratingsList, new RatingComparator());
                notifyDataSetChanged();
                break;
            case DATE:
                Collections.sort(ratingsList, new DateComparator());
                notifyDataSetChanged();
            default:
                break;
        }
    }

    private class RatingComparator implements Comparator<Review> {
        @Override
        public int compare(Review review1, Review review2) {
            // review1 and review2 are reversed so the highest rating is on top
            return Double.compare(review2.getNumberOfStars(), review1.getNumberOfStars());
        }
    }

    private class NameComparator implements Comparator<Review> {
        @Override
        public int compare(Review review1, Review review2) {
            return review1.getDisplayName().compareTo(review2.getDisplayName());
        }
    }

    private class DateComparator implements Comparator<Review> {
        @Override
        public int compare(Review review1, Review review2) {
            return review1.getCreatedAt().compareTo(review2.getCreatedAt());
        }
    }

    // GETTERS & SETTERS
    public void setRatingsList(List<Review> ratingsList) {
        this.ratingsList = ratingsList;
    }


    // TODO add a search bar for filtering?
    private class RatingFilter extends Filter {
        private final List<Review> originalList;
        private final List<Review> filteredList;

        private RatingFilter(List<Review> originalList) {
            super();
            this.originalList = originalList;
            filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                // here the filtering happens
                final String filterString = constraint.toString().toLowerCase();

                for (Review review : originalList) {
                    if (review.getDisplayName().toLowerCase().contains(filterString)
                            || review.getComment().toLowerCase().contains(filterString)) {
                        filteredList.add(review);
                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                notifyItemRangeRemoved(0, ratingsList.size());
            } else {
                ratingsList = (ArrayList<Review>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
