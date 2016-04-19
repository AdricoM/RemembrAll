package com.adricom.remembrall.app.models;

import android.content.Context;

import com.adricom.remembrall.app.R;
import com.adricom.remembrall.app.enums.MainItemEnum;
import com.adricom.remembrall.app.helpers.NavigationHelper;

import java.text.SimpleDateFormat;

/**
 * Created by adrien on 29/08/15.
 */
public class MainItemNextReminder extends MainItem {
    private int id;
    private String name;

    private String formatedTime;
    private String formatedDate;

    public MainItemNextReminder(ReminderModel reminder, Context _context) {
        super(_context, MainItemEnum.NEXT_REMINDER);
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
}
