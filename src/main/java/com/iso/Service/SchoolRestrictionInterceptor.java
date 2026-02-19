package com.iso.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.iso.Model.User;
import com.iso.Repository.UserRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class SchoolRestrictionInterceptor implements HandlerInterceptor {

    @Autowired
    private UserRepository userRepo;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
        	User user = userRepo.findByEmailWithSchool(auth.getName());

            if (user != null && (user.getSchool() != null && !user.getSchool().isActive())) {
                // Redirect to an error page or logout
            	response.sendRedirect(request.getContextPath() + "/login?restricted");
            	return false;

            }
        }

        return true; // allow request
    }
}
