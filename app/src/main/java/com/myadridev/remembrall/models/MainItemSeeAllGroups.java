package com.myadridev.remembrall.models;

import android.content.Context;

import com.adricom.remembrall.app.R;
import com.myadridev.remembrall.enums.MainItemEnum;
import com.myadridev.remembrall.helpers.NavigationHelper;

/**
 * Created by adrien on 29/08/15.
 */
public class MainItemSeeAllGroups extends MainItem {
    private String label;

    public MainItemSeeAllGroups(Context _context) {
        super(_context, MainItemEnum.SEE_ALL_GROUPS);
        label = context.getString(R.string.main_see_all_groups);
    }

    @Override
    public void onClick() {
        // See list of all the groups
        NavigationHelper.navigateToGroups(context);
    }

    public String getLabel() {
        return label;
    }
}
