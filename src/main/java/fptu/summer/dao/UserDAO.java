/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.dao;

import fptu.summer.model.Role;
import fptu.summer.model.User;
import fptu.summer.model.UserSetting;
import java.util.Set;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ADMIN
 */
//@Repository
public class UserDAO extends DAO {

    public UserDAO() {
        super();
    }

    public User insert(User user) {
        try {
            begin();
            getSession().save(user);
            commit();
            return user;
        } catch (Exception e) {
            rollback();
            throw e;
        } finally {
            close();
        }
    }

    public void addRoles(int userId, int roleId) {
        try {
            begin();
            User user = (User) getSession().get(User.class, userId);
            Role role = (Role) getSession().get(Role.class, roleId);
            user.getRoles().add(role);
            getSession().save(user);
            commit();
        } catch (Exception e) {
            rollback();
            throw e;
        } finally {
            close();
        }
    }
    
    public void addSetting(User user, UserSetting us){
        try {
            begin();
            us.setUser(user);
            getSession().save(us);
            commit();
        } catch (Exception e) {
        }
    }
}
