package com.imageprocess.interceptor;

import io.github.bucket4j.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key) {
        return cache.computeIfAbsent(key, k -> createNewBucket());
    }

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(3))); // 10 requests per minute
        return Bucket.builder().addLimit(limit).build();
    }

    public ConsumptionProbe tryConsume(String key) {
        Bucket bucket = resolveBucket(key);
        return bucket.tryConsumeAndReturnRemaining(1); // Consume 1 token
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        ConsumptionProbe probe = tryConsume(request.getRemoteUser());
        if(probe.isConsumed()){
            response.addHeader("X-Rate-Limit-Remaining",String.valueOf(probe.getRemainingTokens()));
            return true;
        }else {
            long waitForRefill = probe.getNanosToWaitForRefill();
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefill));
            response.sendError(HttpStatus.TOO_MANY_REQUESTS.value(),
                    "You have exhausted your API Request Quota");
            return false;


        }

    }
}
