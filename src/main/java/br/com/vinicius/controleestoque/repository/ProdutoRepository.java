package br.com.vinicius.controleestoque.repository;

import br.com.vinicius.controleestoque.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    @Query("""
            SELECT COUNT(p)
            FROM Produto p
            WHERE p.quantidadeAtual <= p.estoqueMinimo
            """)
    long contarProdutosComEstoqueBaixo();

    @Query("""
            SELECT p
            FROM Produto p
            WHERE p.quantidadeAtual <= p.estoqueMinimo
            ORDER BY p.quantidadeAtual ASC
            """)
    List<Produto> listarProdutosComEstoqueBaixo();

    List<Produto> findByNomeContainingIgnoreCase(String nome);

    List<Produto> findByCategoria_Id(Long categoriaId);

}