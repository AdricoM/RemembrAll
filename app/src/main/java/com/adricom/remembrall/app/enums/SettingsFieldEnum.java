package com.adricom.remembrall.app.enums;

import com.adricom.remembrall.app.R;

/**
 * Created by adrien on 30/08/15.
 */
public enum SettingsFieldEnum {
    USE_DEFAULT_REMINDER_TIME(0, R.string.settings_use_default_reminder_time),
    DEFAULT_REMINDER_TIME(1, R.string.settings_default_reminder_time),
    NUMBER_REMINDERS_HOME(2, R.string.settings_number_reminders_home);

    private int index;
    private int stringResource;

    SettingsFieldEnum(int _index, int _stringResource) {
        index = _index;
        stringResource = _stringResource;
    }

    public int getIndex() {
        return index;
    }

    public int getStringResource() {
        return stringResource;
    }

    public int compare(SettingsFieldEnum otherSettingFieldEnum) {
        if (otherSettingFieldEnum.index > index) {
            return -1;
        } else if (otherSettingFieldEnum.index < index) {
            return 1;
        } else {
            return 0;
        }
    }
}
