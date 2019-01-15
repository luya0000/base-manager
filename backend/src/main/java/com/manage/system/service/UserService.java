package com.manage.system.service;

import com.manage.common.Constants;
import com.manage.system.bean.DepartBean;
import com.manage.system.bean.UserBean;
import com.manage.system.dao.UserMapper;
import com.manage.system.dao.UserRoleMapper;
import com.manage.system.model.SysDepartDto;
import com.manage.system.model.SysUserDto;
import com.manage.system.model.SysUserRoleDto;
import org.apache.commons.lang.StringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by luya on 2018/6/8.
 */
@Service
public class UserService {

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserMapper userMapper;

    /*获取用户带权限列表*/
    @Transactional(readOnly = true)
    public List<UserBean> selectUserList(String account, List<Integer> depart, Integer status) throws Exception {
        // 设置模糊查询参数
        account = StringUtils.isBlank(account) ? null : "%" + account.trim() + "%";
        //status = status == null ? Constants.STATUE_INVALID : status;
        List<SysUserDto> userDtos = userMapper.selectAll(account, depart, status);
        List<UserBean> userList = new ArrayList<>();
        if (userDtos != null) {
            for (SysUserDto dto : userDtos) {
                UserBean bean = new UserBean();
                BeanUtils.copyProperties(dto, bean);
                List<Integer> roles = userRoleService.getRolesIdByParam(bean.getUserId(), null);
                bean.setRoles(roles);
                userList.add(bean);
            }
        }
        return userList;
    }

    @Transactional(readOnly = true)
    public UserBean selectByPrimaryKey(String userId) throws Exception {
        SysUserDto userDto = userMapper.selectByPrimaryKey(userId);
        UserBean userBean = new UserBean();
        if (userDto != null) {
            BeanUtils.copyProperties(userDto, userBean);
        }
        return userBean;
    }

    /*获取用户带角色和权限*/
    @Transactional(readOnly = true)
    public UserBean selectUserAndRoleByAccount(String account) throws Exception {
        // 获取用户信息
        SysUserDto userDto = userMapper.selectByUserId(account);

        if (userDto != null) {
            UserBean userBean = new UserBean();
            BeanUtils.copyProperties(userDto, userBean);
            // 获取角色
            List<Integer> roles = userRoleService.getRolesIdByParam(userBean.getUserId(), null);
            userBean.setRoles(roles);
            return userBean;
        }
        return null;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int insertUser(UserBean userBean) throws Exception {
        SysUserDto userDto = new SysUserDto();
        BeanUtils.copyProperties(userBean, userDto);
        userDto.setPassword(BCrypt.hashpw(userBean.getPassword(), BCrypt.gensalt()));
        userDto.setStatus(Constants.STATUE_VALID);
        return userMapper.insert(userDto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updateUser(UserBean userBean) throws Exception {

        SysUserDto userDto = new SysUserDto();
        BeanUtils.copyProperties(userBean, userDto);
        if (StringUtils.isEmpty(userBean.getPassword())) {
            userDto.setPassword(null);
        } else {
            userDto.setPassword(BCrypt.hashpw(userBean.getPassword(), BCrypt.gensalt()));
        }
        return userMapper.updateByPrimaryKey(userDto);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int deleteUser(String userId) throws Exception {
        // 删除用户角色信息
        userRoleService.delUserRoleByUserId(userId, null);
        // 删除用户信息
        return userMapper.deleteByPrimaryKey(userId);
    }

    /**
     * 修改密码,self为true修改自己密码，否则修改他人密码
     *
     * @param userId
     * @param oldPassword
     * @param newPassword
     * @param self
     * @return
     * @throws Exception
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean changePassword(String userId, String oldPassword, String newPassword, boolean self) throws Exception {
        UserBean user = selectByPrimaryKey(userId);
        if (self) {
            if (BCrypt.checkpw(oldPassword, user.getPassword())) {
                return userMapper.changePassword(userId, BCrypt.hashpw(newPassword, BCrypt.gensalt())) == 1;
            }
        } else {
            return userMapper.changePassword(userId, BCrypt.hashpw(newPassword, BCrypt.gensalt())) == 1;
        }
        return false;
    }
}
