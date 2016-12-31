package com.myadridev.rememberall.models;

import android.content.Context;

import com.myadridev.rememberall.R;

public class GroupsItemAddReminder extends GroupsItem {

    public GroupsItemAddReminder(Context _context, int groupId) {
        super(_context);
        Name = context.getString(R.string.groups_add_reminder);
        GroupId = groupId;
    }
}
