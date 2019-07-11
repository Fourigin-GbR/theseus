package com.fourigin.argo.forms.config;

import com.fourigin.argo.forms.scheduling.ProcessFormRequestsJob;
import com.fourigin.utilities.spring.scheduling.AutowiringSpringBeanJobFactory;
import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

@Configuration
public class QuartzConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Value("${scheduling.repeat-interval}")
    private Long repeatInterval;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Bean
    public JobDetailFactoryBean jobDetail() {
        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
        jobDetailFactory.setJobClass(ProcessFormRequestsJob.class);
        jobDetailFactory.setDescription("Invoke ProcessFormRequestsJob service...");
        jobDetailFactory.setDurability(true);

//        Map<String, Object> dataMap = new HashMap<>();
//        dataMap.put("project", "ge-web");
//        dataMap.put("bases", Arrays.asList("DE", "EN", "RU"));
//        jobDetailFactory.setJobDataMap(new JobDataMap(dataMap));

        return jobDetailFactory;
    }

    @Bean
    public SimpleTriggerFactoryBean trigger(JobDetail job) {
        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
        trigger.setJobDetail(job);
        trigger.setRepeatInterval(repeatInterval);
        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
//        trigger.setRepeatCount(0);
        return trigger;
    }

    @Bean
    public SchedulerFactoryBean scheduler(Trigger trigger, JobDetail job) {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setConfigLocation(new ClassPathResource("quartz.properties"));

        schedulerFactory.setJobFactory(springBeanJobFactory(applicationContext));
        schedulerFactory.setJobDetails(job);
        schedulerFactory.setTriggers(trigger);
        return schedulerFactory;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }
}
