package com.emirates.todo.exception;

import com.emirates.todo.model.Todo;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Sajjad Ali on 2017-10-10.
 */
public class EntityNotFoundException extends RuntimeException {

  public EntityNotFoundException(Long id) {
    super(String.format("%s with id = '%d' does not exist", Todo.class.getSimpleName(), id));
  }
}
