package com.drillin.oindrildutta.treeblog;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

@SuppressLint("ValidFragment")
public class AlertDialogFragment extends DialogFragment {
    String title = "Booo!";
    String message = "Wow your internet sucks!";
    String button = "You suck you jerk.";

    @SuppressLint("ValidFragment")
    public AlertDialogFragment(String title, String message, String button) {
        this.title = title;
        this.message = message;
        this.button = button;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title).setMessage(message).setPositiveButton(button, null);
        return builder.create();
    }
}
