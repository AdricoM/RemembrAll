package com.myadridev.remembrall.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myadridev.remembrall.R;
import com.myadridev.remembrall.models.GroupDetailGroup;
import com.myadridev.remembrall.models.GroupsItem;
import com.myadridev.remembrall.models.GroupsItemAddReminder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by adrien on 24/08/15.
 */
public class RemindersAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater layoutInflater;
    private SortedMap<GroupDetailGroup, List<GroupsItem>> items;
    private List<GroupDetailGroup> groups;
    private boolean isHidden;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public RemindersAdapter(Context context, SortedMap<GroupDetailGroup, List<GroupsItem>> _items) {
        layoutInflater = LayoutInflater.from(context);
        items = new TreeMap<>();
        groups = new ArrayList<>(_items.size());
        dateFormat = new SimpleDateFormat(context.getString(R.string.utils_date_format));
        timeFormat = new SimpleDateFormat(context.getString(R.string.utils_time_format));
        for (Map.Entry<GroupDetailGroup, List<GroupsItem>> groupWithChildren : _items.entrySet()) {
            List<GroupsItem> children = new ArrayList<>(groupWithChildren.getValue());
            GroupDetailGroup group = groupWithChildren.getKey();
            groups.add(group);
            items.put(group, children);
        }
    }

    public void setHidden(boolean _isHidden) {
        if (!(isHidden && _isHidden)) {
            isHidden = _isHidden;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getGroupCount() {
        return isHidden ? 0 : items.keySet().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.get(groups.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        GroupDetailGroup key = groups.get(groupPosition);
        List<GroupsItem> groupsItems = items.get(key);
        GroupsItem groupsItem = groupsItems.get(childPosition);
        return groupsItem;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.group_detail_reminders_group, parent, false);
        }

        final GroupDetailGroup group = groups.get(groupPosition);
        if (group != null) {
            TextView label = (TextView) view.findViewById(R.id.group_reminders_group_label);
            label.setText(group.Title);
            ImageView deleteAll = (ImageView) view.findViewById(R.id.groups_reminders_delete);
            deleteAll.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.group_detail_reminders_item, parent, false);

        GroupDetailGroup group = groups.get(groupPosition);
        if (group != null) {
            final GroupsItem item = items.get(group).get(childPosition);
            if (item != null) {
                TextView value = (TextView) view.findViewById(R.id.group_reminders_item_label);
                value.setText(item.Name);

                TextView date = (TextView) view.findViewById(R.id.group_reminders_item_date);
                TextView time = (TextView) view.findViewById(R.id.group_reminders_item_time);
                if (item instanceof GroupsItemAddReminder) {
                    date.setVisibility(View.GONE);
                    time.setVisibility(View.GONE);
                } else {
                    date.setText(dateFormat.format(item.NextReminderDate));
                    date.setVisibility(View.VISIBLE);
                    time.setText(timeFormat.format(item.NextReminderDate));
                    time.setVisibility(View.VISIBLE);
                }

                if (childPosition % 2 == 0) {
                    view.setBackgroundColor(Color.WHITE);
                    value.setTextColor(Color.BLACK);
                    date.setTextColor(Color.BLACK);
                    time.setTextColor(Color.BLACK);
                } else {
                    view.setBackgroundColor(Color.LTGRAY);
                    value.setTextColor(Color.BLACK);
                    date.setTextColor(Color.BLACK);
                    time.setTextColor(Color.BLACK);
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.navigateToReminder();
                    }
                });
            }
        }

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
