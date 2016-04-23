package com.myadridev.rememberall.helpers;

import android.content.Context;

/**
 * Created by adrien on 16/04/16.
 */
public class Helper {

    public static void InitializeHelpers(Context context) {
        if (!SettingsHelper.IsInitialized())
            SettingsHelper.Init(context);

        if (!GroupsHelper.IsInitialized())
            GroupsHelper.Init(context);

        if (!RemindersHelper.IsInitialized())
            RemindersHelper.Init(context);
    }
}
