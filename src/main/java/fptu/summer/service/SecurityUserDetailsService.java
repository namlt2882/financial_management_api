/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.service;

import fptu.summer.config.SecurityUser;
import fptu.summer.dao.UserDAO;
import fptu.summer.model.User;
import fptu.summer.model.enumeration.UserStatus;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author ADMIN
 */
@Service("securityUserDetailsService")
public class SecurityUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String string) throws UsernameNotFoundException {
        User user = userDAO.findByUsername(string);
        if (user != null) {
            return buildUserDetail(user);
        }
        throw new UsernameNotFoundException("Not found any username: " + string);
    }

    public UserDetails buildUserDetail(User user) {
        return new SecurityUser(user.getUsername(), getAuthorities(user), user.getStatus() == UserStatus.ENABLE.getStatus(), user);
    }

    private Set<GrantedAuthority> getAuthorities(User user) {
        Set<GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
        user.getRoles().forEach(r -> {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_" + r.getName());
            authorities.add(grantedAuthority);
        });
        return authorities;
    }
}
