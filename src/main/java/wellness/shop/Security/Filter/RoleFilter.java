package wellness.shop.Security.Filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Security.JWT;

import java.io.IOException;

public class RoleFilter implements Filter{

    private JWT jwt;
    Role[] allowedRoles;

    public RoleFilter(JWT jwt, Role[] allowedRoles) {
        this.jwt = jwt;
        this.allowedRoles = allowedRoles;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String authorizationHeader = ((HttpServletRequest) servletRequest).getHeader("Authorization");

        if(!jwt.checkRolesAndValidateToken(authorizationHeader,allowedRoles)){

            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);

    }


}
