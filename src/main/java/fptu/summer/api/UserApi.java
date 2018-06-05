/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.api;

import fptu.summer.dto.UserWrapper;
import fptu.summer.model.User;
import fptu.summer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ADMIN
 */
@RestController
//@RequestMapping("/user")
public class UserApi {
    @Autowired
    private UserService userService;
    @RequestMapping(name = "/add", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public User addNewUser(@RequestBody UserWrapper userWrapper) {
        User result = userService.createNewUser(userService.convertWrapperToUser(userWrapper));
        return result;
    }
}
