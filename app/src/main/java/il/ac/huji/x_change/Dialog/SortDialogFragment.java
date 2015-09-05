package il.ac.huji.x_change.Dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import il.ac.huji.x_change.R;

/**
 * Created by Gil on 23/08/2015.
 */
public class SortDialogFragment extends DialogFragment {

    private static final int REQ_SORT_CODE = 1;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Fragment targetFragment = getTargetFragment();
        final int reqCode = getTargetRequestCode();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (reqCode == REQ_SORT_CODE) {
            builder.setTitle(R.string.title_sort_dialog);
            builder.setSingleChoiceItems(R.array.sort_options, 0, null);
            builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    AlertDialog alert = (AlertDialog) dialog;
                    int position = alert.getListView().getCheckedItemPosition();
                    targetFragment.onActivityResult(reqCode, position + 1, null);
                }
            });
            builder.setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        }
        return builder.create();
    }
}
