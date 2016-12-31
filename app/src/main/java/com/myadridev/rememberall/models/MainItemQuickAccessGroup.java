package com.myadridev.rememberall.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.myadridev.rememberall.enums.MainItemEnum;
import com.myadridev.rememberall.helpers.NavigationHelper;
import com.myadridev.rememberall.helpers.RemindersHelper;

public class MainItemQuickAccessGroup extends MainItem {
    private int id;
    private String name;
    private int numberItems;

    public MainItemQuickAccessGroup(GroupModel group, Context _context) {
        super(_context, MainItemEnum.QUICK_ACCESS_GROUP);
        id = group.Id;
        name = group.Name;
        numberItems = RemindersHelper.Instance.getRemindersByGroupId(group.Id).size();
    }

    @Override
    public void onClick() {
        // See the detail of the reminder
        NavigationHelper.navigateToGroup(context, id, false);
    }

    public String getName() {
        return name;
    }

    public String getNumberItemsAsString() {
        return String.valueOf(numberItems);
    }

    @Override
    public int compareTo(@NonNull MainItem otherItem) {
        if (otherItem instanceof MainItemQuickAccessGroup) {
            MainItemQuickAccessGroup castedOtherQuickAccessGroup = (MainItemQuickAccessGroup) otherItem;
            return name.compareTo(castedOtherQuickAccessGroup.name);
        } else if (otherItem instanceof MainItemSeeAllGroups) {
            return -1;
        } else {
            return -1;
        }
    }
}
