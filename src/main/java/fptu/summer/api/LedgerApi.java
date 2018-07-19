/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.api;

import fptu.summer.model.Ledger;
import fptu.summer.service.LedgerService;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
@RequestMapping("/ledger")
public class LedgerApi {

    @Secured({"ROLE_USER"})
    @GetMapping
    public Set<Ledger> getUserLedger(Authentication auth, @RequestParam(required = false) Long lastUpdate) {
        Set<Ledger> result = null;
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        LedgerService ledgerService = new LedgerService();
        if (lastUpdate == null) {
            result = ledgerService.findByUsername(username);
        } else {
            Date lastUpdateTime = new Date(lastUpdate);
            result = ledgerService.findByLastUpdate(username, lastUpdateTime);
        }
        return result;
    }

    @Secured({"ROLE_USER"})
    @PostMapping
    public List<Ledger> addLedgers(Authentication auth, @RequestBody Ledger[] ledgerList) {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        return new LedgerService().createNewLedgers(username, Arrays.asList(ledgerList));
    }

    @Secured({"ROLE_USER"})
    @PutMapping
    public void updateLedgers(Authentication auth, @RequestBody Ledger[] ledgerList) {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        new LedgerService().updateLedgers(username, Arrays.asList(ledgerList));
    }

}
