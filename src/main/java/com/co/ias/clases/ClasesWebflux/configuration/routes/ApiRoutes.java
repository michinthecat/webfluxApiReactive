package com.co.ias.clases.ClasesWebflux.configuration.routes;

import com.co.ias.clases.ClasesWebflux.service.EnrollmentHandler;
import com.co.ias.clases.ClasesWebflux.service.StudentHandler;
import com.co.ias.clases.ClasesWebflux.service.SubjectHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ApiRoutes {

    @Value("${PATH_BASE}")
    private String basePath;

    @Bean
    public RouterFunction<ServerResponse> routes(StudentHandler studentHandler,
                                                 SubjectHandler subjectHandler,
                                                 EnrollmentHandler enrollmentHandler) {
        return RouterFunctions
                .nest(path(basePath),
                        RouterFunctions
                                .route()
                                .GET("/students", studentHandler::readStudents)
                                .POST("/students", studentHandler::createStudent)
                                .GET("/students/{id}", studentHandler::readStudent)
                                .PUT("/students/{id}", studentHandler::updateStudent)
                                .DELETE("/students/{id}", studentHandler::deleteStudent)
                                .GET("/subjects", subjectHandler::readSubjects)
                                .POST("/subjects", subjectHandler::createSubject)
                                .GET("/subjects/{id}", subjectHandler::readSubject)
                                .PUT("/subjects/{id}", subjectHandler::updateSubject)
                                .DELETE("/subjects/{id}", subjectHandler::deleteSubject)
                                .GET("/enrollments", enrollmentHandler::readEnrollments)
                                .POST("/enrollments", enrollmentHandler::createEnrollment)
                                .GET("/enrollments/{id}", enrollmentHandler::readEnrollment)
                                .PUT("/enrollments/{id}", enrollmentHandler::updateEnrollment)
                                .DELETE("/enrollments/{id}", enrollmentHandler::deleteEnrollment)
                                .build());
    }

}