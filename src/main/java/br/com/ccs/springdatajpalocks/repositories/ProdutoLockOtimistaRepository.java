package br.com.ccs.springdatajpalocks.repositories;

import br.com.ccs.springdatajpalocks.entities.ProdutoLockOtimista;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProdutoLockOtimistaRepository extends JpaRepository<ProdutoLockOtimista, UUID>, CustomRepository {
}