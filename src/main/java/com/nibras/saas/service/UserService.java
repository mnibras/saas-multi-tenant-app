package com.nibras.saas.service;

import com.nibras.saas.common.PageResponse;
import com.nibras.saas.dto.request.UserRequest;
import com.nibras.saas.dto.response.UserResponse;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {

    void createUser(final UserRequest userRequest);

    void updateUser(final String userId, final UserRequest userRequest);

    void deleteUser(final String userId);

    UserResponse getUserById(final String userId);

    PageResponse<UserResponse> getAllUsers(final int page, final int pageSize);

    void enableUser(final String userId);

    void disableUser(final String userId);

}
