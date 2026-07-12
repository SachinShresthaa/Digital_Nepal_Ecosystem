package np.gov.digital.auth.mapper;

import np.gov.digital.auth.Dto.UserDto;
import np.gov.digital.auth.modal.User;

import java.time.LocalDateTime;

public class UserMapper {

    public static UserDto toDto(User user){

        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .role(user.getRole())
                .provinceId(user.getProvinceId())
                .municipalityId(user.getMunicipalityId())
                .wardId(user.getWardId())
                .active(user.getActive())
                .locked(user.getLocked())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .lockUntil(user.getLockUntil())
                .lastLoginAt(user.getLastLoginAt())
                .passwordChangedAt(user.getPasswordChangedAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }



    public static User toEntity(UserDto userDto){

        return User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .role(userDto.getRole())
                .password(userDto.getPassword())
                .provinceId(userDto.getProvinceId())
                .municipalityId(userDto.getMunicipalityId())
                .wardId(userDto.getWardId())
                .active(userDto.getActive())
                .locked(userDto.getLocked())
                .failedLoginAttempts(userDto.getFailedLoginAttempts())
                .lockUntil(userDto.getLockUntil())
                .lastLoginAt(userDto.getLastLoginAt())
                .passwordChangedAt(userDto.getPasswordChangedAt())
                .createdAt(userDto.getCreatedAt())
                .updatedAt(userDto.getUpdatedAt())
                .build();
    }



}
