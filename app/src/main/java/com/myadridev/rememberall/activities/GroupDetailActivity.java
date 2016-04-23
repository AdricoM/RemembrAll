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
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.myadridev.rememberall.R;
import com.myadridev.rememberall.adapters.RemindersAdapter;
import com.myadridev.rememberall.helpers.ErrorHelper;
import com.myadridev.rememberall.helpers.GroupsHelper;
import com.myadridev.rememberall.helpers.RemindersHelper;
import com.myadridev.rememberall.helpers.SdkHelper;
import com.myadridev.rememberall.models.GroupDetailGroup;
import com.myadridev.rememberall.models.GroupModel;
import com.myadridev.rememberall.models.GroupsItem;
import com.myadridev.rememberall.models.GroupsItemAddReminder;
import com.myadridev.rememberall.models.ReminderModel;
import com.myadridev.rememberall.services.AutoStartService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

public class GroupDetailActivity extends AppCompatActivity {

    private static final int DEFAULT_HOUR = 10;
    private static final int DEFAULT_MINUTE = 0;
    private GroupModel group;
    private boolean isEditable;
    private Menu menu;
    private SimpleDateFormat timeFormat;
    private Calendar cal = Calendar.getInstance();
    private RelativeLayout layout;
    private LinearLayout defaultReminderTimeLayout;

    private TextView nameTextView;
    private EditText nameEditText;

    private TextView descriptionTextView;
    private EditText descriptionEditText;

    private CheckBox useDefaultReminderTimeBox;

    private TextView defaultReminderTimeView;
    private TimePicker defaultReminderTimeEdit;

    private ExpandableListView remindersListView;

    private SortedMap<GroupDetailGroup, List<GroupsItem>> items;
    private boolean isKeyboardShown;
    private View groupDetailHeaderView;
    private RemindersAdapter adapter;

    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        timeFormat = new SimpleDateFormat(getString(R.string.utils_time_format));

        setLayout();

        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(android.R.drawable.ic_menu_view);
        }

        if (!GroupsHelper.IsInitialized()) {
            GroupsHelper.Init(this);
        }

        if (!RemindersHelper.IsInitialized()) {
            RemindersHelper.Init(this);
        }

        Bundle bundle = getIntent().getExtras();
        setEditable(bundle.getBoolean("isEditable"));
        refreshGroup(bundle.getInt("groupId"));
        isKeyboardShown = false;

        items = new TreeMap<>();
    }

    private void setLayout() {
        setContentView(R.layout.group_detail);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.group_coordinator_layout);

        remindersListView = (ExpandableListView) findViewById(R.id.group_reminders_list_view);

        groupDetailHeaderView = getLayoutInflater().inflate(R.layout.group_detail_header, null);

        layout = (RelativeLayout) groupDetailHeaderView.findViewById(R.id.group_details);
        layout.setOnTouchListener(hideKeyboardOnTouch());

        defaultReminderTimeLayout = (LinearLayout) groupDetailHeaderView.findViewById(R.id.group_default_reminder_time_linear);

        nameTextView = (TextView) groupDetailHeaderView.findViewById(R.id.group_name_value);
        nameEditText = (EditText) groupDetailHeaderView.findViewById(R.id.group_name_value_edit);

        descriptionTextView = (TextView) groupDetailHeaderView.findViewById(R.id.group_description_value);
        descriptionEditText = (EditText) groupDetailHeaderView.findViewById(R.id.group_description_value_edit);

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

        defaultReminderTimeView = (TextView) groupDetailHeaderView.findViewById(R.id.group_default_reminder_time_value);
        defaultReminderTimeEdit = (TimePicker) groupDetailHeaderView.findViewById(R.id.group_default_reminder_time_value_edit);

        useDefaultReminderTimeBox = (CheckBox) groupDetailHeaderView.findViewById(R.id.group_use_default_reminder_time_value);
        useDefaultReminderTimeBox.setOnTouchListener(hideKeyboardOnTouch());
        useDefaultReminderTimeBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    defaultReminderTimeLayout.setVisibility(View.VISIBLE);
                    defaultReminderTimeEdit.setVisibility(View.VISIBLE);
                } else {
                    defaultReminderTimeLayout.setVisibility(View.GONE);
                    defaultReminderTimeEdit.setVisibility(View.GONE);
                }
            }
        });

        remindersListView.addHeaderView(groupDetailHeaderView);
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

            useDefaultReminderTimeBox.setClickable(true);
        } else {
            nameTextView.setVisibility(View.VISIBLE);
            nameEditText.setVisibility(View.GONE);

            descriptionTextView.setVisibility(View.VISIBLE);
            descriptionEditText.setVisibility(View.GONE);

            useDefaultReminderTimeBox.setClickable(false);

            defaultReminderTimeView.setVisibility(View.VISIBLE);
            defaultReminderTimeEdit.setVisibility(View.GONE);
        }

        if (adapter != null) {
            setRemindersVisibility();
        }

        if (menu != null) {
            setMenuVisibility();
        }
    }

    private void setRemindersVisibility() {
        adapter.setHidden(isEditable);
        if (!isEditable) {
            for (int position = 0; position < items.keySet().size(); position++) {
                remindersListView.expandGroup(position);
            }
        }
    }

    private void setMenuVisibility() {
        menu.findItem(R.id.menu_group_save).setVisible(isEditable);
        menu.findItem(R.id.menu_group_cancel).setVisible(isEditable);
        menu.findItem(R.id.menu_group_edit).setVisible(!isEditable);
        menu.findItem(R.id.menu_group_delete).setVisible(!isEditable);

        if (group != null) {
            menu.findItem(R.id.menu_group_quick_access_on).setVisible(!isEditable && group.IsQuickAccess);
            menu.findItem(R.id.menu_group_quick_access_off).setVisible(!isEditable && !group.IsQuickAccess);
        }
    }

    private void refreshGroup() {
        refreshGroup(group.Id);
    }

    private void refreshGroup(int groupId) {
        if (groupId == 0) {
            group = new GroupModel();
        } else {
            group = new GroupModel(GroupsHelper.Instance.getGroup(groupId));
        }

        useDefaultReminderTimeBox.setChecked(group.UseDefaultReminderTime);

        if (group.UseDefaultReminderTime) {
            defaultReminderTimeLayout.setVisibility(View.VISIBLE);
        } else {
            defaultReminderTimeLayout.setVisibility(View.GONE);
        }

        TimeZone timeZone = cal.getTimeZone();

        if (group.DefaultReminderTime != null) {
            cal.setTime(group.DefaultReminderTime);
        } else {
            cal.set(Calendar.HOUR_OF_DAY, DEFAULT_HOUR);
            cal.set(Calendar.MINUTE, DEFAULT_MINUTE);
        }

        boolean is12HoursFormat = getString(R.string.utils_time_format).endsWith("a");

        if (isEditable) {
            nameEditText.setText(group.Name);

            descriptionEditText.setText(group.Description);

            SdkHelper.setHour(defaultReminderTimeEdit, cal.get(Calendar.HOUR_OF_DAY));
            SdkHelper.setMinute(defaultReminderTimeEdit, cal.get(Calendar.MINUTE));
            defaultReminderTimeEdit.setIs24HourView(!is12HoursFormat);

            defaultReminderTimeView.setVisibility(View.GONE);
            defaultReminderTimeEdit.setVisibility(group.UseDefaultReminderTime ? View.VISIBLE : View.GONE);
        } else {
            nameTextView.setText(group.Name);

            descriptionTextView.setText(group.Description);

            defaultReminderTimeView.setText(timeFormat.format(cal.getTime()));

            defaultReminderTimeEdit.setVisibility(View.GONE);
            defaultReminderTimeView.setVisibility(group.UseDefaultReminderTime ? View.VISIBLE : View.GONE);
        }
    }

    private boolean setValues() {
        group.Name = nameEditText.getText().toString();
        group.Description = descriptionEditText.getText().toString();
        group.UseDefaultReminderTime = useDefaultReminderTimeBox.isChecked();

        cal.set(Calendar.HOUR_OF_DAY, SdkHelper.getHour(defaultReminderTimeEdit));
        cal.set(Calendar.MINUTE, SdkHelper.getMinute(defaultReminderTimeEdit));

        TimeZone timeZone = cal.getTimeZone();
        group.DefaultReminderTime = cal.getTime();

        return !checkValues();
    }

    private boolean checkValues() {
        boolean isErrors = false;

        String errorMessage = "";
        View viewToFocusBackOnDismiss = null;
        if (group.Name.isEmpty()) {
            errorMessage = getString(R.string.group_error_no_name);
            viewToFocusBackOnDismiss = nameEditText;
            isErrors = true;
        } else if (GroupsHelper.Instance.IsExistingName(group)) {
            errorMessage = getString(R.string.group_error_existing_name);
            viewToFocusBackOnDismiss = nameEditText;
            isErrors = true;
        } else if (!GroupsHelper.Instance.IsReminderTimeForAllReminders(group)) {
            errorMessage = getString(R.string.group_error_no_reminder_time);
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
        getMenuInflater().inflate(R.menu.group_detail, menu);
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

        if (isEditable && group.Id > 0) {
            setEditable(false);
            refreshGroup();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_group_edit:
                // "Edit reminder" button
                setEditable(true);
                refreshGroup();
                return true;
            case R.id.menu_group_delete:
                // "Delete reminder" button
                AlertDialog.Builder deleteGroupDialogBuilder = new AlertDialog.Builder(this);
                deleteGroupDialogBuilder.setTitle(getString(R.string.global_confirmation_title));
                deleteGroupDialogBuilder.setCancelable(true);
                deleteGroupDialogBuilder.setIcon(android.R.drawable.ic_menu_delete);
                deleteGroupDialogBuilder.setMessage(R.string.group_delete_confirmation);
                deleteGroupDialogBuilder.setNegativeButton(R.string.global_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                deleteGroupDialogBuilder.setPositiveButton(R.string.global_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        GroupsHelper.Instance.removeGroup(group.Id);
                        GroupsHelper.Instance.saveGroups();
                        dialog.dismiss();
                        startService(new Intent(GroupDetailActivity.this, AutoStartService.class));
                        finish();
                    }
                });

                final AlertDialog deleteConfirmationDialog = deleteGroupDialogBuilder.create();
                deleteConfirmationDialog.show();
                return true;
            case R.id.menu_group_save:
                hideKeyboard();

                // "Save changes" button
                if (setValues()) {
                    if (group.Id == 0)
                        group.Id = GroupsHelper.Instance.addGroup(group);
                    else
                        GroupsHelper.Instance.editGroup(group);

                    GroupsHelper.Instance.saveGroups();
                    startService(new Intent(this, AutoStartService.class));
                    setEditable(false);
                    refreshGroup();
                    refreshReminders();
                    createAdapter();
                }
                return true;
            case R.id.menu_group_cancel:
                hideKeyboard();

                // "Cancel changes" button
                if (group.Id > 0) {
                    setEditable(false);
                    refreshGroup();
                } else {
                    finish();
                }
                return true;
            case R.id.menu_group_quick_access_on:
                group.IsQuickAccess = false;
                GroupsHelper.Instance.editGroup(group);
                GroupsHelper.Instance.saveGroups();
                refreshGroup();
                menu.findItem(R.id.menu_group_quick_access_on).setVisible(false);
                menu.findItem(R.id.menu_group_quick_access_off).setVisible(true);
                return true;
            case R.id.menu_group_quick_access_off:
                group.IsQuickAccess = true;
                GroupsHelper.Instance.editGroup(group);
                GroupsHelper.Instance.saveGroups();
                refreshGroup();
                menu.findItem(R.id.menu_group_quick_access_on).setVisible(true);
                menu.findItem(R.id.menu_group_quick_access_off).setVisible(false);
                return true;
            default:
                onBackPressed();
                return true;
        }
    }

    private void refreshReminders() {
        List<ReminderModel> remindersModel = RemindersHelper.Instance.getRemindersByGroupId(group.Id);
        List<GroupsItem> reminders = new ArrayList<>(remindersModel.size() + 1);
        for (ReminderModel reminderModel : remindersModel) {
            reminders.add(new GroupsItem(reminderModel, this));
        }
        reminders.add(new GroupsItemAddReminder(this, group.Id));

        Collections.sort(reminders);
        GroupDetailGroup remindersGroup = new GroupDetailGroup(0, getString(R.string.group_reminders_list));
        items.put(remindersGroup, reminders);
    }

    private void createAdapter() {
        adapter = new RemindersAdapter(this, items);
        remindersListView.setAdapter(adapter);

        setRemindersVisibility();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshGroup();
        refreshReminders();
        createAdapter();
    }
}
