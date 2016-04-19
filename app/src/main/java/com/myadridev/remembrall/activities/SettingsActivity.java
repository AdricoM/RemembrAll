package com.myadridev.remembrall.activities;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toolbar;

import com.myadridev.remembrall.R;
import com.myadridev.remembrall.adapters.SettingsAdapter;
import com.myadridev.remembrall.enums.SettingsFieldEnum;
import com.myadridev.remembrall.helpers.SettingsHelper;
import com.myadridev.remembrall.models.SettingsItem;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private List<SettingsItem> items;
    private CoordinatorLayout coordinatorLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.settings_coordinator_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(android.R.drawable.ic_menu_preferences);
        }

        if (!SettingsHelper.IsInitialized())
            SettingsHelper.Init(this);

        final ListView listView = (ListView) findViewById(R.id.settings_list_view);

        items = SettingsHelper.Instance.getAllSettings();

        final SettingsAdapter adapter = new SettingsAdapter(this, coordinatorLayout, R.layout.settings_item, items);
        adapter.setIsItemEnabled(SettingsFieldEnum.DEFAULT_REMINDER_TIME.getIndex(), (boolean) SettingsHelper.Instance.getSetting(SettingsFieldEnum.USE_DEFAULT_REMINDER_TIME).Value);
        listView.setAdapter(adapter);
    }

    @Override
    public void onDestroy() {
        SettingsHelper.Instance.saveSettings();
        super.onDestroy();
    }
}
