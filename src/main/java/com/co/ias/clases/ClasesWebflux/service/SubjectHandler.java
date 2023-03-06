package com.co.ias.clases.ClasesWebflux.service;

import com.co.ias.clases.ClasesWebflux.model.Subject;
import com.co.ias.clases.ClasesWebflux.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class SubjectHandler {

    @Autowired
    private SubjectRepository subjectRepository;

    public SubjectHandler(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Mono<ServerResponse> createSubject(ServerRequest request) {
        return request
                .bodyToMono(Subject.class)
                .flatMap(subjectRepository::save)
                .flatMap(savedSubject -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .bodyValue(savedSubject))
                .onErrorResume(error -> ServerResponse
                        .status(HttpStatus.BAD_REQUEST)
                        .bodyValue("ERROR AL CREAR LA MATERIA: " + error.getMessage()));
    }

    public Mono<ServerResponse> readSubject(ServerRequest request) {

        Integer id = Integer.valueOf(request.pathVariable("id"));

        return subjectRepository.findById(id)
                .flatMap(subject -> ServerResponse.ok().bodyValue(subject))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("No se encontró la materia con el id " + id))
                .onErrorResume(error -> ServerResponse
                        .badRequest()
                        .bodyValue("ERROR AL LEER LA MATERIA: " + error.getMessage()));
    }

    public Mono<ServerResponse> readSubjects(ServerRequest request) {
        return subjectRepository.findAll()
                .collectList()
                .flatMap(subjectList -> {
                    if (subjectList.isEmpty()) {
                        return ServerResponse
                                .status(HttpStatus.NOT_FOUND)
                                .bodyValue("No se encontraron materias en la base de datos.");
                    } else {
                        return ServerResponse
                                .status(HttpStatus.OK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(BodyInserters.fromValue(subjectList));
                    }
                })
                .onErrorResume(error -> ServerResponse
                        .status(HttpStatus.BAD_REQUEST)
                        .bodyValue("ERROR AL LEER LAS MATERIAS: " + error.getMessage()));
    }

    public Mono<ServerResponse> updateSubject(ServerRequest request) {

        Integer id = Integer.valueOf(request.pathVariable("id"));

        return request.bodyToMono(Subject.class)
                .flatMap(updatedSubject -> subjectRepository.findById(id)
                        .flatMap(existingSubject -> {
                            existingSubject.setName(updatedSubject.getName());
                            return subjectRepository.save(existingSubject);
                        })
                        .flatMap(updated -> ServerResponse.ok().bodyValue(updated))
                        .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue("No existe la materia: " + id))
                )
                .onErrorResume(error -> ServerResponse
                        .badRequest()
                        .bodyValue("ERROR AL ACTUALIZAR LA MATERIA: " + error.getMessage()));
    }

    public Mono<ServerResponse> deleteSubject(ServerRequest request) {
        Integer id = Integer.valueOf(request.pathVariable("id"));

        return subjectRepository.findById(id)
                .flatMap(existingSubject ->
                        subjectRepository.delete(existingSubject)
                                .then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue("Materia con " + id + " no encontrada"))
                .onErrorResume(error -> ServerResponse
                        .badRequest()
                        .bodyValue("ERROR AL ELIMINAR LA MATERIA: " + error.getMessage()));
    }

}
