/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.dao;

import fptu.summer.model.Role;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class RoleDAO extends DAO {

    public RoleDAO() {
        super();
    }

    public List<Role> findByList(List<Integer> list) {
        try {
            return getSession()
                    .createQuery("select r from Role r where r.id in (:ids)")
                    .setParameterList("ids", list).list();
        } finally {
            close();
        }
    }

}
