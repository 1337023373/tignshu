package com.atguigu.custom;

import com.atguigu.result.RetVal;
import com.atguigu.result.ResultCodeEnum;
import com.atguigu.util.ResponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) {

        ResponseUtil.out(response, RetVal.build(null, ResultCodeEnum.ACCOUNT_ERROR));
    }
}
