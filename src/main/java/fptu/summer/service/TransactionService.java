/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import fptu.summer.dao.LedgerDAO;
import fptu.summer.dao.TransactionDAO;
import fptu.summer.dto.LedgerTransaction;
import fptu.summer.model.Ledger;
import fptu.summer.model.Transaction;
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
public class TransactionService extends BaseAuthenticatedService {

    public List<Transaction> findByLastUpdate(String username, Date lastUpdate) {
        Set<Ledger> ledger = new LedgerDAO().findByUsername(username);
        List<Long> ledgerIds = ledger.stream().map(l -> l.getId()).collect(Collectors.toList());
        return new TransactionDAO().findByLastUpdate(lastUpdate, ledgerIds);
    }

    public List<Transaction> addNewTransaction(List<Ledger> ledgers) {
        //filter unknown ledgers
        ledgers = ledgers.stream().filter(l -> l.getId() != null).collect(Collectors.toList());
        //filter transaction which don't have local id and transaction group
        ledgers.parallelStream().forEach(l -> {
            Set<Transaction> tmp = l.getTransactions();
            tmp = tmp.stream()
                    .filter(tranc -> {
                        if (tranc.getLocalId() != null && tranc.getTransactionGroup() != null) {
                            //set ledger for transaction objects
                            tranc.setLedger(l);
                            return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toSet());
            l.setTransactions(tmp);
        });
        List<Transaction> result = ledgers.stream()
                .flatMap(ledger -> ledger.getTransactions().stream())
                .collect(Collectors.toList());
        TransactionDAO transactionDAO = new TransactionDAO();
        //insert to db
        transactionDAO.insert(result);
        return result;
    }

    public void updateTransactions(List<Transaction> syncDatas) {
        //filter unknown transaction and transactions which don't have local id
        List<Long> ids = new LinkedList<>();
        syncDatas = syncDatas.stream()
                .filter(tranc -> {
                    if (tranc.getId() != null) {
                        ids.add(tranc.getId());
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        //get original transaction
        TransactionDAO transactionDAO = new TransactionDAO();
        List<Transaction> originalTransactions = transactionDAO.findByIds(ids);
        //update transaction
        originalTransactions = validateTransactions(syncDatas, originalTransactions);
        //update to db
        transactionDAO.update(originalTransactions);
    }

    public List<Transaction> validateTransactions(List<Transaction> lsync, List<Transaction> lorigin) {
        List<Transaction> result = new LinkedList<>();
        Map<Long, Transaction> tmpMap = new HashMap<>();
        lsync.forEach(tg -> tmpMap.put(tg.getId(), tg));
        lorigin.forEach(origin -> {
            Transaction syncData = tmpMap.get(origin.getId());
            if (syncData.getLastUpdate().getTime() > origin.getLastUpdate().getTime()) {
                result.add(origin);
                copyTransaction(syncData, origin);
            }
        });
        return result;
    }

    public static void copyTransaction(Transaction src, Transaction des) {
        des.setBalance(src.getBalance());
        des.setCountedOnReport(src.isCountedOnReport());
        des.setNote(src.getNote());
        des.setDate(src.getDate());
        des.setLastUpdate(src.getLastUpdate());
        des.setLedger(src.getLedger());
        des.setTransactionGroup(src.getTransactionGroup());
        des.setStatus(src.getStatus());
    }

    public static List<Ledger> convertToLedger(List<LedgerTransaction> origin) {
        List<Ledger> result = new ArrayList<>();
        origin.forEach(l -> {
            Ledger lg = new Ledger();
            lg.setId(l.getId());
            lg.setTransactions(l.getTransactions());
            result.add(lg);
        });
        return result;
    }

    public static List<LedgerTransaction> convertToTransactionView(List<Ledger> origin) {
        List<LedgerTransaction> result = new LinkedList<>();
        origin.forEach(l -> {
            LedgerTransaction lt = new LedgerTransaction();
            lt.setId(l.getId());
            lt.setTransactions(l.getTransactions());
            result.add(lt);
        });
        result.stream().map(ltg -> ltg.getTransactions()).flatMap(tl -> tl.stream()).forEach(tranc -> {
            tranc.setLedger(null);
        });
        return result;
    }
}
