/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import fptu.summer.model.TransactionGroup;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author ADMIN
 */
public class LedgerTransactionGroup implements Serializable {

    @JsonProperty("server_id")
    private Long id;

    private Set<TransactionGroup> transactionGroups = new HashSet(0);

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<TransactionGroup> getTransactionGroups() {
        return this.transactionGroups;
    }

    public void setTransactionGroups(Set<TransactionGroup> transactionGroups) {
        this.transactionGroups = transactionGroups;
    }

}
