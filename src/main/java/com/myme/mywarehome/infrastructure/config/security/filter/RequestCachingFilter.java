package com.myme.mywarehome.infrastructure.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
public class RequestCachingFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // 요청을 ContentCachingRequestWrapper로 감싸기
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);

        // 래핑된 요청을 다음 필터로 전달
        filterChain.doFilter(wrappedRequest, response);
    }
}
