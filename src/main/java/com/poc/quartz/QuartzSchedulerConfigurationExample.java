package com.poc.quartz;

import java.io.Serializable;

import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class QuartzSchedulerConfigurationExample implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7662016530887783798L;
	private int repeatCount = 3;

	public static void main(String[] args) throws Exception {
		QuartzSchedulerConfigurationExample quartzSchedulerExample = new QuartzSchedulerConfigurationExample();
		Scheduler scheduler = quartzSchedulerExample.createAndStartScheduler();
		for (int i = 0; i < 1; i++) {
			quartzSchedulerExample.fireJob(scheduler, Job1.class);
			quartzSchedulerExample.fireJob(scheduler, Job2.class);
			quartzSchedulerExample.fireJob(scheduler, Job3.class);
			//scheduler.shutdown();
			
		}
		
	}

	public Scheduler createAndStartScheduler() throws SchedulerException {
		SchedulerFactory schedFact = new org.quartz.impl.StdSchedulerFactory("quartz.properties");
		Scheduler scheduler = schedFact.getScheduler();
		System.out
				.println("Scheduler name is: " + scheduler.getSchedulerName());
		System.out.println("Scheduler instance ID is: "
				+ scheduler.getSchedulerInstanceId());
		System.out.println("Scheduler context's value for key QuartzTopic is "
				+ scheduler.getContext().getString("QuartzTopic"));
		scheduler.start();
		return scheduler;
	}

	public <T extends Job> void fireJob(Scheduler scheduler, Class<T> jobClass)
			throws SchedulerException, InterruptedException {

		// define the job and tie it to our HelloJob class
		JobBuilder jobBuilder = JobBuilder.newJob(jobClass);
		JobDataMap data = new JobDataMap();
		data.put("latch", this);

		JobDetail jobDetail = jobBuilder
				.usingJobData("example",
						"com.poc.quartz.QuartzSchedulerExample")
				.usingJobData(data).build();

		// Trigger the job to run now, and then every 40 seconds
		Trigger trigger = TriggerBuilder
				.newTrigger()
				.startNow()
				.withSchedule(
						SimpleScheduleBuilder.simpleSchedule()
								.withRepeatCount(repeatCount)
								.withIntervalInSeconds(2))
				.withDescription("MyTrigger").build();

		// Tell quartz to schedule the job using our trigger
		scheduler.scheduleJob(jobDetail, trigger);
	}

}
