
package br.com.vinicius.controleestoque.repository;

import br.com.vinicius.controleestoque.entity.Movimentacao;
import br.com.vinicius.controleestoque.enums.TipoMovimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimentacaoRepository
        extends JpaRepository<Movimentacao, Long> {

    List<Movimentacao> findTop5ByOrderByDataMovimentacaoDesc();

    List<Movimentacao> findAllByOrderByDataMovimentacaoDesc();

    List<Movimentacao>
    findByTipoMovimentacaoOrderByDataMovimentacaoDesc(
            TipoMovimentacao tipoMovimentacao
    );

    List<Movimentacao>
    findByDataMovimentacaoBetweenOrderByDataMovimentacaoDesc(
            LocalDateTime dataInicial,
            LocalDateTime dataFinal
    );

    List<Movimentacao> findByProduto_Id(Long produtoId);

    boolean existsByUsuario_Id(Long usuarioId);
}
