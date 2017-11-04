package com.jephy.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by chenshijue on 2017/11/4.
 */

@Component
public class DemoSchedule {

    @Scheduled(fixedRate = 100000)
    public void demoTask(){
        System.out.println("this is from schedule task");
    }

}
