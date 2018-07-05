/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.dao;

import fptu.summer.model.Ledger;
import fptu.summer.model.User;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADMIN
 */
public class LedgerDAO extends DAO {

    public List<Ledger> insert(List<Ledger> list) {
        try {
            begin();
            list.forEach(ledger -> getSession().save(ledger));
            commit();
            return list;
        } catch (Exception e) {
            rollback();
            throw e;
        } finally {
            close();
        }
    }

    public Ledger insert(Ledger ledger) {
        try {
            begin();
            getSession().save(ledger);
            commit();
            return ledger;
        } catch (Exception e) {
            rollback();
            throw e;
        } finally {
            close();
        }
    }

    public List<Ledger> update(List<Ledger> list) {
        try {
            begin();
            list.forEach(ledger -> getSession().update(ledger));
            commit();
            return list;
        } catch (Exception e) {
            rollback();
            throw e;
        } finally {
            close();
        }
    }

    public Ledger update(Ledger ledger) {
        try {
            begin();
            getSession().update(ledger);
            commit();
            return ledger;
        } catch (Exception e) {
            rollback();
            throw e;
        } finally {
            close();
        }
    }

    public Set<Ledger> findByUsername(String username) {
        try {
            List<User> l = getSession().createCriteria(User.class)
                    .add(Restrictions.eq("username", username)).list();
            if (!l.isEmpty()) {
                User user = l.get(0);
                List<Ledger> result = getSession().createCriteria(Ledger.class)
                        .add(Restrictions.eq("userId", user.getId())).list();
                return new HashSet<>(result);
            }
            return new HashSet<>();
        } finally {
            close();
        }
    }

    public Set<Ledger> findByLastUpdate(String username, Date lastUpdate) {
        try {
            List<User> l = getSession().createCriteria(User.class)
                    .add(Restrictions.eq("username", username)).list();
            if (!l.isEmpty()) {
                User user = l.get(0);
                List<Ledger> result = getSession().createCriteria(Ledger.class)
                        .add(Restrictions.eq("userId", user.getId()))
                        .add(Restrictions.gt("lastUpdate", lastUpdate)).list();
                return new HashSet<>(result);
            }
            return new HashSet<>();
        } finally {
            close();
        }
    }

    public List<Ledger> findByIds(List<Long> l) {
        try {
            if (l.isEmpty()) {
                return new LinkedList<>();
            }
            List<Ledger> result = getSession()
                    .createCriteria(Ledger.class)
                    .add(Restrictions.in("id", l)).list();
            return result;
        } finally {
            close();
        }
    }
}
