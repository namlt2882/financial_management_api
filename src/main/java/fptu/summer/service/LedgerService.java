/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import fptu.summer.dao.LedgerDAO;
import fptu.summer.model.Ledger;
import fptu.summer.model.User;
import fptu.summer.model.enumeration.LedgerStatus;
import fptu.summer.utils.DataValidateException;
import java.util.Date;
import java.util.HashMap;
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
            //filter ledger that don't have local id
            l = l.stream().filter(ledger -> ledger.getLocalId() != null).collect(Collectors.toList());
            User user = new User(username);
            if (validate(user)) {
                l.parallelStream().forEach(ledger -> {
                    ledger.setUserId(user.getId());
                });
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

    public List<Ledger> updateLedgers(String username, List<Ledger> list) {
        List<Ledger> originalList = null;
        LedgerDAO ledgerDAO = new LedgerDAO();
        try {
            list = list.stream().filter(ledger -> ledger.getId() != null).collect(Collectors.toList());
            List<Long> ids = list.stream().map(ledger -> ledger.getId()).collect(Collectors.toList());
            //get original ledger
            originalList = ledgerDAO.findByIds(ids);
            //copy
            copyLedgers(list, originalList);
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
        return originalList;
    }

    public List<Ledger> disableLedgers(String username, List<Ledger> l) {
        List<Ledger> originalList = null;
        LedgerDAO ledgerDAO = new LedgerDAO();
        try {
            l = l.stream().filter(ledger -> ledger.getId() != null).collect(Collectors.toList());
            //get original ledger
            List<Long> ids = l.stream().map(ledger -> ledger.getId()).collect(Collectors.toList());
            originalList = ledgerDAO.findByIds(ids);
            originalList = originalList.stream()
                    .filter(ledger -> ledger.getStatus() == LedgerStatus.ENABLE.getStatus())
                    .collect(Collectors.toList());
            //set status
            l.parallelStream().forEach(ledger -> ledger.setStatus(LedgerStatus.DISABLE.getStatus()));
            //set last update time
            Map<Long, Ledger> tmpMap = new HashMap<>();
            l.forEach(ledger -> tmpMap.put(ledger.getId(), ledger));
            originalList.parallelStream().forEach(des -> {
                Ledger src = tmpMap.get(des.getId());
                des.setLastUpdate(src.getLastUpdate());
            });
            User user = new User(username);
            if (!originalList.isEmpty()) {
                if (validate(user)) {
                    ledgerDAO.update(originalList);
                } else {
                    throw new Exception("unable to disable ledger for this user");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
        return originalList;
    }

    public List<Ledger> enableLedgers(String username, List<Ledger> l) {
        List<Ledger> originalList = null;
        LedgerDAO ledgerDAO = new LedgerDAO();
        try {
            l = l.stream().filter(ledger -> ledger.getId() != null).collect(Collectors.toList());
            //get original ledger
            List<Long> ids = l.stream().map(ledger -> ledger.getId()).collect(Collectors.toList());
            originalList = ledgerDAO.findByIds(ids);
            originalList = originalList.stream()
                    .filter(ledger -> ledger.getStatus() == LedgerStatus.DISABLE.getStatus())
                    .collect(Collectors.toList());
            //set status
            l.parallelStream().forEach(ledger -> ledger.setStatus(LedgerStatus.ENABLE.getStatus()));
            //set last update time
            Map<Long, Ledger> tmpMap = new HashMap<>();
            l.forEach(ledger -> tmpMap.put(ledger.getId(), ledger));
            originalList.parallelStream().forEach(des -> {
                Ledger src = tmpMap.get(des.getId());
                des.setLastUpdate(src.getLastUpdate());
            });
            User user = new User(username);
            if (!originalList.isEmpty()) {
                if (validate(user)) {
                    ledgerDAO.update(originalList);
                } else {
                    throw new Exception("unable to disable ledger for this user");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
        return originalList;
    }

    public void copyLedgers(List<Ledger> lsrc, List<Ledger> ldes) {
        Map<Long, Ledger> map = new HashMap<>();
        lsrc.forEach(ledger -> map.put(ledger.getId(), ledger));
        ldes.parallelStream().forEach(des -> {
            Ledger src = map.get(des.getId());
            copyLedger(src, des, src.getLastUpdate());
        });
    }

    private void copyLedger(Ledger src, Ledger des, Date updateTime) {
        des.setName(src.getName());
        des.setCountedOnReport(src.isCountedOnReport());
        des.setCurrency(src.getCurrency());
        des.setLastUpdate(updateTime);
    }
}
