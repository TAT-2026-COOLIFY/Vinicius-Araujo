package br.com.vinicius.controleestoque;

import br.com.vinicius.controleestoque.entity.Usuario;
import br.com.vinicius.controleestoque.enums.Cargo;
import br.com.vinicius.controleestoque.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final Environment environment;

    public AdminInitializer(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            Environment environment) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
    }

    @Override
    public void run(String... args) {

        // Cria o administrador somente se o banco estiver vazio
        if (usuarioRepository.count() > 0) {
            return;
        }

        String nome = environment.getProperty("INITIAL_ADMIN_NAME");
        String usuario = environment.getProperty("INITIAL_ADMIN_USERNAME");
        String senha = environment.getProperty("INITIAL_ADMIN_PASSWORD");

        if (nome == null || usuario == null || senha == null
                || nome.isBlank() || usuario.isBlank() || senha.isBlank()) {
            throw new IllegalStateException(
                    "Configure as variáveis do administrador inicial.");
        }

        Usuario admin = new Usuario();

        admin.setNome(nome);
        admin.setUsuario(usuario);
        admin.setSenha(passwordEncoder.encode(senha));
        admin.setCargo(Cargo.ADMINISTRADOR);
        admin.setAtivo(true);
        admin.setDataCadastro(LocalDateTime.now());

        usuarioRepository.save(admin);

        System.out.println("Administrador inicial criado com sucesso!");
    }
}
