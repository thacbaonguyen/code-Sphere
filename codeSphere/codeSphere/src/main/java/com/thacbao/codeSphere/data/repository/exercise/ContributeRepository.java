package com.thacbao.codeSphere.data.repository.exercise;

import com.thacbao.codeSphere.entities.core.Contribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContributeRepository extends JpaRepository<Contribute, Integer>, ContributeRepositoryCustom {
}
