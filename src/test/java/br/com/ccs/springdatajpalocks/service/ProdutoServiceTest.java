package br.com.ccs.springdatajpalocks.service;

import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Slf4j
class ProdutoServiceTest {

    @Inject
    private ProdutoService produtoService;
    private static final UUID id = UUID.fromString("48446e80-2507-454b-9071-80711e1adafc");

    @Test
    void testComLockNoRepository() {
        log.info("Teste ComLockNoRepository");
        assertDoesNotThrow(() ->
                produtoService.findComLockNoRepository(id));
    }

    @Test
    void testComLockNoService() {
        log.info("Teste ComLockNoService");
        assertDoesNotThrow(() ->
                produtoService.findComLockNoService(id));
    }

    @Test
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    void testComLockNoChamador() {
        log.info("Teste ComLockNoChamador");
        assertDoesNotThrow(() ->
                produtoService.findComLockNoChamador(id));
    }

    @Test
    void testSemLock() {
        log.info("Teste semLock");
        assertDoesNotThrow(() ->
                produtoService.findSemLock(id));
    }

    @Test
    void testComLockViaEntityManager() {
        log.info("Teste ComLockViaEntityManager");
        assertDoesNotThrow(() ->
                produtoService.findComLockViaEntityManager(id));
    }

    @Test
    void testSemTransacao() {
        log.info("Teste SemTransacao");
        assertDoesNotThrow(() ->
                produtoService.findSemTransacao(id));
    }
}