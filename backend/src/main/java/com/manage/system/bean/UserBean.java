package com.manage.system.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luya on 2018/6/5.
 */
public class UserBean implements Serializable{

    private static final long serialVersionUID = 2690909283253774113L;

    /*账号名称*/
    private String userId;
    /*用户名*/
    private String userName;
    /*密码*/
    private String password;
    /*电话*/
    private String phone;
    /*性别*/
    private String sex;
    /*邮箱*/
    private String email;
    /*备注*/
    private String note;
    /*问题*/
    private String question;
    /*答案*/
    private String answer;
    /*用户状态，0有效；1无效*/
    private Integer status = 0;
    /*用户组Id*/
    private List<Integer> group;
    /*用户角色Id*/
    private List<Integer> roles = new ArrayList<>();
    /*最后修改人*/
    private String updateUser;
    /*最后修改时间*/
    private Timestamp updateTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Integer> getGroup() {
        return group;
    }

    public void setGroup(List<Integer> group) {
        this.group = group;
    }

    public List<Integer> getRoles() {
        return roles;
    }

    public void setRoles(List<Integer> roles) {
        this.roles = roles;
    }

    public String getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(String updateUser) {
        this.updateUser = updateUser;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
