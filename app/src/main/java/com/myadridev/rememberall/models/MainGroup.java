package com.myadridev.rememberall.models;

import android.support.annotation.NonNull;

public class MainGroup implements Comparable<MainGroup> {
    public int Index;
    public String Title;
    public int IconResId;

    public MainGroup(int index, String title, int iconResId) {
        Index = index;
        Title = title;
        IconResId = iconResId;
    }

    @Override
    public int compareTo(@NonNull MainGroup otherGroup) {
        if (Index < otherGroup.Index) {
            return -1;
        } else if (Index > otherGroup.Index) {
            return 1;
        } else {
            return 0;
        }
    }
}
