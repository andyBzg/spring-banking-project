package org.crazymages.bankingspringproject.service.utils.mapper.impl;

import org.crazymages.bankingspringproject.dto.TransactionDto;
import org.crazymages.bankingspringproject.entity.Transaction;
import org.crazymages.bankingspringproject.entity.enums.CurrencyCode;
import org.crazymages.bankingspringproject.entity.enums.TransactionType;
import org.crazymages.bankingspringproject.exception.DataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionDtoMapperTest {

    TransactionDtoMapper transactionDtoMapper;
    TransactionDto transactionDto;
    Transaction transaction1;
    Transaction transaction2;

    @BeforeEach
    void setUp() {
        transactionDtoMapper = new TransactionDtoMapper();
        transactionDto = new TransactionDto();

        transaction1 = new Transaction();
        transaction1.setUuid(UUID.randomUUID());
        transaction1.setDebitAccountUuid(UUID.randomUUID());
        transaction1.setCreditAccountUuid(UUID.randomUUID());
        transaction1.setType(TransactionType.TRANSFER);
        transaction1.setCurrencyCode(CurrencyCode.EUR);
        transaction1.setAmount(BigDecimal.valueOf(100));
        transaction1.setDescription("Transaction 1");

        transaction2 = new Transaction();
        transaction2.setUuid(UUID.randomUUID());
        transaction2.setDebitAccountUuid(UUID.randomUUID());
        transaction2.setCreditAccountUuid(UUID.randomUUID());
        transaction2.setType(TransactionType.REFUND);
        transaction2.setCurrencyCode(CurrencyCode.USD);
        transaction2.setAmount(BigDecimal.valueOf(200));
        transaction2.setDescription("Transaction 2");
    }

    @Test
    void mapEntityToDto_validTransaction_success() {
        // when
        TransactionDto transactionDto = transactionDtoMapper.mapEntityToDto(transaction1);

        // then
        assertEquals(transaction1.getUuid().toString(), transactionDto.getUuid());
        assertEquals(transaction1.getDebitAccountUuid().toString(), transactionDto.getDebitAccountUuid());
        assertEquals(transaction1.getCreditAccountUuid().toString(), transactionDto.getCreditAccountUuid());
        assertEquals(transaction1.getType().toString(), transactionDto.getType());
        assertEquals(transaction1.getCurrencyCode().toString(), transactionDto.getCurrencyCode());
        assertEquals(transaction1.getAmount(), transactionDto.getAmount());
        assertEquals(transaction1.getDescription(), transactionDto.getDescription());
    }

    @Test
    void mapEntityToDto_nullTransaction_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> transactionDtoMapper.mapEntityToDto(null));
    }

    @Test
    void mapDtoToEntity_validTransactionDto_success() {
        // given
        transactionDto.setUuid("30348dce-45f7-4e19-aa08-3ed77a8f7ac3");
        transactionDto.setDebitAccountUuid("f59f83b7-9f9b-495b-83e7-09c11856e6a5");
        transactionDto.setCreditAccountUuid("3be3e176-3215-4d57-92c3-3f5b0e06c6c4");
        transactionDto.setType("TRANSFER");
        transactionDto.setCurrencyCode("EUR");
        transactionDto.setAmount(BigDecimal.valueOf(100));
        transactionDto.setDescription("Transaction 1");

        // when
        Transaction transaction = transactionDtoMapper.mapDtoToEntity(transactionDto);

        // then
        assertEquals(UUID.fromString(transactionDto.getUuid()), transaction.getUuid());
        assertEquals(UUID.fromString(transactionDto.getDebitAccountUuid()), transaction.getDebitAccountUuid());
        assertEquals(UUID.fromString(transactionDto.getCreditAccountUuid()), transaction.getCreditAccountUuid());
        assertEquals(TransactionType.valueOf(transactionDto.getType()), transaction.getType());
        assertEquals(CurrencyCode.valueOf(transactionDto.getCurrencyCode()), transaction.getCurrencyCode());
        assertEquals(transactionDto.getAmount(), transaction.getAmount());
        assertEquals(transactionDto.getDescription(), transaction.getDescription());
    }

    @Test
    void mapDtoToEntity_nullTransactionDto_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> transactionDtoMapper.mapDtoToEntity(null));
    }

    @Test
    void mapDtoToEntity_missingManagerDtoProperties_throwsIllegalArgumentException() {
        // given
        transactionDto.setUuid("30348dce-45f7-4e19-aa08-3ed77a8f7ac3");

        // when, then
        assertThrows(NullPointerException.class, () -> transactionDtoMapper.mapDtoToEntity(transactionDto));
    }

    @Test
    void getDtoList_validTransactionList_success() {
        // given
        List<Transaction> transactionList = List.of(transaction1, transaction2);

        // when
        List<TransactionDto> actual = transactionDtoMapper.getDtoList(transactionList);

        // then
        assertEquals(2, actual.size());

        TransactionDto transactionDto1 = actual.get(0);
        assertEquals(transaction1.getUuid().toString(), transactionDto1.getUuid());
        assertEquals(transaction1.getDebitAccountUuid().toString(), transactionDto1.getDebitAccountUuid());
        assertEquals(transaction1.getCreditAccountUuid().toString(), transactionDto1.getCreditAccountUuid());
        assertEquals(transaction1.getType().toString(), transactionDto1.getType());
        assertEquals(transaction1.getCurrencyCode().toString(), transactionDto1.getCurrencyCode());
        assertEquals(transaction1.getAmount(), transactionDto1.getAmount());
        assertEquals(transaction1.getDescription(), transactionDto1.getDescription());

        TransactionDto transactionDto2 = actual.get(1);
        assertEquals(transaction2.getUuid().toString(), transactionDto2.getUuid());
        assertEquals(transaction2.getDebitAccountUuid().toString(), transactionDto2.getDebitAccountUuid());
        assertEquals(transaction2.getCreditAccountUuid().toString(), transactionDto2.getCreditAccountUuid());
        assertEquals(transaction2.getType().toString(), transactionDto2.getType());
        assertEquals(transaction2.getCurrencyCode().toString(), transactionDto2.getCurrencyCode());
        assertEquals(transaction2.getAmount(), transactionDto2.getAmount());
        assertEquals(transaction2.getDescription(), transactionDto2.getDescription());
    }

    @Test
    void getDtoList_nullTransactionList_throwsDataNotFoundException() {
        assertThrows(DataNotFoundException.class, () -> transactionDtoMapper.getDtoList(null));
    }

    @Test
    void getDtoList_emptyTransactionList_returnsEmptyList() {
        // given
        List<Transaction> transactionList = Collections.emptyList();

        // when
        List<TransactionDto> actual = transactionDtoMapper.getDtoList(transactionList);

        // then
        assertTrue(actual.isEmpty());
    }
}
