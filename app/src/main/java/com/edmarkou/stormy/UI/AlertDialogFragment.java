package com.edmarkou.stormy.UI;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import com.edmarkou.stormy.R;

public class AlertDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(R.string.errorTitle)
                .setMessage(R.string.errorMessage)
                .setPositiveButton(R.string.PositiveButtonTitle, null);

        AlertDialog alertDialog = builder.create();
        return alertDialog;
    }
}
