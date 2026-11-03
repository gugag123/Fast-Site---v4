package com.example.demo.config;

import com.example.demo.controller.AuthController;
import com.example.demo.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Controla quem acessa cada rota da API:
 *   - POST /api/cadastros (se cadastrar)            -> qualquer pessoa
 *   - GET  /api/servicos e /api/servicos/{id}       -> qualquer pessoa logada (cliente escolhe o serviço)
 *   - POST /api/agendamentos e GET /api/agendamentos/meus -> qualquer pessoa logada (cliente)
 *   - todo o resto (cadastros, CRUD de serviços, lista e aceitar/recusar agendamentos) -> só ADMIN
 *   - sem login         -> 401
 *   - logado, não admin -> 403
 */
public class AdminInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        boolean ehCadastroNovo = "POST".equals(request.getMethod())
                && request.getRequestURI().equals("/api/cadastros");
        if (ehCadastroNovo) {
            return true;
        }

        String metodo = request.getMethod();
        String uri = request.getRequestURI();
        boolean leituraDeServicos = "GET".equals(metodo) && uri.startsWith("/api/servicos");
        boolean rotaDoCliente = ("POST".equals(metodo) && uri.equals("/api/agendamentos"))
                || ("GET".equals(metodo) && uri.equals("/api/agendamentos/meus"));
        boolean bastaLogin = leituraDeServicos || rotaDoCliente;

        HttpSession session = request.getSession(false);
        Object email = session != null ? session.getAttribute(AuthController.SESSAO_EMAIL) : null;
        Object perfil = session != null ? session.getAttribute(AuthController.SESSAO_PERFIL) : null;

        if (email == null) {
            return negar(response, HttpServletResponse.SC_UNAUTHORIZED, "Faça login para continuar");
        }
        if (bastaLogin) {
            return true;
        }
        if (!Usuario.ADMIN.equals(perfil)) {
            return negar(response, HttpServletResponse.SC_FORBIDDEN, "Acesso permitido só para o administrador");
        }
        return true;
    }

    private boolean negar(HttpServletResponse response, int status, String mensagem) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensagem\":\"" + mensagem + "\"}");
        return false;
    }
}
