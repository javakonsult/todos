package com.emirates.todo.repository;

import com.emirates.todo.model.Todo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * JPa repository for {@link Todo}
 * Created by Sajjad Ali on 2017-10-10.
 */
public interface TodoRepository extends JpaRepository<Todo, Long> {

  List<Todo> findByCompleted(Boolean completed);
}
