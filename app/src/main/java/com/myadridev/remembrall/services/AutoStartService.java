package com.myadridev.remembrall.services;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.myadridev.remembrall.helpers.Helper;
import com.myadridev.remembrall.helpers.RemindersHelper;
import com.myadridev.remembrall.models.ReminderModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutoStartService extends Service {
    private Map<Integer, Date> remindersIdDate;
    private JobScheduler jobScheduler;

    public AutoStartService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Helper.InitializeHelpers(this);

        if (remindersIdDate == null)
            remindersIdDate = new HashMap<>();

        jobScheduler = (JobScheduler) getApplication().getSystemService(Context.JOB_SCHEDULER_SERVICE);

        Map<Integer, ReminderModel> notAchievedReminders = RemindersHelper.Instance.getNotAchievedReminders();
        launchNeededScheduledJobs(notAchievedReminders);
        removeNotNeededScheduledJobs(notAchievedReminders);

        return Service.START_STICKY;
    }

    public void launchNeededScheduledJobs(Map<Integer, ReminderModel> notAchievedReminders) {
        for (ReminderModel reminder : notAchievedReminders.values()) {
            boolean needsLaunch = true;
            if (remindersIdDate.containsKey(reminder.Id)) {
                Date oldDate = remindersIdDate.get(reminder.Id);
                Date newDate = reminder.NextReminderDate;
                if (newDate.equals(oldDate)) {
                    needsLaunch = false;
                }
            }

            if (needsLaunch) {
                launchScheduledJob(reminder);
            }
        }
    }

    private void launchScheduledJob(ReminderModel reminder) {
        Calendar calendar = Calendar.getInstance();
        Date nextReminderDate = reminder.NextReminderDate;
        int reminderId = reminder.Id;
        remindersIdDate.put(reminderId, nextReminderDate);

        ComponentName serviceComponent = new ComponentName(this, ReminderJobService.class);
        JobInfo.Builder builder = new JobInfo.Builder(reminderId, serviceComponent);
        long delayBeforeLaunch = Math.max(0, nextReminderDate.getTime() - calendar.getTime().getTime());
        builder.setMinimumLatency(delayBeforeLaunch);
        builder.setOverrideDeadline(delayBeforeLaunch);

        jobScheduler.schedule(builder.build());
    }

    private void removeNotNeededScheduledJobs(Map<Integer, ReminderModel> notAchievedReminders) {
        List<Integer> idsToRemove = new ArrayList<>(remindersIdDate.size());

        for (Map.Entry<Integer, Date> reminderIdDate : remindersIdDate.entrySet()) {
            int reminderId = reminderIdDate.getKey();
            if (!notAchievedReminders.containsKey(reminderId)) {
                idsToRemove.add(reminderId);
            }
        }

        for (Integer id : idsToRemove) {
            remindersIdDate.remove(id);
            jobScheduler.cancel(id);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
