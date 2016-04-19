package com.adricom.remembrall.app.activities;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.Toolbar;

import com.adricom.remembrall.app.R;
import com.adricom.remembrall.app.adapters.MainAdapter;
import com.adricom.remembrall.app.enums.SettingsFieldEnum;
import com.adricom.remembrall.app.helpers.GroupsHelper;
import com.adricom.remembrall.app.helpers.Helper;
import com.adricom.remembrall.app.helpers.NavigationHelper;
import com.adricom.remembrall.app.helpers.RemindersHelper;
import com.adricom.remembrall.app.helpers.SettingsHelper;
import com.adricom.remembrall.app.models.GroupModel;
import com.adricom.remembrall.app.models.MainGroup;
import com.adricom.remembrall.app.models.MainItem;
import com.adricom.remembrall.app.models.MainItemNextReminder;
import com.adricom.remembrall.app.models.MainItemQuickAccessGroup;
import com.adricom.remembrall.app.models.MainItemSeeAllGroups;
import com.adricom.remembrall.app.models.ReminderModel;
import com.adricom.remembrall.app.models.SettingsItem;
import com.adricom.remembrall.app.services.AutoStartService;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private SortedMap<MainGroup, List<MainItem>> items;
    private MainGroup quickAccessGroup;
    private MainGroup comingRemindersGroup;
    private ExpandableListView expListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(R.drawable.ic_launcher);
        }

        Helper.InitializeHelpers(this);

        expListView = (ExpandableListView) findViewById(R.id.main_exp_list_view);
        initializeItems();

        startService(new Intent(this, AutoStartService.class));
    }

    private void createAdapter() {
        MainAdapter adapter = new MainAdapter(this, items);
        expListView.setAdapter(adapter);

        for (int groupPosition = 0; groupPosition < items.keySet().size(); groupPosition++) {
            expListView.expandGroup(groupPosition);
        }
    }

    private void initializeItems() {
        items = new TreeMap<>();
        initializeNextReminders();
        initializeQuickAccessGroups();
    }

    private void initializeQuickAccessGroups() {
        // Quick access groups
        quickAccessGroup = new MainGroup(1, getString(R.string.main_group_quick_access), android.R.drawable.btn_star_big_on);
    }

    private void initializeNextReminders() {
        // Next coming reminders
        comingRemindersGroup = new MainGroup(0, getString(R.string.main_group_next_coming_reminders), android.R.drawable.ic_popup_reminder);
    }

    private void refreshQuickAccessGroups() {
        List<GroupModel> quickAccessModelList = GroupsHelper.Instance.getQuickAccessGroups();
        List<MainItem> quickAccessList = new ArrayList<>(quickAccessModelList.size());

        for (GroupModel group : quickAccessModelList) {
            quickAccessList.add(new MainItemQuickAccessGroup(group, this));
        }

        quickAccessList.add(new MainItemSeeAllGroups(this));
        items.put(quickAccessGroup, quickAccessList);
    }

    private void refreshNextReminders() {
        SettingsItem settingNumberRemindersHome = SettingsHelper.Instance.getSetting(SettingsFieldEnum.NUMBER_REMINDERS_HOME);
        int numberRemindersHome = (int) settingNumberRemindersHome.Value;

        List<ReminderModel> nextReminderModelList = RemindersHelper.Instance.getNextReminders(numberRemindersHome);
        List<MainItem> nextReminderList = new ArrayList<>(numberRemindersHome);

        for (ReminderModel reminder : nextReminderModelList) {
            nextReminderList.add(new MainItemNextReminder(reminder, this));
        }

        items.put(comingRemindersGroup, nextReminderList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                // "Settings" button
                NavigationHelper.navigateToSettings(this);
                return true;
            case R.id.menu_about:
                // "About" button
                NavigationHelper.navigateToAbout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshNextReminders();
        refreshQuickAccessGroups();
        createAdapter();
    }
}
