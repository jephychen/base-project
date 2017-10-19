package com.jephy.startup;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * Created by chenshijue on 2017/10/19.
 */

@Service
public class DemoStartupService implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        System.out.println("Spring start up!! this is DemoStartupService");
    }
}
