package com.adricom.remembrall.app.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.adricom.remembrall.app.R;
import com.adricom.remembrall.app.enums.SettingsFieldEnum;
import com.adricom.remembrall.app.helpers.ErrorHelper;
import com.adricom.remembrall.app.helpers.GroupsHelper;
import com.adricom.remembrall.app.helpers.RemindersHelper;
import com.adricom.remembrall.app.helpers.SettingsHelper;
import com.adricom.remembrall.app.models.SettingsItem;
import com.adricom.remembrall.app.services.AutoStartService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by adrien on 24/08/15.
 */
public class SettingsAdapter extends ArrayAdapter<SettingsItem> {

    CoordinatorLayout coordinatorLayout;
    private HashMap<Integer, Boolean> isItemEnabled;
    private LayoutInflater layoutInflater;
    private Calendar cal = Calendar.getInstance();
    private Activity activity;
    private SimpleDateFormat timeFormat = new SimpleDateFormat(getContext().getString(R.string.utils_time_format));

    public SettingsAdapter(Activity _activity, CoordinatorLayout _coordinatorLayout, int resource, List<SettingsItem> items) {
        super(_activity, resource, items);
        activity = _activity;
        coordinatorLayout = _coordinatorLayout;
        isItemEnabled = new HashMap<>();
        layoutInflater = LayoutInflater.from(activity);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.settings_item, null);

        final SettingsItem item = getItem(position);

        if (item != null) {
            TextView labelView = (TextView) view.findViewById(R.id.settings_label);
            TextView valueView = (TextView) view.findViewById(R.id.settings_value);

            labelView.setText(item.Label);
            if (item.Value instanceof Boolean) {
                valueView.setText((boolean) item.Value ? activity.getString(R.string.global_yes) : activity.getString(R.string.global_no));
            } else if (item.Value instanceof Date) {
                valueView.setText(timeFormat.format(item.Value));
            } else {
                valueView.setText(item.StringValue);
            }
            if (isEnabled(position)) {
                view.setBackgroundColor(Color.WHITE);
                labelView.setTextColor(Color.BLACK);
                valueView.setTextColor(Color.BLACK);
            } else {
                view.setBackgroundColor(Color.LTGRAY);
                labelView.setTextColor(Color.GRAY);
                valueView.setTextColor(Color.GRAY);
            }

            switch (item.SettingsFieldEnum) {
                case USE_DEFAULT_REMINDER_TIME:
                    view.setOnClickListener(useDefaultReminderTimeOnClickListener(item));
                    break;
                case DEFAULT_REMINDER_TIME:
                    view.setOnClickListener(defaultReminderTimeOnClickListener(item));
                    break;
                case NUMBER_REMINDERS_HOME:
                    view.setOnClickListener(numberRemindersHomeOnClickListener(item));
                    break;
                default:
                    break;
            }
        }

        return view;
    }

    public View.OnClickListener numberRemindersHomeOnClickListener(final SettingsItem item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int oldNumber = (int) item.Value;
                final int[] newNumber = {oldNumber};

                AlertDialog.Builder numberRemindersDialogBuilder = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);

                LayoutInflater layoutInflater = activity.getLayoutInflater();
                View numberPickerView = layoutInflater.inflate(R.layout.number_picker_dialog, null);
                final NumberPicker numberPicker = (NumberPicker) numberPickerView.findViewById(R.id.number_picker);
                numberPicker.setMinValue(1);
                numberPicker.setMaxValue(10);
                numberPicker.setValue(oldNumber);
                numberPicker.setWrapSelectorWheel(false);

                numberRemindersDialogBuilder.setTitle(activity.getString(R.string.settings_number_reminders_home_title));
                numberRemindersDialogBuilder.setView(numberPickerView);
                numberRemindersDialogBuilder.setPositiveButton(activity.getString(R.string.global_ok),
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int index) {
                                newNumber[0] = numberPicker.getValue();
                                item.setValue(newNumber[0]);
                                SettingsHelper.Instance.editSetting(item);

                                notifyDataSetChanged();
                                dialog.dismiss();
                            }
                        });
                numberRemindersDialogBuilder.setNegativeButton(activity.getString(R.string.global_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int index) {
                        dialog.dismiss();
                    }
                });

                final AlertDialog numberRemindersDialog = numberRemindersDialogBuilder.create();
                numberRemindersDialog.show();
            }
        };
    }

    public View.OnClickListener defaultReminderTimeOnClickListener(final SettingsItem item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date oldDate = (Date) item.Value;
                boolean is12HoursFormat = activity.getString(R.string.utils_time_format).endsWith("a");

                cal.setTime(oldDate);

                int hour = cal.get(Calendar.HOUR_OF_DAY);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(activity, R.style.AlertDialogStyle, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        cal.set(Calendar.HOUR_OF_DAY, selectedHour);
                        cal.set(Calendar.MINUTE, selectedMinute);

                        item.setValue(cal.getTime());
                        SettingsHelper.Instance.editSetting(item);

                        // just for display
                        item.StringValue = timeFormat.format(item.Value);
                        for (int groupId : GroupsHelper.Instance.getAllGroupIds()) {
                            RemindersHelper.Instance.refreshNextReminderDateByGroupIs(groupId);
                        }
                        activity.startService(new Intent(activity, AutoStartService.class));
                        notifyDataSetChanged();
                    }
                }, hour, minute, !is12HoursFormat); // true = 24 hour time

                timePickerDialog.setTitle(activity.getString(R.string.settings_timepicker_title));
                timePickerDialog.show();
            }
        };
    }

    public View.OnClickListener useDefaultReminderTimeOnClickListener(final SettingsItem item) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean oldValue = (boolean) item.Value;
                final CharSequence[] choices = {activity.getString(R.string.global_yes), activity.getString(R.string.global_no)};

                AlertDialog.Builder defaultTimeDialogBuilder = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
                defaultTimeDialogBuilder.setTitle(activity.getString(R.string.settings_use_default_reminder_time_title));
                defaultTimeDialogBuilder.setSingleChoiceItems(choices, oldValue ? 0 : 1,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int index) {
                                boolean choice = false;
                                boolean isChoiceOk = false;
                                switch (index) {
                                    case 0:
                                        isChoiceOk = true;
                                        choice = true;
                                        break;
                                    case 1:
                                        choice = false;
                                        isChoiceOk = true;
                                        break;
                                    default:
                                        isChoiceOk = false;
                                        break;
                                }
                                if (isChoiceOk) {
                                    item.setValue(choice);
                                    if (choice || RemindersHelper.Instance.IsReminderTimeForAllReminders(item)) {
                                        SettingsHelper.Instance.editSetting(item);
                                        for (int groupId : GroupsHelper.Instance.getAllGroupIds()) {
                                            RemindersHelper.Instance.refreshNextReminderDateByGroupIs(groupId);
                                        }
                                        setIsItemEnabled(SettingsFieldEnum.DEFAULT_REMINDER_TIME.getIndex(), choice);
                                        notifyDataSetChanged();
                                    } else {
                                        item.setValue(oldValue);
                                        ErrorHelper.getSnackbar(activity, coordinatorLayout, activity.getString(R.string.settings_error_no_reminder_time)).show();
                                    }
                                    dialog.dismiss();
                                }
                            }
                        });

                final AlertDialog useDefaultDialog = defaultTimeDialogBuilder.create();
                useDefaultDialog.show();
            }
        };
    }

    public void setIsItemEnabled(int position, boolean isEnabled) {
        isItemEnabled.put(position, isEnabled);
    }

    @Override
    public boolean isEnabled(int position) {
        return isItemEnabled.containsKey(position) ? isItemEnabled.get(position) : true;
    }
}
