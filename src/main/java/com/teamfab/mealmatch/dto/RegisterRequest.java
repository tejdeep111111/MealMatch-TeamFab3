package com.teamfab.mealmatch.dto;

import com.teamfab.mealmatch.enums.DietProfile;
import com.teamfab.mealmatch.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private Role role;

    private DietProfile dietProfile;
}

