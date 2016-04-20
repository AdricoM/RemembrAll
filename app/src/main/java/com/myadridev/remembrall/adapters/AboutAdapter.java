package com.myadridev.remembrall.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.myadridev.remembrall.R;
import com.myadridev.remembrall.models.AboutItem;

import java.util.List;

/**
 * Created by adrien on 24/08/15.
 */
public class AboutAdapter extends ArrayAdapter<AboutItem> {
    private LayoutInflater layoutInflater;
    private Activity activity;
    private String contactSubject;

    public AboutAdapter(Activity _activity, int resource, List<AboutItem> items, String _contactSubject) {
        super(_activity, resource, items);

        activity = _activity;
        layoutInflater = LayoutInflater.from(activity);
        contactSubject = _contactSubject;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.about_item, null);

        AboutItem item = getItem(position);

        if (item != null) {
            TextView labelView = (TextView) view.findViewById(R.id.about_label);
            TextView valueView = (TextView) view.findViewById(R.id.about_value);

            labelView.setText(item.Label);
            valueView.setText(item.Value);

            switch (item.AboutFieldsEnum) {
                case CONTACT:
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_SENDTO);
                            intent.setData(Uri.parse(activity.getString(R.string.about_mail_uri)));
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {activity.getString(R.string.about_mail)});
                            intent.putExtra(Intent.EXTRA_SUBJECT, contactSubject);
                            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.about_mail_client_choice_label)));
                        }
                    });
                    break;
                case SOURCES:
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(activity.getString(R.string.about_sources_url)));
                            activity.startActivity(Intent.createChooser(browserIntent, activity.getString(R.string.about_browser_choice_label)));
                        }
                    });
                    break;
                default:
                    break;
            }
        }

        return view;
    }
}
