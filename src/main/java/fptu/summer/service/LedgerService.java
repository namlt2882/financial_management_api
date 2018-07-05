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
            User user = new User(username);
            if (validate(user)) {
                Date currentTime = new Date();
                l.parallelStream().forEach(ledger -> {
                    ledger.setUserId(user.getId());
                    ledger.setInsertDate(currentTime);
                    ledger.setLastUpdate(currentTime);
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
        List<Ledger> ledgerList = null;
        LedgerDAO ledgerDAO = new LedgerDAO();
        try {
            list = list.stream().filter(ledger -> ledger.getId() != null).collect(Collectors.toList());
            List<Long> ids = list.stream().map(ledger -> ledger.getId()).collect(Collectors.toList());
            //get original ledger
            ledgerList = ledgerDAO.findByIds(ids);
            //copy
            copyLedgers(list, ledgerList);
            //update
            User user = new User(username);
            if (!ledgerList.isEmpty()) {
                if (validate(user)) {
                    ledgerDAO.update(ledgerList);
                } else {
                    throw new Exception("unable to update ledger for this user");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
        return ledgerList;
    }

    public List<Ledger> disableLedgers(String username, List<Long> ids) {
        List<Ledger> ledgerList = null;
        LedgerDAO ledgerDAO = new LedgerDAO();
        try {
            //get original ledger
            ledgerList = ledgerDAO.findByIds(ids);
            ledgerList = ledgerList.stream()
                    .filter(ledger -> ledger.getStatus() == LedgerStatus.ENABLE.getStatus())
                    .collect(Collectors.toList());
            setStatusLedgers(ledgerList, LedgerStatus.DISABLE);
            User user = new User(username);
            if (!ledgerList.isEmpty()) {
                if (validate(user)) {
                    ledgerDAO.update(ledgerList);
                } else {
                    throw new Exception("unable to disable ledger for this user");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
        return ledgerList;
    }

    public List<Ledger> enableLedgers(String username, List<Long> ids) {
        List<Ledger> ledgerList = null;
        LedgerDAO ledgerDAO = new LedgerDAO();
        try {
            //get original ledger
            ledgerList = ledgerDAO.findByIds(ids);
            ledgerList = ledgerList.stream()
                    .filter(ledger -> ledger.getStatus() == LedgerStatus.DISABLE.getStatus())
                    .collect(Collectors.toList());
            setStatusLedgers(ledgerList, LedgerStatus.ENABLE);
            User user = new User(username);
            if (!ledgerList.isEmpty()) {
                if (validate(user)) {
                    ledgerDAO.update(ledgerList);
                } else {
                    throw new Exception("unable to disable ledger for this user");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataValidateException(e.getMessage());
        }
        return ledgerList;
    }

    private void setStatusLedgers(List<Ledger> l, LedgerStatus ledgerStatus) {
        Date updateTime = new Date();
        l.parallelStream().forEach(ledger -> {
            ledger.setStatus(ledgerStatus.getStatus());
            ledger.setLastUpdate(updateTime);
        });
    }

    public static void processLedgersBeforeParse(Set<Ledger> l) {
        l.stream().flatMap(ledger -> ledger.getTransactionGroups().stream()).forEach(tg -> {
            tg.setLedger(null);
        });
    }

    public void copyLedgers(List<Ledger> lsrc, List<Ledger> ldes) {
        Date updateTime = new Date();
        Map<Long, Ledger> map = new HashMap<>();
        for (Ledger ledger : lsrc) {
            map.put(ledger.getId(), ledger);
        }
        ldes.parallelStream().forEach(ledger -> {
            Ledger tmp = map.get(ledger.getId());
            copyLedger(tmp, ledger, updateTime);
        });
    }

    private void copyLedger(Ledger src, Ledger des, Date updateTime) {
        des.setName(src.getName());
        des.setCountedOnReport(src.isCountedOnReport());
        des.setCurrency(src.getCurrency());
        des.setLastUpdate(updateTime);
    }
}
