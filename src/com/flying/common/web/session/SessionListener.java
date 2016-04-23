package com.flying.common.web.session;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.flying.common.util.Constants;
/**
 * 
 * @author liuyuan
 *
 */
public class SessionListener implements HttpSessionListener {

	public void sessionCreated(HttpSessionEvent event) {
		SessionManager.put(event.getSession());
	}

	public void sessionDestroyed(HttpSessionEvent event) {
		SessionManager.remove(event.getSession());
		destroySession(event.getSession());
	}
	
	/**
     * Static method to destroy all ServiceLifecycle objects within an
     * Axis session.
     */ 
    static void destroySession(HttpSession session)
    {
        // Check for our marker so as not to do unneeded work
        if (session.getAttribute(Constants.AXIS_SESSION_MARKER) == null)
            return;
        
//        Enumeration e = session.getAttributeNames();
//        while (e.hasMoreElements()) {
//            Object next = e.nextElement();
//            if (next instanceof ServiceLifecycle) {
//                ((ServiceLifecycle)next).destroy();
//            }
//        }
    }       

}
