/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.dao;

import fptu.summer.model.Notification;
import fptu.summer.model.User;
import fptu.summer.model.UserNotification;
import fptu.summer.model.enumeration.NotificationStatus;
import java.util.Date;
import java.util.List;
import javax.persistence.criteria.Root;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import static org.hibernate.criterion.Restrictions.and;
import static org.hibernate.criterion.Restrictions.or;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.gt;
import static org.hibernate.criterion.Restrictions.isNotEmpty;
import org.hibernate.transform.Transformers;

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

    public List<Notification> findByLastUpdate(Integer userId, Date lastUpdate) {
        try {
            return getSession().createCriteria(Notification.class, "noti")
                    .createAlias("noti.userNotifications", "userNoti", Criteria.LEFT_JOIN)
                    .add(eq("noti.status", NotificationStatus.ENABLE.getStatus()))
                    .add(or(eq("noti.isSystemNotification", true),
                            and(eq("noti.isSystemNotification", false), eq("noti.userId", userId))))
                    .add(Restrictions.gt("noti.lastUpdate", lastUpdate))
                    .setFetchMode("noti.userNoti", FetchMode.JOIN)
                    .setMaxResults(50)
                    .list();
        } finally {
            close();
        }
    }

    public List<Notification> findUsrNotiByLastUpdate(Integer userId, Date lastUpdate, List<Long> ungetNotiIds) {
        try {
            Criteria criteria = getSession().createCriteria(Notification.class,"noti")
                    .setFetchMode("noti.userNoti", FetchMode.JOIN)
                    .createAlias("noti.userNotifications", "userNoti", Criteria.INNER_JOIN)
                    .add(eq("userNoti.id.userId", userId))
                    .add(gt("userNoti.lastUpdate", lastUpdate));
                    
            if (ungetNotiIds != null && !ungetNotiIds.isEmpty()) {
                criteria.add(Restrictions.not(Restrictions.in("userNoti.id.notificationId", ungetNotiIds)));
            }
//            //init user notification collection
            List<Notification> rs = criteria.list();
            rs.forEach(n -> n.getUserNotifications().size());
            return criteria.list();
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
