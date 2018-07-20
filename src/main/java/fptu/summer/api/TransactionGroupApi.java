/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.api;

import fptu.summer.dto.LedgerTransactionGroup;
import fptu.summer.model.Ledger;
import fptu.summer.model.TransactionGroup;
import fptu.summer.service.TransactionGroupService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import static fptu.summer.service.TransactionGroupService.convertToLedger;
import static fptu.summer.service.TransactionGroupService.convertToTransactionGroupView;
import java.util.Arrays;
import java.util.Date;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("tranc_group")
public class TransactionGroupApi {

    @Secured({"ROLE_USER"})
    @GetMapping
    public List<TransactionGroup> getTransactionGroup(Authentication auth, @RequestParam Long lastUpdate) {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        return new TransactionGroupService().findByLastUpdate(username, new Date(lastUpdate));
    }

    @Secured({"ROLE_USER"})
    @PostMapping
    public List<TransactionGroup> addNewGroup(@RequestBody LedgerTransactionGroup[] input) {
        List<Ledger> ll = convertToLedger(Arrays.asList(input));
        List<TransactionGroup> rs = new TransactionGroupService().addNewTransactionGroup(ll);
        return rs;
    }

    @Secured({"ROLE_USER"})
    @PutMapping
    public void updateGroups(@RequestBody TransactionGroup[] input) {
        new TransactionGroupService().updateTransactionGroup(Arrays.asList(input));
    }

}
