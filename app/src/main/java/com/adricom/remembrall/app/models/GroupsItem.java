package com.adricom.remembrall.app.models;

import android.content.Context;

import com.adricom.remembrall.app.helpers.NavigationHelper;

/**
 * Created by adrien on 29/08/15.
 */
public class GroupsItem extends ReminderModel implements Comparable<GroupsItem> {
    protected Context context;

    public GroupsItem(Context _context){
        context = _context;
    }

    public GroupsItem(ReminderModel reminder, Context _context) {
        super(reminder);
        context = _context;
    }

    public void navigateToReminder() {
        NavigationHelper.navigateToReminder(context, Id, GroupId, Id == 0);
    }

    @Override
    public int compareTo(GroupsItem otherReminder) {
        int compare = Name.compareToIgnoreCase(otherReminder.Name);
        if (compare < 0) {
            return -1;
        } else if (compare > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
