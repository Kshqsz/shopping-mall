package cn.edu.usts.cs2022.pojo.dto;


import lombok.Data;

@Data
public class ChangePasswordDto {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}
