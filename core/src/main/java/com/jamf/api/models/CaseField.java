package com.jamf.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseField {

  private int id;
  private String label;
  private String name;
  private String description;
  private String systemName;
  private int typeId;
  private String type;
  private int displayOrder;
  private String configs;

  public int getId() {
    return id;
  }

  public CaseField setId(int id) {
    this.id = id;
    return this;
  }

  public String getLabel() {
    return label;
  }

  public CaseField setLabel(String label) {
    this.label = label;
    return this;
  }

  public String getName() {
    return name;
  }

  public CaseField setName(String name) {
    this.name = name;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public CaseField setDescription(String description) {
    this.description = description;
    return this;
  }

  public String getSystemName() {
    return systemName;
  }

  public CaseField setSystemName(String systemName) {
    this.systemName = systemName;
    return this;
  }

  public int getTypeId() {
    return typeId;
  }

  public CaseField setTypeId(int typeId) {
    this.typeId = typeId;
    return this;
  }

  public String getType() {
    return type;
  }

  public CaseField setType(String type) {
    this.type = type;
    return this;
  }

  public int getDisplayOrder() {
    return displayOrder;
  }

  public CaseField setDisplayOrder(int displayOrder) {
    this.displayOrder = displayOrder;
    return this;
  }

  public String getConfigs() {
    return configs;
  }

  public CaseField setConfigs(String configs) {
    this.configs = configs;
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
    CaseField caseField = (CaseField) o;
    return id == caseField.id && typeId == caseField.typeId && displayOrder == caseField.displayOrder && Objects.equal(label, caseField.label) && Objects.equal(name, caseField.name) && Objects.equal(
        description, caseField.description) && Objects.equal(systemName, caseField.systemName) && Objects.equal(type, caseField.type) && Objects.equal(configs, caseField.configs);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id, label, name, description, systemName, typeId, type, displayOrder, configs);
  }
}
