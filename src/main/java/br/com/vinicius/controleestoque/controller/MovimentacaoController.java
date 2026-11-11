package br.com.vinicius.controleestoque.controller;

import br.com.vinicius.controleestoque.entity.Movimentacao;
import br.com.vinicius.controleestoque.exception.EstoqueInsuficienteException;
import br.com.vinicius.controleestoque.service.MovimentacaoService;
import br.com.vinicius.controleestoque.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;
    private final ProdutoService produtoService;

    public MovimentacaoController(
            MovimentacaoService movimentacaoService,
            ProdutoService produtoService) {

        this.movimentacaoService = movimentacaoService;
        this.produtoService = produtoService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "movimentacoes",
                movimentacaoService.listarTodas()
        );

        return "movimentacoes/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {

        model.addAttribute(
                "movimentacao",
                new Movimentacao()
        );

        model.addAttribute(
                "produtos",
                produtoService.listarTodos()
        );

        return "movimentacoes/form";
    }

    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute Movimentacao movimentacao,
            Model model) {

        try {

            movimentacaoService.salvar(movimentacao);

            return "redirect:/movimentacoes";

        } catch (EstoqueInsuficienteException e) {

            model.addAttribute(
                    "erro",
                    e.getMessage()
            );

            model.addAttribute(
                    "movimentacao",
                    movimentacao
            );

            model.addAttribute(
                    "produtos",
                    produtoService.listarTodos()
            );

            return "movimentacoes/form";
        }
    }
}