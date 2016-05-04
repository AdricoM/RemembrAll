package com.myadridev.rememberall.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.myadridev.rememberall.R;
import com.myadridev.rememberall.helpers.ErrorHelper;
import com.myadridev.rememberall.helpers.RemindersHelper;
import com.myadridev.rememberall.helpers.SdkHelper;
import com.myadridev.rememberall.models.ReminderModel;
import com.myadridev.rememberall.services.AutoStartService;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ReminderDetailActivity extends AppCompatActivity {

    private static final int DEFAULT_HOUR = 10;
    private static final int DEFAULT_MINUTE = 0;
    private ReminderModel reminder;
    private boolean isEditable;
    private Menu menu;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateFormat;
    private Calendar cal = Calendar.getInstance();
    private Calendar cal2 = Calendar.getInstance();
    private RelativeLayout layout;
    private LinearLayout customReminderTimeLayout;

    private TextView nameTextView;
    private EditText nameEditText;

    private TextView descriptionTextView;
    private EditText descriptionEditText;

    private TextView reminderDateView;
    private DatePicker reminderDateEdit;

    private CheckBox useCustomReminderTimeBox;

    private TextView customReminderTimeView;
    private TimePicker customReminderTimeEdit;
    private boolean isKeyboardShown;

    private CoordinatorLayout coordinatorLayout;
    private int groupId;

    private boolean wasEditableBeforePause;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timeFormat = new SimpleDateFormat(getString(R.string.utils_time_format));
        dateFormat = new SimpleDateFormat(getString(R.string.utils_date_format));

        setLayout();

        Toolbar toolbar = (Toolbar) findViewById(R.id.reminder_toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(android.R.drawable.ic_menu_view);
        }

        if (!RemindersHelper.IsInitialized()) {
            RemindersHelper.Init(this);
        }

        Bundle bundle = getIntent().getExtras();
        setEditable(bundle.getBoolean("isEditable"));
        if (bundle.containsKey("groupId")) {
            groupId = bundle.getInt("groupId");
        }

        refreshReminder(bundle.getInt("reminderId"));
        isKeyboardShown = false;
        wasEditableBeforePause = false;
    }

    private void setLayout() {
        setContentView(R.layout.reminder_detail);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.reminder_coordinator_layout);

        layout = (RelativeLayout) findViewById(R.id.reminder_layout);
        layout.setOnTouchListener(hideKeyboardOnTouch());

        customReminderTimeLayout = (LinearLayout) findViewById(R.id.reminder_custom_time_linear);

        nameTextView = (TextView) findViewById(R.id.reminder_name_value);
        nameEditText = (EditText) findViewById(R.id.reminder_name_value_edit);

        descriptionTextView = (TextView) findViewById(R.id.reminder_description_value);
        descriptionEditText = (EditText) findViewById(R.id.reminder_description_value_edit);

        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isKeyboardShown = true;
                }
            }
        });

        nameEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.hasFocus()) {
                    isKeyboardShown = true;
                }
                return false;
            }
        });

        descriptionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    isKeyboardShown = true;
                }
            }
        });

        descriptionEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v.hasFocus()) {
                    isKeyboardShown = true;
                }
                return false;
            }
        });

        reminderDateView = (TextView) findViewById(R.id.reminder_date_value);
        reminderDateEdit = (DatePicker) findViewById(R.id.reminder_date_value_edit);

        useCustomReminderTimeBox = (CheckBox) findViewById(R.id.reminder_use_custom_reminder_time_value);
        useCustomReminderTimeBox.setOnTouchListener(hideKeyboardOnTouch());
        useCustomReminderTimeBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    customReminderTimeLayout.setVisibility(View.VISIBLE);
                    customReminderTimeEdit.setVisibility(View.VISIBLE);
                } else {
                    customReminderTimeLayout.setVisibility(View.GONE);
                    customReminderTimeEdit.setVisibility(View.GONE);
                }
            }
        });

        customReminderTimeView = (TextView) findViewById(R.id.reminder_custom_time_value);
        customReminderTimeEdit = (TimePicker) findViewById(R.id.reminder_custom_time_value_edit);
    }

    private View.OnTouchListener hideKeyboardOnTouch() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboardIfNeeded();
                return false;
            }
        };
    }

    private void hideKeyboardIfNeeded() {
        if (isKeyboardShown) {
            hideKeyboard();
            isKeyboardShown = false;
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isAcceptingText()) {
            imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
        }
    }

    private void setEditable(boolean _isEditable) {
        isEditable = _isEditable;

        if (isEditable) {
            nameTextView.setVisibility(View.GONE);
            nameEditText.setVisibility(View.VISIBLE);

            descriptionTextView.setVisibility(View.GONE);
            descriptionEditText.setVisibility(View.VISIBLE);

            reminderDateView.setVisibility(View.GONE);
            reminderDateEdit.setVisibility(View.VISIBLE);

            useCustomReminderTimeBox.setClickable(true);
        } else {
            nameTextView.setVisibility(View.VISIBLE);
            nameEditText.setVisibility(View.GONE);

            descriptionTextView.setVisibility(View.VISIBLE);
            descriptionEditText.setVisibility(View.GONE);

            reminderDateView.setVisibility(View.VISIBLE);
            reminderDateEdit.setVisibility(View.GONE);

            useCustomReminderTimeBox.setClickable(false);

            customReminderTimeView.setVisibility(View.VISIBLE);
            customReminderTimeEdit.setVisibility(View.GONE);
        }

        if (menu != null) {
            setMenuVisibility();
        }
    }

    private void setMenuVisibility() {
        menu.findItem(R.id.menu_reminder_save).setVisible(isEditable);
        menu.findItem(R.id.menu_reminder_cancel).setVisible(isEditable);
        menu.findItem(R.id.menu_reminder_edit).setVisible(!isEditable);
        menu.findItem(R.id.menu_reminder_delete).setVisible(!isEditable);
    }

    private void refreshReminder() {
        refreshReminder(reminder.Id);
    }

    private void refreshReminder(int reminderId) {
        if (reminderId == 0) {
            reminder = new ReminderModel();
            reminder.GroupId = groupId;
        } else {
            reminder = new ReminderModel(RemindersHelper.Instance.getReminder(reminderId));
        }

        useCustomReminderTimeBox.setChecked(reminder.UseCustomReminderTime);

        if (reminder.UseCustomReminderTime) {
            customReminderTimeLayout.setVisibility(View.VISIBLE);
        } else {
            customReminderTimeLayout.setVisibility(View.GONE);
        }

        if (reminder.CustomReminderTime != null) {
            cal.setTime(reminder.CustomReminderTime);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, DEFAULT_HOUR);
            cal.set(Calendar.MINUTE, DEFAULT_MINUTE);
        }

        if (reminder.ReminderDate != null) {
            cal2.setTime(reminder.ReminderDate);
        }

        boolean is12HoursFormat = getString(R.string.utils_time_format).endsWith("a");

        if (isEditable) {
            nameEditText.setText(reminder.Name);

            descriptionEditText.setText(reminder.Description);

            SdkHelper.setHour(customReminderTimeEdit, cal.get(Calendar.HOUR_OF_DAY));
            SdkHelper.setMinute(customReminderTimeEdit, cal.get(Calendar.MINUTE));
            customReminderTimeEdit.setIs24HourView(!is12HoursFormat);

            reminderDateEdit.updateDate(cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH));

            customReminderTimeView.setVisibility(View.GONE);
            customReminderTimeEdit.setVisibility(reminder.UseCustomReminderTime ? View.VISIBLE : View.GONE);
        } else {
            nameTextView.setText(reminder.Name);

            descriptionTextView.setText(reminder.Description);

            customReminderTimeView.setText(timeFormat.format(cal.getTime()));
            reminderDateView.setText(dateFormat.format(cal2.getTime()));

            customReminderTimeEdit.setVisibility(View.GONE);
            customReminderTimeView.setVisibility(reminder.UseCustomReminderTime ? View.VISIBLE : View.GONE);
        }
    }

    private boolean setValues() {
        reminder.Name = nameEditText.getText().toString();
        reminder.Description = descriptionEditText.getText().toString();
        reminder.UseCustomReminderTime = useCustomReminderTimeBox.isChecked();

        cal.set(Calendar.HOUR_OF_DAY, SdkHelper.getHour(customReminderTimeEdit));
        cal.set(Calendar.MINUTE, SdkHelper.getMinute(customReminderTimeEdit));
        reminder.CustomReminderTime = cal.getTime();

        cal2.set(Calendar.DAY_OF_MONTH, reminderDateEdit.getDayOfMonth());
        cal2.set(Calendar.MONTH, reminderDateEdit.getMonth());
        cal2.set(Calendar.YEAR, reminderDateEdit.getYear());
        reminder.ReminderDate = cal2.getTime();

        return !checkValues();
    }

    private boolean checkValues() {
        boolean isErrors = false;

        String errorMessage = "";
        View viewToFocusBackOnDismiss = null;
        if (reminder.Name.isEmpty()) {
            errorMessage = getString(R.string.reminder_error_no_name);
            viewToFocusBackOnDismiss = nameEditText;
            isErrors = true;
        } else if (RemindersHelper.Instance.IsExistingName(reminder)) {
            errorMessage = getString(R.string.reminder_error_existing_name);
            viewToFocusBackOnDismiss = nameEditText;
            isErrors = true;
        } else if (!RemindersHelper.Instance.IsReminderTime(reminder)) {
            errorMessage = getString(R.string.reminder_error_no_reminder_time);
            isErrors = true;
        }

        if (isErrors) {
            final Snackbar snackbar = ErrorHelper.getSnackbar(this, coordinatorLayout, errorMessage, viewToFocusBackOnDismiss);
            snackbar.show();
        }

        return isErrors;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.reminder_detail, menu);
        this.menu = menu;

        setMenuVisibility();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (!performOnBack()) {
            super.onBackPressed();
        }
    }

    private boolean performOnBack() {
        hideKeyboard();

        if (isEditable && reminder.Id > 0) {
            setEditable(false);
            refreshReminder();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_reminder_edit:
                // "Edit reminder" button
                setEditable(true);
                refreshReminder();
                return true;
            case R.id.menu_reminder_delete:
                // "Delete reminder" button
                AlertDialog.Builder deleteReminderDialogBuilder = new AlertDialog.Builder(this);
                deleteReminderDialogBuilder.setTitle(getString(R.string.global_confirmation_title));
                deleteReminderDialogBuilder.setCancelable(true);
                deleteReminderDialogBuilder.setIcon(android.R.drawable.ic_menu_delete);
                deleteReminderDialogBuilder.setMessage(R.string.reminder_delete_confirmation);
                deleteReminderDialogBuilder.setNegativeButton(R.string.global_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                deleteReminderDialogBuilder.setPositiveButton(R.string.global_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        RemindersHelper.Instance.removeReminder(reminder.Id);
                        RemindersHelper.Instance.saveReminders();
                        dialog.dismiss();
                        startService(new Intent(ReminderDetailActivity.this, AutoStartService.class));
                        finish();
                    }
                });

                final AlertDialog deleteConfirmationDialog = deleteReminderDialogBuilder.create();
                deleteConfirmationDialog.show();
                return true;
            case R.id.menu_reminder_save:
                hideKeyboard();

                // "Save changes" button
                if (setValues()) {
                    reminder.Achieved = false;
                    if (reminder.Id == 0)
                        reminder.Id = RemindersHelper.Instance.addReminder(reminder);
                    else
                        RemindersHelper.Instance.editReminder(reminder);

                    RemindersHelper.Instance.saveReminders();
                    startService(new Intent(this, AutoStartService.class));
                    setEditable(false);
                    refreshReminder();
                }
                return true;
            case R.id.menu_reminder_cancel:
                hideKeyboard();

                // "Cancel changes" button
                if (reminder.Id > 0) {
                    setEditable(false);
                    refreshReminder();
                } else {
                    finish();
                }
                return true;
            default:
                onBackPressed();
                return true;
        }
    }

    @Override
    protected void onPause() {
        wasEditableBeforePause = isEditable;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!wasEditableBeforePause) {
            refreshReminder();
        }
    }
}
