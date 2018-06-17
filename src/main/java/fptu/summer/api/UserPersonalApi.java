/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.api;

import fptu.summer.config.SecurityUser;
import fptu.summer.model.User;
import fptu.summer.model.UserSetting;
import fptu.summer.service.UserService;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
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
public class UserPersonalApi {

    @Autowired
    private UserService userService;

    @Secured({"ROLE_USER"})
    @GetMapping(value = "/")
    public User getUser(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userService.findUserByUsername(username);
    }

//    @PreAuthorize(value = "hasAnyRole({'USER'})")
    @Secured({"ROLE_USER"})
    @PostMapping(value = "/")
    public String updateUser(Authentication authentication, @RequestBody User user) {
        String result = "success";
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        user.setUsername(username);
        userService.updateUser(user);
        return result;
    }

    @Secured({"ROLE_USER"})
    @PostMapping(value = "/setPassword", produces = {MediaType.TEXT_PLAIN_VALUE})
    public String changePassword(Authentication authentication, @RequestParam("password") String password) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        String result = "Successfully!";
        userService.changeUserPassword(username, password);
        return result;
    }

    @Secured({"ROLE_USER"})
    @GetMapping(value = "/settings", produces = {MediaType.APPLICATION_JSON_VALUE})
    public UserSetting getUserSettingByUsername(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        return userService.getSettingByUsername(username);
    }

    @Secured({"ROLE_USER"})
    @PostMapping(value = "/settings", produces = {MediaType.TEXT_PLAIN_VALUE})
    public UserSetting updateUserSetting(Authentication authentication, @RequestParam("monthStartDate") int monthStartDate,
            @RequestParam("timeFormat") String timeFormat) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        UserSetting setting = new UserSetting();
        setting.setMonthStartDate(monthStartDate);
        setting.setTimeFormat(timeFormat);
        return userService.updateUserSetting(username, setting);
    }
}
