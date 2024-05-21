package br.com.ccs.springdatajpalocks.repositories;

import jakarta.persistence.EntityManager;

public interface CustomRepository {

    EntityManager getEntityManager();
}
