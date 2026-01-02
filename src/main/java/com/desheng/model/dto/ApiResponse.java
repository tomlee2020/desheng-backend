package com.desheng.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    @Builder.Default
    private Integer code = 200;
    
    @Builder.Default
    private String message = "success";
    
    private T data;
    
    private String error;
    
    private Object details;

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(Integer code, String message, String error) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .error(error)
                .build();
    }

    public static <T> ApiResponse<T> error(Integer code, String message, String error, Object details) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .error(error)
                .details(details)
                .build();
    }
}