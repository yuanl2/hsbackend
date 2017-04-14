package com.hansun.server.metrics;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;

//TODO: finish converting this entire class to use MetricRegistry.  It's unclear who, if anyone, is relying on the
//existing metric names so I'm punting for now.
public class AuthMetrics extends AbstractMetrics {

    private final Counter nonSelfContainedTokenCounter;
    private final Counter selfContainedTokenCounter;
    private final Counter tokenSignatureFailureCounter;
    private final Counter tokenDecryptionFailureCounter;
    private final Counter unknownSelfContainedTokenFailureCounter;
    private final Counter tokenSignatureKeyRefreshCounter;
    private final Counter tokenDecryptionKeyRefreshCounter;
    private final Counter tokenSignatureVerificationRetryCounter;
    private final Counter tokenDecryptionRetryCounter;
    private final Histogram nonSelfContainedTokenValidationTimer;
    private final Histogram selfContainedTokenValidationTimer;
    private final Histogram selfContainedTokenSignatureValidationTimer;
    private final Histogram selfContainedTokenDecryptionTimer;

    public AuthMetrics(MetricRegistry metricRegistry, StatsdClient statsdClient, String prefix) {
        super(statsdClient, prefix);
        nonSelfContainedTokenCounter = metricRegistry.counter("oauth.non_self_contained_token");
        selfContainedTokenCounter = metricRegistry.counter("oauth.self_contained_token");
        tokenSignatureFailureCounter = metricRegistry.counter("oauth.self_contained_token.signature_failure");
        tokenDecryptionFailureCounter = metricRegistry.counter("oauth.self_contained_token.decryption_failure");
        unknownSelfContainedTokenFailureCounter = metricRegistry.counter("oauth.self_contained_token.unknown_failure");
        tokenSignatureKeyRefreshCounter = metricRegistry.counter("oauth.self_contained_token.signature_key_refresh");
        tokenDecryptionKeyRefreshCounter = metricRegistry.counter("oauth.self_contained_token.decryption_key_refresh");
        tokenSignatureVerificationRetryCounter = metricRegistry.counter("oauth.self_contained_token.signature_verification_retry");
        tokenDecryptionRetryCounter = metricRegistry.counter("oauth.self_contained_token.decryption_retry");
        nonSelfContainedTokenValidationTimer = metricRegistry.histogram("oauth.non_self_contained_token.validation");
        selfContainedTokenValidationTimer = metricRegistry.histogram("oauth.self_contained_token.validation");
        selfContainedTokenSignatureValidationTimer = metricRegistry.histogram("oauth.self_contained_token.signature_verification");
        selfContainedTokenDecryptionTimer = metricRegistry.histogram("oauth.self_contained_token.decryption");

    }

    public void oAuthValidationResponse(int httpStatus) {
        statsdClient.increment(stat("oauth.validation.response", httpStatus));
    }

    public void oAuthInvalidAccessScope() {
        statsdClient.increment(stat("oauth.invalidaccess.scope"));
    }

    public void oAuthInvalidAccessRole() {
        statsdClient.increment(stat("oauth.invalidaccess.role"));
    }

    public void oAuthValidationDuration(long ms) {
        statsdClient.timing(stat("oauth.validation.duration"), ms);
    }
    
    public void accountExpireyWarning() {
        statsdClient.increment(stat("oauth.account.expiry"));
    }

    public void incrementNonSelfContainedTokenCount() {
        nonSelfContainedTokenCounter.inc();
    }

    public void incrementSelfContainedTokenCount() {
        selfContainedTokenCounter.inc();
    }

    public void selfContainedTokenSignatureCheckResult(boolean success) {
        if (!success) {
            tokenSignatureFailureCounter.inc();
        }
    }

    public void selfContainedTokenDecryptionResult(boolean success) {
        if (!success) {
            tokenDecryptionFailureCounter.inc();
        }
    }

    public void incrementUnknownSelfContainedTokenFailure() {
        unknownSelfContainedTokenFailureCounter.inc();
    }

    public void nonSelfContainedTokenValidationTime(long ms) {
        nonSelfContainedTokenValidationTimer.update(ms);
    }

    public void selfContainedTokenValidationTime(long ms) {
        selfContainedTokenValidationTimer.update(ms);
    }

    public void selfContainedTokenSignatureValidationTime(long ms) {
        selfContainedTokenSignatureValidationTimer.update(ms);
    }

    public void selfContainedTokenDecryptionTime(long ms) {
        selfContainedTokenDecryptionTimer.update(ms);
    }

    public void refreshTokenSignatureVerifier() {
        tokenSignatureKeyRefreshCounter.inc();
    }

    public void refreshTokenDecrypter() {
        tokenDecryptionKeyRefreshCounter.inc();
    }

    public void selfContainedTokenSignatureVerificationRetry() {
        tokenSignatureVerificationRetryCounter.inc();
    }

    public void selfContainedTokenDecryptionRetry() {
        tokenDecryptionRetryCounter.inc();
    }
}
