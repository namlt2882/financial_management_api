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
import java.util.HashSet;
import java.util.Set;
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
            //insert user
            Set<Role> roles = user.getRoles();
            user.setRoles(null);
            User result = userDAO.insert(user);
            //insert user setting
            userDAO.addSetting(user, new UserSetting());
            //insert role for user
            roles.stream().forEach(r -> {
                if (r.getName().equals(fptu.summer.model.enumeration.Role.ADMIN.name())) {
                    throw new DataValidateException("Can not create admin!");
                }
                userDAO.addRoles(user.getId(), r.getId());
            });
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e);
        }
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
