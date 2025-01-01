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
    Role[] allowedEmployeeRoles = {Role.ADMIN, Role.EMPLOYEE};
    Role[] allowedUserRoles = {Role.ADMIN, Role.EMPLOYEE, Role.REGULAR};
    Role[] allowedGuestRoles = {Role.ADMIN, Role.EMPLOYEE, Role.REGULAR, Role.GUEST};

    @Bean
    public FilterRegistrationBean<RoleFilter> addAdminFilter() {
        return createFilter("/admin/*", allowedAdminRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addEmployeeFilter() {
        return createFilter("/employee/*", allowedEmployeeRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addRegularUserFilter() {
        return createFilter("/user/*", allowedUserRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addGuestFilter() {
        return createFilter("/guest/*", allowedGuestRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addPublicProductControllerFilter() {
        return createFilter("/product/public/*", allowedGuestRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addSecuredProductControllerFilter() {
        return createFilter("/product/secured/*", allowedAdminRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addUserDietControllerFilter() {
        return createFilter("/diet/users/*", allowedUserRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addSecuredDietControllerFilter() {
        return createFilter("/diet/secured/*", allowedEmployeeRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addUserFoodControllerFilter() {
        return createFilter("/food/users/*", allowedUserRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addSecuredFoodControllerFilter() {
        return createFilter("/food/secured/*", allowedAdminRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addUserOrderControllerFilter() {
        return createFilter("/order/public/*", allowedGuestRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addSecuredOrderControllerFilter() {
        return createFilter("/order/secured/*", allowedAdminRoles);
    }

    @Bean
    public FilterRegistrationBean<RoleFilter> addPublicControllerFilter() {
        return createFilter("/stripe/*", allowedGuestRoles);
    }

    private FilterRegistrationBean<RoleFilter> createFilter(String urlPattern, Role[] allowedRoles) {
        FilterRegistrationBean<RoleFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RoleFilter(jwt, allowedRoles));
        registrationBean.addUrlPatterns(urlPattern);
        registrationBean.setOrder(3);
        return registrationBean;
    }



}
