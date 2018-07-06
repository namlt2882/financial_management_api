/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import fptu.summer.dao.LedgerDAO;
import fptu.summer.dao.TransactionDAO;
import fptu.summer.dao.TransactionGroupDAO;
import fptu.summer.dto.LedgerTransaction;
import fptu.summer.model.Ledger;
import fptu.summer.model.Transaction;
import fptu.summer.model.TransactionGroup;
import fptu.summer.model.enumeration.TransactionStatus;
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

    public List<Ledger> addNewTransaction(List<Ledger> ledgers) {
        //filter unknown ledgers
        ledgers = ledgers.stream().filter(l -> l.getId() != null).collect(Collectors.toList());
        //filter transaction which don't have local id
        Date currentTime = new Date();
        ledgers.parallelStream().forEach(l -> {
            Set<Transaction> tmp = l.getTransactions();
            tmp = tmp.stream().filter(tg -> tg.getLocalId() != null).collect(Collectors.toSet());
            l.setTransactions(tmp);
            //set insert date, last update time
            //set ledger id for transaction objects
            tmp.forEach(tranc -> {
                tranc.setLedger(l);
                tranc.setInsertDate(currentTime);
                tranc.setLastUpdate(currentTime);
            });
        });
        List<Transaction> result = ledgers.stream()
                .flatMap(ledger -> ledger.getTransactions().stream())
                .collect(Collectors.toList());
        TransactionDAO transactionDAO = new TransactionDAO();
        //insert to db
        transactionDAO.insert(result);
        return ledgers;
    }

    public List<Transaction> updateTransactions(List<Transaction> l) {
        //filter unknown transaction and transactions which don't have local id
        l = filterTransactions(l);
        //get original transaction
        List<Long> ids = getIdList(l);
        TransactionDAO transactionDAO = new TransactionDAO();
        List<Transaction> originalTransactions = transactionDAO.findByIds(ids);
        //update transaction
        Map<Long, Transaction> tmpMap = new HashMap<>();
        originalTransactions.forEach(tranc -> tmpMap.put(tranc.getId(), tranc));
        l.forEach(src -> {
            Transaction des = tmpMap.get(src.getId());
            copyTransactions(src, des);
        });
        //update to db
        transactionDAO.update(originalTransactions);
        //return
        return originalTransactions;
    }

    public List<Transaction> disableTransactions(List<Transaction> l) {
        l = filterTransactions(l);
        List<Long> ids = getIdList(l);
        TransactionDAO transactionDAO = new TransactionDAO();
        List<Transaction> originalTransactions = transactionDAO.findByIds(ids);
        //set transaction status
        Map<Long, Transaction> tmpMap = new HashMap<>();
        originalTransactions.forEach(tranc -> tmpMap.put(tranc.getId(), tranc));
        l.forEach(src -> {
            Transaction des = tmpMap.get(src.getId());
            des.setStatus(TransactionStatus.DISABLE.getStatus());
            des.setLastUpdate(src.getLastUpdate());
        });
        //update to db
        transactionDAO.update(originalTransactions);
        //return
        return originalTransactions;
    }

    public List<Long> getIdList(List<Transaction> l) {
        return l.stream().map(tranc -> tranc.getId()).collect(Collectors.toList());
    }

    private List<Transaction> filterTransactions(List<Transaction> l) {
        return l.stream()
                .filter(tranc -> tranc.getId() != null)
                .collect(Collectors.toList());
    }

    public static void copyTransactions(Transaction src, Transaction des) {
        des.setBalance(src.getBalance());
        des.setCountedOnReport(src.isCountedOnReport());
        des.setNote(src.getNote());
        des.setDate(src.getDate());
        des.setLastUpdate(src.getLastUpdate());
        des.setLedger(src.getLedger());
        des.setTransactionGroup(src.getTransactionGroup());
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
