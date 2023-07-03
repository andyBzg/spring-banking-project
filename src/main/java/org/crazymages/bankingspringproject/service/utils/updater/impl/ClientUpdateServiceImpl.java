package org.crazymages.bankingspringproject.service.utils.updater.impl;

import org.crazymages.bankingspringproject.entity.Client;
import org.crazymages.bankingspringproject.service.utils.updater.EntityUpdateService;
import org.springframework.stereotype.Component;

/**
 * A class implementing the EntityUpdateService interface for updating Client entities.
 * It provides custom update logic for the Client entity.
 */
@Component
public class ClientUpdateServiceImpl implements EntityUpdateService<Client> {

    /**
     * Updates the given Client entity with the provided Client update.
     * Only non-null fields in the Client update will be applied to the original Client.
     *
     * @param client The existing entity to be updated.
     * @param clientUpdate The entity containing the updated data.
     * @return The updated Client entity.
     */
    @Override
    public Client update(Client client, Client clientUpdate) {
        if (client != null && clientUpdate != null) {
            if (clientUpdate.getManagerUuid() != null) {
                client.setManagerUuid(clientUpdate.getManagerUuid());
            }
            if (clientUpdate.getStatus() != null) {
                client.setStatus(clientUpdate.getStatus());
            }
            if (clientUpdate.getTaxCode() != null) {
                client.setTaxCode(clientUpdate.getTaxCode());
            }
            if (clientUpdate.getFirstName() != null) {
                client.setFirstName(clientUpdate.getFirstName());
            }
            if (clientUpdate.getLastName() != null) {
                client.setLastName(clientUpdate.getLastName());
            }
            if (clientUpdate.getEmail() != null) {
                client.setEmail(clientUpdate.getEmail());
            }
            if (clientUpdate.getAddress() != null) {
                client.setAddress(clientUpdate.getAddress());
            }
            if (clientUpdate.getPhone() != null) {
                client.setPhone(clientUpdate.getPhone());
            }
        }
        return client;
    }
}
