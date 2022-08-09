package io.dmly.userservice.service;

import io.dmly.userservice.domain.AppUser;
import io.dmly.userservice.domain.Role;

import java.util.List;

public interface UserService {
    AppUser saveUser(AppUser appUser);

    Role saveRole(Role role);

    void addRoleToUser(String username, String roleName);

    AppUser getUser(String username);

    List<AppUser> getUsers();
}
