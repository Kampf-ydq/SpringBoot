package com.ditecting.honeyeye.inputer.capturer.scheduler;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.SchedulingException;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * @author CSheng
 * @version 1.0
 * @date 2020/6/3 9:05
 */
@Configuration
@EnableScheduling
public class DefaultSchedulingConfigurer implements SchedulingConfigurer {
    private ScheduledTaskRegistrar taskRegistrar;
    private Set<ScheduledFuture<?>> scheduledFutures = null;
    private Map<String, ScheduledFuture<?>> taskFutures = new ConcurrentHashMap<>();

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
    }

    @SuppressWarnings("unchecked")
    private Set<ScheduledFuture<?>> getScheduledFutures() {
        if (scheduledFutures == null) {
            try {
                scheduledFutures = (Set<ScheduledFuture<?>>) BeanUtils.getProperty(taskRegistrar, "scheduledTasks");
            } catch (NoSuchFieldException e) {
                throw new SchedulingException("Not found scheduledFutures field.");
            }
        }
        return scheduledFutures;
    }

    /**
     * add task
     */
    public void addTriggerTask(String taskId, TriggerTask triggerTask) {
        if (taskFutures.containsKey(taskId)) {
            throw new SchedulingException("The taskId[" + taskId + "] was added.");
        }
        TaskScheduler scheduler = taskRegistrar.getScheduler();
        ScheduledFuture<?> future = scheduler.schedule(triggerTask.getRunnable(), triggerTask.getTrigger());
        getScheduledFutures().add(future);
        taskFutures.put(taskId, future);
    }

    /**
     * cancel task
     */
    public void cancelTriggerTask(String taskId) {
        ScheduledFuture<?> future = taskFutures.get(taskId);
        if (future != null) {
            future.cancel(true);
        }
        taskFutures.remove(taskId);
        getScheduledFutures().remove(future);
    }

    /**
     * reset task
     */
    public void resetTriggerTask(String taskId, TriggerTask triggerTask) {
        cancelTriggerTask(taskId);
        addTriggerTask(taskId, triggerTask);
    }

    /**
     * list all taskIds
     */
    public Set<String> taskIds() {
        return taskFutures.keySet();
    }

    /**
     * check if the task exists
     */
    public boolean hasTask(String taskId) {
        return this.taskFutures.containsKey(taskId);
    }

    /**
     * check if the taskRegistrar has been initiated successfully
     */
    public boolean inited() {
        return this.taskRegistrar != null && this.taskRegistrar.getScheduler() != null;
    }

    public void destroy () {
        this.taskRegistrar.destroy();
    }
}