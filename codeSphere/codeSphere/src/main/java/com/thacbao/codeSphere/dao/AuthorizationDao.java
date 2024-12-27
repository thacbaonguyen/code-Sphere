package com.thacbao.codeSphere.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;

@Repository
public class AuthorizationDao {
    @PersistenceContext
    private javax.persistence.EntityManager entityManager;


}
