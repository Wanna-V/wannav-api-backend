package com.ssg.wannavapibackend.service;

import com.ssg.wannavapibackend.domain.User;
import com.ssg.wannavapibackend.dto.response.KakaoResponseDTO;

public interface UserService {

    Boolean saveUser(User user);

    KakaoResponseDTO getUser(Long userId);
}
