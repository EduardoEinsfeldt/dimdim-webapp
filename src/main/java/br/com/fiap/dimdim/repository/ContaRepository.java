package br.com.fiap.dimdim.repository;

import br.com.fiap.dimdim.model.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaRepository extends JpaRepository<Conta, Integer> {
}
