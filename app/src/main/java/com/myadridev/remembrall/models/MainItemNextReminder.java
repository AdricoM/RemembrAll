package com.myadridev.remembrall.models;

import android.content.Context;

import com.myadridev.remembrall.R;
import com.myadridev.remembrall.enums.MainItemEnum;
import com.myadridev.remembrall.helpers.NavigationHelper;

import java.text.SimpleDateFormat;

/**
 * Created by adrien on 29/08/15.
 */
public class MainItemNextReminder extends MainItem {
    private int id;
    private String name;

    private String formatedTime;
    private String formatedDate;
    private ReminderModel reminder;

    public MainItemNextReminder(ReminderModel _reminder, Context _context) {
        super(_context, MainItemEnum.NEXT_REMINDER);
        reminder = _reminder;
        id = reminder.Id;
        name = reminder.Name;
        SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.utils_date_format));
        formatedDate = dateFormat.format(reminder.NextReminderDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat(context.getString(R.string.utils_time_format));
        formatedTime = timeFormat.format(reminder.NextReminderDate);
    }

    @Override
    public void onClick() {
        // See the detail of the reminder
        NavigationHelper.navigateToReminder(context, id, false);
    }

    public String getFormatedDate() {
        return formatedDate;
    }

    public String getFormatedTime() {
        return formatedTime;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(MainItem otherItem) {
        if (otherItem instanceof MainItemNextReminder) {
            MainItemNextReminder castedOtherNextReminder = (MainItemNextReminder) otherItem;
            int compare = reminder.NextReminderDate.compareTo(castedOtherNextReminder.reminder.NextReminderDate);
            if (compare < 0) {
                return -1;
            } else if (compare > 0) {
                return 1;
            } else {
                return name.compareTo(castedOtherNextReminder.name);
            }
        } else {
            return -1;
        }
    }
}
