package jp.co.kccs.greenearth.xform.code.jdbc.service;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

	@Override
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(5);      // Minimum threads
		executor.setMaxPoolSize(10);      // Max concurrent requests
		executor.setQueueCapacity(25);    // Requests in queue before rejecting
		executor.setThreadNamePrefix("AsyncExecutor-");
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (ex, method, params) -> {
			System.err.println("Async error in method: " + method.getName());
			ex.printStackTrace();
		};
	}
}
