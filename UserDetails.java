package de.zeroco.apm.configuration;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import de.zeroco.apm.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Component
public class UserDetails implements UserDetailsService {

	@Autowired
	UserService userService;
	@Autowired
	HttpSession session;
	
	@Override
	public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Map<String, Object> user = userService.getUserDataByValue(username);
		if (user == null) {
            throw new UsernameNotFoundException("Could not find user");
        }
//		session.setAttribute("id", user.get("pk_id"));
		user.put("username", username);
		return new UserInfo(user);
	}

}
