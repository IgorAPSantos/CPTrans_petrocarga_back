package com.cptrans.petrocarga.services;

import com.cptrans.petrocarga.models.Veiculo;
import com.cptrans.petrocarga.repositories.VeiculoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.cptrans.petrocarga.enums.PermissaoEnum;
import com.cptrans.petrocarga.models.Usuario;
import com.cptrans.petrocarga.security.UserAuthenticated;

@Service
public class VeiculoService {

    @Autowired
    private VeiculoRepository veiculoRepository;

    @Autowired
    private UsuarioService usuarioService;

    public List<Veiculo> findAll() {
        return veiculoRepository.findAll();
    }

    public Veiculo findById(UUID id) {
        Veiculo veiculo = veiculoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Veiculo não encontrado."));
        UserAuthenticated usuarioLogado = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> authorities = usuarioLogado.userDetails().getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        if(authorities.contains(PermissaoEnum.MOTORISTA.getRole()) || authorities.contains(PermissaoEnum.EMPRESA.getRole())) {
            if(!veiculo.getUsuario().getId().equals(usuarioLogado.id())) {
                throw new IllegalArgumentException("Usuário não pode ver os veículos de outro usuário.");   
            }
        }
        return veiculoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Veiculo nao encontrado."));
    }

    public List<Veiculo> findByUsuarioId(UUID usuarioId) {
        UserAuthenticated usuarioLogado = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Usuario usuario = usuarioService.findById(usuarioId);
        List<Veiculo> veiculos = veiculoRepository.findByUsuario(usuario);
        
        List<String> authorities = usuarioLogado.userDetails().getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        if(authorities.contains(PermissaoEnum.MOTORISTA.getRole()) || authorities.contains(PermissaoEnum.EMPRESA.getRole())) {
            if(!usuario.getId().equals(usuarioLogado.id())) {
                throw new IllegalArgumentException("Usuário não pode ver os veículos de outro usuário.");
            }
        }

        return veiculos;
    }

    public Veiculo createVeiculo(Veiculo novoVeiculo, UUID usuarioId) {
        Usuario usuario = usuarioService.findById(usuarioId);
        Optional<Veiculo> veiculoByPlaca = veiculoRepository.findByPlaca(novoVeiculo.getPlaca());
        
        UserAuthenticated usuarioLogado = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> authorities = usuarioLogado.userDetails().getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        if(authorities.contains(PermissaoEnum.MOTORISTA.getRole()) || authorities.contains(PermissaoEnum.EMPRESA.getRole())) {
            if(!usuario.getId().equals(usuarioLogado.id())) {
                throw new IllegalArgumentException("Usuário não pode cadastrar veículos de outro usuário.");
            }
        }

        if(veiculoByPlaca.isPresent()){
            if(veiculoByPlaca.get().getUsuario().getId().equals(usuario.getId())) {
                throw new IllegalArgumentException("Voce já possui um veículo cadastrado com essa placa.");
            }
        }
        novoVeiculo.setUsuario(usuario);
        return veiculoRepository.save(novoVeiculo);
    }

    public Veiculo updateVeiculo(UUID veiculoId, Veiculo novoVeiculo, UUID usuarioId) {
        Veiculo veiculoRegistrado = findById(veiculoId);
        Usuario usuario = usuarioService.findById(usuarioId);
        UserAuthenticated usuarioLogado = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> authorities = usuarioLogado.userDetails().getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        if(authorities.contains(PermissaoEnum.MOTORISTA.getRole()) || authorities.contains(PermissaoEnum.EMPRESA.getRole())) {
            if(!usuario.getId().equals(usuarioLogado.id())) {
                throw new IllegalArgumentException("Usuário não pode editar veículo de outro usuário.");
            }
        }
        Optional<Veiculo> veiculoByPlaca = veiculoRepository.findByPlaca(novoVeiculo.getPlaca());

        if(veiculoByPlaca.isPresent() && !veiculoByPlaca.get().getId().equals(veiculoRegistrado.getId())) {
            throw new IllegalArgumentException("Você já possui um veículo cadastrado com essa placa.");
        }
        veiculoRegistrado.setPlaca(novoVeiculo.getPlaca());
        veiculoRegistrado.setMarca(novoVeiculo.getMarca());
        veiculoRegistrado.setModelo(novoVeiculo.getModelo());
        veiculoRegistrado.setTipo(novoVeiculo.getTipo());
        veiculoRegistrado.setComprimento(novoVeiculo.getComprimento());
        veiculoRegistrado.setCpfProprietario(novoVeiculo.getCpfProprietario());
        veiculoRegistrado.setCnpjProprietario(novoVeiculo.getCnpjProprietario());
        veiculoRegistrado.setUsuario(usuario);
        return veiculoRepository.save(veiculoRegistrado);
    }

    public void deleteById(UUID id) {
        Veiculo veiculo = veiculoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Veiculo nao encontrado."));
        UserAuthenticated usuarioLogado = (UserAuthenticated) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<String> authorities = usuarioLogado.userDetails().getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        if(authorities.contains(PermissaoEnum.MOTORISTA.getRole()) || authorities.contains(PermissaoEnum.EMPRESA.getRole())) {
            if(!veiculo.getUsuario().getId().equals(usuarioLogado.id())) {
                throw new IllegalArgumentException("Usuário nao pode deletar veiculo de outro usuário.");
            }
        }
        veiculoRepository.deleteById(id);
    }
}
