package com.myadridev.remembrall.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toolbar;

import com.adricom.remembrall.app.R;
import com.myadridev.remembrall.adapters.GroupsAdapter;
import com.myadridev.remembrall.helpers.GroupsHelper;
import com.myadridev.remembrall.helpers.NavigationHelper;
import com.myadridev.remembrall.helpers.RemindersHelper;
import com.myadridev.remembrall.models.GroupModel;
import com.myadridev.remembrall.models.GroupsGroup;
import com.myadridev.remembrall.models.GroupsItem;
import com.myadridev.remembrall.models.GroupsItemAddReminder;
import com.myadridev.remembrall.models.ReminderModel;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class GroupsActivity extends AppCompatActivity {

    private SortedMap<GroupsGroup, List<GroupsItem>> items;
    private ExpandableListView expListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups);

        Toolbar toolbar = (Toolbar) findViewById(R.id.groups_toolbar);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(android.R.drawable.ic_menu_sort_by_size);
        }

        expListView = (ExpandableListView) findViewById(R.id.groups_exp_list_view);
        if (!GroupsHelper.IsInitialized()) {
            GroupsHelper.Init(this);
        }
    }

    private void createAdapter() {
        GroupsAdapter adapter = new GroupsAdapter(this, items);
        expListView.setAdapter(adapter);

        for (int groupPosition = 0; groupPosition < items.keySet().size(); groupPosition++) {
            expListView.expandGroup(groupPosition);
        }
    }

    private void refreshItems() {
        items = new TreeMap<>();

        List<GroupModel> groups = GroupsHelper.Instance.getGroups();
        for (GroupModel group : groups) {
            List<ReminderModel> reminders = RemindersHelper.Instance.getRemindersByGroupId(group.Id);
            List<GroupsItem> children = new ArrayList<>(reminders.size() + 1);
            for (ReminderModel reminder : reminders) {
                children.add(new GroupsItem(reminder, this));
            }

            children.add(new GroupsItemAddReminder(this, group.Id));
            items.put(new GroupsGroup(group, this), children);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.groups, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_groups_add:
                // "Add reminder" button
                NavigationHelper.navigateToGroup(this, 0, true);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshItems();
        createAdapter();
    }
}
