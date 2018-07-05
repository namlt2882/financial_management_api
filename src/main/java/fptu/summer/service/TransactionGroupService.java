/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import fptu.summer.dao.LedgerDAO;
import fptu.summer.dao.TransactionGroupDAO;
import fptu.summer.dto.LedgerTransactionGroup;
import fptu.summer.model.Ledger;
import fptu.summer.model.TransactionGroup;
import fptu.summer.model.enumeration.TransactionGroupStatus;
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
public class TransactionGroupService extends BaseAuthenticatedService {

    public List<TransactionGroup> findByLastUpdate(String username, Date lastUpdate) {
        Set<Ledger> ledger = new LedgerDAO().findByUsername(username);
        List<Long> ledgerIds = ledger.stream().map(l -> l.getId()).collect(Collectors.toList());
        return new TransactionGroupDAO().findByLastUpdate(lastUpdate, ledgerIds);
    }

    public List<Ledger> addNewGroup(List<Ledger> ledgers) {
        LedgerDAO ledgerDao = new LedgerDAO();
        //filter unknown ledgers
        ledgers = ledgers.stream().filter(l -> l.getId() != null).collect(Collectors.toList());
        //filter transaction groups which don't have local id
        ledgers.forEach(l -> {
            Set<TransactionGroup> tmp = l.getTransactionGroups();
            tmp = tmp.stream().filter(tg -> tg.getLocalId() != null).collect(Collectors.toSet());
            l.setTransactionGroups(tmp);
        });

        List<Long> ledgerIds = ledgers.stream().map(l -> l.getId()).collect(Collectors.toList());
        //get original ledgers
        List<Ledger> originLedgers = ledgerDao.findByIds(ledgerIds);
        //add new transaction group
        Map<Long, Ledger> map = new HashMap<>();
        for (Ledger ledger : ledgers) {
            map.put(ledger.getId(), ledger);
        }
        originLedgers.parallelStream().forEach(des -> {
            Ledger src = map.get(des.getId());
            addTransactionGroup(src.getTransactionGroups(), des.getTransactionGroups(), des);
        });
        //insert new transaction groups
        ledgerDao.update(originLedgers);
        return ledgers;
    }

    public List<TransactionGroup> updateTransactionGroup(List<TransactionGroup> ltg) {
        TransactionGroupDAO transactionGroupDAO = new TransactionGroupDAO();
        //remove unknown transaction group
        ltg = ltg.stream().filter(tg -> tg.getId() != null).collect(Collectors.toList());
        //get original transaction group from db
        List<Long> ids = ltg.stream().map(tg -> tg.getId()).collect(Collectors.toList());
        List<TransactionGroup> originalList = transactionGroupDAO.findByIds(ids);
        //copy transaction group
        Map<Long, TransactionGroup> tmpMap = new HashMap<>();
        originalList.forEach(tg -> tmpMap.put(tg.getId(), tg));
        ltg.forEach(src -> {
            TransactionGroup des = tmpMap.get(src.getId());
            copyTransactionGroup(src, des);
        });
        //update
        transactionGroupDAO.updateTransactionGroup(originalList);
        //return updated list
        originalList.forEach(tg -> {
            tg.setLedger(null);
        });
        return originalList;
    }

    public List<TransactionGroup> disableTransactionGroup(String username, List<Long> ids) {
        TransactionGroupDAO transactionGroupDAO = new TransactionGroupDAO();
        //get original transaction group from db
        List<TransactionGroup> originalList = transactionGroupDAO.findByIds(ids);
        //filter groups which have disable status
        originalList = originalList.stream()
                .filter(tg -> tg.getStatus() != TransactionGroupStatus.DISABLE.getStatus())
                .collect(Collectors.toList());
        //set status
        Date currentTime = new Date();
        originalList.forEach(tg -> {
            tg.setStatus(TransactionGroupStatus.DISABLE.getStatus());
            tg.setLastUpdate(currentTime);
        });
        //update
        transactionGroupDAO.updateTransactionGroup(originalList);
        //return updated list
        originalList.forEach(tg -> {
            tg.setLedger(null);
        });
        return originalList;
    }

    public static void copyTransactionGroup(TransactionGroup src, TransactionGroup des) {
        des.setName(src.getName());
        des.setStatus(src.getStatus());
        des.setTransactionType(src.getTransactionType());
        des.setLastUpdate(src.getLastUpdate());
    }

    private void addTransactionGroup(Set<TransactionGroup> src, Set<TransactionGroup> des, Ledger ledger) {
        Date curTime = new Date();
        src.stream().forEach(tg -> {
            tg.setInsertDate(curTime);
            tg.setLastUpdate(curTime);
            tg.setLedger(ledger);
            des.add(tg);
        });
    }

    public static List<LedgerTransactionGroup> convertToTransactionGroupView(List<Ledger> origin) {
        List<LedgerTransactionGroup> result = new LinkedList<>();
        origin.forEach(l -> {
            LedgerTransactionGroup ltg = new LedgerTransactionGroup();
            ltg.setId(l.getId());
            ltg.setTransactionGroups(l.getTransactionGroups());
            result.add(ltg);
        });
        result.stream().map(ltg -> ltg.getTransactionGroups()).flatMap(tgl -> tgl.stream()).forEach(tg -> {
            tg.setLedger(null);
        });
        return result;
    }

    public static List<Ledger> convertToLedger(List<LedgerTransactionGroup> origin) {
        List<Ledger> result = new ArrayList<>();
        origin.forEach(l -> {
            Ledger lg = new Ledger();
            lg.setId(l.getId());
            lg.setTransactionGroups(l.getTransactionGroups());
            result.add(lg);
        });
        return result;
    }

}
