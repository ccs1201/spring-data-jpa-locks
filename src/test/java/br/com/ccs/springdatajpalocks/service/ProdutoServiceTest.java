package br.com.ccs.springdatajpalocks.service;

import br.com.ccs.springdatajpalocks.entities.Produto;
import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TransactionRequiredException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@SpringBootTest
@Slf4j
class ProdutoServiceTest {

    @Inject
    private ProdutoService produtoService;
    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = Produto.builder()
                .id(UUID.fromString("48446e80-2507-454b-9071-80711e1adafc"))
                .build();
    }

    @Test
    void testComLockNoRepository() {
        log.info("Teste ComLockNoRepository");
        produtoService.findComLockNoRepository(produto.getId());
    }

    @Test
    void testComLockNoService() {
        log.info("Teste ComLockNoService");
        produtoService.findComLockNoService(produto.getId());
    }

    @Test
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void testComLockNoChamador() {
        log.info("Teste ComLockNoChamador");
        produtoService.findComLockNoChamador(produto.getId());
    }

    @Test
    void testSemLock() {
        log.info("Teste semLock");
        produtoService.findSemLock(produto.getId());
    }

    @Test
    void testComLockViaEntityManager() {
        log.info("Teste ComLockViaEntityManager");
        produtoService.findComLockViaEntityManager(produto.getId());
    }

    @Test
    void testSemTransacao() {
        log.info("Teste SemTransacao");
        Assertions.assertThrows(TransactionRequiredException.class, () ->
                produtoService.findSemTransacao(produto.getId()));
    }
}