package br.com.ccs.springdatajpalocks.repositories.impl;

import br.com.ccs.springdatajpalocks.repositories.CustomRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

public class CustomRepositoryImpl implements CustomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }
}
