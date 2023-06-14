package org.crazymages.bankingspringproject.repository;

import org.crazymages.bankingspringproject.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    List<Client> findClientsByStatusIs(Integer status);

    /*
    getAllClientsWhereStatusIsActive(): List<Client>
    getAllClientsWhereBalanceMoreThan(balance: BigDecimal): List<Client>
    getAllClientsWhereTransactionMoreThan(transactionCount: Int): List<Client>
    */
}
