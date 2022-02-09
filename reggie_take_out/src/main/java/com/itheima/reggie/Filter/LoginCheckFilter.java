package com.itheima.reggie.Filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter
@Slf4j
public class LoginCheckFilter implements Filter {
    // 路径匹配器, 支持通配符
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     *  配置网页拦截器
     * @param servletRequest
     * @param servletResponse
     * @param filterChain
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 请求访问的路径
        String requestURI = request.getRequestURI();

        log.info("拦截到请求地址: {}", requestURI);        // 日志

        // 添加不需要处理的路径
        String[] allowedURL = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/email/getCode",
                "/user/sendMsg",
                "/user/login"
        };

        // 判断请求的路径是否需要处理
        if(checkURL(allowedURL, requestURI)){       // 方法结果返回true放行
            filterChain.doFilter(request, response);        //放行
            return;
        }

        // 判断后台用户是否已经登录
        if(request.getSession().getAttribute("employee") != null){      // 方法结果返回true放行
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("employee"));      //保存后端管理用户id到线程局部变量, 方便在其他方法中使用
            filterChain.doFilter(request, response);        //放行
            return;
        }

        // 判断客户端用户是否已经登录
        if(request.getSession().getAttribute("user") != null){      // 方法结果返回true放行
            BaseContext.setCurrentId((Long)request.getSession().getAttribute("user"));      //保存前台用户id到线程局部变量, 方便在其他方法中使用
            filterChain.doFilter(request, response);        //放行
            return;
        }

        // 不满足上述条件响应页面没有登录信息
        log.info("没有登录过的用户, 该访问被拦截!");      //日志
        String notLogin = new ObjectMapper().writeValueAsString(R.error("NOTLOGIN"));       //将数据转为 json格式
        response.getWriter().write(notLogin);       //使用response将信息响应会页面

    }

    /**
     * 自定义方法
     *  此方法检查请求地址是否需要处理
     * @param allowedURL
     * @param requestURL
     * @return 返回检查结果 true表示可以放行
     */
    private boolean checkURL(String[] allowedURL, String requestURL) {
        // 遍历所有被允许的访问路径
        for (String url : allowedURL) {
            if(pathMatcher.match(url, requestURL)){     // 使用路径匹配器
                return true;
            }
        }
        return false;
    }
}
