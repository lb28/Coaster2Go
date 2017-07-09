package de.uulm.dbis.coaster2go.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.pchmn.materialchips.ChipView;

import de.uulm.dbis.coaster2go.R;

/**
 * the attraction info dialog
 */
public class AttractionInfoDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        String attrDesc = getArguments().getString("attrDesc");
        String[] types = getArguments().getStringArray("attrTypes");

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View dialogView = inflater.inflate(R.layout.dialog_attraction_info, null);

        if (types != null) {
            LinearLayout chipLayout = (LinearLayout) dialogView.findViewById(R.id.chipsLayoutAttrTypes);

            for (String type : types) {
                ChipView chip = new ChipView(getContext());
                chip.setLabel(type);
                chip.setChipBackgroundColor(
                        ContextCompat.getColor(getContext(), R.color.colorPrimary));

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 5, 5, 10);

                chipLayout.addView(chip, layoutParams);
            }
        }


        builder.setView(dialogView)
                .setMessage(attrDesc)
                .setNegativeButton("Schließen", null);


        return builder.create();
    }
}
