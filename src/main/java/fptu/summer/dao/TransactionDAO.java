/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.dao;

import fptu.summer.model.Transaction;
import fptu.summer.model.TransactionGroup;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADMIN
 */
public class TransactionDAO extends DAO {

    public List<Transaction> insert(List<Transaction> l) {
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

    public List<Transaction> update(List<Transaction> l) {
        try {
            begin();
            l.forEach(tranc -> getSession().update(tranc));
            commit();
            return l;
        } catch (Exception e) {
            rollback();
            throw e;
        } finally {
            close();
        }
    }

    public List<Transaction> findByIds(List<Long> ids) {
        try {
            if (ids.isEmpty()) {
                return new LinkedList<>();
            }
            List<Transaction> result = getSession()
                    .createCriteria(Transaction.class)
                    .add(Restrictions.in("id", ids)).list();
            return result;
        } finally {
            close();
        }
    }

    public List<Transaction> findByLastUpdate(Date lastUpdate, List<Long> ledgerIds) {
        try {
            if (ledgerIds.isEmpty()) {
                return new LinkedList<>();
            }
            return getSession().createCriteria(Transaction.class)
                    .add(Restrictions.gt("lastUpdate", lastUpdate))
                    .add(Restrictions.in("ledger.id", ledgerIds)).list();
        } finally {
            close();
        }
    }
}
