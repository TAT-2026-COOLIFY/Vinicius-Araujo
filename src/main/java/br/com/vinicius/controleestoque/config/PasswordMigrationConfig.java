package br.com.vinicius.controleestoque.config;

import br.com.vinicius.controleestoque.entity.Usuario;
import br.com.vinicius.controleestoque.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

// Essa classe é responsável por verificar e atualizar as senhas dos usuários.
@Configuration
public class PasswordMigrationConfig {

    // Executa a verificação das senhas quando o sistema é iniciado.
    @Bean
    public CommandLineRunner migrarSenhas(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Busca todos os usuários cadastrados no banco de dados.
            for (Usuario usuario : usuarioRepository.findAll()) {

                // Pega a senha cadastrada do usuário.
                String senha = usuario.getSenha();

                // Verifica se a senha existe e se ainda não está no formato BCrypt.
                if (senha != null
                        && !senha.startsWith("$2a$")
                        && !senha.startsWith("$2b$")
                        && !senha.startsWith("$2y$")) {

                    // Criptografa a senha antes de salvar no banco de dados.
                    usuario.setSenha(
                            passwordEncoder.encode(senha)
                    );

                    // Salva o usuário com a senha atualizada.
                    usuarioRepository.save(usuario);
                }
            }
        };
    }
}