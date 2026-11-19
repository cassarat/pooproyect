package com.example.demo.modelo.repositorio;

import com.example.demo.modelo.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, String> {
}
