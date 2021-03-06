package com.myadridev.rememberall.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myadridev.rememberall.R;
import com.myadridev.rememberall.helpers.GroupsHelper;
import com.myadridev.rememberall.models.GroupsGroup;
import com.myadridev.rememberall.models.GroupsItem;
import com.myadridev.rememberall.models.GroupsItemAddReminder;
import com.myadridev.rememberall.services.AutoStartService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class GroupsAdapter extends BaseExpandableListAdapter {

    private final LayoutInflater layoutInflater;
    private Context context;
    private SortedMap<GroupsGroup, List<GroupsItem>> items;
    private List<GroupsGroup> groups;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    public GroupsAdapter(Context _context, SortedMap<GroupsGroup, List<GroupsItem>> _items) {
        context = _context;
        layoutInflater = LayoutInflater.from(context);
        items = new TreeMap<>();
        groups = new ArrayList<>(_items.size());
        dateFormat = new SimpleDateFormat(context.getString(R.string.utils_date_format));
        timeFormat = new SimpleDateFormat(context.getString(R.string.utils_time_format));
        for (Map.Entry<GroupsGroup, List<GroupsItem>> groupWithChildren : _items.entrySet()) {
            List<GroupsItem> children = new ArrayList<>(groupWithChildren.getValue());
            GroupsGroup group = groupWithChildren.getKey();
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, final ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = layoutInflater.inflate(R.layout.groups_group, parent, false);
        }

        final GroupsGroup group = groups.get(groupPosition);
        if (group != null) {
            final ImageView star = (ImageView) view.findViewById(R.id.groups_group_star);
            if (group.IsQuickAccess) {
                star.setImageResource(android.R.drawable.btn_star_big_on);
            } else {
                star.setImageResource(android.R.drawable.btn_star_big_off);
            }
            star.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    group.IsQuickAccess = !group.IsQuickAccess;
                    GroupsHelper.Instance.editGroup(group);
                    GroupsHelper.Instance.saveGroups();
                    if (group.IsQuickAccess) {
                        star.setImageResource(android.R.drawable.btn_star_big_on);
                    } else {
                        star.setImageResource(android.R.drawable.btn_star_big_off);
                    }
                }
            });

            ImageView editImage = (ImageView) view.findViewById(R.id.groups_group_detail);
            editImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    group.navigateToGroup();
                }
            });

            ImageView deleteImage = (ImageView) view.findViewById(R.id.groups_group_delete);
            deleteImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    // "Delete reminder" button
                    AlertDialog.Builder deleteGroupDialogBuilder = new AlertDialog.Builder(group.getContext());
                    deleteGroupDialogBuilder.setTitle(group.getContext().getString(R.string.global_confirmation_title));
                    deleteGroupDialogBuilder.setCancelable(true);
                    deleteGroupDialogBuilder.setIcon(android.R.drawable.ic_menu_delete);
                    deleteGroupDialogBuilder.setMessage(R.string.group_delete_confirmation);
                    deleteGroupDialogBuilder.setNegativeButton(R.string.global_no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    deleteGroupDialogBuilder.setPositiveButton(R.string.global_yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            GroupsHelper.Instance.removeGroup(group.Id);
                            GroupsHelper.Instance.saveGroups();
                            groups.remove(group);
                            items.remove(group);
                            dialog.dismiss();
                            notifyDataSetChanged();
                            context.startService(new Intent(context, AutoStartService.class));
                        }
                    });

                    final AlertDialog deleteConfirmationDialog = deleteGroupDialogBuilder.create();
                    deleteConfirmationDialog.show();
                }
            });

            TextView label = (TextView) view.findViewById(R.id.groups_group_label);
            label.setText(group.Name);
        }

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.groups_item, parent, false);

        GroupsGroup group = groups.get(groupPosition);
        if (group != null) {
            final GroupsItem item = items.get(group).get(childPosition);
            if (item != null) {
                TextView value = (TextView) view.findViewById(R.id.groups_item);
                value.setText(item.Name);

                TextView date = (TextView) view.findViewById(R.id.groups_item_date);
                TextView time = (TextView) view.findViewById(R.id.groups_item_time);
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
                } else {
                    view.setBackgroundColor(Color.LTGRAY);
                    value.setTextColor(Color.BLACK);
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
