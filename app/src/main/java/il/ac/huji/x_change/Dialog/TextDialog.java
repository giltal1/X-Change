package il.ac.huji.x_change.Dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;

import il.ac.huji.x_change.Model.Constants;
import il.ac.huji.x_change.R;

/**
 * Created by Gil on 26/08/2015.
 */
public class TextDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Fragment targetFragment = getTargetFragment();
        final int reqCode = getTargetRequestCode();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (reqCode == Constants.REQ_FILTER_DISTANCE_CODE)
            builder.setTitle(R.string.max_distance);
        else
            builder.setTitle(R.string.min_rating);
        final EditText distance = new EditText(getActivity());
        distance.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_CLASS_PHONE);
        builder.setView(distance);
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                int resultCode = Integer.parseInt(distance.getText().toString());
                targetFragment.onActivityResult(reqCode, resultCode, null);
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
