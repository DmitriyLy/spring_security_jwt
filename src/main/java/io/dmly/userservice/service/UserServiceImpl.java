package io.dmly.userservice.service;

import io.dmly.userservice.domain.AppUser;
import io.dmly.userservice.domain.Role;
import io.dmly.userservice.repository.RoleRepository;
import io.dmly.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser saveUser(AppUser appUser) {
        log.info("Saving appUser {} to DB", appUser);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return userRepository.save(appUser);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving role {} to DB", role);
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {

        log.info("Adding a role '{}' to a user '{}'", roleName, username);

        Optional<AppUser> user = userRepository.findByUsername(username);
        Optional<Role> role = roleRepository.findByName(roleName);

        if (user.isEmpty()) {
            throw new IllegalArgumentException("AppUser not found");
        }

        if (role.isEmpty()) {
            throw new IllegalArgumentException("Role not found");
        }

        user.get().getRoles().add(role.get());
    }

    @Override
    public AppUser getUser(String username) {

        log.info("Fetching user '{}'", username);

        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<AppUser> getUsers() {

        log.info("Fetching all users");

        return userRepository.findAll();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> appUser = userRepository.findByUsername(username);

        if (appUser.isEmpty()) {
            log.error("User not found in DB");
            throw new UsernameNotFoundException("User not found in DB");
        } else {
            log.info("User '{}' found in DB", username);

        }

        AppUser user = appUser.get();
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> authorities.add(new SimpleGrantedAuthority(role.getName())));

        return new User(user.getUsername(), user.getPassword(), authorities);
    }
}
