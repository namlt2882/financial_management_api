/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fptu.summer.config;

import fptu.summer.model.User;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 *
 * @author ADMIN
 */
public class SecurityUser implements UserDetails {

    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;
    private User userInfo;//khong bat buoc

    public SecurityUser() {
    }

    public SecurityUser(String username, Collection<? extends GrantedAuthority> authorities, boolean enabled, User userInfo) {
        this.username = username;
        this.authorities = authorities;
        this.enabled = enabled;
        this.userInfo = userInfo;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public User getUserInfo() {
        return userInfo;
    }

    @Override
    public String getPassword() {
        return null;
    }

}
