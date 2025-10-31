package gaun.apply.security;

import gaun.apply.application.dto.StudentDto;
import gaun.apply.common.util.ConvertUtil;
import gaun.apply.domain.user.entity.User;
import gaun.apply.domain.user.repository.UserRepository;
import gaun.apply.domain.user.service.StudentService;
import gaun.apply.domain.user.service.UserService;
import gaun.apply.infrastructure.service.SmsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Value("${base.url}")
    private String baseUrl;

    private final UserRepository userRepository;
    private final StudentService studentService;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate restTemplate;
    private final SmsService smsService;

    public CustomAuthenticationProvider(UserRepository userRepository,
                                       StudentService studentService,
                                       UserService userService,
                                       PasswordEncoder passwordEncoder,
                                       RestTemplate restTemplate,
                                       SmsService smsService) {
        this.userRepository = userRepository;
        this.studentService = studentService;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.restTemplate = restTemplate;
        this.smsService = smsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String identityNumber = authentication.getName();
        String password = authentication.getCredentials().toString();

        // 1. Önce veritabanında kullanıcıyı ara
        User user = userRepository.findByIdentityNumber(identityNumber);

        if (user != null) {
            // Kullanıcı var, şifre kontrolü yap
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Şifre doğru, authentication başarılı
                Collection<GrantedAuthority> authorities = user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList());
                
                return new UsernamePasswordAuthenticationToken(identityNumber, password, authorities);
            } else {
                // Şifre yanlış
                throw new BadCredentialsException("Hatalı kullanıcı adı veya şifre");
            }
        } else {
            System.out.println("Kullanıcı DB'de bulunamadı, API'ye istek gönderiliyor: " + identityNumber);
            // Kullanıcı yok, API'den kontrol et
            try {
                String url = baseUrl + "?check=gaun_mobil&u=" + identityNumber + "&p=" + ConvertUtil.convertPasswordToMD5(password);
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    System.out.println("API'den başarılı yanıt alındı");
                    // API'den başarılı yanıt geldi, SMS doğrulaması için exception fırlat
                    StudentDto studentDto = ConvertUtil.convertJsonToStudentDto(response.getBody());

                    if (studentDto.getOgrenciNo() != null) {
                        // Öğrenci bilgilerini hazırla
                        studentDto.setPassword(password);
                        
                        // SMS doğrulama kodu oluştur
                        String verificationCode = String.valueOf(new Random().nextInt(999999));
                        
                        // SMS gönder
                        System.out.println("SMS gönderiliyor: " + studentDto.getGsm1());
                        smsService.sendSms(new String[]{studentDto.getGsm1()}, "Doğrulama Kodu : " + verificationCode);
                        
                        // SMS doğrulaması gerektiğini belirten exception fırlat
                        throw new SmsVerificationRequiredException(
                            "SMS doğrulaması gerekli", 
                            studentDto, 
                            verificationCode
                        );
                    } else {
                        throw new BadCredentialsException("Hatalı kullanıcı adı veya şifre");
                    }
                } else {
                    // API'den başarısız yanıt
                    throw new BadCredentialsException("Hatalı kullanıcı adı veya şifre");
                }
            } catch (SmsVerificationRequiredException e) {
                // SMS doğrulaması gerekiyor, exception'ı yukarı fırlat
                throw e;
            } catch (BadCredentialsException e) {
                throw e;
            } catch (Exception e) {
                // API hatası veya diğer hatalar
                throw new BadCredentialsException("Hatalı kullanıcı adı veya şifre");
            }
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
