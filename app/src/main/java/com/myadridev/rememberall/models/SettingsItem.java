package com.myadridev.rememberall.models;

import android.content.Context;

import com.myadridev.rememberall.R;
import com.myadridev.rememberall.enums.SettingsFieldEnum;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingsItem implements Comparable<SettingsItem> {
    public String Label;
    public Object Value;
    public String StringValue;
    public SettingsFieldEnum SettingsFieldEnum;
    private Context context;

    public SettingsItem(SettingsItem settingsItem) {
        this(settingsItem.context, settingsItem.SettingsFieldEnum, settingsItem.Value);
    }

    public SettingsItem(Context _context, SettingsFieldEnum settingsFieldEnum, Object value) {
        context = _context;
        Label = context.getString(settingsFieldEnum.getStringResource());
        Value = value;
        StringValue = setStringValue(value);
        SettingsFieldEnum = settingsFieldEnum;
    }

    public void setValue(Object value) {
        Value = value;
        StringValue = setStringValue(value);
    }

    private String setStringValue(Object value) {
        if (value instanceof Integer) {
            return value.toString();
        } else if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Boolean) {
            return (boolean) value ? context.getString(R.string.utils_true) : context.getString(R.string.utils_false);
        } else if (value instanceof Date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(context.getString(R.string.utils_storage_time_format));
            return dateFormat.format((Date) value);
        }
        return "";
    }

    @Override
    public int compareTo(SettingsItem otherSetting) {
        int compare = SettingsFieldEnum.compare(otherSetting.SettingsFieldEnum);
        if (compare < 0) {
            return -1;
        } else if (compare > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
