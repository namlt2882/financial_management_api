/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.api;

import fptu.summer.dto.NotificationDto;
import fptu.summer.service.NotificationService;
import java.util.Date;
import java.util.List;
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
@RequestMapping("noti")
public class NotificationApi {

    @Secured({"ROLE_USER"})
    @GetMapping
    public List<NotificationDto> findByLastUpdate(Authentication auth, @RequestParam Long lastUpdate) {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        return new NotificationService().findByLastUpdate(username, new Date(lastUpdate));
    }

    @Secured({"ROLE_USER"})
    @PostMapping(value = "/readed")
    public void checkNotificationReaded(Authentication auth, @RequestBody List<Long> dtos) {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        new NotificationService().checkNotificationReaded(username, dtos);
    }
}
