package com.example.learnspringlogback.runner;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StartupRunner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("启动。。。。。。");
        log.debug("AAAA debug msg");
        log.error("BBBB error msg");
        log.warn("CCCC warn msg");
        log.trace("DDDD trace msg");
        log.info("预热结束。。。。。");
    }
}
