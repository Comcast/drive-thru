/**
 * Copyright 2013 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.comcast.drivethru.spring;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * Common exception handler for the TokenController. This will log the error and then present a
 * common page with the HTTP status code and reason. The application using this ExceptionHandler
 * must provide a view capable of rendering the error dialog. This is set with the
 * <code>viewName</code> property. In addition, an application can provide a custom mapping of error
 * codes or mark an exception using the {@link ResponseStatus} annotation. The default HTTP response
 * code is <code>500 Internal Server Error</code>.
 *
 * <p>
 * An example use of this is below:
 * </p>
 *
 * <pre>
 *   &lt;!-- Common Exception Handling --&gt;
 *   &lt;bean class="com.comcast.tvx.megahttp.spring.ExceptionHandler"&gt;
 *     &lt;property name="defaultResponse" value="500" /&gt;
 *     &lt;property name="viewName" value="error.html" /&gt;
 *     &lt;property name="exceptionMap"&gt;
 *       &lt;map&gt;
 *         &lt;entry key="com.comcast.cvs.xtream.security.SecurityException" value="500" /&gt;
 *       &lt;/map&gt;
 *     &lt;/property&gt;
 *   &lt;/bean&gt;
 * </pre>
 *
 * <p>
 * The view will have access to three values: the <code>status</code> which is the integer HTTP
 * status code, the <code>reason</code> which is the String reason code and the
 * <code>exception</code> which is the causing exception. The view can decide whether or not to
 * include the exception stack trace in it's final output.
 * </p>
 *
 * @author <a href="mailto:cmalmgren@gmail.com">Clark Malmgren</a>
 */
public class ExceptionHandler implements HandlerExceptionResolver, Ordered {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);

    private int defaultResponse = 500;
    private String viewName = "error.html";
    private Map<Class<? extends Exception>, Integer> exceptionMap = null;

    /**
     * Set the default response code. If not set, this will default to 500.
     *
     * @param defaultResponse
     *            the new default response code to use
     */
    public void setDefaultResponse(int defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    /**
     * Get the default response code.
     *
     * @return the default response code
     */
    public int getDefaultResponse() {
        return defaultResponse;
    }

    /**
     * Set the view name to use. If not set, this will default to <code>"error.html"</code>.
     *
     * @param viewName
     *            the new view name to use
     */
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    /**
     * Get the view name
     *
     * @return the view name
     */
    public String getViewName() {
        return viewName;
    }

    /**
     * Add a custom mapping of exceptions to HTTP status codes.
     *
     * @param exceptionMap
     *            the new custom mapping to use
     */
    public void setExceptionMap(Map<Class<? extends Exception>, Integer> exceptionMap) {
        this.exceptionMap = exceptionMap;
    }

    /**
     * Get the custom exception map if one was set.
     *
     * @return the custom exception mapping
     */
    public Map<Class<? extends Exception>, Integer> getExceptionMap() {
        return exceptionMap;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.core.Ordered#getOrder()
     */
    @Override
    public int getOrder() {
        /* Ensure that we are first up! */
        return Integer.MIN_VALUE;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.web.servlet.HandlerExceptionResolver#resolveException(javax.servlet.http
     * .HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object,
     * java.lang.Exception)
     */
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
            Object handler, Exception ex) {
        /* Log the exception */
        LOGGER.error("Exception thrown by controller: ", ex);

        /* Build the response */
        HttpStatus status = null;
        String reason = null;

        /* Look for a @ResponseStatus */
        Class<? extends Exception> clazz = ex.getClass();
        ResponseStatus responseStatus = clazz.getAnnotation(ResponseStatus.class);
        if (null != responseStatus) {
            status = responseStatus.value();
            reason = responseStatus.reason();
        } else if (null != exceptionMap) {
            /* See if there is a specific exception mapping */
            for (Entry<Class<? extends Exception>, Integer> entry : exceptionMap.entrySet()) {
                if (entry.getKey().isAssignableFrom(clazz)) {
                    status = HttpStatus.valueOf(entry.getValue());
                    break;
                }
            }
        }

        /* If no specific status was found, use the default */
        if (null == status) {
            status = HttpStatus.valueOf(defaultResponse);
        }

        /* If no custom reason phrase was provide, use the default for the HttpStatus code */
        if (null == reason || "".equals(reason)) {
            reason = status.getReasonPhrase();
        }

        /*
         * Add the response code to the response, we do not add the custom reason although it will
         * be potentially consumed by the view
         */
        response.setStatus(status.value());

        /* Create the ModelAndView */
        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("status", status.value());
        mav.addObject("reason", reason);
        mav.addObject("exception", ex);

        return mav;
    }
}
