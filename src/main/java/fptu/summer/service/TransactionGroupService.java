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
    
    public List<TransactionGroup> addNewTransactionGroup(List<Ledger> ledgers) {
        //filter unknown ledgers
        //filter transaction group which don't have local id
        List<Ledger> toCreateList = new LinkedList<>();
        ledgers.parallelStream().filter(l -> l.getId() != null).forEach(ledger -> {
            toCreateList.add(ledger);
            Set<TransactionGroup> tmp = ledger.getTransactionGroups();
            tmp = tmp.stream().filter(tg -> tg.getLocalId() != null).collect(Collectors.toSet());
            ledger.setTransactionGroups(tmp);
            //set ledger id for transaction group objects
            tmp.forEach(tranc -> {
                tranc.setLedger(ledger);
            });
        });
        List<TransactionGroup> result = toCreateList.stream()
                .flatMap(ledger -> ledger.getTransactionGroups().stream())
                .filter(tg -> tg.getLocalId() != null)
                .collect(Collectors.toList());
        TransactionGroupDAO transactionGroupDAO = new TransactionGroupDAO();
        //insert to db
        transactionGroupDAO.insert(result);
        return result;
    }
    
    public void updateTransactionGroup(List<TransactionGroup> syncDatas) {
        TransactionGroupDAO transactionGroupDAO = new TransactionGroupDAO();
        //remove unknown transaction group
        //get original transaction group from db
        List<Long> ids = new LinkedList<>();
        syncDatas = syncDatas.stream().filter(tg -> {
            if (tg.getId() != null) {
                ids.add(tg.getId());
                return true;
            }
            return false;
        }).collect(Collectors.toList());
        List<TransactionGroup> originalList = transactionGroupDAO.findByIds(ids);
        //copy transaction group
        originalList = validateTrancGroups(syncDatas, originalList);
        //update
        transactionGroupDAO.update(originalList);
    }
    
    public List<TransactionGroup> validateTrancGroups(List<TransactionGroup> lsync, List<TransactionGroup> lorigin) {
        List<TransactionGroup> result = new LinkedList<>();
        Map<Long, TransactionGroup> tmpMap = new HashMap<>();
        lsync.forEach(tg -> tmpMap.put(tg.getId(), tg));
        lorigin.forEach(origin -> {
            TransactionGroup syncData = tmpMap.get(origin.getId());
            if (syncData.getLastUpdate().getTime() > origin.getLastUpdate().getTime()) {
                result.add(origin);
                copyTransactionGroup(syncData, origin);
            }
        });
        return result;
    }
    
    public static void copyTransactionGroup(TransactionGroup src, TransactionGroup des) {
        des.setName(src.getName());
        des.setStatus(src.getStatus());
        des.setTransactionType(src.getTransactionType());
        des.setLastUpdate(src.getLastUpdate());
    }
    
    public static List<LedgerTransactionGroup> convertToTransactionGroupView(List<Ledger> origin) {
        List<LedgerTransactionGroup> result = new LinkedList<>();
        origin.forEach(l -> {
            LedgerTransactionGroup ltg = new LedgerTransactionGroup();
            ltg.setId(l.getId());
            ltg.setTransactionGroups(l.getTransactionGroups());
            ltg.getTransactionGroups().forEach(tg -> {
                tg.setLedger(null);
            });
            result.add(ltg);
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
