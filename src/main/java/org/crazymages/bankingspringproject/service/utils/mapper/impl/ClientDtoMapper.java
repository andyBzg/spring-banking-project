package org.crazymages.bankingspringproject.service.utils.mapper.impl;

import org.crazymages.bankingspringproject.dto.ClientDto;
import org.crazymages.bankingspringproject.entity.Client;
import org.crazymages.bankingspringproject.entity.enums.ClientStatus;
import org.crazymages.bankingspringproject.exception.DataNotFoundException;
import org.crazymages.bankingspringproject.service.utils.mapper.DtoMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Component class that provides mapping functionality between Client and ClientDTO objects.
 */
@Component
public class ClientDtoMapper implements DtoMapper<Client, ClientDto> {

    @Override
    public ClientDto mapEntityToDto(Client client) {
        return new ClientDto(
                String.valueOf(client.getUuid()),
                String.valueOf(client.getManagerUuid()),
                String.valueOf(client.getStatus()),
                client.getTaxCode(),
                client.getFirstName(),
                client.getLastName(),
                client.getEmail(),
                client.getAddress(),
                client.getPhone()
        );
    }

    @Override
    public Client mapDtoToEntity(ClientDto clientDto) {
        Client client = new Client();
        client.setUuid(UUID.fromString(clientDto.getUuid()));
        client.setManagerUuid(UUID.fromString(clientDto.getManagerUuid()));
        client.setStatus(ClientStatus.valueOf(clientDto.getStatus()));
        client.setTaxCode(clientDto.getTaxCode());
        client.setFirstName(clientDto.getFirstName());
        client.setLastName(clientDto.getLastName());
        client.setEmail(clientDto.getEmail());
        client.setAddress(clientDto.getAddress());
        client.setPhone(clientDto.getPhone());
        return client;
    }

    @Override
    public List<ClientDto> getDtoList(List<Client> clientList) {
        return Optional.ofNullable(clientList)
                .orElseThrow(() -> new DataNotFoundException("list is null"))
                .stream()
                .filter(Objects::nonNull)
                .map(this::mapEntityToDto)
                .toList();
    }
}