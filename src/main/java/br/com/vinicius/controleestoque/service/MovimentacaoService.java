package br.com.vinicius.controleestoque.service;

import br.com.vinicius.controleestoque.entity.Movimentacao;
import br.com.vinicius.controleestoque.entity.Produto;
import br.com.vinicius.controleestoque.entity.Usuario;
import br.com.vinicius.controleestoque.enums.TipoMovimentacao;
import br.com.vinicius.controleestoque.exception.EstoqueInsuficienteException;
import br.com.vinicius.controleestoque.repository.MovimentacaoRepository;
import br.com.vinicius.controleestoque.repository.ProdutoRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioService usuarioService;

    public MovimentacaoService(
            MovimentacaoRepository movimentacaoRepository,
            ProdutoRepository produtoRepository,
            UsuarioService usuarioService) {

        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
        this.usuarioService = usuarioService;
    }

    public List<Movimentacao> listarTodas() {
        return movimentacaoRepository.findAll();
    }

    public List<Movimentacao> listarUltimasCinco() {
        return movimentacaoRepository
                .findTop5ByOrderByDataMovimentacaoDesc();
    }

    public List<Movimentacao> listarTodasOrdenadas() {
        return movimentacaoRepository
                .findAllByOrderByDataMovimentacaoDesc();
    }

    public List<Movimentacao> listarPorTipo(
            TipoMovimentacao tipo) {

        return movimentacaoRepository
                .findByTipoMovimentacaoOrderByDataMovimentacaoDesc(
                        tipo
                );
    }

    public List<Movimentacao> listarPorPeriodo(
            LocalDateTime dataInicial,
            LocalDateTime dataFinal) {

        return movimentacaoRepository
                .findByDataMovimentacaoBetweenOrderByDataMovimentacaoDesc(
                        dataInicial,
                        dataFinal
                );
    }

    @Transactional
    public Movimentacao salvar(Movimentacao movimentacao) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String nomeUsuario =
                authentication.getName();

        Usuario usuarioLogado =
                usuarioService.buscarPorUsuario(nomeUsuario);

        movimentacao.setUsuario(usuarioLogado);

        Produto produto =
                movimentacao.getProduto();

        if (movimentacao.getTipoMovimentacao()
                == TipoMovimentacao.ENTRADA) {

            produto.setQuantidadeAtual(
                    produto.getQuantidadeAtual()
                            + movimentacao.getQuantidade()
            );

        } else {

            if (produto.getQuantidadeAtual()
                    < movimentacao.getQuantidade()) {

                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para realizar esta movimentação."
                );
            }

            produto.setQuantidadeAtual(
                    produto.getQuantidadeAtual()
                            - movimentacao.getQuantidade()
            );
        }

        produtoRepository.save(produto);

        movimentacao.setDataMovimentacao(
                LocalDateTime.now()
        );

        return movimentacaoRepository.save(movimentacao);
    }
}