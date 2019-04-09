package pl.blaszak.trainingproject.service;

import org.apache.commons.lang.StringUtils;
import pl.blaszak.trainingproject.exception.LogonServiceException;

public class LogonService {

    public static final String EMPTY_USERNAME_MESSAGE = "Empty userName";
    public static final String EMPTY_PASSWORD_MESSAGE = "Empty password";
    public static final String USER_NOT_FOUND = "User not found";

    private final LogonDbService dbService;
    private final SessionService sessionService;

    public LogonService(LogonDbService dbService, SessionService sessionService) {
        this.dbService = dbService;
        this.sessionService = sessionService;
    }

    public String login(String userName, String password) throws LogonServiceException {
        if (StringUtils.isEmpty(userName)) {
            throw new LogonServiceException(EMPTY_USERNAME_MESSAGE);
        }
        if (StringUtils.isEmpty(password)) {
            throw new LogonServiceException(EMPTY_PASSWORD_MESSAGE);
        }
        String dbPassword = dbService.getPassword(userName);
        if (dbPassword == null) {
            throw new LogonServiceException(USER_NOT_FOUND);
        }
        if (password.equals(dbPassword)) {
            return sessionService.createSession();
        }
        return null;
    }
}
