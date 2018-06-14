/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import fptu.summer.dao.UserDAO;
import fptu.summer.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author ADMIN
 */
@Service
public class AuthenticationService {

    @Autowired
    UserDAO userDAO;

    public boolean checkLogin(User user) {
        return userDAO.checkLogin(user) != null;
    }
}
