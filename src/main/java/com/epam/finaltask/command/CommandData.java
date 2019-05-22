package com.epam.finaltask.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Class instances of which contain data for command execution.
 */
public class CommandData {
    private static final int FIRST_ELEMENT_INDEX = 0;

    /**
     * Parameters of HttpServletRequest.
     */
    private Map<String, String> requestParameters = new HashMap<>();
    /**
     * Attributes of HttpServletRequest.
     */
    private Map<String, Object> requestAttributes = new HashMap<>();
    /**
     * Attributes of the session.
     */
    private Map<String, Object> sessionAttributes = new HashMap<>();

    /**
     * Creates CommandData, fetching data from the chosen request.
     * @param request HttpServletRequest to retrieve data from
     */
    public CommandData(HttpServletRequest request) {
        request.getParameterMap().forEach((key, value) -> requestParameters.put(key, value[FIRST_ELEMENT_INDEX]));

        Enumeration<String> requestAttributesNamesEnumeration = request.getAttributeNames();
        while (requestAttributesNamesEnumeration.hasMoreElements()) {
            String attributeName = requestAttributesNamesEnumeration.nextElement();
            requestAttributes.put(attributeName, request.getAttribute(attributeName));
        }

        Enumeration<String> sessionAttributesNamesEnumeration = request.getSession().getAttributeNames();
        while (sessionAttributesNamesEnumeration.hasMoreElements()) {
            String attributeName = sessionAttributesNamesEnumeration.nextElement();
            sessionAttributes.put(attributeName, request.getSession().getAttribute(attributeName));
        }
    }

    /**
     * Updates attributes of the chosen HttpSession or adds them if they do not exist.
     * @param session Session, attributes of which will be updated
     */
    public void updateSessionAttributes(HttpSession session) {
        sessionAttributes.forEach(session::setAttribute);
    }

    /**
     * Updates attributes of the chosen HttpServletRequest or adds them if they do not exist.
     * @param request Request, attributes of which will be updated
     */
    public void updateRequestAttributes(HttpServletRequest request) {
        requestAttributes.forEach(request::setAttribute);
    }

    /**
     * Returns request parameter by key.
     * @param key Key of the request parameter
     * @return Request parameter
     */
    public String getRequestParameter(String key) {
        return requestParameters.get(key);
    }

    /**
     * Puts request attribute.
     * @param key Key of the attribute
     * @param value Value of the attribute
     */
    public void putRequestAttribute(String key, Object value) {
        requestAttributes.put(key, value);
    }

    /**
     * Returns request attribute by key.
     * @param key Key of the request attribute
     * @return Request attribute
     */
    public Object getRequestAttribute(String key) {
        return requestAttributes.get(key);
    }

    /**
     * Puts session attribute.
     * @param key Key of the session attribute
     * @param value Value of the session attribute
     */
    public void putSessionAttribute(String key, Object value) {
        sessionAttributes.put(key, value);
    }

    /**
     * Returns session attribute by key
     * @param key Key of the session attribute
     * @return Session attribute
     */
    public Object getSessionAttribute(String key) {
        return sessionAttributes.get(key);
    }
}
