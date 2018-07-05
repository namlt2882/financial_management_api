/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.dao;

import fptu.summer.model.Role;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class RoleDAO extends DAO {

    public RoleDAO() {
        super();
    }

    public List<Role> findAll() {
        try {
            return getSession().createCriteria(Role.class).list();
        } finally {
            close();
        }
    }

    public List<Role> findByList(List<Integer> l) {
        try {
            if (l.isEmpty()) {
                return new LinkedList<>();
            }
            return getSession()
                    .createQuery("select r from Role r where r.id in (:ids)")
                    .setParameterList("ids", l).list();
        } finally {
            close();
        }
    }

}
