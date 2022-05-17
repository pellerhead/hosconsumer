package com.crst.api.hosconsumer.config;

import com.crst.api.hosconsumer.service.HosService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;;

@Component
public class ScheduledTasks {

    protected static Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    HosService hosService;

    @Scheduled(fixedDelayString = "${fixed-delay.in.milliseconds}")
    public void process() {

        logger.info("ScheduledTasks.process()");

        long startTime = System.currentTimeMillis();

        hosService.process();

        logger.debug("ScheduledTasks.process() - Took {} ms", System.currentTimeMillis() - startTime);
    }

}
