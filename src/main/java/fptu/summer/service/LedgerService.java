/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import fptu.summer.dao.LedgerDAO;
import fptu.summer.model.Ledger;
import fptu.summer.model.User;
import fptu.summer.utils.DataValidateException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author ADMIN
 */
public class LedgerService extends BaseAuthenticatedService {

    public Set<Ledger> findByUsername(String username) {
        LedgerDAO ledgerDAO = new LedgerDAO();
        return ledgerDAO.findByUsername(username);
    }

    public Set<Ledger> findByLastUpdate(String username, Date lastUpdate) {
        LedgerDAO ledgerDAO = new LedgerDAO();
        return ledgerDAO.findByLastUpdate(username, lastUpdate);
    }

    public List<Ledger> createNewLedgers(String username, List<Ledger> l) {
        try {
            User user = new User(username);
            if (validate(user)) {
                //filter ledger that don't have local id
                l = l.stream().filter(ledger -> {
                    if (ledger.getLocalId() != null) {
                        ledger.setUserId(user.getId());
                        return true;
                    }
                    return false;
                }).collect(Collectors.toList());
                LedgerDAO ledgerDAO = new LedgerDAO();
                ledgerDAO.insert(l);
            } else {
                throw new Exception("unable to update ledger for this user");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException("Insert ledger fail!");
        }
        return l;
    }

    public void updateLedgers(String username, List<Ledger> list) {
        List<Ledger> originalList = null;
        LedgerDAO ledgerDAO = new LedgerDAO();
        try {
            List<Long> ids = new ArrayList<>();
            list = list.stream().filter(ledger -> {
                if (ledger.getId() != null) {
                    ids.add(ledger.getId());
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
            //get original ledger
            originalList = ledgerDAO.findByIds(ids);
            //copy
            originalList = validateLedger(list, originalList);
            //update
            User user = new User(username);
            if (!originalList.isEmpty()) {
                if (validate(user)) {
                    ledgerDAO.update(originalList);
                } else {
                    throw new Exception("unable to update ledger for this user");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
    }

    public List<Ledger> validateLedger(List<Ledger> lsync, List<Ledger> lorigin) {
        List<Ledger> result = new LinkedList<>();
        Map<Long, Ledger> map = new HashMap<>();
        lsync.forEach(ledger -> map.put(ledger.getId(), ledger));
        lorigin.parallelStream().forEach(origin -> {
            Ledger sync = map.get(origin.getId());
            if (sync.getLastUpdate().getTime() > origin.getLastUpdate().getTime()) {
                result.add(origin);
                copyLedger(sync, origin, sync.getLastUpdate());
            }
        });
        return result;
    }

    private void copyLedger(Ledger src, Ledger des, Date updateTime) {
        des.setName(src.getName());
        des.setCountedOnReport(src.isCountedOnReport());
        des.setCurrency(src.getCurrency());
        des.setLastUpdate(updateTime);
        des.setStatus(src.getStatus());
    }
}
