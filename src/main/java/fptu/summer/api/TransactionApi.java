/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.api;

import fptu.summer.dto.LedgerTransaction;
import fptu.summer.dto.LedgerTransactionGroup;
import fptu.summer.model.Ledger;
import fptu.summer.model.Transaction;
import fptu.summer.model.TransactionGroup;
import fptu.summer.service.TransactionGroupService;
import static fptu.summer.service.TransactionService.convertToLedger;
import fptu.summer.service.TransactionService;
import static fptu.summer.service.TransactionService.convertToTransactionView;
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
        return new TransactionService().findByLastUpdate(username, new Date(lastUpdate));
    }

    @Secured({"ROLE_USER"})
    @PostMapping
    public List<Transaction> addNewTransaction(@RequestBody List<LedgerTransaction> input) {
        List<Ledger> ll = convertToLedger(input);
        return new TransactionService().addNewTransaction(ll);
    }

    @Secured({"ROLE_USER"})
    @PutMapping
    public List<Transaction> updateTransactions(@RequestBody List<Transaction> input) {
        return new TransactionService().updateTransactions(input);
    }

    @Secured({"ROLE_USER"})
    @PostMapping(value = "/disable")
    public List<Long> disableTransactions(@RequestBody List<Transaction> input) {
        TransactionService transactionService = new TransactionService();
        List<Transaction> rs = transactionService.disableTransactions(input);
        return transactionService.getIdList(rs);
    }
}
