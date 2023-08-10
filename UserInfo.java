package de.zeroco.apm.configuration;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UserInfo implements UserDetails {

	private static final long serialVersionUID = 1L;
	private String userName;
	private String password;
	private String role;
	
	public UserInfo(Map<String, Object> user) {
	    this.userName = (String) user.get("username");
	    this.password = (String) user.get("password");
	    this.role = (String) user.get("role");
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);
        return Arrays.asList(authority);
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return userName;
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
		return true;
	}

}
