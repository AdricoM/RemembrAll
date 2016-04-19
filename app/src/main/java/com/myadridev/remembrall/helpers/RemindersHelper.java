package com.myadridev.remembrall.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.myadridev.remembrall.R;
import com.myadridev.remembrall.enums.SettingsFieldEnum;
import com.myadridev.remembrall.models.GroupModel;
import com.myadridev.remembrall.models.ReminderModel;
import com.myadridev.remembrall.models.SettingsItem;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by adrien on 30/08/15.
 */
public class RemindersHelper {

    public static RemindersHelper Instance;
    private static int MaxReminderId;
    private static boolean _isInitialized;
    private final Map<Integer, ReminderModel> reminders;
    private final Map<Integer, String> remindersJson;
    private final Context context;
    private final String storedFileName;
    private final String remindersSet;
    private final int openMode;
    private ObjectMapper jsonMapper = new ObjectMapper();
    private Calendar calendar = Calendar.getInstance();
    private Calendar calendar2 = Calendar.getInstance();

    private RemindersHelper(Context _context) {
        context = _context;
        MaxReminderId = 0;
        reminders = new HashMap<>();
        remindersJson = new HashMap<>();
        storedFileName = context.getString(R.string.filename_reminders);
        remindersSet = context.getString(R.string.store_reminders);
        openMode = Context.MODE_PRIVATE;
        if (!GroupsHelper.IsInitialized())
            GroupsHelper.Init(context);
        initializeReminders();
    }

    public static boolean IsInitialized() {
        return _isInitialized;
    }

    public static void Init(Context context) {
        Instance = new RemindersHelper(context);
        _isInitialized = true;
    }

    private void initializeReminders() {
        SharedPreferences storedReminders = context.getSharedPreferences(storedFileName, openMode);
        loadAllReminders(storedReminders);
    }

    private void loadAllReminders(SharedPreferences storedReminders) {
        Set<String> allRemindersJson = storedReminders.getStringSet(remindersSet, new HashSet<String>());
        for (String reminderJson : allRemindersJson) {
            ReminderModel reminder = null;
            try {
                reminder = jsonMapper.readValue(reminderJson, ReminderModel.class);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            refreshNextReminderDate(reminder);

            if (reminder.Id > MaxReminderId)
                MaxReminderId = reminder.Id;
            reminders.put(reminder.Id, reminder);
            remindersJson.put(reminder.Id, reminderJson);
        }
    }

    private void refreshNextReminderDate(ReminderModel reminder) {
        GroupModel group = GroupsHelper.Instance.getGroup(reminder.GroupId);

        if (reminder.UseCustomReminderTime) {
            calendar.setTime(reminder.CustomReminderTime);
        } else if (group.UseDefaultReminderTime) {
            calendar.setTime(group.DefaultReminderTime);
        } else {
            calendar.setTime((Date) SettingsHelper.Instance.getSetting(SettingsFieldEnum.DEFAULT_REMINDER_TIME).Value);
        }

        calendar2.setTime(reminder.ReminderDate);

        calendar.set(Calendar.YEAR, calendar2.get(Calendar.YEAR));
        calendar.set(Calendar.MONTH, calendar2.get(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar2.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        reminder.NextReminderDate = calendar.getTime();
    }

    public ReminderModel getReminder(int id) {
        return reminders.containsKey(id) ? reminders.get(id) : null;
    }

    public int addReminder(ReminderModel reminder) {
        MaxReminderId++;
        reminder.Id = MaxReminderId;
        refreshNextReminderDate(reminder);

        String reminderJson = "";
        try {
            reminderJson = jsonMapper.writeValueAsString(reminder);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            MaxReminderId--;
            return 0;
        }

        reminders.put(MaxReminderId, reminder);
        remindersJson.put(MaxReminderId, reminderJson);
        return MaxReminderId;
    }

    public void editReminder(ReminderModel reminder) {
        refreshNextReminderDate(reminder);

        String reminderJson = "";
        try {
            reminderJson = jsonMapper.writeValueAsString(reminder);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        reminders.put(reminder.Id, reminder);
        remindersJson.put(reminder.Id, reminderJson);
    }

    public void removeReminder(int id) {
        if (reminders.containsKey(id)) {
            reminders.remove(id);
            remindersJson.remove(id);
            setMaxReminderId();
        }
    }

    public List<ReminderModel> getRemindersByGroupId(int groupId) {
        List<ReminderModel> reminderList = new ArrayList<>(reminders.size());
        for (Map.Entry<Integer, ReminderModel> reminderEntry : reminders.entrySet()) {
            if (reminderEntry.getValue().GroupId == groupId)
                reminderList.add(reminderEntry.getValue());
        }
        return reminderList;
    }

    public void removeRemindersByGroupId(int groupId) {
        List<Integer> remindersIdToRemove = new ArrayList<>(reminders.size());
        for (Map.Entry<Integer, ReminderModel> reminderEntry : reminders.entrySet()) {
            if (reminderEntry.getValue().GroupId == groupId)
                remindersIdToRemove.add(reminderEntry.getKey());
        }
        for (int id : remindersIdToRemove) {
            reminders.remove(id);
            remindersJson.remove(id);
        }
        if (!remindersIdToRemove.isEmpty())
            setMaxReminderId();
    }

    public void refreshNextReminderDateByGroupIs(int groupId) {
        List<ReminderModel> remindersToEdit = new ArrayList<>(reminders.size());
        for (Map.Entry<Integer, ReminderModel> reminderEntry : reminders.entrySet()) {
            ReminderModel reminder = reminderEntry.getValue();
            if (reminder.GroupId == groupId)
                remindersToEdit.add(reminder);
        }
        for (ReminderModel reminder : remindersToEdit) {
            reminder.Achieved = false;
            editReminder(reminder);
        }
    }

    public boolean IsExistingName(ReminderModel reminder) {
        boolean exists = false;

        for (ReminderModel existingReminder : reminders.values()) {
            if (reminder.Id != existingReminder.Id && reminder.Name.equalsIgnoreCase(existingReminder.Name)) {
                exists = true;
                break;
            }
        }

        return exists;
    }

    public boolean IsReminderTime(ReminderModel reminder, SettingsItem item, GroupModel group) {
        return reminder.UseCustomReminderTime || group.UseDefaultReminderTime || (boolean) item.Value;
    }

    public boolean IsReminderTime(ReminderModel reminder, GroupModel group) {
        return IsReminderTime(reminder, SettingsHelper.Instance.getSetting(SettingsFieldEnum.USE_DEFAULT_REMINDER_TIME), group);
    }

    public boolean IsReminderTime(ReminderModel reminder, SettingsItem item) {
        return IsReminderTime(reminder, item, GroupsHelper.Instance.getGroup(reminder.GroupId));
    }

    public boolean IsReminderTime(ReminderModel reminder) {
        return IsReminderTime(reminder, SettingsHelper.Instance.getSetting(SettingsFieldEnum.USE_DEFAULT_REMINDER_TIME), GroupsHelper.Instance.getGroup(reminder.GroupId));
    }

    public boolean IsReminderTimeForAllReminders(SettingsItem item) {
        for (ReminderModel reminder : reminders.values()) {
            if (!RemindersHelper.Instance.IsReminderTime(reminder, item)) {
                return false;
            }
        }
        return true;
    }

    private void setMaxReminderId() {
        int maxId = -1;
        for (int id : reminders.keySet()) {
            if (id > maxId)
                maxId = id;
        }
        MaxReminderId = maxId;
    }

    public void saveReminders() {
        SharedPreferences storedReminders = context.getSharedPreferences(storedFileName, openMode);
        SharedPreferences.Editor editor = storedReminders.edit();
        Set<String> remindersJsonSet = new HashSet<>();
        for (String reminderJson : remindersJson.values()) {
            remindersJsonSet.add(reminderJson);
        }
        editor.putStringSet(remindersSet, remindersJsonSet);
        editor.apply();
    }

    public List<ReminderModel> getNextReminders(int number) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();

        List<ReminderModel> nextReminders = new ArrayList<>(number);
        int currentNumber = 0;
        for (Map.Entry<Integer, ReminderModel> reminderEntry : reminders.entrySet()) {
            ReminderModel reminder = reminderEntry.getValue();

            if (reminder.NextReminderDate.before(now)) {
                continue;
            }

            int indexInsert = currentNumber;
            for (int i = currentNumber - 1; i >= 0; i--) {
                if (reminder.NextReminderDate.before(nextReminders.get(i).NextReminderDate))
                    indexInsert--;
                else
                    break;
            }
            if (indexInsert < number) {
                nextReminders.add(indexInsert, reminder);
                currentNumber++;
            }
        }
        return nextReminders.subList(0, Math.min(number, currentNumber));
    }

    public Map<Integer, ReminderModel> getNotAchievedReminders() {
        Map<Integer, ReminderModel> notAchievedReminders = new HashMap<>(reminders.size());
        for (Map.Entry<Integer, ReminderModel> reminderWithId : reminders.entrySet()) {
            ReminderModel reminder = reminderWithId.getValue();
            if (!reminder.Achieved) {
                notAchievedReminders.put(reminder.Id, reminder);
            }
        }
        return notAchievedReminders;
    }
}
