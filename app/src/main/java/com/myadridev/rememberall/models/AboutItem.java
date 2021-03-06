package com.myadridev.rememberall.models;

import android.content.Context;
import android.support.annotation.NonNull;

import com.myadridev.rememberall.enums.AboutFieldsEnum;

public class AboutItem implements Comparable<AboutItem> {
    public AboutFieldsEnum AboutFieldsEnum;
    public String Label;
    public String Value;
    private Context context;

    public AboutItem(Context _context, AboutFieldsEnum aboutFieldsEnum, String value) {
        context = _context;
        AboutFieldsEnum = aboutFieldsEnum;
        Label = context.getString(aboutFieldsEnum.getStringResource());
        Value = value;
    }

    @Override
    public int compareTo(@NonNull AboutItem otherItem) {
        int compare = AboutFieldsEnum.compare(otherItem.AboutFieldsEnum);
        if (compare < 0) {
            return -1;
        } else if (compare > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
