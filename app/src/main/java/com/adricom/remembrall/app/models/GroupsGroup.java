package com.adricom.remembrall.app.models;

import android.content.Context;

import com.adricom.remembrall.app.helpers.NavigationHelper;

/**
 * Created by adrien on 29/08/15.
 */
public class GroupsGroup extends GroupModel implements Comparable<GroupsGroup> {
    private Context context;

    public GroupsGroup(GroupModel group, Context _context) {
        super(group);
        context = _context;
    }

    public void navigateToGroup() {
        NavigationHelper.navigateToGroup(context, Id, false);
    }

    public Context getContext() {
        return context;
    }

    @Override
    public int compareTo(GroupsGroup otherGroup) {
        int compare = Name.compareToIgnoreCase(otherGroup.Name);
        if (compare < 0) {
            return -1;
        } else if (compare > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
