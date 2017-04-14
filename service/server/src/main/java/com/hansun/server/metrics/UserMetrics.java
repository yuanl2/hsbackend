package com.hansun.server.metrics;

public class UserMetrics extends AbstractMetrics {
    public UserMetrics(StatsdClient statsdClient, String prefix) {
        super(statsdClient, prefix);
    }

    public void commonIdentityGetUserResponse(int httpStatus) {
        statsdClient.increment(stat("scim.get_user.response", httpStatus));
    }

    public void commonIdentityGetUserRetried() {
        statsdClient.increment(stat("scim.get_user.retried"));
    }

    public void commonIdentityGetUserDuration(long ms) {
        statsdClient.timing(stat("scim.get_user.duration"), ms);
    }

    public void commonIdentityCreateUserResponse(int httpStatus) {
        statsdClient.increment(stat("scim.create_user.response", httpStatus));
    }

    public void commonIdentityCreateUserRetried() {
        statsdClient.increment(stat("scim.create_user.retried"));
    }

    public void commonIdentityCreateUserDuration(long ms) {
        statsdClient.timing(stat("scim.create_user.duration"), ms);
    }
}
