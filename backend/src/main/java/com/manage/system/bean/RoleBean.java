package com.manage.system.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class RoleBean implements Serializable{

    private Integer id;
    private String name;
    private Integer departId;
    private String departName;
    private String note;
    private List<Integer> permList; // 权限数组
    private String updateTime;
    private String updateUser;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDepartId() {
        return departId;
    }

    public void setDepartId(Integer departId) {
        this.departId = departId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Integer> getPermList() {
        return permList;
    }

    public void setPermList(List<Integer> permList) {
        this.permList = permList;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }
}
