package br.com.vinicius.controleestoque.controller;

import br.com.vinicius.controleestoque.entity.Usuario;
import br.com.vinicius.controleestoque.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listar(Model model) {

        model.addAttribute(
                "usuarios",
                usuarioService.listarTodos()
        );

        return "usuarios/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {

        model.addAttribute(
                "usuarioForm",
                new Usuario()
        );

        return "usuarios/form";
    }

    @PostMapping("/salvar")
    public String salvar(
            @ModelAttribute("usuarioForm") Usuario usuario,
            Model model) {

        try {

            usuarioService.salvar(usuario);

            return "redirect:/usuarios";

        } catch (RuntimeException e) {

            model.addAttribute(
                    "erro",
                    e.getMessage()
            );

            return "usuarios/form";
        }
    }

    @GetMapping("/editar/{id}")
    public String editar(
            @PathVariable Long id,
            Model model) {

        model.addAttribute(
                "usuarioForm",
                usuarioService.buscarPorId(id)
        );

        return "usuarios/form";
    }

    @GetMapping("/alterar-senha/{id}")
    public String alterarSenha(
            @PathVariable Long id,
            Model model) {

        Usuario usuario = usuarioService.buscarPorId(id);

        model.addAttribute(
                "usuario",
                usuario
        );

        return "usuarios/alterar-senha";
    }

    @PostMapping("/alterar-senha/{id}")
    public String salvarNovaSenha(
            @PathVariable Long id,
            @RequestParam String novaSenha,
            @RequestParam String confirmarSenha,
            Model model) {

        if (!novaSenha.equals(confirmarSenha)) {

            Usuario usuario = usuarioService.buscarPorId(id);

            model.addAttribute(
                    "usuario",
                    usuario
            );

            model.addAttribute(
                    "erro",
                    "As senhas não coincidem."
            );

            return "usuarios/alterar-senha";
        }

        usuarioService.alterarSenha(
                id,
                novaSenha
        );

        return "redirect:/usuarios";
    }

    @GetMapping("/excluir/{id}")
    public String excluir(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            usuarioService.excluir(id);

            redirectAttributes.addFlashAttribute(
                    "mensagemSucesso",
                    "Usuário excluído com sucesso."
            );

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute(
                    "mensagemErro",
                    e.getMessage()
            );
        }

        return "redirect:/usuarios";
    }
}