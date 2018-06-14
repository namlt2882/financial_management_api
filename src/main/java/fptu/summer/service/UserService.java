/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import fptu.summer.dao.RoleDAO;
import fptu.summer.dao.UserDAO;
import fptu.summer.dto.UserWrapper;
import fptu.summer.model.Role;
import fptu.summer.model.User;
import fptu.summer.model.UserSetting;
import fptu.summer.utils.DataValidateException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ADMIN
 */
@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private RoleDAO roleDAO;

    public UserService() {
    }

    public User createNewUser(User user) {
        try {
            //insert role for user
            List<Role> roles = roleDAO.findAll();
            roles.stream().filter(r -> r.getName().equalsIgnoreCase(fptu.summer.model.enumeration.Role.USER.name())).findAny().ifPresent(r -> {
                user.getRoles().add(r);
            });
            //insert user setting
            UserSetting userSetting = new UserSetting();
            user.setUserSetting(userSetting);
            userSetting.setUser(user);
            //insert user
            User result = userDAO.insert(user);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
    }

    public void updateUser(User user) {
        try {
            User tmp = userDAO.findByUsername(user.getUsername());
            if (tmp != null) {
                tmp.setFirstName(user.getFirstName());
                tmp.setLastName(user.getLastName());
                tmp.setBirthday(user.getBirthday());
                userDAO.update(tmp);
            } else {
                throw new DataValidateException("The user is not existed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
    }

    public void changeUserPassword(String username, String password) {
        try {
            User tmp = userDAO.findByUsername(username);
            if (tmp != null) {
                tmp.setPassword(password);
                tmp.setLastUpdate(new Date());
                userDAO.update(tmp);
            } else {
                throw new DataValidateException("The user is not existed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
    }

    public UserSetting updateUserSetting(String username, UserSetting userSetting) {
        try {
            UserSetting tmp = userDAO.findSettingByUsername(username);
            if (tmp != null) {
                tmp.setMonthStartDate(userSetting.getMonthStartDate());
                tmp.setTimeFormat(userSetting.getTimeFormat());
                tmp.setLastUpdate(new Date());
                userDAO.updateSetting(tmp);
                return tmp;
            } else {
                throw new DataValidateException("The user is not existed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
    }

    public UserSetting getSettingByUsername(String username) {
        try {
            UserSetting tmp = userDAO.findSettingByUsername(username);
            if (tmp != null) {
                return tmp;
            } else {
                throw new DataValidateException("The user is not existed!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
    }

    public User findUserByUsername(String username) {
        return userDAO.findByUsername(username);
    }

    public User convertWrapperToUser(UserWrapper wrapper) {
        User user = new User();
        user.setId(wrapper.getId());
        user.setUsername(wrapper.getUsername());
        user.setFirstName(wrapper.getFirstName());
        user.setLastName(wrapper.getLastName());
        user.setPassword(wrapper.getPassword());
        user.setBirthday(wrapper.getBirthday());
        user.setInsertDate(wrapper.getInsertDate());
        user.setLastUpdate(wrapper.getLastUpdate());
        user.setStatus(wrapper.getStatus());
        user.setUserSetting(wrapper.getUserSetting());
        user.setRoles(new HashSet(roleDAO.findByList(wrapper.getRoles())));
        return user;
    }
}
