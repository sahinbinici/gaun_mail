package gaun.apply.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;

@Component
public class AuthenticationLoggingListener implements ApplicationListener<AbstractAuthenticationEvent> {

    @Override
    public void onApplicationEvent(@NonNull AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            AuthenticationFailureBadCredentialsEvent failureEvent = (AuthenticationFailureBadCredentialsEvent) event;
            System.out.println("Giriş Denemesi Detayları:");
            System.out.println("Kimlik No: " + failureEvent.getAuthentication().getPrincipal());
            System.out.println("Girilen Şifre: " + failureEvent.getAuthentication().getCredentials());
        }
    }
} 