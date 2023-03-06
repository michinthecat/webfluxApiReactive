package com.co.ias.clases.ClasesWebflux.service;

import com.co.ias.clases.ClasesWebflux.model.Enrollment;
import com.co.ias.clases.ClasesWebflux.model.Subject;
import com.co.ias.clases.ClasesWebflux.repository.EnrollmentRepository;
import com.co.ias.clases.ClasesWebflux.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EnrollmentHandler {

    @Autowired
    private EnrollmentRepository enrollmentRepository;
    private StudentRepository studentRepository;

    public EnrollmentHandler(EnrollmentRepository enrollmentRepository) {
        this.enrollmentRepository = enrollmentRepository;
    }

    public Mono<ServerResponse> createEnrollment(ServerRequest request) {
        return request
                .bodyToMono(Enrollment.class)
                .flatMap(enrollmentRepository::save)
                .flatMap(savedEnrollment -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(savedEnrollment))
                .onErrorResume(error -> {
                    return ServerResponse
                            .status(HttpStatus.BAD_REQUEST)
                            .bodyValue("MENSAJE MAL DADO POR SQL: ".concat(error.getMessage()));
                });
    }

    public Mono<ServerResponse> readEnrollment(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));

        return enrollmentRepository.findById(id)
                .flatMap(enrollment -> ServerResponse.ok().bodyValue(enrollment))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("No se encontró la inscripción con el id " + id))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue("ERROR AL LEER LA INSCRIPCIÓN: " + error.getMessage()));
    }

    public Mono<ServerResponse> readEnrollments(ServerRequest request) {
        return enrollmentRepository.findAll()
                .collectList()
                .flatMap(enrollmentList -> {
                    if (enrollmentList.isEmpty()) {
                        return ServerResponse
                                .status(HttpStatus.NOT_FOUND)
                                .bodyValue("No se encontraron inscripciones en la base de datos.");
                    } else {
                        return ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(enrollmentList));
                    }
                })
                .onErrorResume(error -> {
                    return ServerResponse
                            .status(HttpStatus.BAD_REQUEST)
                            .bodyValue("MENSAJE MAL DADO POR SQL: ".concat(error.getMessage()));
                });
    }

    public Mono<ServerResponse> readSubjectByStudent(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));

        return studentRepository.findById(id)
                .flatMap(student -> enrollmentRepository.findByStudent(student))
                .flatMap(enrollments -> {
                    if (enrollments.isEmpty()) {
                        return ServerResponse
                                .status(HttpStatus.NOT_FOUND)
                                .bodyValue("No se encontraron materias para el estudiante con el id " + id);
                    } else {
                        List<Subject> subjects = enrollments.stream()
                                .map(Enrollment::getSubject)
                                .collect(Collectors.toList());
                        return ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(subjects));
                    }
                })
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("No se encontró el estudiante con el id " + id))
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue("ERROR AL LEER LAS MATERIAS DEL ESTUDIANTE: " + error.getMessage()));
    }

    public Mono<ServerResponse> updateEnrollment(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));

        return request.bodyToMono(Enrollment.class)
                .flatMap(updatedEnrollment -> enrollmentRepository.findById(id)
                        .flatMap(existingEnrollment -> {
                            existingEnrollment.setStudent(updatedEnrollment.getStudent());
                            existingEnrollment.setSubject(updatedEnrollment.getSubject());
                            return enrollmentRepository.save(existingEnrollment);
                        })
                        .flatMap(updated -> ServerResponse.ok().bodyValue(updated))
                        .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("No existe la inscripción: " + id))
                )
                .onErrorResume(error -> ServerResponse.badRequest().bodyValue("ERROR AL ACTUALIZAR LA INSCRIPCIÓN: " + error.getMessage()));
    }

    public Mono<ServerResponse> deleteEnrollment(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));

        return enrollmentRepository.findById(id)
                .flatMap(existingEnrollment ->
                        enrollmentRepository.delete(existingEnrollment)
                                .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("Inscripción con " + id + " no encontrada"));
    }

}
