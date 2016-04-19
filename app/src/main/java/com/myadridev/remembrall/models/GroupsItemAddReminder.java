package com.myadridev.remembrall.models;

import android.content.Context;

import com.adricom.remembrall.app.R;

/**
 * Created by adrien on 29/08/15.
 */
public class GroupsItemAddReminder extends GroupsItem {

    public GroupsItemAddReminder(Context _context, int groupId) {
        super(_context);
        Name = context.getString(R.string.groups_add_reminder);
        GroupId = groupId;
    }
}
