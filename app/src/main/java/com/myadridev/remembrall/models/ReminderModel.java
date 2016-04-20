package com.myadridev.remembrall.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by adrien on 02/03/16.
 */
@JsonSerialize (as = ReminderModel.class)
public class ReminderModel {
    public int Id;
    public String Name;

    public String Description;

    public boolean Achieved;

    @JsonIgnore
    public Date NextReminderDate;

    @JsonProperty ("rd")
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "yyyy/MM/dd")
    public Date ReminderDate;

    @JsonProperty ("ucrt")
    public boolean UseCustomReminderTime;

    @JsonProperty ("crt")
    @JsonFormat (shape = JsonFormat.Shape.STRING, pattern = "HH:mm", timezone = "CET")
    public Date CustomReminderTime;

    @JsonProperty ("gId")
    public int GroupId;

    public ReminderModel() {
    }

    public ReminderModel(ReminderModel reminder) {
        Id = reminder.Id;
        Name = reminder.Name;
        Description = reminder.Description;
        GroupId = reminder.GroupId;
        ReminderDate = reminder.ReminderDate;
        UseCustomReminderTime = reminder.UseCustomReminderTime;
        CustomReminderTime = reminder.CustomReminderTime;
        NextReminderDate = reminder.NextReminderDate;
        Achieved = reminder.Achieved;
    }
}
