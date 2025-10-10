package gaun.apply.security;

import gaun.apply.application.dto.StudentDto;
import gaun.apply.common.util.ConvertUtil;
import gaun.apply.domain.user.entity.Role;
import gaun.apply.domain.user.entity.User;
import gaun.apply.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.lang.NonNull;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthenticationLoggingListener implements ApplicationListener<AbstractAuthenticationEvent> {
    @Value("${base.url}")
    private String baseUrl;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationLoggingListener(UserRepository userRepository, RestTemplate restTemplate, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    User user = new User();

    @Override
    public void onApplicationEvent(@NonNull AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            AuthenticationFailureBadCredentialsEvent failureEvent = (AuthenticationFailureBadCredentialsEvent) event;
            System.out.println("Giriş Denemesi Detayları:");
            String identityNumber = failureEvent.getAuthentication().getPrincipal().toString();
            String password = failureEvent.getAuthentication().getCredentials().toString();

            // Otomatik kayıt işlemi devre dışı bırakıldı
            // Kayıt işlemi artık CustomAuthenticationProvider tarafından yönetiliyor
            System.out.println("Başarısız giriş denemesi: " + identityNumber);
        }
    }
}
