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

/**
 *
 * @author ADMIN
 */
public class UserService {

    public UserService() {
    }

    public User createNewUser(User user) {
        RoleDAO roleDAO = new RoleDAO();
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
            UserDAO userDAO = new UserDAO();
            User result = userDAO.insert(user);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
    }

    public void updateUser(User user) {
        UserDAO userDAO = new UserDAO();
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
        UserDAO userDAO = new UserDAO();
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
        UserDAO userDAO = new UserDAO();
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
        UserDAO userDAO = new UserDAO();
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
        UserDAO userDAO = new UserDAO();
        return userDAO.findByUsername(username);
    }

    public User convertWrapperToUser(UserWrapper wrapper) {
        RoleDAO roleDAO = new RoleDAO();
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
