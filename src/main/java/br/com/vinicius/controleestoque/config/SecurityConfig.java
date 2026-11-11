package br.com.vinicius.controleestoque.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Essa classe configura as regras de segurança do sistema.
@Configuration
public class SecurityConfig {

    // Define o metodo que será usado para criptografar as senhas dos usuários.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configura as permissões de acesso e as opções de login e logout.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        // Permite o acesso aos arquivos de estilo e JavaScript sem precisar fazer login.
                        .requestMatchers(
                                "/style.css",
                                "/css/**",
                                "/js/**"
                        ).permitAll()

                        // Permite que qualquer pessoa acesse a página de login.
                        .requestMatchers("/login")
                        .permitAll()

                        // Somente usuários com o perfil de administrador podem acessar a área de usuários.
                        .requestMatchers("/usuarios/**")
                        .hasRole("ADMINISTRADOR")

                        // As demais páginas só podem ser acessadas por usuários autenticados.
                        .anyRequest()
                        .authenticated()
                )

                // Configura a página de login e redireciona o usuário para a página inicial após entrar.
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )

                // Define para onde o usuário será enviado depois de sair do sistema.
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                // Define a página que será exibida quando o usuário não tiver permissão de acesso.
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/403")
                )

                // Desativa a proteção CSRF nas requisições do sistema.
                .csrf(csrf -> csrf.disable());

        // Finaliza a configuração e retorna as regras de segurança.
        return http.build();
    }
}