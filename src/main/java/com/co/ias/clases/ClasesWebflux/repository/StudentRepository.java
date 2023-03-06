package com.co.ias.clases.ClasesWebflux.repository;

import com.co.ias.clases.ClasesWebflux.model.Student;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends ReactiveCrudRepository<Student, Integer> {
}
