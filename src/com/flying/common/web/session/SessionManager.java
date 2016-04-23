package com.flying.common.web.session;

import java.util.Map;

import javax.servlet.http.HttpSession;

import com.flying.common.util.StringUtils;
import com.flying.common.util.Utils;

/**
 * 
 * @author liuyuan
 *
 */
public class SessionManager {
	private static Map<String,HttpSession> map = Utils.newHashMap();

    public static synchronized void put(HttpSession session) {
        if (session != null) {
            map.put(session.getId(), session);
        }
    }

    public static synchronized void remove(HttpSession session) {
        if (session != null) {
        	map.remove(session.getId());
        }
    }

    public static HttpSession get(String sessionId) {
        if (StringUtils.isBlank(sessionId)){
        	return null;
        }
        return map.get(sessionId);
    }
}
