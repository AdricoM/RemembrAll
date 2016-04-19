package com.myadridev.remembrall.models;

/**
 * Created by adrien on 29/08/15.
 */
public class GroupDetailGroup implements Comparable<GroupDetailGroup> {
    public int Index;
    public String Title;

    public GroupDetailGroup(int index, String title) {
        Index = index;
        Title = title;
    }

    @Override
    public int compareTo(GroupDetailGroup otherGroup) {
        if (Index < otherGroup.Index) {
            return -1;
        } else if (Index > otherGroup.Index) {
            return 1;
        } else {
            return 0;
        }
    }
}
