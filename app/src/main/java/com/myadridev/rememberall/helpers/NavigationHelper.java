package com.myadridev.rememberall.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.myadridev.rememberall.activities.AboutActivity;
import com.myadridev.rememberall.activities.GroupDetailActivity;
import com.myadridev.rememberall.activities.GroupsActivity;
import com.myadridev.rememberall.activities.ReminderDetailActivity;
import com.myadridev.rememberall.activities.SettingsActivity;

/**
 * Created by adrien on 02/03/16.
 */
public class NavigationHelper {

    public static void navigateToGroup(Context context, int groupId, boolean isEditable) {
        Bundle bundle = new Bundle();
        bundle.putInt("groupId", groupId);
        bundle.putBoolean("isEditable", isEditable);
        Intent intent = new Intent(context, GroupDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void navigateToReminder(Context context, int reminderId, boolean isEditable) {
        context.startActivity(getNavigateToReminderIntent(context, reminderId, isEditable));
    }

    public static void navigateToReminder(Context context, int reminderId, int groupId, boolean isEditable) {
        Bundle bundle = new Bundle();
        bundle.putInt("reminderId", reminderId);
        bundle.putInt("groupId", groupId);
        bundle.putBoolean("isEditable", isEditable);
        Intent intent = new Intent(context, ReminderDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void navigateToGroups(Context context) {
        context.startActivity(new Intent(context, GroupsActivity.class));
    }

    public static void navigateToSettings(Context context) {
        context.startActivity(new Intent(context, SettingsActivity.class));
    }

    public static void navigateToAbout(Context context) {
        context.startActivity(new Intent(context, AboutActivity.class));
    }

    public static Intent getNavigateToReminderIntent(Context context, int reminderId, boolean isEditable) {
        Bundle bundle = new Bundle();
        bundle.putInt("reminderId", reminderId);
        bundle.putBoolean("isEditable", isEditable);
        Intent intent = new Intent(context, ReminderDetailActivity.class);
        intent.putExtras(bundle);
        return intent;
    }
}
