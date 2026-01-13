package com.example.todolist.task;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        taskModel.setUserId((UUID) request.getAttribute("userId"));
        var currentDate = LocalDateTime.now();

        if(currentDate.isAfter(taskModel.getStartingDate()) || currentDate.isAfter(taskModel.getEndingDate())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro Ao validar as datas");
        }
        if(taskModel.getStartingDate().isAfter(taskModel.getEndingDate())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Data de início não pode ser ser depois da data de término");
        }
            var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var userId = request.getAttribute("userId");
        return this.taskRepository.findByUserId((UUID) userId);
    }

}
