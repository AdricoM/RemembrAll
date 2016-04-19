package com.myadridev.remembrall.helpers;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.myadridev.remembrall.R;

/**
 * Created by adrien on 02/04/16.
 */
public class ErrorHelper {

    public static Snackbar getSnackbar(final Context context, CoordinatorLayout coordinatorLayout, String errorMessage) {
        return getSnackbar(context, coordinatorLayout, errorMessage, null, R.string.global_ok);
    }

    public static Snackbar getSnackbar(final Context context, CoordinatorLayout coordinatorLayout, String errorMessage, final View viewToFocusBackOnDismiss) {
        return getSnackbar(context, coordinatorLayout, errorMessage, viewToFocusBackOnDismiss, R.string.global_ok);
    }

    public static Snackbar getSnackbar(final Context context, CoordinatorLayout coordinatorLayout, String errorMessage, int actionResId) {
        return getSnackbar(context, coordinatorLayout, errorMessage, null, actionResId);
    }

    public static Snackbar getSnackbar(final Context context, CoordinatorLayout coordinatorLayout, String errorMessage, final View viewToFocusBackOnDismiss, int actionResId) {
        final Snackbar snackbar = Snackbar.make(coordinatorLayout, errorMessage, Snackbar.LENGTH_LONG);

        snackbar.setAction(context.getString(actionResId), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(context, R.color.ErrorBack));
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextSize(18);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(context, R.color.ErrorFront));

        if (viewToFocusBackOnDismiss != null) {
            snackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    viewToFocusBackOnDismiss.requestFocus();
                    InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(viewToFocusBackOnDismiss, InputMethodManager.SHOW_IMPLICIT);
                    super.onDismissed(snackbar, event);
                }
            });
        }
        return snackbar;
    }
}
