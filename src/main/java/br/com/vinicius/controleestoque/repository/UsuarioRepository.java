package br.com.vinicius.controleestoque.repository;

import br.com.vinicius.controleestoque.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsuario(String usuario);

    Optional<Usuario> findByUsuarioIgnoreCase(String usuario);

}