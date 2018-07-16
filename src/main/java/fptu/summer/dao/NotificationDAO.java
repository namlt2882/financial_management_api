/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.dao;

import fptu.summer.model.Notification;
import fptu.summer.model.User;
import fptu.summer.model.UserNotification;
import java.util.Date;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import static org.hibernate.criterion.Restrictions.and;
import static org.hibernate.criterion.Restrictions.or;
import static org.hibernate.criterion.Restrictions.eq;

/**
 *
 * @author ADMIN
 */
public class NotificationDAO extends DAO {

    public void update(List<Notification> notifications) {
        try {
            begin();
            notifications.forEach(noti -> getSession().update(noti));
            commit();
        } catch (Exception e) {
            rollback();
            throw e;
        } finally {
            close();
        }
    }

    public List<Notification> findByLastUpdate(String username, Date lastUpdate) {
        try {
            User user = (User) getSession().createCriteria(User.class)
                    .add(Restrictions.eq("username", username)).uniqueResult();
            return getSession().createCriteria(Notification.class, "noti")
                    .createAlias("noti.userNotifications", "userNoti", Criteria.LEFT_JOIN)
                    .add(or(eq("noti.isSystemNotification", true),
                            and(eq("noti.isSystemNotification", false), eq("noti.userId", user.getId()))))
                    .add(Restrictions.gt("noti.lastUpdate", lastUpdate))
//                    .add(eq("userNoti.userId", user.getId()))
                    .addOrder(Order.desc("noti.lastUpdate"))
                    .setFetchMode("noti.userNoti", FetchMode.JOIN)
                    .setMaxResults(50)
                    .list();
        } finally {
            close();
        }
    }

    public void updateUserNotification(List<UserNotification> l) {
        try {
            begin();
            l.forEach(usrNoti -> getSession().saveOrUpdate(usrNoti));
            commit();
        } catch (Exception e) {
            rollback();
            throw e;
        } finally {
            close();
        }
    }

}
