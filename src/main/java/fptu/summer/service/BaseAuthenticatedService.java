/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import fptu.summer.dao.UserDAO;
import fptu.summer.model.User;
import fptu.summer.model.enumeration.UserStatus;

/**
 *
 * @author ADMIN
 */
public class BaseAuthenticatedService {

    protected boolean validate(User user) {
        UserDAO userDAO = new UserDAO();
        User tmp = userDAO.findByUsername(user.getUsername());
        user.setId(tmp.getId());
        if (user != null && user.getStatus() != UserStatus.DISABLE.getStatus()) {
            return true;
        }
        return false;
    }

}
