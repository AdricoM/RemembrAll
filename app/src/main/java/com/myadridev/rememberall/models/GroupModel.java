package com.myadridev.rememberall.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

@JsonSerialize(as = GroupModel.class)
public class GroupModel {
    public int Id;
    public String Name;

    public String Description;

    @JsonProperty("iqa")
    public boolean IsQuickAccess;

    @JsonProperty("udrt")
    public boolean UseDefaultReminderTime;

    @JsonProperty("drt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "CET")
    public Date DefaultReminderTime;

    public GroupModel() {
    }

    public GroupModel(GroupModel group) {
        Id = group.Id;
        Name = group.Name;
        Description = group.Description;
        IsQuickAccess = group.IsQuickAccess;
        UseDefaultReminderTime = group.UseDefaultReminderTime;
        DefaultReminderTime = group.DefaultReminderTime;
    }
}
