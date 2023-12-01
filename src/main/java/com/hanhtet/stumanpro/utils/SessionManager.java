package com.hanhtet.stumanpro.utils;

import java.util.HashMap;
import java.util.Map;

public class SessionManager {
    private static final Map<String, UserSession> activeSessions = new HashMap<>();

    public static void createSession(UserSession userSession) {
        activeSessions.put(userSession.getId(), userSession);
    }

    public static UserSession getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    public static void invalidateSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
}
