package com.co.ias.clases.ClasesWebflux.repository;

import com.co.ias.clases.ClasesWebflux.model.Subject;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends ReactiveCrudRepository<Subject, Integer> {
}
