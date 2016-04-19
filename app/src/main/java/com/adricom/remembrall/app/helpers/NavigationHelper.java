package com.adricom.remembrall.app.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.adricom.remembrall.app.activities.AboutActivity;
import com.adricom.remembrall.app.activities.GroupDetailActivity;
import com.adricom.remembrall.app.activities.GroupsActivity;
import com.adricom.remembrall.app.activities.ReminderDetailActivity;
import com.adricom.remembrall.app.activities.SettingsActivity;

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
