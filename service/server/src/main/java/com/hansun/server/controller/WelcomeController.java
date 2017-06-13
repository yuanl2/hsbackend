package com.hansun.server.controller;


import com.hansun.server.service.TimerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
public class WelcomeController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private TimerService timerService;

    @RequestMapping("/cmd")
    public void timerSwitch(@RequestParam(value = "flag", required = true, defaultValue = "false") String flag,
                            HttpServletRequest request, HttpServletResponse response) throws IOException {

        logger.info("timerSwitch = " + flag);
        if (flag.equals("true")) {
            timerService.setFlag(true);
        } else {
            timerService.setFlag(false);
        }

    }

}