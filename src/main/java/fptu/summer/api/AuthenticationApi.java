/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.api;

import fptu.summer.model.User;
import fptu.summer.service.AuthenticationService;
import fptu.summer.service.JwtService;
import fptu.summer.service.UserService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("")
public class AuthenticationApi {

    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    UserService userService;

    @Autowired
    private JwtService jwtService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {
        String result = "";
        HttpStatus httpStatus = null;
        try {
            User user = new User();
            user.setUsername(username.toUpperCase());
            user.setPassword(password);
            if (authenticationService.checkLogin(user)) {
                user = userService.findUserByUsername(username);
                result = jwtService.generateTokenLogin(user);
                httpStatus = HttpStatus.OK;
            } else {
                result = "Wrong userId and password";
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception ex) {
            result = "Server Error";
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<String>(result, httpStatus);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public ResponseEntity<String> logout(@RequestHeader(name = "Authorization") String token) {
        String result = "success";
        HttpStatus httpStatus = null;
        try {
            if (jwtService.validateTokenLogin(token)) {
                jwtService.invalidateToken(token);
                httpStatus = HttpStatus.OK;
            } else {
                result = "token is not existed";
                httpStatus = HttpStatus.BAD_REQUEST;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = e.getMessage();
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return new ResponseEntity<String>(result, httpStatus);
    }
}
