package com.myadridev.remembrall.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.myadridev.remembrall.R;
import com.myadridev.remembrall.enums.SettingsFieldEnum;
import com.myadridev.remembrall.models.SettingsItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by adrien on 30/08/15.
 */
public class SettingsHelper {

    private static final int DEFAULT_HOUR = 10;
    private static final int DEFAULT_MINUTE = 0;
    private static final String DEFAULT_TIME = String.format("%02d:%02d", DEFAULT_HOUR, DEFAULT_MINUTE);
    private static final int DEFAULT_NUMBER_REMINDER_HOME = 5;
    public static SettingsHelper Instance;
    private static boolean _isInitialized;
    private final Map<SettingsFieldEnum, SettingsItem> settings;
    private SimpleDateFormat StorageDateFormat;
    private Context context;
    private int openMode;

    private SettingsHelper(Context _context) {
        context = _context;
        StorageDateFormat = new SimpleDateFormat(_context.getString(R.string.utils_storage_time_format));
        settings = new HashMap<>();
        openMode = Context.MODE_PRIVATE;
        initializeSettings();
    }

    public static boolean IsInitialized() {
        return _isInitialized;
    }

    public static void Init(Context context) {
        Instance = new SettingsHelper(context);
        _isInitialized = true;
    }

    private void initializeSettings() {
        SharedPreferences storedSettings = context.getSharedPreferences(context.getString(R.string.filename_settings), openMode);

        settings.put(SettingsFieldEnum.USE_DEFAULT_REMINDER_TIME, getSetting(SettingsFieldEnum.USE_DEFAULT_REMINDER_TIME, storedSettings));
        settings.put(SettingsFieldEnum.DEFAULT_REMINDER_TIME, getSetting(SettingsFieldEnum.DEFAULT_REMINDER_TIME, storedSettings));
        settings.put(SettingsFieldEnum.NUMBER_REMINDERS_HOME, getSetting(SettingsFieldEnum.NUMBER_REMINDERS_HOME, storedSettings));
    }

    public List<SettingsItem> getAllSettings() {
        List<SettingsItem> allSettings = new ArrayList<>(settings.values());
        Collections.sort(allSettings);
        return allSettings;
    }

    public void saveSettings() {
        SharedPreferences storedSettings = context.getSharedPreferences(context.getString(R.string.filename_settings), openMode);
        SharedPreferences.Editor editor = storedSettings.edit();
        for (Map.Entry<SettingsFieldEnum, SettingsItem> setting : settings.entrySet()) {
            SettingsFieldEnum field = setting.getKey();
            SettingsItem item = setting.getValue();
            switch (field) {
                case USE_DEFAULT_REMINDER_TIME:
                    editor.putBoolean(field.name(), (boolean) item.Value);
                    break;
                case DEFAULT_REMINDER_TIME:
                    editor.putString(field.name(), StorageDateFormat.format((Date) item.Value));
                    break;
                case NUMBER_REMINDERS_HOME:
                    editor.putInt(field.name(), (int) item.Value);
                    break;
            }
        }
        editor.apply();
    }

    public SettingsItem editSetting(SettingsItem settingsItem) {
        return settings.put(settingsItem.SettingsFieldEnum, settingsItem);
    }

    public SettingsItem getSetting(SettingsFieldEnum field) {
        return settings.get(field);
    }

    private SettingsItem getSetting(SettingsFieldEnum field, SharedPreferences storedSettings) {
        switch (field) {
            case USE_DEFAULT_REMINDER_TIME:
                boolean useDefault = storedSettings.getBoolean(SettingsFieldEnum.USE_DEFAULT_REMINDER_TIME.name(), true);
                return new SettingsItem(context, SettingsFieldEnum.USE_DEFAULT_REMINDER_TIME, useDefault);
            case DEFAULT_REMINDER_TIME:
                String defaultHour = storedSettings.getString(SettingsFieldEnum.DEFAULT_REMINDER_TIME.name(), DEFAULT_TIME);

                Date date;
                try {
                    date = StorageDateFormat.parse(defaultHour);
                } catch (ParseException e) {
                    date = null;
                }
                Calendar cal = Calendar.getInstance();
                if (date != null) {
                    cal.setTime(date);
                } else {
                    cal.set(Calendar.HOUR_OF_DAY, DEFAULT_HOUR);
                    cal.set(Calendar.MINUTE, DEFAULT_MINUTE);
                }
                return new SettingsItem(context, SettingsFieldEnum.DEFAULT_REMINDER_TIME, cal.getTime());
            case NUMBER_REMINDERS_HOME:
                int numberRemindersHome = storedSettings.getInt(SettingsFieldEnum.NUMBER_REMINDERS_HOME.name(), DEFAULT_NUMBER_REMINDER_HOME);
                return new SettingsItem(context, SettingsFieldEnum.NUMBER_REMINDERS_HOME, numberRemindersHome);
            default:
                return null;
        }
    }
}
