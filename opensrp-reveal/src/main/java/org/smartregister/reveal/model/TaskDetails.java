package org.smartregister.reveal.model;

import android.location.Location;
import android.support.annotation.NonNull;

/**
 * Created by samuelgithengi on 3/20/19.
 */
public class TaskDetails implements Comparable<TaskDetails> {

    private String taskId;

    private String taskCode;

    private String taskEntity;

    private String businessStatus;

    private Location location;

    private String structureName;

    private String familyName;

    private float distanceFromUser;

    public TaskDetails(@NonNull String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(String taskCode) {
        this.taskCode = taskCode;
    }

    public String getTaskEntity() {
        return taskEntity;
    }

    public void setTaskEntity(String taskEntity) {
        this.taskEntity = taskEntity;
    }

    public String getBusinessStatus() {
        return businessStatus;
    }

    public void setBusinessStatus(String businessStatus) {
        this.businessStatus = businessStatus;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getStructureName() {
        return structureName;
    }

    public void setStructureName(String structureName) {
        this.structureName = structureName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public float getDistanceFromUser() {
        return distanceFromUser;
    }

    public void setDistanceFromUser(float distanceFromUser) {
        this.distanceFromUser = distanceFromUser;
    }

    @Override
    public int compareTo(@NonNull TaskDetails other) {
        return Double.compare(distanceFromUser, other.getDistanceFromUser());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TaskDetails))
            return false;
        TaskDetails other = (TaskDetails) obj;
        return taskId.equals(other.getTaskId());
    }
}
