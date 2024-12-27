package gaun.apply.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.event.AbstractAuthenticationEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import gaun.apply.dto.StudentDto;
import gaun.apply.entity.Role;
import gaun.apply.entity.User;
import gaun.apply.repository.UserRepository;
import gaun.apply.util.ConvertUtil;

@Service
public class CustomUserDetailsService implements UserDetailsService, ApplicationListener<AbstractAuthenticationEvent> {
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;

    @Value("${base.url}")
    private String baseUrl;
    private String password;

    User user=new User();

    public CustomUserDetailsService(UserRepository userRepository, 
                                  RestTemplate restTemplate,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String identityNumber) throws UsernameNotFoundException {
        user = userRepository.findByIdentityNumber(identityNumber);

        return new org.springframework.security.core.userdetails.User(
                user.getIdentityNumber(),
                user.getPassword(),
                mapRolesToAuthorities(user.getRoles())
        );
    }
    
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        Collection<? extends GrantedAuthority> mapRoles = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return mapRoles;
    }

    @Override
    public void onApplicationEvent(@NonNull AbstractAuthenticationEvent event) {
        if (event instanceof AuthenticationFailureBadCredentialsEvent) {
            AuthenticationFailureBadCredentialsEvent failureEvent = (AuthenticationFailureBadCredentialsEvent) event;
            System.out.println("Giriş Denemesi Detayları:");
            String identityNumber= failureEvent.getAuthentication().getPrincipal().toString();
            password= failureEvent.getAuthentication().getCredentials().toString();


            try {
                // Şifre bilgisini authentication request'ten al
                String url = baseUrl + "?check=gaun_mobil&u=" + identityNumber + "&p=" + password;

                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    StudentDto studentDto = ConvertUtil.convertJsonToStudentDto(response.getBody());

                    // Yeni kullanıcı oluştur
                    user.setIdentityNumber(studentDto.getIdentityNumber());
                    user.setPassword(passwordEncoder.encode(studentDto.getPassword()));
                    // Diğer gerekli alanları set et
                    user.setName(studentDto.getName());
                    user.setLastname(studentDto.getSurname());
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