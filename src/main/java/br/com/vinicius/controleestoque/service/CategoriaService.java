
package br.com.vinicius.controleestoque.service;

import br.com.vinicius.controleestoque.entity.Categoria;
import br.com.vinicius.controleestoque.entity.Produto;
import br.com.vinicius.controleestoque.repository.CategoriaRepository;
import br.com.vinicius.controleestoque.repository.ProdutoRepository;
import br.com.vinicius.controleestoque.repository.MovimentacaoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final MovimentacaoRepository movimentacaoRepository;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            ProdutoRepository produtoRepository,
            MovimentacaoRepository movimentacaoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.produtoRepository = produtoRepository;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria salvar(Categoria categoria) {
        String nome = categoria.getNome().trim();

        Optional<Categoria> existente =
                categoriaRepository.findByNomeIgnoreCase(nome);

        if (existente.isPresent()
                && !existente.get().getId().equals(categoria.getId())) {
            throw new RuntimeException(
                    "Já existe uma categoria com esse nome."
            );
        }

        categoria.setNome(nome);
        return categoriaRepository.save(categoria);
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Categoria não encontrada."));
    }

    @Transactional
    public void excluir(Long id) {
        Categoria categoria = buscarPorId(id);

        List<Produto> produtos =
                produtoRepository.findByCategoria_Id(id);

        for (Produto produto : produtos) {
            var movimentacoes =
                    movimentacaoRepository.findByProduto_Id(
                            produto.getId()
                    );

            movimentacaoRepository.deleteAll(movimentacoes);
        }

        produtoRepository.deleteAll(produtos);
        categoriaRepository.delete(categoria);
    }

    public long contar() {
        return categoriaRepository.count();
    }
}
