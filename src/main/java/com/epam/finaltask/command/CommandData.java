package com.epam.finaltask.command;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandData {
    private static final int FIRST_ELEMENT_INDEX = 0;

    private Map<String, String> requestParameters = new HashMap<>();
    private Map<String, Object> requestAttributes = new HashMap<>();
    private Map<String, Object> sessionAttributes = new HashMap<>();

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

    public void updateSessionAttributes(HttpSession session) {
        sessionAttributes.forEach(session::setAttribute);
    }

    public void updateRequestAttributes(HttpServletRequest request) {
        requestAttributes.forEach(request::setAttribute);
    }

    public String getRequestParameter(String key) {
        return requestParameters.get(key);
    }

    public void putRequestAttribute(String key, Object value) {
        requestAttributes.put(key, value);
    }

    public Object getRequestAttribute(String key) {
        return requestAttributes.get(key);
    }

    public void putSessionAttribute(String key, Object value) {
        sessionAttributes.put(key, value);
    }

    public Object getSessionAttribute(String key) {
        return sessionAttributes.get(key);
    }
}
