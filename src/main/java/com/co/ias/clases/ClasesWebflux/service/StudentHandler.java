package com.co.ias.clases.ClasesWebflux.service;

import com.co.ias.clases.ClasesWebflux.model.Student;
import com.co.ias.clases.ClasesWebflux.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class StudentHandler {

    @Autowired
    private StudentRepository studentRepository;

    public StudentHandler(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Mono<ServerResponse> createStudent(ServerRequest request) {
        return request
                .bodyToMono(Student.class)
                .flatMap(studentRepository::save)
                .flatMap(savedStudent -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(savedStudent))
        .onErrorResume(error -> {
            return ServerResponse
                    .status(HttpStatus.BAD_REQUEST)
                    .bodyValue("MENSAJE MAL DADO POR SQL: ".concat(error.getMessage()));
        });

    }

    public Mono<ServerResponse> readStudent(ServerRequest request) {

        Integer id = Integer.valueOf(request.pathVariable("id"));

        return studentRepository.findById(id)
                .flatMap(student -> ServerResponse.ok().bodyValue(student))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("No se encontró el estudiante con el id " + id))
                .onErrorResume(error -> ServerResponse
                        .badRequest()
                        .bodyValue("ERROR AL LEER EL ESTUDIANTE: " + error.getMessage()));
    }

    public Mono<ServerResponse> readStudents(ServerRequest request) {
        return studentRepository.findAll()
                .collectList()
                .flatMap(studentList -> {
                    if (studentList.isEmpty()) {
                        return ServerResponse
                                .status(HttpStatus.NOT_FOUND)
                                .bodyValue("No se encontraron estudiantes en la base de datos.");
                    } else {
                        return ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(studentList));
                    }
                })
                .onErrorResume(error -> {
                    return ServerResponse
                            .status(HttpStatus.BAD_REQUEST)
                            .bodyValue("MENSAJE MAL DADO POR SQL: ".concat(error.getMessage()));
                });
    }

    public Mono<ServerResponse> updateStudent(ServerRequest request) {

        Integer id = Integer.valueOf(request.pathVariable("id"));

        return request.bodyToMono(Student.class)
                .flatMap(updatedStudent -> studentRepository.findById(id)
                        .flatMap(existingStudent -> {
                            existingStudent.setName(updatedStudent.getName());
                            existingStudent.setEmail(updatedStudent.getEmail());
                            return studentRepository.save(existingStudent);
                        })
                        .flatMap(updated -> ServerResponse.ok().bodyValue(updated))
                        .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("No existe el estudiante: " + id))
                )
                .onErrorResume(error -> ServerResponse
                        .badRequest()
                        .bodyValue("ERROR AL ACTUALIZAR EL ESTUDIANTE: " + error.getMessage()));
    }

    public Mono<ServerResponse> deleteStudent(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));

        return studentRepository.findById(id)
                .flatMap(existingStudent ->
                        studentRepository.delete(existingStudent)
                                .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("Estudiante con " + id + " no encontrado"))
        .onErrorResume(error -> ServerResponse
                .badRequest()
                .bodyValue("ERROR AL ELIMINAR EL ESTUDIANTE: " + error.getMessage()));
    }
}