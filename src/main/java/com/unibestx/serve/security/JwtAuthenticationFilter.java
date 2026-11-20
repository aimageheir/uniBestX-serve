package com.unibestx.serve.security;

import com.unibestx.serve.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /** 令牌请求头，直接放原始 token，不带 Bearer 前缀 */
    private static final String ACCESS_TOKEN_HEADER = "accessToken";
    private static final String SECRET = "unibestx-serve-default-secret";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        if (token != null && JwtUtil.verify(token, SECRET)) {
            Long userId = JwtUtil.getUserId(token);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 取 token：直接读 {@code accessToken} 请求头，值就是原始 token。
     *
     * @return token，取不到返回 {@code null}
     */
    private String resolveToken(HttpServletRequest request) {
        String accessToken = request.getHeader(ACCESS_TOKEN_HEADER);
        return StringUtils.hasText(accessToken) ? accessToken : null;
    }
}
