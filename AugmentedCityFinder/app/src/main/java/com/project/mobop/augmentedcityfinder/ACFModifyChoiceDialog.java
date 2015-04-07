package com.project.mobop.augmentedcityfinder;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by tom on 07.04.2015.
 */
public class ACFModifyChoiceDialog extends DialogFragment {

    private ACFCity city;
    private ACFCityGroup group;

    public ACFCity getCity() {
        return city;
    }

    public void setCity(ACFCity city) {
        this.city = city;
    }

    public ACFCityGroup getGroup() {
        return group;
    }

    public void setGroup(ACFCityGroup group) {
        this.group = group;
    }

    public interface ChoiceDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    ChoiceDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (ChoiceDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setMessage("Was wollen Sie tun?")
                .setPositiveButton("Bearbeiten", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(ACFModifyChoiceDialog.this);
                    }
                })
                .setNegativeButton("Löschen", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(ACFModifyChoiceDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
