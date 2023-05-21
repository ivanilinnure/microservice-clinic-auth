package com.clinic.auth.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;

@Value
@Builder
public class ErrorDto implements Serializable {

    @Builder.Default
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    LocalDateTime timestamp = LocalDateTime.now();

    Integer status;

    String error;

    String message;

    @Builder.Default
    String path = "";

}
