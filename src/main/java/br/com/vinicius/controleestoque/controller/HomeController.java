package br.com.vinicius.controleestoque.controller;

import br.com.vinicius.controleestoque.entity.Movimentacao;
import br.com.vinicius.controleestoque.entity.Produto;
import br.com.vinicius.controleestoque.enums.TipoMovimentacao;
import br.com.vinicius.controleestoque.service.CategoriaService;
import br.com.vinicius.controleestoque.service.MovimentacaoService;
import br.com.vinicius.controleestoque.service.ProdutoService;
import br.com.vinicius.controleestoque.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final CategoriaService categoriaService;
    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;
    private final MovimentacaoService movimentacaoService;

    public HomeController(CategoriaService categoriaService,
                          ProdutoService produtoService,
                          UsuarioService usuarioService,
                          MovimentacaoService movimentacaoService) {

        this.categoriaService = categoriaService;
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
        this.movimentacaoService = movimentacaoService;
    }

    @GetMapping("/")
    public String home(Model model) {

        /*
         * =====================================================
         * CARDS DO DASHBOARD
         * =====================================================
         */

        model.addAttribute("totalCategorias",
                categoriaService.contar());

        model.addAttribute("totalProdutos",
                produtoService.contar());

        model.addAttribute("totalUsuarios",
                usuarioService.contar());

        model.addAttribute("estoqueBaixo",
                produtoService.contarProdutosComEstoqueBaixo());

        model.addAttribute("ultimasMovimentacoes",
                movimentacaoService.listarUltimasCinco());



        /*
         * =====================================================
         * DADOS DO GRÁFICO DE MOVIMENTAÇÕES
         * =====================================================
         *
         * Considera os últimos 6 meses.
         */

        List<Movimentacao> movimentacoes =
                movimentacaoService.listarTodasOrdenadas();

        List<String> meses = new ArrayList<>();

        List<Integer> entradas = new ArrayList<>();

        List<Integer> saidas = new ArrayList<>();


        LocalDate hoje = LocalDate.now();


        for (int i = 5; i >= 0; i--) {

            LocalDate data =
                    hoje.withDayOfMonth(1).minusMonths(i);

            int mes = data.getMonthValue();

            int ano = data.getYear();


            String nomeMes = switch (mes) {

                case 1 -> "Jan";
                case 2 -> "Fev";
                case 3 -> "Mar";
                case 4 -> "Abr";
                case 5 -> "Mai";
                case 6 -> "Jun";
                case 7 -> "Jul";
                case 8 -> "Ago";
                case 9 -> "Set";
                case 10 -> "Out";
                case 11 -> "Nov";
                case 12 -> "Dez";

                default -> "";
            };


            int totalEntradas = 0;

            int totalSaidas = 0;


            for (Movimentacao movimentacao : movimentacoes) {

                if (movimentacao.getDataMovimentacao() == null) {
                    continue;
                }


                if (movimentacao.getDataMovimentacao().getMonthValue() == mes
                        && movimentacao.getDataMovimentacao().getYear() == ano) {


                    if (movimentacao.getTipoMovimentacao()
                            == TipoMovimentacao.ENTRADA) {

                        totalEntradas += movimentacao.getQuantidade();

                    }


                    if (movimentacao.getTipoMovimentacao()
                            == TipoMovimentacao.SAIDA) {

                        totalSaidas += movimentacao.getQuantidade();

                    }

                }

            }


            meses.add(nomeMes);

            entradas.add(totalEntradas);

            saidas.add(totalSaidas);

        }


        model.addAttribute("graficoMeses", meses);

        model.addAttribute("graficoEntradas", entradas);

        model.addAttribute("graficoSaidas", saidas);



        /*
         * =====================================================
         * DADOS DO GRÁFICO DE PRODUTOS POR CATEGORIA
         * =====================================================
         */

        List<Produto> produtos =
                produtoService.listarTodos();


        Map<String, Integer> produtosPorCategoria =
                new LinkedHashMap<>();


        for (Produto produto : produtos) {

            if (produto.getCategoria() == null) {
                continue;
            }


            String nomeCategoria =
                    produto.getCategoria().getNome();


            produtosPorCategoria.merge(
                    nomeCategoria,
                    1,
                    Integer::sum
            );

        }


        List<String> categorias =
                new ArrayList<>(
                        produtosPorCategoria.keySet()
                );


        List<Integer> quantidadePorCategoria =
                new ArrayList<>(
                        produtosPorCategoria.values()
                );


        model.addAttribute(
                "graficoCategorias",
                categorias
        );


        model.addAttribute(
                "graficoCategoriasValores",
                quantidadePorCategoria
        );



        /*
         * =====================================================
         * RETORNO
         * =====================================================
         */

        return "index";
    }
}