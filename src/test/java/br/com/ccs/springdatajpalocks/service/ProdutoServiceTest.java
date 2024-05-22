package br.com.ccs.springdatajpalocks.service;

import jakarta.inject.Inject;
import jakarta.persistence.LockModeType;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectDeletedException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

    /**
     * <p>Aqui como a entidade não fica com {@link org.hibernate.engine.spi.Status#MANAGED}.</p>
     * <p>Então o hibernate executa o select e logo em seguida remove o objeto
     * do contexto gerenciado, o que acarreta na {@link ObjectDeletedException} sendo lançada,
     * quando tentarmos verificar o {@link org.hibernate.LockMode}.</p>
     * <p>Isso não significa que o produto foi excluído da base, ele apenas
     * não esta presente no contexto de gerenciamento do Hibernate.
     * A prova disto é que no find subsequente não é lançada uma exceção
     * e é impresso {@code Transação ativa: false} .</p>
     */
    @Test
    void testComReadOnly() {
        log.info("Teste com readOnly");
        assertThrows(ObjectDeletedException.class, () ->
                produtoService.findComReadOnly(id));

        assertDoesNotThrow(() ->
                produtoService.findSemTransacao(id));
    }
}