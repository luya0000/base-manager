package com.manage.system.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luya on 2018/6/5.
 */
public class UserRoleBean implements Serializable{

    /*账号名称*/
    private Integer departId;
    /*用户名*/
    private String departName;
    /*用户组Id*/
    private List<RoleBean> roleBeanList = new ArrayList<>();

    public Integer getDepartId() {
        return departId;
    }

    public void setDepartId(Integer departId) {
        this.departId = departId;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public List<RoleBean> getRoleBeanList() {
        return roleBeanList;
    }

    public void setRoleBeanList(List<RoleBean> roleBeanList) {
        this.roleBeanList = roleBeanList;
    }
}
