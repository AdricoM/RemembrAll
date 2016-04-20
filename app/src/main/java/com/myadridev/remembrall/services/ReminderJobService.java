package com.myadridev.remembrall.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.myadridev.remembrall.R;
import com.myadridev.remembrall.helpers.NavigationHelper;
import com.myadridev.remembrall.helpers.RemindersHelper;
import com.myadridev.remembrall.models.ReminderModel;

import java.text.SimpleDateFormat;

/**
 * Created by adrien on 16/04/16.
 */
public class ReminderJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters params) {
        if (!RemindersHelper.IsInitialized()) {
            RemindersHelper.Init(this);
        }

        int reminderId = params.getJobId();
        ReminderModel reminder = RemindersHelper.Instance.getReminder(reminderId);

        Intent resultIntent = NavigationHelper.getNavigateToReminderIntent(this, reminderId, false);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        SimpleDateFormat dateFormat = new SimpleDateFormat(getString(R.string.utils_date_format_full));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(ContextCompat.getColor(this, R.color.NotificationBack))
                .setContentTitle(dateFormat.format(reminder.NextReminderDate) + " - " + reminder.Name)
                .setContentText(reminder.Description)
                .setContentIntent(resultPendingIntent)
                .setCategory(ALARM_SERVICE)
                .setAutoCancel(true);

        Notification notification = notificationBuilder.build();
        notification.defaults |= Notification.DEFAULT_ALL;

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(reminderId, notification);

        reminder.Achieved = true;
        RemindersHelper.Instance.editReminder(reminder);
        RemindersHelper.Instance.saveReminders();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
