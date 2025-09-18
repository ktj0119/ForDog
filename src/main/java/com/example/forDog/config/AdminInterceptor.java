package com.example.forDog.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// 관리자 페이지 로그인 session 확인 및 처리(TJ)
@Component
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        HttpSession session = request.getSession(false);
        Object admin = (session != null) ? session.getAttribute("admin") : null;

        if (admin == null) {
            response.sendRedirect("/manager/login");
            return false;
        }

        return true;
    }
}
