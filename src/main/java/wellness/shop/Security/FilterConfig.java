package wellness.shop.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wellness.shop.Models.Users.Enums.Role;
import wellness.shop.Security.Filter.RoleFilter;
import wellness.shop.Security.Filter.FilterBannedIPs;
import wellness.shop.Security.Filter.FilterIPCounter;

@Configuration
public class FilterConfig {


    @Value("${redis.host}")
    private String host;
    @Value("${redis.port}")
    private int port;

    @Bean
    public FilterRegistrationBean<FilterBannedIPs> addFilterIPAddress() {
        FilterRegistrationBean<FilterBannedIPs> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FilterBannedIPs(host,port));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }


    @Value("${rabbit.host}")
    private String rabbitHost;
    @Bean
    public FilterRegistrationBean<FilterIPCounter> addFilterRabbitMQ() {
        FilterRegistrationBean<FilterIPCounter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new FilterIPCounter(rabbitHost));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Autowired
    JWT jwt;
    Role[] allowedAdminRoles = {Role.ADMIN};
    Role[] allowedEmployeeRoles = {Role.ADMIN,Role.EMPLOYEE};
    Role[] allowedUserRoles = {Role.ADMIN,Role.EMPLOYEE,Role.REGULAR};
    Role[] allowedGuestRoles = {Role.ADMIN,Role.EMPLOYEE,Role.REGULAR,Role.GUEST};
    @Bean
    public FilterRegistrationBean<RoleFilter> addAdminFilter() {
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RoleFilter(jwt,allowedAdminRoles));
        registrationBean.addUrlPatterns("/admin/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }


    @Bean
    public FilterRegistrationBean<RoleFilter> addEmployeeFilter() {
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RoleFilter(jwt,allowedEmployeeRoles));
        registrationBean.addUrlPatterns("/employee/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addRegularUserFilter() {
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RoleFilter(jwt,allowedUserRoles));
        registrationBean.addUrlPatterns("/user/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }


    @Bean
    public FilterRegistrationBean<RoleFilter> addGuestFilter() {
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RoleFilter(jwt,allowedGuestRoles));
        registrationBean.addUrlPatterns("/guest/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addPublicProductControllerFilter() {
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RoleFilter(jwt,allowedGuestRoles));
        registrationBean.addUrlPatterns("/product/public/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addSecuredProductControllerFilter() {
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RoleFilter(jwt,allowedAdminRoles));
        registrationBean.addUrlPatterns("/product/secured/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }





}
