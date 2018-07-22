/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.api;

import fptu.summer.dto.LedgerTransaction;
import fptu.summer.model.Ledger;
import fptu.summer.model.Transaction;
import static fptu.summer.service.TransactionService.convertToLedger;
import fptu.summer.service.TransactionService;
import java.util.Date;
import java.util.List;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("/transaction")
public class TransactionApi {
    
    @Secured({"ROLE_USER"})
    @GetMapping
    public List<Transaction> getTransactionGroup(Authentication auth, @RequestParam Long lastUpdate) {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        List<Transaction> result = new TransactionService().findByLastUpdate(username, new Date(lastUpdate));
        removeUnnecessaryElements(result);
        return result;
    }
    
    @Secured({"ROLE_USER"})
    @PostMapping
    public List<Transaction> addNewTransaction(@RequestBody List<LedgerTransaction> input) {
        List<Ledger> ll = convertToLedger(input);
        List<Transaction> result = new TransactionService().addNewTransaction(ll);
        removeUnnecessaryElements(result);
        return result;
    }
    
    @Secured({"ROLE_USER"})
    @PutMapping
    public void updateTransactions(@RequestBody List<Transaction> input) {
        new TransactionService().updateTransactions(input);
    }
    
    public void removeUnnecessaryElements(List<Transaction> transactions) {
        transactions.stream().forEach(transaction -> {
            transaction.getTransactionGroup().setLedger(null);
        });
    }
}
