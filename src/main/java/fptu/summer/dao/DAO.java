/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author ADMIN
 */
@Repository
public abstract class DAO {

    @Autowired
    private SessionFactory sessionFactory;

    private Session session;

    public DAO() {
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    protected Session getSession() {
        if (session == null) {
            session = sessionFactory.openSession();
        }
        return session;
    }

    protected void close() {
        if (session != null) {
            session.close();
            session = null;
        }
    }

    protected Transaction begin() {
        return (Transaction) getSession().beginTransaction();
    }

    protected void commit() {
        getSession().getTransaction().commit();
    }

    protected void rollback() {
        getSession().getTransaction().rollback();
    }
}
