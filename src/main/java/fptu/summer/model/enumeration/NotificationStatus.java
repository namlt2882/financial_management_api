/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.model.enumeration;

/**
 *
 * @author ADMIN
 */
public enum NotificationStatus {
    DISABLE(0), ENABLE(1);
    private int status;

    NotificationStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
