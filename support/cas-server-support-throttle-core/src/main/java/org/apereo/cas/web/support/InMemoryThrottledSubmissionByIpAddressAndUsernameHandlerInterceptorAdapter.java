package org.apereo.cas.web.support;

import org.apereo.cas.audit.AuditTrailExecutionPlan;
import org.apereo.cas.throttle.ThrottledRequestExecutor;
import org.apereo.cas.throttle.ThrottledRequestResponseHandler;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apereo.inspektr.common.web.ClientInfoHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.ConcurrentMap;

/**
 * Attempts to throttle by both IP Address and username.  Protects against instances where there is a NAT, such as
 * a local campus wireless network.
 *
 * @author Scott Battaglia
 * @since 3.3.5
 */
public class InMemoryThrottledSubmissionByIpAddressAndUsernameHandlerInterceptorAdapter
    extends AbstractInMemoryThrottledSubmissionHandlerInterceptorAdapter {

    public InMemoryThrottledSubmissionByIpAddressAndUsernameHandlerInterceptorAdapter(final int failureThreshold,
                                                                                      final int failureRangeInSeconds,
                                                                                      final String usernameParameter,
                                                                                      final String authenticationFailureCode,
                                                                                      final AuditTrailExecutionPlan auditTrailExecutionPlan,
                                                                                      final String applicationCode,
                                                                                      final ThrottledRequestResponseHandler throttledRequestResponseHandler,
                                                                                      final ConcurrentMap map,
                                                                                      final ThrottledRequestExecutor throttledRequestExecutor) {
        super(failureThreshold, failureRangeInSeconds, usernameParameter,
            authenticationFailureCode, auditTrailExecutionPlan, applicationCode,
            throttledRequestResponseHandler, map, throttledRequestExecutor);
    }

    @Override
    public String constructKey(final HttpServletRequest request) {
        val username = request.getParameter(getUsernameParameter());

        if (StringUtils.isBlank(username)) {
            return request.getRemoteAddr();
        }

        return ClientInfoHolder.getClientInfo().getClientIpAddress() + ';' + username.toLowerCase();
    }


    @Override
    public String getName() {
        return "inMemoryIpAddressUsernameThrottle";
    }
}
