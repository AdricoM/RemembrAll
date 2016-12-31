package com.myadridev.rememberall.models;

import android.content.Context;

import com.myadridev.rememberall.helpers.NavigationHelper;

public class GroupsItem extends ReminderModel {
    protected Context context;

    public GroupsItem(Context _context) {
        context = _context;
    }

    public GroupsItem(ReminderModel reminder, Context _context) {
        super(reminder);
        context = _context;
    }

    public void navigateToReminder() {
        NavigationHelper.navigateToReminder(context, Id, GroupId, Id == 0);
    }
}
