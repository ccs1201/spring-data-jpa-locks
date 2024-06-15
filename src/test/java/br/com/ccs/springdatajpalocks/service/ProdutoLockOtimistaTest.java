package br.com.ccs.springdatajpalocks.service;

import br.com.ccs.springdatajpalocks.entities.Produto;
import br.com.ccs.springdatajpalocks.entities.ProdutoLockOtimista;
import br.com.ccs.springdatajpalocks.repositories.ProdutoLockOtimistaRepository;
import br.com.ccs.springdatajpalocks.repositories.ProdutoRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class ProdutoLockOtimistaTest {

    @Autowired
    private ProdutoLockOtimistaRepository repositoryComVersion;
    @Autowired
    private ProdutoRepository repositorySemVersion;
    private static final UUID id = UUID.fromString("48446e80-2507-454b-9071-80711e1adafc");


    @Test
    void testUpdateConcorrenteComVersionValorVendaDeveriaSer200() {

        log.info("\n>>>>>>> Concorrência com Version Valor venda deve ser igual a $200.00 <<<<<<<<<<\n");

        var produto = ProdutoLockOtimista.builder()
                .id(id)
                .nome("Produto 1")
                .valorCompra(BigDecimal.valueOf(100.00))
                .valorVenda(BigDecimal.valueOf(150.00))
                .versao(0L)
                .build();

        repositoryComVersion.saveAndFlush(produto);

        var ex = assertThrows(Exception.class, this::iniciarConcorreciaComVersion);

        assertEquals(ObjectOptimisticLockingFailureException.class, ex.getCause().getClass());

        log.info("Thread principal - Recuperando produto do BD");
        produto = repositoryComVersion.findById(id).orElseThrow();
        log.info("Thread principal - Produto recuperado {}", produto);
        assertEquals(BigDecimal.valueOf(200.00).setScale(2), produto.getValorVenda());
    }

    private void iniciarConcorreciaComVersion() {
        var futures = new LinkedList<CompletableFuture>();

        futures.add(CompletableFuture.runAsync(() -> {
            log.info("Thread 1 iniciada");
            log.info("Thread 1 - Recuperando produto do db");
            var produto = repositoryComVersion.findById(id).orElseThrow();
            log.info("Thread 1 - Produto recuperado {}", produto);

            log.info("Thread 1 - Aguardando 1 segundo");
            sleep(1);

            log.info("Thread 1 - Atualizando valor venda do produto para 200");
            produto.setValorVenda(BigDecimal.valueOf(200.00));
            log.info("Thread 1 - Salvando produto");
            repositoryComVersion.saveAndFlush(produto);
        }, ForkJoinPool.commonPool()));

        futures.add(CompletableFuture.runAsync(() -> {
            log.info("Thread 2 - Recuperando produto do db");
            var produto = repositoryComVersion.findById(id).orElseThrow();
            log.info("Thread 2 - Produto recuperado {}", produto);
            log.info("Thread 2 iniciada");

            log.info("Thread 2 - Aguardando 3 segundo");
            sleep(3);

            log.info("Thread 2 - Atualizando valor venda do produto para 300");
            produto.setValorVenda(BigDecimal.valueOf(300.00));
            log.info("Thread 2 - Salvando produto");
            repositoryComVersion.saveAndFlush(produto);
        }, ForkJoinPool.commonPool()));

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[2])).join();
        /*
        O Log nunca é atingido, pois o lock otimista funciona
        causando um CompletationException que "embrulha"
        uma org.springframework.orm.ObjectOptimisticLockingFailureException
         */
        log.info("Concorrência encerrada");
    }

    @Test
    void testUpdateConcorrenteSemVersionValorVendaDeveriaSer200() {

        log.info("\n>>>>>>> Concorrência sem Version Valor venda deve ser igual a $200.00 <<<<<<<<<<\n");

        var produto = Produto.builder()
                .id(id)
                .nome("Produto 1")
                .valorCompra(BigDecimal.valueOf(100.00))
                .valorVenda(BigDecimal.valueOf(150.00))
                .build();

        repositorySemVersion.saveAndFlush(produto);
        repositorySemVersion.getEntityManager().clear();

        assertDoesNotThrow(this::iniciarConcorreciaSemVersion);

        log.info("Thread principal - Recuperando produto do BD");
        produto = repositorySemVersion.findById(id).orElseThrow();
        log.info("Thread principal - Produto recuperado {}", produto);

        /*
         Se o lock otimista funcionasse sem o @Version o valor deveria ser $200
         como no teste com Version, porém como o lock otimista não funciona
         temos como resultado $300 senda vencedora thread 2 e nenhuma exception lançada.
         */
        assertEquals(BigDecimal.valueOf(200.00).setScale(2), produto.getValorVenda());
    }

    private void iniciarConcorreciaSemVersion() {
        var futures = new LinkedList<CompletableFuture>();

        futures.add(CompletableFuture.runAsync(() -> {
            log.info("Thread 1 iniciada");
            log.info("Thread 1 - Recuperando produto do db");
            var produto = repositorySemVersion.findById(id).orElseThrow();
            log.info("Thread 1 - Produto recuperado {}", produto);

            log.info("Thread 1 - Aguardando 1 segundo");
            sleep(1);

            log.info("Thread 1 - Atualizando valor venda do produto para 200");
            produto.setValorVenda(BigDecimal.valueOf(200.00));
            log.info("Thread 1 - Salvando produto");
            repositorySemVersion.saveAndFlush(produto);
        }, Executors.newVirtualThreadPerTaskExecutor()));

        futures.add(CompletableFuture.runAsync(() -> {
            log.info("Thread 2 - Recuperando produto do db");
            var produto = repositorySemVersion.findById(id).orElseThrow();
            log.info("Thread 2 - Produto recuperado {}", produto);
            log.info("Thread 2 iniciada");

            log.info("Thread 2 - Aguardando 3 segundo");
            sleep(3);

            log.info("Thread 2 - Atualizando valor venda do produto para 300");
            produto.setValorVenda(BigDecimal.valueOf(300.00));
            log.info("Thread 2 - Salvando produto");
            repositorySemVersion.saveAndFlush(produto);
        }, Executors.newVirtualThreadPerTaskExecutor()));

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[2])).join();
        log.info("Concorrência encerrada");
    }

    private static void sleep(long segundos) {
        try {
            Thread.sleep(segundos * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
