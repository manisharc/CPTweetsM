package com.codepath.apps.CPTweetsM.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;

/**
 * Created by chmanish on 10/31/16.
 */
public class SaveDraftFragment extends DialogFragment {

    public SaveDraftFragment() {
        // Empty constructor required for DialogFragment
    }

    public static SaveDraftFragment newInstance(String message) {
        SaveDraftFragment frag = new SaveDraftFragment();
        Bundle args = new Bundle();
        args.putString("draft", message);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String draft = getArguments().getString("draft");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        //alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage("Save Draft?");
        alertDialogBuilder.setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                SharedPreferences pref = PreferenceManager.
                        getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit = pref.edit();
                edit.putString("draft", draft);
                edit.commit();
            }
        });
        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences pref = PreferenceManager.
                        getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor edit = pref.edit();
                edit.remove("draft");//clean this key.
                edit.commit();
                dialog.dismiss();
            }
        });

        return alertDialogBuilder.create();
    }

    @Override
    public void onResume() {
        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();
        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);
        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.6), (int) (size.y * 0.25));
        window.setGravity(Gravity.CENTER);
        // Call super onResume after sizing
        super.onResume();
    }
}
