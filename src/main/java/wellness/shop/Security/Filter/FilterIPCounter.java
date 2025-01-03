package wellness.shop.Security.Filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import wellness.shop.Integration.RabbitMQ;

import java.io.IOException;

public class FilterIPCounter implements Filter {

    private final RabbitMQ rabbitMQIP;

    public FilterIPCounter(String rabbitHost) {
        this.rabbitMQIP = new RabbitMQ(rabbitHost);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String ipAddress = servletRequest.getRemoteAddr();
        rabbitMQIP.sendObjectToQueue(ipAddress, "IP");
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }


}
