/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fptu.summer.model.Transaction;
import fptu.summer.model.TransactionGroup;
import fptu.summer.model.enumeration.LedgerStatus;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ADMIN
 */
public class LedgerTransaction implements Serializable {

    @JsonProperty("server_id")
    private Long id;

    private Set<Transaction> transactions = new HashSet(0);

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

}
