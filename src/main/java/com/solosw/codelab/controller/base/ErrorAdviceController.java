package com.solosw.codelab.controller.base;

import org.eclipse.jgit.api.errors.CannotDeleteCurrentBranchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


public class ErrorAdviceController {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        // 创建错误响应对象
        ErrorResponse errorResponse =  ErrorResponse.create(ex,HttpStatusCode.valueOf(500),ex.getMessage());

        // 返回 HTTP 500 状态码和错误信息
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(CannotDeleteCurrentBranchException.class)
    public ResponseEntity<ErrorResponse> handleCannotDeleteCurrentBranchException(Exception ex) {
        // 创建错误响应对象
        ErrorResponse errorResponse =  ErrorResponse.create(ex,HttpStatusCode.valueOf(500),ex.getMessage());
        System.err.println(ex);
        // 返回 HTTP 500 状态码和错误信息
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
