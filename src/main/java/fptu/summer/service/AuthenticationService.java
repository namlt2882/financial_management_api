/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import fptu.summer.dao.UserDAO;
import fptu.summer.model.User;

/**
 *
 * @author ADMIN
 */
public class AuthenticationService {

    public boolean checkLogin(User user) {
        UserDAO userDAO = new UserDAO();
        return userDAO.checkLogin(user) != null;
    }
}
