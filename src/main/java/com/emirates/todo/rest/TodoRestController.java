package com.emirates.todo.rest;

import com.emirates.todo.exception.EntityNotFoundException;
import com.emirates.todo.model.Todo;
import com.emirates.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST controller to perform CRUD operations  on {@link Todo}
 * Created by Sajjad Ali on 2017-10-10.
 */

@RestController
@RequestMapping(value = TodosRestPath.TODOS_REST_PATH)
public class TodoRestController {

  private final TodoRepository todoRepository;

  @Autowired
  public TodoRestController(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;
  }

  @GetMapping("/v1")
  public ResponseEntity<List<Todo>> getAllTodos() {
    Sort sortByCreatedAtDesc = new Sort(Sort.Direction.DESC, "createdAt");
    List<Todo> todos = todoRepository.findAll(sortByCreatedAtDesc);
    return new ResponseEntity<>(todos, HttpStatus.OK);
  }

  @GetMapping("/v1" + TodosRestPath.COMPLETED)
  public ResponseEntity<List<Todo>> getAllCompletedTodos() {

    List<Todo> todos = todoRepository.findByCompleted(true);
    return new ResponseEntity<>(todos, HttpStatus.OK);
  }

  @GetMapping("/v1" + TodosRestPath.PENDING)
  public ResponseEntity<List<Todo>> getAllPendingTodos() {

    List<Todo> todos = todoRepository.findByCompleted(false);
    return new ResponseEntity<>(todos, HttpStatus.OK);
  }


  @GetMapping(value = "/v1/{id}")
  public ResponseEntity<Todo> getTodoById(@PathVariable("id") Long id) {
    Todo todo = todoRepository.findOne(id);
    if (todo == null) {
      throw new EntityNotFoundException(id);
    } else {
      return new ResponseEntity<>(todo, HttpStatus.OK);
    }
  }

  @PostMapping("/v1")
  public ResponseEntity<Todo> createTodo(@RequestBody Todo todo) {
    todo.setCompleted(false);
    todo.setCreatedAt(LocalDateTime.now());
    try {
      todo = todoRepository.save(todo);
      return new ResponseEntity<>(todo, HttpStatus.OK);
    }catch (DataIntegrityViolationException e){
      return new ResponseEntity<>(todo, HttpStatus.CONFLICT);
    }
  }

  @PatchMapping(value = "/v1/{id}")
  public ResponseEntity<Todo> updateTodo(@PathVariable("id") Long id, @RequestBody Todo todo) {
    Todo todoData = todoRepository.findOne(id);
    if (todoData == null) {
      throw new EntityNotFoundException(id);
    }
    todoData.setTitle(todo.getTitle());
    todoData.setCompleted(todo.getCompleted());
    Todo updatedTodo = todoRepository.save(todoData);
    return new ResponseEntity<>(updatedTodo, HttpStatus.OK);
  }

  @DeleteMapping(value = "/v1/{id}")
  public ResponseEntity<String> deleteTodo(@PathVariable("id") Long id) {
    todoRepository.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

}
