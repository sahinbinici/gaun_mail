package gaun.apply.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import gaun.apply.domain.user.entity.Role;
import gaun.apply.domain.user.entity.User;
import gaun.apply.domain.user.repository.UserRepository;
import gaun.apply.domain.user.service.StaffService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        Collection<GrantedAuthority> authorities = new ArrayList<>(user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList()));
        
        // Check if user is a staff member with admin privileges
        boolean hasStaffRole = authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_STAFF"));
                
        if (hasStaffRole) {
            // Check if the staff has admin privileges in the Staff entity
            var staff = staffService.findByTcKimlikNo(user.getTcKimlikNo());
            
            if (staff != null && staff.isAdmin()) {
                // Add both ROLE_ADMIN and ROLE_STAFF_ADMIN authorities
                boolean hasAdminRole = authorities.stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
                if (!hasAdminRole) {
                    authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }
                authorities.add(new SimpleGrantedAuthority("ROLE_STAFF_ADMIN"));
            }
        }
        
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