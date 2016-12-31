package com.myadridev.rememberall.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.myadridev.rememberall.R;
import com.myadridev.rememberall.enums.MainItemEnum;
import com.myadridev.rememberall.helpers.NavigationHelper;

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

    @Override
    public int compareTo(@NonNull MainItem otherItem) {
        if (otherItem instanceof MainItemSeeAllGroups) {
            return 0;
        } else {
            return 1;
        }
    }
}
