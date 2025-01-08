package gaun.apply.security;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import gaun.apply.entity.user.Role;
import gaun.apply.entity.user.User;
import gaun.apply.repository.UserRepository;
import gaun.apply.service.StaffService;

@Service
public class CustomUserDetailsService implements UserDetailsService{
    private final UserRepository userRepository;
    private final StaffService staffService;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserRepository userRepository,
                                  StaffService staffService,
                                  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.staffService = staffService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String identityNumber) throws UsernameNotFoundException {
        User user = userRepository.findByIdentityNumber(identityNumber);
        
        if (user == null) {
            throw new UsernameNotFoundException("Kullanıcı bulunamadı: " + identityNumber);
        }

        // Kullanıcının veritabanındaki rollerini kullan
        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        
        return new org.springframework.security.core.userdetails.User(
                user.getIdentityNumber(),
                user.getPassword(),
                authorities
        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

}