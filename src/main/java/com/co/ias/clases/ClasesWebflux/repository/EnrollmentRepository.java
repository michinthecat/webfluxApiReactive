package com.co.ias.clases.ClasesWebflux.repository;

import com.co.ias.clases.ClasesWebflux.model.Enrollment;
import com.co.ias.clases.ClasesWebflux.model.Student;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface EnrollmentRepository extends ReactiveCrudRepository<Enrollment, Integer>
{
    Mono<Enrollment> findByStudent(Student student);

}
