package com.myadridev.rememberall.models;

import android.support.annotation.NonNull;

public class GroupDetailGroup implements Comparable<GroupDetailGroup> {
    public int Index;
    public String Title;

    public GroupDetailGroup(int index, String title) {
        Index = index;
        Title = title;
    }

    @Override
    public int compareTo(@NonNull GroupDetailGroup otherGroup) {
        if (Index < otherGroup.Index) {
            return -1;
        } else if (Index > otherGroup.Index) {
            return 1;
        } else {
            return 0;
        }
    }
}
