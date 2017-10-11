package com.emirates.todo;

import com.emirates.todo.model.Todo;
import com.emirates.todo.repository.TodoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class TodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodoApplication.class, args);
	}


	@Bean
	public CommandLineRunner demo(TodoRepository repository) {
		return (args) -> {
			// save a couple of customers
			repository.save(givenTodo("Todo Spring boot app", false));
			repository.save(givenTodo("Emirates todos app", true));
			repository.save(givenTodo("Angularjs todos UI", false));
			repository.save(givenTodo("Jump from Khalifa Burj", false));
		};
	}

	private Todo givenTodo(String title, Boolean completed){
		Todo todo = new Todo();
		todo.setTitle(title);
		todo.setCompleted(completed);
		todo.setCreatedAt(LocalDateTime.now());
		return todo;
	}

}
