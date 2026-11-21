package com.example.demo.modelo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AsientoRepository extends JpaRepository<Asiento, String> {
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Asiento a set a.disponible = false where a.id = :id and a.disponible = true")
    int ocuparSiDisponible(@Param("id") String id);
}
