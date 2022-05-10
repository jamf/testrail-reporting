package com.jamf.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import java.util.Date;
import java.util.StringJoiner;

public class Project {

  private int id;
  private String name;
  private String announcement;
  @JsonProperty("show_announcement")
  private Boolean showAnnouncement;
  @JsonProperty("is_completed")
  private Boolean isCompleted;
  @JsonProperty("completed_on")
  private Date completedOn;
  private String url;
  @JsonProperty("suite_mode")
  private Integer suiteMode;

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAnnouncement() {
    return announcement;
  }

  public void setAnnouncement(String announcement) {
    this.announcement = announcement;
  }

  public Boolean getShowAnnouncement() {
    return showAnnouncement;
  }

  public void setShowAnnouncement(Boolean showAnnouncement) {
    this.showAnnouncement = showAnnouncement;
  }

  public Boolean getCompleted() {
    return isCompleted;
  }

  public void setCompleted(Boolean completed) {
    this.isCompleted = completed;
  }

  public Date getCompletedOn() {
    return completedOn;
  }

  public void setCompletedOn(Date completedOn) {
    this.completedOn = completedOn;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Integer getSuiteMode() {
    return suiteMode;
  }

  public void setSuiteMode(Integer suiteMode) {
    this.suiteMode = suiteMode;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Project.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("name='" + name + "'")
        .add("announcement='" + announcement + "'")
        .add("showAnnouncement=" + showAnnouncement)
        .add("isCompleted=" + isCompleted)
        .add("completedOn=" + completedOn)
        .add("url='" + url + "'")
        .add("suiteMode=" + suiteMode)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Project project = (Project) o;
    return id == project.id && Objects.equal(name, project.name) && Objects.equal(announcement, project.announcement) && Objects.equal(showAnnouncement, project.showAnnouncement) && Objects.equal(
        isCompleted, project.isCompleted) && Objects.equal(completedOn, project.completedOn) && Objects.equal(url, project.url) && Objects.equal(suiteMode, project.suiteMode);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, name, announcement, showAnnouncement, isCompleted, completedOn, url, suiteMode);
  }
}
