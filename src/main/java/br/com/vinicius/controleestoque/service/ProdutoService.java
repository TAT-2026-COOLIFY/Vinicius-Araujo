
package br.com.vinicius.controleestoque.service;

import br.com.vinicius.controleestoque.entity.Produto;
import br.com.vinicius.controleestoque.repository.ProdutoRepository;
import br.com.vinicius.controleestoque.repository.MovimentacaoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public ProdutoService(
            ProdutoRepository produtoRepository,
            MovimentacaoRepository movimentacaoRepository) {
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id).orElse(null);
    }

    public Produto salvar(Produto produto) {
        if (produto.getTipoUnidade() != null) {
            produto.setTipoUnidade(
                    produto.getTipoUnidade().trim().toUpperCase()
            );
        }

        return produtoRepository.save(produto);
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Produto não encontrado."
                        ));

        var movimentacoes =
                movimentacaoRepository.findByProduto_Id(id);

        movimentacaoRepository.deleteAll(movimentacoes);
        produtoRepository.delete(produto);
    }

    public long contar() {
        return produtoRepository.count();
    }

    public long contarProdutosComEstoqueBaixo() {
        return produtoRepository.contarProdutosComEstoqueBaixo();
    }

    public List<Produto> listarProdutosComEstoqueBaixo() {
        return produtoRepository.listarProdutosComEstoqueBaixo();
    }
}
