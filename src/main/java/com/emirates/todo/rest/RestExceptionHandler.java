package com.emirates.todo.rest;

import com.emirates.todo.exception.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

/**
 * Created by Sajjad Ali on 2017-10-10.
 */
@ControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler( {IllegalStateException.class, IllegalArgumentException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleBadRequests(Exception e) {
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler( { ConstraintViolationException.class, DataIntegrityViolationException.class})
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handleHibernateException(Exception e) {
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler( {EntityNotFoundException.class})
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handleNotFound(Exception e) {
    return new ErrorResponse(e.getMessage());
  }

  public static class ErrorResponse {
    private final String message;

    public ErrorResponse(String message) {
      this.message = message;
    }

    public String getMessage() {
      return message;
    }
  }
}
