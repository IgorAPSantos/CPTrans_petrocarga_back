// package com.cptrans.petrocarga.services;
// TODO: Refatorar ReservaRapidaService
// import com.cptrans.petrocarga.models.ReservaRapida;
// import com.cptrans.petrocarga.repositories.ReservaRapidaRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.List;
// import java.util.Optional;
// import java.util.UUID;

// @Service
// public class ReservaRapidaService {

//     @Autowired
//     private ReservaRapidaRepository reservaRapidaRepository;

//     public List<ReservaRapida> findAll() {
//         return reservaRapidaRepository.findAll();
//     }

//     public Optional<ReservaRapida> findById(UUID id) {
//         return reservaRapidaRepository.findById(id);
//     }

//     public ReservaRapida save(ReservaRapida reservaRapida) {
//         return reservaRapidaRepository.save(reservaRapida);
//     }

//     public void deleteById(UUID id) {
//         reservaRapidaRepository.deleteById(id);
//     }
// }
