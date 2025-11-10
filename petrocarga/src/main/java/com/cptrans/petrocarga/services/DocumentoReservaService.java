package com.cptrans.petrocarga.services;

import com.cptrans.petrocarga.models.Reserva;
import com.cptrans.petrocarga.repositories.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DocumentoReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    public Reserva findReservaWithDetails(UUID id) {
        return reservaRepository.findByIdWithJoins(id);
    }
}