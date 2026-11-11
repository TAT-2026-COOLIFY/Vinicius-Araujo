package br.com.vinicius.controleestoque.controller;

import br.com.vinicius.controleestoque.service.MovimentacaoService;
import br.com.vinicius.controleestoque.service.ProdutoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/relatorios")
public class RelatorioController {

    private final ProdutoService produtoService;
    private final MovimentacaoService movimentacaoService;

    public RelatorioController(
            ProdutoService produtoService,
            MovimentacaoService movimentacaoService) {

        this.produtoService = produtoService;
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping
    public String estoque(Model model) {

        model.addAttribute(
                "produtos",
                produtoService.listarTodos()
        );

        return "relatorios/estoque";
    }

    @GetMapping("/estoque-baixo")
    public String estoqueBaixo(Model model) {

        model.addAttribute(
                "produtos",
                produtoService.listarProdutosComEstoqueBaixo()
        );

        return "relatorios/estoque-baixo";
    }

    @GetMapping("/movimentacoes")
    public String movimentacoes(

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataInicial,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dataFinal,

            Model model) {

        if (dataInicial != null && dataFinal != null) {

            LocalDateTime inicio =
                    dataInicial.atStartOfDay();

            LocalDateTime fim =
                    dataFinal.atTime(
                            LocalTime.MAX
                    );

            model.addAttribute(
                    "movimentacoes",
                    movimentacaoService.listarPorPeriodo(
                            inicio,
                            fim
                    )
            );

        } else {

            model.addAttribute(
                    "movimentacoes",
                    movimentacaoService.listarTodasOrdenadas()
            );
        }

        model.addAttribute(
                "dataInicial",
                dataInicial
        );

        model.addAttribute(
                "dataFinal",
                dataFinal
        );

        return "relatorios/movimentacoes";
    }
}