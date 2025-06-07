package com.wzu.exam.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // 获取Authorization头
            String authHeader = request.getHeader("Authorization");
            String requestURI = request.getRequestURI();
            
            log.debug("处理请求: {}, Authorization头: {}", requestURI, authHeader);
            
            // 如果没有Authorization头或者不是以Bearer开头，直接放行，由后续的过滤器处理
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.debug("没有有效的Authorization头，请求将作为匿名处理: {}", requestURI);
                filterChain.doFilter(request, response);
                return;
            }
            
            // 提取JWT令牌
            String jwt = authHeader.substring(7);
            log.debug("提取到JWT令牌: {}", jwt);
            
            try {
                // 从JWT中提取用户名
                String username = jwtService.extractUsername(jwt);
                log.debug("从JWT中提取的用户名: {}", username);
                
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // 加载用户详情
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                    log.debug("已加载用户 {} 的详情，角色: {}", username, userDetails.getAuthorities());
                    
                    // 验证令牌
                    if (jwtService.validateToken(jwt)) {
                        // 从JWT中提取角色信息
                        String role = jwtService.extractRole(jwt);
                        log.debug("从JWT中提取的角色 {}: {}", username, role);
                        
                        // 创建认证令牌
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        
                        // 设置认证详情
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // 设置认证信息到SecurityContext
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        log.info("用户认证成功: {}, 角色: {}", username, role);
                    } else {
                        log.warn("JWT令牌验证失败，用户: {}", username);
                    }
                }
            } catch (Exception e) {
                log.error("处理JWT令牌时出错: {}", e.getMessage());
            }
        } catch (Exception e) {
            log.error("JWT过滤器处理中出现异常", e);
        }

        filterChain.doFilter(request, response);
    }
}
