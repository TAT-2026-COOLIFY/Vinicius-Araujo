package br.com.vinicius.controleestoque.controller;

import br.com.vinicius.controleestoque.entity.Produto;
import br.com.vinicius.controleestoque.service.CategoriaService;
import br.com.vinicius.controleestoque.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final CategoriaService categoriaService;

    public ProdutoController(ProdutoService produtoService,
                             CategoriaService categoriaService) {

        this.produtoService = produtoService;
        this.categoriaService = categoriaService;
    }


    @GetMapping
    public String listar(
            @RequestParam(required = false) String nome,
            Model model) {

        if (nome != null && !nome.trim().isEmpty()) {

            model.addAttribute(
                    "produtos",
                    produtoService.buscarPorNome(nome.trim())
            );

        } else {

            model.addAttribute(
                    "produtos",
                    produtoService.listarTodos()
            );

        }

        model.addAttribute("nomeBusca", nome);

        return "produtos/lista";
    }


    @GetMapping("/novo")
    public String novo(Model model) {

        model.addAttribute("produto", new Produto());

        model.addAttribute(
                "categorias",
                categoriaService.listarTodas()
        );

        return "produtos/form";
    }


    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Produto produto) {

        produtoService.salvar(produto);

        return "redirect:/produtos";
    }


    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        model.addAttribute(
                "produto",
                produtoService.buscarPorId(id)
        );

        model.addAttribute(
                "categorias",
                categoriaService.listarTodas()
        );

        return "produtos/form";
    }


    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {

        produtoService.excluir(id);

        return "redirect:/produtos";
    }

}