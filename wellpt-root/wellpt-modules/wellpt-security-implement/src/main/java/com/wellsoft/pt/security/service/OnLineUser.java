package com.wellsoft.pt.security.service;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lilin
 * @ClassName: OnLineUser
 * @Description: spring security 的在线用户统计功能
 */
@Service
@Transactional
public class OnLineUser {
    //	@Autowired(required = true)
    private SessionRegistry sessionRegistry;

    public int getNumberOfUsers() {

        return sessionRegistry.getAllPrincipals().size();
    }

    public Map<Object, Date> getActiveUsers() {
        Map<Object, Date> lastActivityDates = new HashMap<Object, Date>();
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            // a principal may have multiple active sessions
            for (SessionInformation session : sessionRegistry.getAllSessions(principal, false)) {
                // no last activity stored
                if (lastActivityDates.get(principal) == null) {
                    lastActivityDates.put(principal, session.getLastRequest());
                } else {
                    // check to see if this session is newer than the last stored
                    Date prevLastRequest = lastActivityDates.get(principal);
                    if (session.getLastRequest().after(prevLastRequest)) {
                        // update if so
                        lastActivityDates.put(principal, session.getLastRequest());
                    }
                }
            }
        }
        return lastActivityDates;
    }
}
