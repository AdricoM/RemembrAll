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
import com.myadridev.remembrall.models.MainGroup;
import com.myadridev.remembrall.models.MainItem;
import com.myadridev.remembrall.models.MainItemNextReminder;
import com.myadridev.remembrall.models.MainItemQuickAccessGroup;
import com.myadridev.remembrall.models.MainItemSeeAllGroups;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by adrien on 24/08/15.
 */
public class MainAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater layoutInflater;
    private SortedMap<MainGroup, List<MainItem>> items;
    private List<MainGroup> groups;

    public MainAdapter(Context context, SortedMap<MainGroup, List<MainItem>> _items) {
        layoutInflater = LayoutInflater.from(context);
        items = new TreeMap<>();
        groups = new ArrayList<>();
        for (Map.Entry<MainGroup, List<MainItem>> groupWithChildren : _items.entrySet()) {
            List<MainItem> children = new ArrayList<>(groupWithChildren.getValue());
            MainGroup group = groupWithChildren.getKey();
            groups.add(group);
            items.put(group, children);
        }
    }

    @Override
    public int getGroupCount() {
        return items.keySet().size();
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
        return items.get(groups.get(groupPosition)).get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.main_group, parent, false);
        }

        MainGroup group = groups.get(groupPosition);
        if (group != null) {
            ImageView icon = (ImageView) view.findViewById(R.id.main_group_icon);
            icon.setImageResource(group.IconResId);
            TextView label = (TextView) view.findViewById(R.id.main_group_label);
            label.setText(group.Title);
        }

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        MainGroup group = groups.get(groupPosition);
        if (group != null) {
            final MainItem item = items.get(group).get(childPosition);
            if (item != null) {

                View view;

                switch (item.getItemType()){
                    case NEXT_REMINDER:
                        view = layoutInflater.inflate(R.layout.main_item_next_reminder, parent, false);
                        MainItemNextReminder itemNextReminder = (MainItemNextReminder) item;

                        TextView nextReminderName = (TextView) view.findViewById(R.id.main_item_next_reminder_name);
                        nextReminderName.setText(itemNextReminder.getName());

                        TextView nextReminderDate = (TextView) view.findViewById(R.id.main_item_next_reminder_date);
                        nextReminderDate.setText(itemNextReminder.getFormatedDate());

                        TextView nextReminderTime = (TextView) view.findViewById(R.id.main_item_next_reminder_time);
                        nextReminderTime.setText(itemNextReminder.getFormatedTime());

                        if (childPosition % 2 == 0) {
                            view.setBackgroundColor(Color.WHITE);
                            nextReminderName.setTextColor(Color.BLACK);
                            nextReminderDate.setTextColor(Color.BLACK);
                            nextReminderTime.setTextColor(Color.BLACK);
                        } else {
                            view.setBackgroundColor(Color.LTGRAY);
                            nextReminderName.setTextColor(Color.BLACK);
                            nextReminderDate.setTextColor(Color.BLACK);
                            nextReminderTime.setTextColor(Color.BLACK);
                        }
                        break;
                    case QUICK_ACCESS_GROUP:
                        view = layoutInflater.inflate(R.layout.main_item_quick_access_group, parent, false);
                        MainItemQuickAccessGroup itemQuickAccessGroup = (MainItemQuickAccessGroup) item;

                        TextView groupNameView = (TextView) view.findViewById(R.id.main_item_quick_access_group_label);
                        groupNameView.setText(itemQuickAccessGroup.getName());

                        TextView numberItemsView = (TextView) view.findViewById(R.id.main_item_quick_access_group_number_items);
                        numberItemsView.setText(itemQuickAccessGroup.getNumberItemsAsString());

                        if (childPosition % 2 == 0) {
                            view.setBackgroundColor(Color.WHITE);
                            groupNameView.setTextColor(Color.BLACK);
                            numberItemsView.setTextColor(Color.BLACK);
                        } else {
                            view.setBackgroundColor(Color.LTGRAY);
                            groupNameView.setTextColor(Color.BLACK);
                            numberItemsView.setTextColor(Color.BLACK);
                        }
                        break;
                    case SEE_ALL_GROUPS:
                        view = layoutInflater.inflate(R.layout.main_item_see_all_groups, parent, false);
                        MainItemSeeAllGroups itemSeeAllGroups = (MainItemSeeAllGroups) item;

                        TextView seeAllGroupsLabelView = (TextView) view.findViewById(R.id.main_item_see_all_groups_label);
                        seeAllGroupsLabelView.setText(itemSeeAllGroups.getLabel());

                        if (childPosition % 2 == 0) {
                            view.setBackgroundColor(Color.WHITE);
                            seeAllGroupsLabelView.setTextColor(Color.BLACK);
                        } else {
                            view.setBackgroundColor(Color.LTGRAY);
                            seeAllGroupsLabelView.setTextColor(Color.BLACK);
                        }
                        break;
                    default:
                        return null;
                }

                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        item.onClick();
                    }
                });
                return view;
            }
        }
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
