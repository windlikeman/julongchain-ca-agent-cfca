package demo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class PerfRunner {

	public static void main(String[] args) throws Exception {

		final AtomicInteger allTask = new AtomicInteger(0);
		final AtomicInteger numTask = new AtomicInteger(0);
		final AtomicInteger errTask = new AtomicInteger(0);
		final AtomicInteger zeroTask = new AtomicInteger(0);

		final int interval = 5000;
		final int numThread = 64;

		System.err.println("numThread: " + numThread);

		final Random random = new Random();

		byte[] sourceData = new byte[32];
		random.nextBytes(sourceData);

		ChainCode.call(true);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				System.err.println("Java Finished...");
			}
		}));

//		Thread.sleep(500);

		final ExecutorService executor = Executors.newFixedThreadPool(numThread);
		for (int i = 0; i < numThread; i++) {
			executor.execute(new Runnable() {

				public void run() {

					while (true) {
						try {
							allTask.incrementAndGet();
							ChainCode.call(false);

							numTask.incrementAndGet();

						} catch (Exception e) {
							e.printStackTrace();
							errTask.incrementAndGet();
						}
					}
				}
			});
		}

		String dateTime;
		final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

		long n;
		long x;
		long y;
		long runTime0 = System.nanoTime();
		long runTime1 = System.nanoTime();
		long exeTime;
		long allNumb = 0;
		long allTime = 0;

		while (true) {
			x = System.currentTimeMillis();
			Thread.sleep(interval);
			n = numTask.get();
			y = System.currentTimeMillis();
			numTask.set(0);

			dateTime = dateFormat.format(new Date(y));

			runTime1 = System.nanoTime();
			exeTime = (runTime1 - runTime0) / 1000000;
			runTime0 = runTime1;

			allNumb += n;
			allTime += exeTime;

			System.err.println(String.format(
					"%s  sysRunTime=%16.3f TPS[x=%8.2f, y=%8.2f, all=%8.2f],  #all=%s    ###err=%s   ####zero=%s",
					dateTime, runTime0 / 1000000000.0, n * 1000.0 / (y - x), (n * 1000.0 / exeTime),
					(allNumb * 1000.0 / allTime), allTask.get(), errTask.get(), zeroTask.get()));

			
		}
	}

}
