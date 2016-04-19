package com.adricom.remembrall.app.models;

import android.content.Context;

import com.adricom.remembrall.app.enums.MainItemEnum;
import com.adricom.remembrall.app.helpers.NavigationHelper;
import com.adricom.remembrall.app.helpers.RemindersHelper;

/**
 * Created by adrien on 29/08/15.
 */
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
}
