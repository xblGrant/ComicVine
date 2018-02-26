package edu.psu.gsa5054.comicvine;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;

/**
 * Created by Grant Abbondanza on 2/26/2018.
 */

public class clearFavoritesDialog extends DialogFragment {

    //interface of clearFavoritesDialog
    public interface clearFavoritesDialogListener {
        public void onPositiveClick();
    }

    private clearFavoritesDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstaceState) {

        //this subclasses the AlertDialog Class by using a static Builder method in AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Clear All Favorites?")
                .setMessage("Favorites will not be recoverable!")
                .setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onPositiveClick();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //should do nothing. cancels out alert dialog
                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        Activity activity = null;

        if (context instanceof Activity){
            activity=(Activity) context;
        }

        try{
            listener=(clearFavoritesDialogListener) activity;
        }
        catch(Exception e)
        {

        }
    }
}
