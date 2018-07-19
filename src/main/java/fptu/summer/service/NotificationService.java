/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import fptu.summer.dao.NotificationDAO;
import fptu.summer.dto.NotificationDto;
import fptu.summer.model.Notification;
import fptu.summer.model.User;
import fptu.summer.model.UserNotification;
import fptu.summer.model.id.UserNotificationId;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author ADMIN
 */
public class NotificationService {

    public Date findLastUpdate(String username) {
        User user = new UserService().findUserByUsername(username);
        return new NotificationDAO().findLastUpdate(user.getId());
    }

    public List<NotificationDto> findByLastUpdate(String username, Date lastUpdate) {
        User user = new UserService().findUserByUsername(username);
        NotificationDAO notificationDAO = new NotificationDAO();
        List<Notification> notifications = notificationDAO.findByLastUpdate(user.getId(), user.getInsertDate(), lastUpdate);
        List<UserNotification> unmappedUserNotification = notifications.stream()
                .filter(noti -> noti.getUserNotifications() == null || noti.getUserNotifications().isEmpty())
                .map(noti -> {
                    Set<UserNotification> usrNotifications = new HashSet<>();
                    UserNotification tmp = new UserNotification();
                    tmp.setId(new UserNotificationId(user.getId(), noti.getId()));
                    tmp.setLastUpdate(noti.getLastUpdate());
                    usrNotifications.add(tmp);
                    noti.setUserNotifications(usrNotifications);
                    return tmp;
                }).collect(Collectors.toList());
        //check table user notification
        List<Long> ids = notifications.stream().map(noti -> noti.getId()).collect(Collectors.toList());
        List<Notification> notifications1 = notificationDAO.findUsrNotiByLastUpdate(user.getId(), user.getInsertDate(), lastUpdate, ids);
//                .stream()
//                .map(usrNoti -> usrNoti.getNotification()).collect(Collectors.toList());
        notifications1.forEach(noti -> notifications.add(noti));
        notificationDAO.updateUserNotification(unmappedUserNotification);
        return convertToDto(notifications);
    }

    public void checkNotificationReaded(String username, List<NotificationDto> dtos) {
        User user = new UserService().findUserByUsername(username);
        //filter unknown notification
        dtos = dtos.stream().filter(notiDto -> notiDto.getId() != null)
                .collect(Collectors.toList());
        //check notification read
        List<UserNotification> rs = dtos.stream()
                .map(dto -> new UserNotification(new UserNotificationId(user.getId(), dto.getId()), true, dto.getLastUpdate()))
                .collect(Collectors.toList());
        NotificationDAO notificationDAO = new NotificationDAO();
        notificationDAO.updateUserNotification(rs);
    }

    public static List<NotificationDto> convertToDto(List<Notification> notifications) {
        List<NotificationDto> rs = new LinkedList<>();
        notifications.forEach(noti -> {
            NotificationDto dto = new NotificationDto();
            dto.setId(noti.getId());
            dto.setTitle(noti.getTitle());
            dto.setContent(noti.getContent());
            dto.setInsertDate(noti.getInsertDate());
            dto.setStatus(noti.getStatus());
            dto.setIsSystemNotification(noti.isIsSystemNotification());

            UserNotification userNotification = noti.getUserNotifications().iterator().next();
            dto.setIsReaded(userNotification.isIsReaded());
            //set last update
            Date notiLastUpdate = noti.getLastUpdate();
            Date userLastUpdate = userNotification.getLastUpdate();
            if (userLastUpdate.getTime() > notiLastUpdate.getTime()) {
                dto.setLastUpdate(userLastUpdate);
            } else {
                dto.setLastUpdate(notiLastUpdate);
            }
            rs.add(dto);
        });
        return rs;
    }

    public static List<Notification> convertToNotification(List<NotificationDto> dtos) {
        List<Notification> rs = new LinkedList<>();
        dtos.forEach(noti -> {
            Notification dto = new Notification();
            dto.setId(noti.getId());
            dto.setTitle(noti.getTitle());
            dto.setContent(noti.getContent());
            dto.setInsertDate(noti.getInsertDate());
            dto.setLastUpdate(noti.getLastUpdate());
            dto.setStatus(dto.getStatus());
            dto.setIsSystemNotification(dto.isIsSystemNotification());
            //set user notification
            dto.setUserNotifications(null);
            rs.add(dto);
        });
        return rs;
    }
}
