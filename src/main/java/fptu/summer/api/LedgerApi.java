/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.api;

import fptu.summer.model.Ledger;
import fptu.summer.service.LedgerService;
import java.util.Arrays;
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
    public Set<Ledger> getUserLedger(Authentication auth) {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        return new LedgerService().findByUsername(username);
    }

    @Secured({"ROLE_USER"})
    @PostMapping
    public List<Ledger> addLedgers(Authentication auth, @RequestBody List<Ledger> ledgerList) {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        return new LedgerService().createNewLedgers(username, ledgerList);
    }

    @Secured({"ROLE_USER"})
    @PutMapping
    public List<Ledger> updateLedgers(Authentication auth, @RequestBody List<Ledger> ledgerList) {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        return new LedgerService().updateLedgers(username, ledgerList);
    }

    @Secured({"ROLE_USER"})
    @PostMapping(value = "/disable")
    public List<Ledger> disableLedgers(Authentication auth, @RequestParam String ledgerIds) {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        List<Long> ids = Arrays.asList(ledgerIds.split(",")).parallelStream()
                .map(s -> Long.parseLong(s.trim()))
                .collect(Collectors.toList());
        return new LedgerService().disableLedgers(username, ids);
    }

    @Secured({"ROLE_USER"})
    @PostMapping(value = "/enable")
    public List<Ledger> enableLedgers(Authentication auth, @RequestParam String ledgerIds) {
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        List<Long> ids = Arrays.asList(ledgerIds.split(",")).parallelStream()
                .map(s -> Long.parseLong(s.trim()))
                .collect(Collectors.toList());
        return new LedgerService().enableLedgers(username, ids);
    }

}
