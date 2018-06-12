/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.api;

import fptu.summer.model.User;
import fptu.summer.model.UserSetting;
import fptu.summer.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("user")
public class UserApi {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/register", produces = {MediaType.APPLICATION_JSON_VALUE})
    public User addNewUser(@RequestBody User user) {
        User result = userService.createNewUser(user);
        return result;
    }

    @PostMapping(value = "/{username}")
    public String updateUser(@RequestBody User user) {
        String result = "success";
        userService.updateUser(user);
        return result;
    }

    @PostMapping(value = "/{username}/setPassword", produces = {MediaType.TEXT_PLAIN_VALUE})
    public String changePassword(@PathVariable("username") String username, @RequestParam("password") String password) {
        String result = "Successfully!";
        userService.changeUserPassword(username, password);
        return result;
    }

    @GetMapping(value = "/{username}/settings", produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserSetting getUserSettingByUsername(@PathVariable("username") String username) {
        return userService.getSettingByUsername(username);
    }

    @PostMapping(value = "/{username}/settings", produces = {MediaType.TEXT_PLAIN_VALUE})
    public UserSetting updateUserSetting(@PathVariable("username") String username,
            @RequestParam("monthStartDate") int monthStartDate,
            @RequestParam("timeFormat") String timeFormat) {
        UserSetting setting = new UserSetting();
        setting.setMonthStartDate(monthStartDate);
        setting.setTimeFormat(timeFormat);
        return userService.updateUserSetting(username, setting);
    }

}
