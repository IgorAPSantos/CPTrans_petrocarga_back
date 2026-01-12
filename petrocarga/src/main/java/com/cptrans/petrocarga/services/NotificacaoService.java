package com.cptrans.petrocarga.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.domain.event.NotificacaoCriadaEvent;
import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.enums.StatusDenunciaEnum;
import com.cptrans.petrocarga.enums.TipoNotificacaoEnum;
import com.cptrans.petrocarga.infrastructure.event.SpringDomainEventPublisher;
import com.cptrans.petrocarga.models.Notificacao;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.repositories.NotificacaoRepository;
import com.cptrans.petrocarga.security.UserAuthenticated;
import com.cptrans.petrocarga.utils.NotificacaoUtils;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class NotificacaoService {
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private NotificacaoRepository notificacaoRepository;
    @Autowired
    private SpringDomainEventPublisher eventPublisher;
 
    private Notificacao createNotificacao(UUID usuarioId, String titulo, String mensagem, TipoNotificacaoEnum tipo, Map<String, Object> dadosAdicionais) {
        Usuario usuarioLogado = usuarioService.findById(((UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id());
        Usuario usuarioDestinatario = usuarioService.findById(usuarioId);
        NotificacaoUtils.validateByPermissao(usuarioLogado.getPermissao(), usuarioDestinatario.getPermissao());
        if (dadosAdicionais == null) dadosAdicionais = new HashMap<>();
        dadosAdicionais.put("remetenteId", usuarioLogado.getId());
        dadosAdicionais.put("remetente", usuarioLogado.getNome());
        Notificacao novaNotificacao = new Notificacao(usuarioDestinatario.getId(), titulo, mensagem, tipo, dadosAdicionais);
        
        return notificacaoRepository.save(novaNotificacao);
    }

    public List<Notificacao> findAllbyUsuarioId(UUID usuarioId) {
        return notificacaoRepository.findByUsuarioId(usuarioId);
    }

    public List<Notificacao> findAllbyUsuarioId(UUID usuarioId, boolean lida) {
        return notificacaoRepository.findByUsuarioIdAndLida(usuarioId, lida);
    }

    public Notificacao findById(UUID notificacaoId) {
        return notificacaoRepository.findById(notificacaoId).orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada"));
    }
    public Notificacao findByIdAndUsuarioId(UUID notificacaoId, UUID usuarioId) {
        return notificacaoRepository.findByIdAndUsuarioId(notificacaoId, usuarioId).orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada"));
    }

    public Notificacao findByIdAndSetLida(UUID notificacaoId) {
        UserAuthenticated userAuthenticated = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> authorities = userAuthenticated.userDetails().getAuthorities().stream().map(GrantedAuthority::toString).toList();
        Notificacao notificacao = notificacaoRepository.findById(notificacaoId).orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada"));
        if ((userAuthenticated.id().equals(notificacao.getUsuarioId()) || (authorities.contains(PermissaoEnum.ADMIN.getRole()) || authorities.contains(PermissaoEnum.GESTOR.getRole())))) {
            notificacao.marcarComoLida();
            return notificacaoRepository.save(notificacao);
        } else {
            throw new AuthorizationDeniedException("Acesso negado à notificação");
        }
    }

    @Transactional
    public Notificacao sendNotificationToUsuario(UUID usuarioId, Notificacao novaNotificacao) {
        Usuario usuario = usuarioService.findByIdAndAtivo(usuarioId, true);
        novaNotificacao.setUsuarioId(usuario.getId());
        Notificacao notificacaoSalva = createNotificacao(novaNotificacao.getUsuarioId(), novaNotificacao.getTitulo(), novaNotificacao.getMensagem(), novaNotificacao.getTipo(), novaNotificacao.getMetadata());
        eventPublisher.publish(new NotificacaoCriadaEvent(notificacaoSalva));
        return notificacaoSalva;
    }

    @Transactional
    public List<Notificacao> sendNotificacaoToUsuariosByPermissao(PermissaoEnum permissao, Notificacao novaNotificacao) {
        List<Usuario> usuarios = usuarioService.findByPermissaoAndAtivo(permissao, true);
        Usuario usuarioLogado = usuarioService.findById(((UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).id());
        List<Notificacao> notificacoesSalvas = new ArrayList<>();
        Map<String, Object> dadosAdicionais = new HashMap<>();

        if (novaNotificacao.getMetadata() != null) dadosAdicionais.putAll(novaNotificacao.getMetadata());
        
        dadosAdicionais.put("remetenteId", usuarioLogado.getId());
        dadosAdicionais.put("remetente", usuarioLogado.getNome());

        if(usuarios.isEmpty()) {
            throw new EntityNotFoundException("Nenhum usuário encontrado com a permissão: " + permissao);
        }
        for (Usuario usuario : usuarios) {
            Notificacao novaNotificacaoUsuario = new Notificacao(usuario.getId(), novaNotificacao.getTitulo(), novaNotificacao.getMensagem(), novaNotificacao.getTipo());
            novaNotificacaoUsuario.setMetadata(dadosAdicionais);
            notificacoesSalvas.add(novaNotificacaoUsuario);
        }
        if (notificacoesSalvas.isEmpty()) return notificacoesSalvas;

        List<Notificacao> notificacoesCriadas = notificacaoRepository.saveAll(notificacoesSalvas);
        if(!notificacoesCriadas.isEmpty()) {
            for (Notificacao notificacao : notificacoesCriadas) {
                eventPublisher.publish(new NotificacaoCriadaEvent(notificacao));
            }
        }
        return notificacoesCriadas;
    }

    public Notificacao marcarComoLida(UUID usuarioId, UUID notificacaoId) {
        Notificacao notificacao = findByIdAndUsuarioId(notificacaoId, usuarioId);
        notificacao.marcarComoLida();
        return notificacaoRepository.save(notificacao);
    }

    public void deleteById(UUID notificacaoId, UUID usuarioId) {
        Notificacao notificacao = findByIdAndUsuarioId(notificacaoId, usuarioId);
        notificacaoRepository.delete(notificacao);
    }

    public void notificarDenunciaCriada(Map<String, Object> dadosAdicionais) {
        Notificacao notificacaoDenuncia = new Notificacao("Nova Denúncia", "Uma nova denúncia foi criada", TipoNotificacaoEnum.DENUNCIA, dadosAdicionais);
        sendNotificacaoToUsuariosByPermissao(PermissaoEnum.GESTOR, notificacaoDenuncia);
        sendNotificacaoToUsuariosByPermissao(PermissaoEnum.AGENTE, notificacaoDenuncia);
    }

    public void notificarDenunciaAtualizada(Map<String, Object> dadosAdicionais, StatusDenunciaEnum statusDenuncia, UUID usuarioIdDenuncia) {
        Notificacao notificacaoDenuncia = new Notificacao("Denúncia Atualizada", "Sua denúncia foi atualizada para o status: '" + statusDenuncia + "'", TipoNotificacaoEnum.DENUNCIA, dadosAdicionais);
        sendNotificationToUsuario(usuarioIdDenuncia, notificacaoDenuncia);
    }

}