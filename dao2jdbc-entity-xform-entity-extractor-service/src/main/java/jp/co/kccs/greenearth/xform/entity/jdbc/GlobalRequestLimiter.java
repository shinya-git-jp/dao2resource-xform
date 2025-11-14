package jp.co.kccs.greenearth.xform.entity.jdbc;

import org.springframework.stereotype.Component;

import java.util.concurrent.Semaphore;

@Component
public class GlobalRequestLimiter {
	private final Semaphore semaphore = new Semaphore(10);

	public void acquire() throws InterruptedException {
		semaphore.acquire();
	}

	public void release() {
		semaphore.release();
	}
}
