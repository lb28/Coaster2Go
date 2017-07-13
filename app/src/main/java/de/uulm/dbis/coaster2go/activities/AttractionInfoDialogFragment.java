package de.uulm.dbis.coaster2go.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.pchmn.materialchips.ChipView;

import de.uulm.dbis.coaster2go.R;

/**
 * the attraction info dialog
 */
public class AttractionInfoDialogFragment extends DialogFragment {

    public static final String TAG = "AttrInfoDialog";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        String attrName = getArguments().getString("attrName");
        String attrDesc = getArguments().getString("attrDesc");
        String[] types = getArguments().getStringArray("attrTypes");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.dialog_attraction_info, null);

        if (types != null && types.length > 0) {
            LinearLayout chipLayout = (LinearLayout) dialogView.findViewById(R.id.chipsLayoutAttrTypes);

            for (String type : types) {
                try {
                    ChipView chip = new ChipView(getContext());
                    chip.setLabel(type);
                    chip.setChipBackgroundColor(
                            ContextCompat.getColor(getContext(), R.color.colorPrimary));

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(0, 5, 5, 10);

                    chipLayout.addView(chip, layoutParams);
                } catch (Exception e) {
                    Log.e(TAG, "onCreateDialog: failed to load chip", e);
                }
            }

            // only add the view if there is something to show
            builder.setView(dialogView);
        }


        builder.setTitle(attrName)
                .setMessage(attrDesc)
                .setNegativeButton("Schließen", null);


        return builder.create();
    }
}
