package com.cptrans.petrocarga.services;

import java.time.OffsetDateTime;
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

    private final Notificacao CHECKIN_DISPONIVEL_NOTIFICACAO = new Notificacao(
        "Check-In Disponível",
        "Você tem um check-in disponível. Por favor, realize o check-in para continuar com sua reserva.",
        TipoNotificacaoEnum.RESERVA
    );

    private final Notificacao FIM_PROXIMO_NOTIFICACAO = new Notificacao(
        "Fim de Reserva Próximo",
        "Sua reserva está prestes a terminar. Assegure-se de concluir suas atividades a tempo.",
        TipoNotificacaoEnum.RESERVA
    );
 
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

    @Transactional
    private Notificacao saveNotificacao(Notificacao novaNotificacao) {
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

    @Transactional
    public List<Notificacao> sendNotificacaoToUsuariosByPermissaoBySystem(PermissaoEnum permissao, Notificacao novaNotificacao) {
        List<Usuario> usuarios = usuarioService.findByPermissaoAndAtivo(permissao, true);
        List<Notificacao> notificacoesSalvas = new ArrayList<>();
        Map<String, Object> dadosAdicionais = new HashMap<>();

        if (novaNotificacao.getMetadata() != null) dadosAdicionais.putAll(novaNotificacao.getMetadata());

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

    public List<Notificacao> marcarSelecionadasComoLida(UUID usuarioId, List<UUID> listaNotificacaoId) {
        List<Notificacao> notificacoes = notificacaoRepository.findByIdInAndUsuarioId(listaNotificacaoId, usuarioId);
        List<Notificacao> notificacoesLidas = new ArrayList<>();
        if(notificacoes == null || notificacoes.isEmpty()) throw new EntityNotFoundException("Nenhuma notificação encontrada");
        for(Notificacao notificacao : notificacoes) {
            notificacao.marcarComoLida();
            notificacoesLidas.add(notificacao);
        }
        return notificacaoRepository.saveAll(notificacoesLidas);
    }

    public void deletarSelecionadas(UUID usuarioId, List<UUID> listaNotificacaoId) {
        List<Notificacao> notificacoes = notificacaoRepository.findByIdInAndUsuarioId(listaNotificacaoId, usuarioId);
        if(notificacoes == null || notificacoes.isEmpty()) throw new EntityNotFoundException("Nenhuma notificação encontrada");
        notificacaoRepository.deleteAll(notificacoes);
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

    @Transactional
    public Notificacao notificarCheckInDisponivel(UUID usuarioId, OffsetDateTime dataCheckin) {
        Map<String, Object> dadosAdicionais = new HashMap<>();
        dadosAdicionais.put("inicioReserva", dataCheckin.toString());
        CHECKIN_DISPONIVEL_NOTIFICACAO.setMetadata(dadosAdicionais);
        CHECKIN_DISPONIVEL_NOTIFICACAO.setUsuarioId(usuarioId);
        CHECKIN_DISPONIVEL_NOTIFICACAO.setTitulo("Check-In Disponível");
        Notificacao notificacaoSalva = saveNotificacao(CHECKIN_DISPONIVEL_NOTIFICACAO);
        eventPublisher.publish(new NotificacaoCriadaEvent(notificacaoSalva));
        return notificacaoSalva;
    }

    @Transactional
    public Notificacao notificarFimProximo(UUID usuarioId, OffsetDateTime dataFim) {
        Map<String, Object> dadosAdicionais = new HashMap<>();
        dadosAdicionais.put("fimReserva", dataFim.toString());
        FIM_PROXIMO_NOTIFICACAO.setMetadata(dadosAdicionais);
        FIM_PROXIMO_NOTIFICACAO.setUsuarioId(usuarioId);
        Notificacao notificacaoSalva = saveNotificacao(FIM_PROXIMO_NOTIFICACAO);
        eventPublisher.publish(new NotificacaoCriadaEvent(notificacaoSalva));
        return notificacaoSalva;
    }
}