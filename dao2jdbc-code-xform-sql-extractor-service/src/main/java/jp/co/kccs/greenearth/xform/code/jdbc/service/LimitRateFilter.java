package jp.co.kccs.greenearth.xform.code.jdbc.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
public class LimitRateFilter extends OncePerRequestFilter {
	private final GlobalRequestLimiter limiter;

	public LimitRateFilter(GlobalRequestLimiter limiter) {
		this.limiter = limiter;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
									HttpServletResponse response,
									FilterChain filterChain)
			throws ServletException, IOException {
		try {
			limiter.acquire();
			filterChain.doFilter(request, response);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			response.sendError(HttpStatus.SERVICE_UNAVAILABLE.value(), "Server is busy");
		} finally {
			limiter.release();
		}
	}
}
