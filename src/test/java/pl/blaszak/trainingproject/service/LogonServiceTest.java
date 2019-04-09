package pl.blaszak.trainingproject.service;

import org.assertj.core.api.WithAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import pl.blaszak.trainingproject.exception.LogonServiceException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static pl.blaszak.trainingproject.service.LogonService.EMPTY_PASSWORD_MESSAGE;
import static pl.blaszak.trainingproject.service.LogonService.EMPTY_USERNAME_MESSAGE;
import static pl.blaszak.trainingproject.service.LogonService.USER_NOT_FOUND;

@RunWith(MockitoJUnitRunner.class)
public class LogonServiceTest implements WithAssertions {

    @Mock
    private LogonDbService dbService;

    @Mock
    private SessionService sessionService;

    private LogonService logonService;

    @Before
    public void setUp() {
        logonService = new LogonService(dbService, sessionService);
    }

    @Test
    public void shouldThrowExceptionForEmptyUsername() {
        // given
        // when
        Throwable throwable = catchThrowable(() -> logonService.login(null, "myPassword"));
        // then
        assertThat(throwable).isInstanceOf(LogonServiceException.class).hasMessage(EMPTY_USERNAME_MESSAGE);
    }

    @Test
    public void shouldThrowExceptionForEmptyPassword() {
        // given
        // when
        Throwable throwable = catchThrowable(() -> logonService.login("myUser", null));
        // then
        assertThat(throwable).isInstanceOf(LogonServiceException.class).hasMessage(EMPTY_PASSWORD_MESSAGE);
    }

    @Test
    public void shouldThrowExceptionForUnknownUser() {
        // given
        given(dbService.getPassword(any())).willReturn(null);
        // when
        Throwable throwable = catchThrowable(() -> logonService.login("myUser", "myPassword"));
        // then
        assertThat(throwable).isInstanceOf(LogonServiceException.class).hasMessage(USER_NOT_FOUND);
    }

    @Test
    public void shouldReturnNullForNotAuthenticatedUser() throws LogonServiceException {
        // given
        given(dbService.getPassword(any())).willReturn("foo");
        // when
        String sessionId =  logonService.login("myUser", "bar");
        // then
        assertThat(sessionId).isNull();
    }

    @Test
    public void shouldReturnSessionIdForAuthenticatedUser() throws LogonServiceException {
        // given
        String password = "myPassword";
        given(dbService.getPassword(any())).willReturn(password);
        given(sessionService.createSession()).willReturn("mySessionId");
        // when
        String sessionId =  logonService.login("myUser", password);
        // then
        assertThat(sessionId).isNotNull();
    }
}