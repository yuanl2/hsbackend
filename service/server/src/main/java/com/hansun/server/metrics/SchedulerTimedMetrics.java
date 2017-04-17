package com.hansun.server.metrics;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SchedulerTimedMetrics {
    String value() default "";
}
