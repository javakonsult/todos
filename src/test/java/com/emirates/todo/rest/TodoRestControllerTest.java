package com.emirates.todo.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.emirates.todo.model.Todo;
import com.emirates.todo.repository.TodoRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;


public class TodoRestControllerTest extends RestTesting {

  private String restUrl = TodosRestPath.TODOS_REST_PATH + "/v1";

  private String test_title = "create todo app for emirates";

  @Autowired
  private TodoRepository todoRepository;

  @Before
  public void cleanUp() {
    todoRepository.deleteAll();
  }


  @Test
  public void testGetAllTodos() throws Exception {
    todoRepository.save(givenTodo(test_title, false));
    final ResponseEntity<Todo[]> responseEntity = restTemplate.getForEntity(restUrl,
        Todo[].class);
    List<Todo> response = Arrays.asList(responseEntity.getBody());

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response).isNotNull();
    assertThat(response).hasSize(1);
  }

  @Test
  public void testGetTodoById_shouldReturnTodo() throws Exception {
    Todo persistedTodo = todoRepository.save(givenTodo(test_title, true));

    String url = restUrl + "/" + persistedTodo.getId();

    final ResponseEntity<Todo> responseEntity = restTemplate.getForEntity(url, Todo.class);
    Todo response = responseEntity.getBody();

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response).isNotNull();
    assertThat(response.getId()).isEqualTo(persistedTodo.getId());
    assertThat(response.getTitle()).isEqualTo(test_title);
    assertThat(response.getCompleted()).isTrue();
  }

  @Test
  public void testGetTodoById_shouldReturn404() throws Exception {
    Long nonExistingTodoId = 2L;
    String url = restUrl + "/" + nonExistingTodoId;

    final ResponseEntity<Todo> responseEntity = restTemplate.getForEntity(url, Todo.class);
    Todo response = responseEntity.getBody();

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response).isNotNull();
    assertThat(response.getId()).isNull();
    assertThat(response.getTitle()).isNull();
    assertThat(response.getCompleted()).isNull();
  }

  @Test
  public void testCreateTodo_shouldCreateNewTodo() throws Exception {

    final HttpEntity<Todo> requestHttpEntity = new HttpEntity<>(givenTodo(test_title, false));

    final ResponseEntity<Todo> responseEntity =
        restTemplate.postForEntity(restUrl, requestHttpEntity, Todo.class);

    Long id = responseEntity.getBody().getId();

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(todoRepository.findAll()).hasSize(1);

    Todo persistedTodo = todoRepository.findOne(id);

    assertThat(persistedTodo.getId()).isEqualTo(id);
    assertThat(persistedTodo.getTitle()).isEqualTo(test_title);
    assertThat(persistedTodo.getCreatedAt()).isNotNull();
    assertThat(persistedTodo.getCompleted()).isFalse();
  }

  @Test
  public void testCreateTodo_notUniqueTitle_shouldReturnStatusCode409() throws Exception {
    todoRepository.save(givenTodo(test_title,true));
    final HttpEntity<Todo> requestHttpEntity = new HttpEntity<>(givenTodo(test_title, false));

    final ResponseEntity<Todo> responseEntity =
        restTemplate.postForEntity(restUrl, requestHttpEntity, Todo.class);

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
  }

  @Test
  public void testDeleteTodo() throws Exception {
    Todo persistedTodo = todoRepository.save(givenTodo(test_title, true));

    String url = restUrl + "/" + persistedTodo.getId();
    restTemplate.delete(url);

    assertThat(todoRepository.findOne(persistedTodo.getId())).isNull();
  }

  @Test
  public void testCompletedTodos() throws Exception {
    Todo persistedTodo1 = todoRepository.save(givenTodo("task 1", true));
    todoRepository.save(givenTodo("task 2", false));
    Todo persistedTodo3 = todoRepository.save(givenTodo("task 3", true));

    String url = restUrl + TodosRestPath.COMPLETED;

    final ResponseEntity<Todo[]> responseEntity = restTemplate.getForEntity(url,
        Todo[].class);
    List<Todo> response = Arrays.asList(responseEntity.getBody());

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response).isNotNull();
    assertThat(response).hasSize(2);
    assertThat(response).extracting("id", "title", "completed").containsExactlyInAnyOrder(
            tuple(persistedTodo1.getId(), persistedTodo1.getTitle(), true),
            tuple(persistedTodo3.getId(), persistedTodo3.getTitle(), true));

  }

  @Test
  public void testPendingTodos() throws Exception {
    Todo persistedTodo1 = todoRepository.save(givenTodo("task 1", true));
    Todo persistedTodo2= todoRepository.save(givenTodo("task 2", false));
    Todo persistedTodo3 = todoRepository.save(givenTodo("task 3", true));

    String url = restUrl + TodosRestPath.PENDING;

    final ResponseEntity<Todo[]> responseEntity = restTemplate.getForEntity(url,
        Todo[].class);
    List<Todo> response = Arrays.asList(responseEntity.getBody());

    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response).isNotNull();
    assertThat(response).hasSize(1);
    assertThat(response).extracting("id", "title", "completed").contains(
        tuple(persistedTodo2.getId(), persistedTodo2.getTitle(), false));

  }

  private Todo givenTodo(String title, Boolean completed) {
    Todo todo = new Todo();
    todo.setTitle(title);
    todo.setCompleted(completed);
    return todo;
  }

}
