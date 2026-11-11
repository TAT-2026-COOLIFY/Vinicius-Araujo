package br.com.vinicius.controleestoque.controller;

import br.com.vinicius.controleestoque.entity.Categoria;
import br.com.vinicius.controleestoque.service.CategoriaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute("categorias",
                categoriaService.listarTodas());

        return "categorias/lista";
    }

    @GetMapping("/nova")
    public String novaCategoria(Model model) {

        model.addAttribute("categoria",
                new Categoria());

        return "categorias/form";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {

        Categoria categoria = categoriaService.buscarPorId(id);

        model.addAttribute("categoria", categoria);

        return "categorias/form";
    }

    @PostMapping("/salvar")
    public String salvar(@ModelAttribute Categoria categoria,
                         Model model) {

        try {

            categoriaService.salvar(categoria);

            return "redirect:/categorias";

        } catch (RuntimeException e) {

            model.addAttribute("erro", e.getMessage());

            return "categorias/form";
        }
    }

    @GetMapping("/excluir/{id}")
    public String excluir(@PathVariable Long id) {

        categoriaService.excluir(id);

        return "redirect:/categorias";
    }

}