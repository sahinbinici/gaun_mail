package gaun.apply.security;

import gaun.apply.dto.StudentDto;
import gaun.apply.entity.user.Role;
import gaun.apply.entity.user.User;
import gaun.apply.repository.UserRepository;
import gaun.apply.util.ConvertUtil;
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
            String identityNumber= failureEvent.getAuthentication().getPrincipal().toString();
            String password= failureEvent.getAuthentication().getCredentials().toString();


            try {
                // Şifre bilgisini authentication request'ten al
                String url = baseUrl + "?check=gaun_mobil&u=" + identityNumber + "&p=" + password;

                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    StudentDto studentDto = ConvertUtil.convertJsonToStudentDto(response.getBody());

                    // Yeni kullanıcı oluştur
                    user.setIdentityNumber(studentDto.getOgrenciNo());
                    user.setPassword(passwordEncoder.encode(studentDto.getPassword()));
                    // Diğer gerekli alanları set et
                    user.setName(studentDto.getAd());
                    user.setLastname(studentDto.getSoyad());
                    // Varsayılan rol ataması
                    List<Role> roles = new ArrayList<>();
                    roles.add(new Role("ROLE_USER"));
                    user.setRoles(roles);

                    userRepository.save(user);
                } else {
                    throw new UsernameNotFoundException("Kullanıcı bulunamadı");
                }
            } catch (Exception e) {
                throw new UsernameNotFoundException("Servis hatası: " + e.getMessage());
            }
        }
    }
} 