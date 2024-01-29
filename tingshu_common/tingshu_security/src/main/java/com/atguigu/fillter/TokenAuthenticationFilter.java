package com.atguigu.fillter;


import com.alibaba.fastjson.JSON;
import com.atguigu.entity.SysUser;
import com.atguigu.result.RetVal;
import com.atguigu.result.ResultCodeEnum;
import com.atguigu.util.AuthContextHolder;
import com.atguigu.util.ResponseUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 身份验证过滤器
 * </p>
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private RedisTemplate redisTemplate;

    private String ADMIN_LOGIN_KEY_PREFIX = "admin:login:";
    private AntPathMatcher antPathMatcher = new AntPathMatcher();


    public TokenAuthenticationFilter(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        //如果是登录接口或非admin大头的直接放行
        String uri = request.getRequestURI();
        if(!request.getRequestURI().startsWith("/admin") || antPathMatcher.match("/admin/system/securityLogin/**", uri)) {
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if(null != authentication) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        } else {
            ResponseUtil.out(response, RetVal.build(null, ResultCodeEnum.PERMISSION));
        }
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        // token置于header里
        String token = request.getHeader("token");
        logger.info("token:"+token);
        if (!StringUtils.isEmpty(token)) {
            SysUser sysUser = (SysUser)redisTemplate.opsForValue().get(ADMIN_LOGIN_KEY_PREFIX+token);
            logger.info("sysUser:"+JSON.toJSONString(sysUser));
            if (null != sysUser) {
                AuthContextHolder.setUserId(sysUser.getId());
                AuthContextHolder.setUsername(sysUser.getUsername());

                if (null != sysUser.getUserPermsList() && sysUser.getUserPermsList().size() > 0) {
                    List<SimpleGrantedAuthority> authorities = sysUser.getUserPermsList().stream().filter(code -> !StringUtils.isEmpty(code.trim())).map(code -> new SimpleGrantedAuthority(code.trim())).collect(Collectors.toList());
                    return new UsernamePasswordAuthenticationToken(sysUser.getUsername(), null, authorities);
                } else {
                    return new UsernamePasswordAuthenticationToken(sysUser.getUsername(), null, new ArrayList<>());
                }
            }
        }
        return null;
    }
}
