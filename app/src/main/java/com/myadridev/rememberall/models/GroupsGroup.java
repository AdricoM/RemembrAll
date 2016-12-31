package com.myadridev.rememberall.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.myadridev.rememberall.helpers.NavigationHelper;

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
    public int compareTo(@NonNull GroupsGroup otherGroup) {
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
