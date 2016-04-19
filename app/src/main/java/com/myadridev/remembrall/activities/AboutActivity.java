package com.myadridev.remembrall.activities;

import android.app.ActionBar;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.Toolbar;

import com.adricom.remembrall.app.R;
import com.myadridev.remembrall.adapters.AboutAdapter;
import com.myadridev.remembrall.enums.AboutFieldsEnum;
import com.myadridev.remembrall.models.AboutItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    private String version;
    private String contactSubject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.about_toolbar);
        setActionBar(toolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setDisplayUseLogoEnabled(true);
            actionBar.setLogo(android.R.drawable.ic_menu_info_details);
        }

        ListView listView = (ListView) findViewById(R.id.about_list_view);

        List<AboutItem> items = initializeItems();

        AboutAdapter adapter = new AboutAdapter(this, R.layout.about_item, items, contactSubject);
        listView.setAdapter(adapter);
    }

    private List<AboutItem> initializeItems() {
        List<AboutItem> items = new ArrayList<>(2);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = getString(R.string.global_default);
        }

        contactSubject = computeEmailSubject();

        items.add(new AboutItem(this, AboutFieldsEnum.VERSION, version));
        items.add(new AboutItem(this, AboutFieldsEnum.CONTACT, getString(R.string.about_mail)));

        Collections.sort(items);

        return items;
    }

    private String computeEmailSubject() {
        return "[" + getString(R.string.app_name) + "]"
                + (
                !version.equals(getString(R.string.global_default))
                        ? "[" + getString(R.string.about_version_prefix) + version + "]"
                        : "");
    }
}
