/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.dao;

import fptu.summer.model.Ledger;
import fptu.summer.model.Transaction;
import fptu.summer.model.TransactionGroup;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADMIN
 */
public class TransactionGroupDAO extends DAO {

    public List<TransactionGroup> insert(List<TransactionGroup> l) {
        try {
            begin();
            l.forEach(tranc -> getSession().save(tranc));
            commit();
            return l;
        } catch (Exception e) {
            rollback();
            throw e;
        } finally {
            close();
        }
    }
    
    public List<TransactionGroup> update(List<TransactionGroup> l) {
        try {
            begin();
            l.forEach(tg -> getSession().update(tg));
            commit();
            return l;
        } catch (Exception e) {
            rollback();
            throw e;
        } finally {
            close();
        }
    }

    public List<TransactionGroup> findByLastUpdate(Date lastUpdate, List<Long> ledgerIds) {
        try {
            if (ledgerIds.isEmpty()) {
                return new LinkedList<>();
            }
            return getSession().createCriteria(TransactionGroup.class)
                    .add(Restrictions.gt("lastUpdate", lastUpdate))
                    .add(Restrictions.in("ledger.id", ledgerIds)).list();
        } finally {
            close();
        }
    }

    public List<TransactionGroup> findByIds(List<Long> ids) {
        try {
            if (ids.isEmpty()) {
                return new LinkedList<>();
            }
            List<TransactionGroup> result = getSession()
                    .createCriteria(TransactionGroup.class)
                    .add(Restrictions.in("id", ids)).list();
            return result;
        } finally {
            close();
        }
    }
}
