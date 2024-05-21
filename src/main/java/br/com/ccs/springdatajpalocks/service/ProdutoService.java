package br.com.ccs.springdatajpalocks.service;

import br.com.ccs.springdatajpalocks.entities.Produto;
import br.com.ccs.springdatajpalocks.repositories.ProdutoRepository;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProdutoService {

    private final ProdutoRepository repository;

    @PostConstruct
    void init() {
        var produto = Produto.builder()
                .id(UUID.fromString("48446e80-2507-454b-9071-80711e1adafc"))
                .nome("Produto 1")
                .valorCompra(BigDecimal.valueOf(100.00))
                .valorVenda(BigDecimal.valueOf(150.00))
                .build();

        repository.saveAndFlush(produto);
    }

    @Transactional
    public void findComLockNoRepository(UUID id) {
        checkTransacaoELockMode(repository.findByIdLocking(id)
                .orElseThrow(() -> new RuntimeException("Produto inexistente.")));
    }

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public void findComLockNoService(UUID id) {
        checkTransacaoELockMode(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto inexistente.")));
    }

    @Transactional
    public void findComLockNoChamador(UUID id) {
        checkTransacaoELockMode(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto inexistente.")));
    }

    @Transactional
    public void findSemLock(UUID id) {
        checkTransacaoELockMode(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto inexistente.")));
    }

    @Transactional
    public void findComLockViaEntityManager(UUID id) {
        var produto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto inexistente."));

        repository.getEntityManager().lock(produto, LockModeType.PESSIMISTIC_WRITE);

        checkTransacaoELockMode(produto);
    }

    public void findSemTransacao(UUID id) {
        checkTransacaoELockMode(repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto inexistente.")));
    }

    private void checkTransacaoELockMode(Produto produto) {
        log.info("Transação ativa: {}", TransactionSynchronizationManager.isActualTransactionActive());
        log.info("Lock Mode: {}", repository.getEntityManager().getLockMode(produto));
    }
}

