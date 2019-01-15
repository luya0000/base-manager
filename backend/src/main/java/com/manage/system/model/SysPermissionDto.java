package com.manage.system.model;

public class SysPermissionDto {
  private Integer id;
  private String note;
  private String name;
  private Integer permType;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getPermType() {
    return permType;
  }

  public void setPermType(Integer permType) {
    this.permType = permType;
  }
}
