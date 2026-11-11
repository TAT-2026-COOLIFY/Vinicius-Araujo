package br.com.vinicius.controleestoque.service;

import br.com.vinicius.controleestoque.entity.Usuario;
import br.com.vinicius.controleestoque.repository.MovimentacaoRepository;
import br.com.vinicius.controleestoque.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MovimentacaoRepository movimentacaoRepository;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            MovimentacaoRepository movimentacaoRepository) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.movimentacaoRepository = movimentacaoRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Usuario salvar(Usuario usuario) {

        String nomeUsuario = usuario.getUsuario().trim();

        Optional<Usuario> existente =
                usuarioRepository.findByUsuarioIgnoreCase(nomeUsuario);

        if (existente.isPresent()
                && !existente.get().getId().equals(usuario.getId())) {

            throw new RuntimeException(
                    "Já existe um usuário com esse nome de usuário."
            );
        }

        if (usuario.getId() == null) {

            usuario.setUsuario(nomeUsuario);
            usuario.setDataCadastro(LocalDateTime.now());

            if (usuario.getSenha() == null
                    || usuario.getSenha().isBlank()) {
                throw new RuntimeException("A senha é obrigatória.");
            }

            usuario.setSenha(
                    passwordEncoder.encode(usuario.getSenha())
            );

            if (usuario.getAtivo() == null) {
                usuario.setAtivo(true);
            }

            return usuarioRepository.save(usuario);
        }

        Usuario existenteBanco = buscarPorId(usuario.getId());

        existenteBanco.setNome(usuario.getNome().trim());
        existenteBanco.setUsuario(nomeUsuario);
        existenteBanco.setCargo(usuario.getCargo());

        existenteBanco.setAtivo(
                usuario.getAtivo() != null
                        ? usuario.getAtivo()
                        : existenteBanco.getAtivo()
        );

        // Só altera a senha se uma nova senha for informada.
        if (usuario.getSenha() != null
                && !usuario.getSenha().isBlank()) {

            existenteBanco.setSenha(
                    passwordEncoder.encode(usuario.getSenha())
            );
        }

        // Preserva a data de cadastro e os demais dados originais.
        return usuarioRepository.save(existenteBanco);
    }

    public Usuario buscarPorId(Long id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado."));
    }

    public Usuario buscarPorUsuario(String nomeUsuario) {

        return usuarioRepository.findByUsuario(nomeUsuario)
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado."));
    }

    public void alterarSenha(Long id, String novaSenha) {

        Usuario usuario = buscarPorId(id);

        usuario.setSenha(
                passwordEncoder.encode(novaSenha)
        );

        usuarioRepository.save(usuario);
    }

    public void excluir(Long id) {

        Usuario usuario = buscarPorId(id);

        if (movimentacaoRepository.existsByUsuario_Id(id)) {
            throw new RuntimeException(
                    "Não é possível excluir este usuário, pois existem "
                            + "movimentações vinculadas a ele."
            );
        }

        usuarioRepository.delete(usuario);
    }

    public long contar() {
        return usuarioRepository.count();
    }
}