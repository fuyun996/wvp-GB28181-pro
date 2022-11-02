package com.genersoft.iot.vmp.conf.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于动态判断用户可以访问哪些接口
 */
@Component("rbacService")
public class RBACService {
    public boolean hasPermission(HttpServletRequest request, Authentication authentication) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;

            String requestURI = request.getRequestURI();
            System.out.println(requestURI);
            String[] split = requestURI.split("/");
            String auth = split[split.length - 1];
            SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(auth);
            boolean contains = userDetails.getAuthorities().contains(simpleGrantedAuthority);
            System.out.println(contains);
            return contains;
        }

        return false;
    }
}
