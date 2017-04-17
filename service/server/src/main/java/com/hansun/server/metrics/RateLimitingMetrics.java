package com.hansun.server.metrics;

public class RateLimitingMetrics extends AbstractMetrics {
    public RateLimitingMetrics(StatsdClient statsdClient, String prefix) {
        super(statsdClient, prefix);
    }

    public void rateLimitingFilterResponse(String authType, int httpStatus) {
        statsdClient.increment(stat(authType, "rate_limiting_filter.response", httpStatus));
    }

    public void rateLimitingFilterZeroMatchResponse(int httpStatus) {
        statsdClient.increment(stat("rate_limiting_zero_match_filter.response", httpStatus));
    }

    public void rateLimitingFilterAccountRateLimitedDuration(long ms, int httpStatus) {
        // use 0.1 sample rate to cut down the metrics volume being emitted
        statsdClient.timing(stat("rate_limiting_filter.http.status", httpStatus, "duration"), ms, 0.1);
    }

    public void rateLimitingFilterHttpStatus200Duration(boolean isUserOrConsumerAuth, long ms) {
        if (isUserOrConsumerAuth) {
            statsdClient.timing(stat("rate_limiting_filter.user.or.consumer.auth.duration"), ms);
        } else {
            statsdClient.timing(stat("rate_limiting_filter.service.or.client.auth.duration"), ms);
        }
    }
}
