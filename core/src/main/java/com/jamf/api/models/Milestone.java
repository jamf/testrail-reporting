package com.jamf.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import java.util.Date;

public class Milestone {

  private int id;
  //used in updating and adding
  private String name;
  //used in updating and adding
  private String description;
  @JsonProperty("project_id")
  private int projectId;
  //used in updating and adding
  @JsonProperty("due_on")
  private Date dueOn;
  //used in updating
  @JsonProperty("is_completed")
  private Boolean isCompleted;
  @JsonProperty("is_started")
  private Boolean isStarted;

  public int getId() {
    return id;
  }

  public Milestone setId(int id) {
    this.id = id;
    return this;
  }

  public String getName() {
    return name;
  }

  public Milestone setName(String name) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public Milestone setDescription(String description) {
    this.description = description;
    return this;
  }

  public int getProjectId() {
    return projectId;
  }

  public Milestone setProjectId(int projectId) {
    this.projectId = projectId;
    return this;
  }

  public Date getDueOn() {
    return dueOn;
  }

  public Milestone setDueOn(Date dueOn) {
    this.dueOn = dueOn;
    return this;
  }

  public Boolean getCompleted() {
    return isCompleted;
  }

  public Milestone setCompleted(Boolean completed) {
    isCompleted = completed;
    return this;
  }

  public Boolean getStarted() {
    return isStarted;
  }

  public Milestone setStarted(Boolean started) {
    isStarted = started;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Milestone milestone = (Milestone) o;
    return id == milestone.id && projectId == milestone.projectId && Objects.equal(name, milestone.name) && Objects.equal(description, milestone.description) && Objects.equal(dueOn, milestone.dueOn)
        && Objects.equal(isCompleted, milestone.isCompleted) && Objects.equal(isStarted, milestone.isStarted);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, name, description, projectId, dueOn, isCompleted, isStarted);
  }
}
