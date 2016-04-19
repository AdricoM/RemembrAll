package com.adricom.remembrall.app.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import com.adricom.remembrall.app.R;
import com.adricom.remembrall.app.models.GroupModel;
import com.adricom.remembrall.app.models.ReminderModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by adrien on 30/08/15.
 */
public class GroupsHelper {

    public static GroupsHelper Instance;
    private static int MaxGroupId;
    private static boolean _isInitialized;
    private final Map<Integer, GroupModel> groups;
    private final Map<Integer, String> groupsJson;
    private final Context context;
    private final String storedFileName;
    private final String groupsSet;
    private final int openMode;
    private ObjectMapper jsonMapper = new ObjectMapper();

    private GroupsHelper(Context _context) {
        context = _context;
        MaxGroupId = 0;
        groups = new HashMap<>();
        groupsJson = new HashMap<>();
        storedFileName = context.getString(R.string.filename_groups);
        groupsSet = context.getString(R.string.store_groups);
        openMode = Context.MODE_PRIVATE;
        initializeGroups();
    }

    public static boolean IsInitialized() {
        return _isInitialized;
    }

    public static void Init(Context context) {
        Instance = new GroupsHelper(context);
        _isInitialized = true;
    }

    private void initializeGroups() {
        SharedPreferences storedGroups = context.getSharedPreferences(storedFileName, openMode);
        loadAllGroups(storedGroups);
    }

    private void loadAllGroups(SharedPreferences storedGroups) {
        Set<String> allGroupsJson = storedGroups.getStringSet(groupsSet, new HashSet<String>());
        for (String groupJson : allGroupsJson) {
            GroupModel group = null;
            try {
                group = jsonMapper.readValue(groupJson, GroupModel.class);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            if (group.Id > MaxGroupId)
                MaxGroupId = group.Id;
            groups.put(group.Id, group);
            groupsJson.put(group.Id, groupJson);
        }
    }

    public List<GroupModel> getGroups() {
        return new ArrayList<>(groups.values());
    }

    public GroupModel getGroup(int id) {
        GroupModel groupModel = groups.containsKey(id) ? groups.get(id) : null;
        return groupModel;
    }

    public int addGroup(GroupModel group) {
        MaxGroupId++;
        group.Id = MaxGroupId;

        String groupJson = "";
        try {
            groupJson = jsonMapper.writeValueAsString(group);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            MaxGroupId--;
            return 0;
        }

        groups.put(MaxGroupId, group);
        groupsJson.put(MaxGroupId, groupJson);
        return MaxGroupId;
    }

    public void editGroup(GroupModel group) {
        String groupJson = "";
        try {
            groupJson = jsonMapper.writeValueAsString(group);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return;
        }

        groups.put(group.Id, group);
        groupsJson.put(group.Id, groupJson);

        RemindersHelper.Instance.refreshNextReminderDateByGroupIs(group.Id);
    }

    public void removeGroup(int id) {
        if (groups.containsKey(id)) {
            RemindersHelper.Instance.removeRemindersByGroupId(id);
            groups.remove(id);
            groupsJson.remove(id);
            setMaxGroupId();
        }
    }

    public List<Integer> getAllGroupIds() {
        return new ArrayList<>(groups.keySet());
    }

    public boolean IsExistingName(GroupModel group) {
        boolean exists = false;

        for (GroupModel existingGroup : groups.values()) {
            if (group.Id != existingGroup.Id && group.Name.equalsIgnoreCase(existingGroup.Name)) {
                exists = true;
                break;
            }
        }

        return exists;
    }

    public boolean IsReminderTimeForAllReminders(GroupModel group) {
        for (ReminderModel reminder : RemindersHelper.Instance.getRemindersByGroupId(group.Id)) {
            if (!RemindersHelper.Instance.IsReminderTime(reminder, group)) {
                return false;
            }
        }
        return true;
    }

    private void setMaxGroupId() {
        int maxId = -1;
        for (int id : groups.keySet()) {
            if (id > maxId)
                maxId = id;
        }
        MaxGroupId = maxId;
    }

    public void saveGroups() {
        SharedPreferences storedGroups = context.getSharedPreferences(storedFileName, openMode);
        SharedPreferences.Editor editor = storedGroups.edit();
        Set<String> groupJsonSet = new HashSet<>();
        for (String groupJson : groupsJson.values()) {
            groupJsonSet.add(groupJson);
        }
        editor.putStringSet(groupsSet, groupJsonSet);
        editor.apply();
    }

    public List<GroupModel> getQuickAccessGroups() {
        List<GroupModel> quickAccessGroups = new ArrayList<>(groups.size());
        for (Map.Entry<Integer, GroupModel> groupEntry : groups.entrySet()) {
            GroupModel group = groupEntry.getValue();
            if (group.IsQuickAccess)
                quickAccessGroups.add(group);
        }
        return quickAccessGroups;
    }
}
