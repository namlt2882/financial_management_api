package fptu.summer.model;
// Generated Jun 3, 2018 2:37:40 PM by Hibernate Tools 4.3.1

import fptu.summer.model.id.UserNotificationId;
import java.util.Date;

/**
 * UserNotification generated by hbm2java
 */
public class UserNotification implements java.io.Serializable {

    private UserNotificationId id;
    private Notification notification;
    private boolean isReaded = false;
    private Date lastUpdate;

    public UserNotification() {
    }

    public UserNotification(UserNotificationId id, boolean isReaded, Date lastUpdate) {
        this.id = id;
        this.isReaded = isReaded;
        this.lastUpdate = lastUpdate;
    }

    public UserNotificationId getId() {
        return this.id;
    }

    public void setId(UserNotificationId id) {
        this.id = id;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public boolean isIsReaded() {
        return this.isReaded;
    }

    public void setIsReaded(boolean isReaded) {
        this.isReaded = isReaded;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

}
