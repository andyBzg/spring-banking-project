package org.crazymages.bankingspringproject.service.database.impl;

import org.crazymages.bankingspringproject.dto.AccountDto;
import org.crazymages.bankingspringproject.dto.TransactionDto;
import org.crazymages.bankingspringproject.entity.Account;
import org.crazymages.bankingspringproject.entity.Transaction;
import org.crazymages.bankingspringproject.entity.enums.AccountStatus;
import org.crazymages.bankingspringproject.entity.enums.CurrencyCode;
import org.crazymages.bankingspringproject.exception.DataNotFoundException;
import org.crazymages.bankingspringproject.exception.InsufficientFundsException;
import org.crazymages.bankingspringproject.exception.TransactionNotAllowedException;
import org.crazymages.bankingspringproject.repository.TransactionRepository;
import org.crazymages.bankingspringproject.service.database.AccountDatabaseService;
import org.crazymages.bankingspringproject.service.database.ClientDatabaseService;
import org.crazymages.bankingspringproject.service.utils.converter.CurrencyConverter;
import org.crazymages.bankingspringproject.service.utils.mapper.impl.AccountDtoMapper;
import org.crazymages.bankingspringproject.service.utils.mapper.impl.TransactionDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionDatabaseServiceImplTest {

    @Mock
    TransactionRepository transactionRepository;
    @Mock
    TransactionDtoMapper transactionDtoMapper;
    @Mock
    AccountDatabaseService accountDatabaseService;
    @Mock
    AccountDtoMapper accountDtoMapper;
    @Mock
    ClientDatabaseService clientDatabaseService;
    @Mock
    CurrencyConverter currencyConverter;

    @InjectMocks
    TransactionDatabaseServiceImpl transactionDatabaseService;

    Transaction transaction;
    TransactionDto transactionDto;
    UUID uuid;
    List<Transaction> transactions;
    List<TransactionDto> expected;
    AccountDto senderAccountDto;
    AccountDto recipientAccountDto;
    Account senderAccount;
    Account recipientAccount;

    @BeforeEach
    void setUp() {
        transaction = new Transaction();
        transactionDto = TransactionDto.builder().build();
        uuid = UUID.randomUUID();
        transactions = List.of(new Transaction(), new Transaction());
        expected = List.of(TransactionDto.builder().build(), TransactionDto.builder().build());
        senderAccountDto = AccountDto.builder().build();
        recipientAccountDto = AccountDto.builder().build();
        senderAccount = Account.builder().build();
        recipientAccount = Account.builder().build();
    }

    @Test
    void create_success() {
        // given
        when(transactionDtoMapper.mapDtoToEntity(transactionDto)).thenReturn(transaction);

        // when
        transactionDatabaseService.create(transactionDto);

        // then
        verify(transactionDtoMapper).mapDtoToEntity(transactionDto);
        verify(transactionRepository).save(transaction);
    }

    @Test
    void findAll_success() {
        // given
        when(transactionRepository.findAll()).thenReturn(transactions);
        when(transactionDtoMapper.getDtoList(transactions)).thenReturn(expected);

        // when
        List<TransactionDto> actual = transactionDatabaseService.findAll();

        // then
        assertEquals(expected, actual);
        verify(transactionRepository).findAll();
        verify(transactionDtoMapper).getDtoList(transactions);
    }

    @Test
    void findAll_withNull_emptyListReturned() {
        // given
        when(transactionRepository.findAll()).thenReturn(null);

        // when
        List<TransactionDto> actual = transactionDatabaseService.findAll();

        //then
        assertTrue(actual.isEmpty());
    }

    @Test
    void findById_success() {
        // given
        TransactionDto expected = TransactionDto.builder().build();
        when(transactionRepository.findById(uuid)).thenReturn(Optional.ofNullable(transaction));
        when(transactionDtoMapper.mapEntityToDto(transaction)).thenReturn(expected);

        // when
        TransactionDto actual = transactionDatabaseService.findById(String.valueOf(uuid));

        // then
        assertEquals(expected, actual);
        verify(transactionRepository).findById(uuid);
        verify(transactionDtoMapper).mapEntityToDto(transaction);
    }

    @Test
    void findById_nonExistentTransaction_throwsDataNotFoundException() {
        // given
        UUID uuid = UUID.fromString("f0621a3c-6849-4ef5-8fc2-7bf7dd450d26");
        String uuidString = "f0621a3c-6849-4ef5-8fc2-7bf7dd450d26";
        when(transactionRepository.findById(uuid)).thenReturn(Optional.empty());

        // when, then
        assertThrows(DataNotFoundException.class, () -> transactionDatabaseService.findById(uuidString));
        verify(transactionRepository).findById(uuid);
        verifyNoInteractions(transactionDtoMapper);
    }

    @Test
    void findOutgoingTransactions_success() {
        // given
        when(transactionRepository.findTransactionsByDebitAccountUuid(uuid)).thenReturn(transactions);
        when(transactionDtoMapper.getDtoList(transactions)).thenReturn(expected);

        // when
        List<TransactionDto> actual = transactionDatabaseService.findOutgoingTransactions(String.valueOf(uuid));

        // then
        assertEquals(expected, actual);
        verify(transactionRepository).findTransactionsByDebitAccountUuid(uuid);
        verify(transactionDtoMapper).getDtoList(transactions);
    }

    @Test
    void findIncomingTransactions_success() {
        /// given
        when(transactionRepository.findTransactionsByCreditAccountUuid(uuid)).thenReturn(transactions);
        when(transactionDtoMapper.getDtoList(transactions)).thenReturn(expected);

        // when
        List<TransactionDto> actual = transactionDatabaseService.findIncomingTransactions(String.valueOf(uuid));

        // then
        assertEquals(expected, actual);
        verify(transactionRepository).findTransactionsByCreditAccountUuid(uuid);
        verify(transactionDtoMapper).getDtoList(transactions);
    }

    @Test
    void findAllTransactionsByClientId_success() {
        // given
        when(transactionRepository.findAllTransactionsWhereClientIdIs(uuid)).thenReturn(transactions);
        when(transactionDtoMapper.getDtoList(transactions)).thenReturn(expected);

        // when
        List<TransactionDto> actual = transactionDatabaseService.findAllTransactionsByClientId(String.valueOf(uuid));

        // then
        assertEquals(expected, actual);
        verify(transactionRepository).findAllTransactionsWhereClientIdIs(uuid);
        verify(transactionDtoMapper).getDtoList(transactions);
    }

    @Test
    void transferFunds_validData_sameCurrency_success() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDebitAccountUuid(UUID.fromString("ed3a5e5a-cd77-4052-91fc-b042f2aa4dbe"));
        transaction.setCreditAccountUuid(UUID.fromString("b0e642b4-d957-4cee-b4ca-13839ad16a20"));

        Account sender = Account.builder().build();
        sender.setClientUuid(UUID.fromString("1989d4da-0f91-46d3-96c6-2b4a72950c89"));
        sender.setBalance(BigDecimal.valueOf(200));
        sender.setStatus(AccountStatus.ACTIVE);
        sender.setCurrencyCode(CurrencyCode.EUR);
        when(accountDatabaseService.findById(transaction.getDebitAccountUuid())).thenReturn(sender);

        Account recipient = Account.builder().build();
        recipient.setClientUuid(UUID.fromString("7e3dc741-7e9a-4b60-9f96-da9fc0924927"));
        recipient.setBalance(BigDecimal.ZERO);
        recipient.setStatus(AccountStatus.ACTIVE);
        recipient.setCurrencyCode(CurrencyCode.EUR);
        when(accountDatabaseService.findById(transaction.getCreditAccountUuid())).thenReturn(recipient);

        when(clientDatabaseService.isClientStatusActive(sender.getClientUuid())).thenReturn(true);
        when(clientDatabaseService.isClientStatusActive(recipient.getClientUuid())).thenReturn(true);

        // when
        transactionDatabaseService.transferFunds(transaction);

        // then
        verify(accountDatabaseService).findById(transaction.getDebitAccountUuid());
        verify(accountDatabaseService).findById(transaction.getCreditAccountUuid());
        verify(clientDatabaseService).isClientStatusActive(sender.getClientUuid());
        verify(clientDatabaseService).isClientStatusActive(recipient.getClientUuid());
        verify(accountDatabaseService).update(sender.getUuid(), sender);
        verify(accountDatabaseService).update(recipient.getUuid(), recipient);
        verify(transactionRepository).save(transaction);
        verifyNoInteractions(currencyConverter);
        assertEquals(recipient.getBalance(), amount);
    }

    @Test
    void transferFunds_validData_withNegativeAmount_throwsIllegalArgumentException() {
        // given
        BigDecimal amount = BigDecimal.valueOf(-100);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDebitAccountUuid(UUID.fromString("ed3a5e5a-cd77-4052-91fc-b042f2aa4dbe"));
        transaction.setCreditAccountUuid(UUID.fromString("b0e642b4-d957-4cee-b4ca-13839ad16a20"));

        Account sender = Account.builder().build();
        sender.setClientUuid(UUID.fromString("1989d4da-0f91-46d3-96c6-2b4a72950c89"));
        sender.setBalance(BigDecimal.valueOf(200));
        sender.setStatus(AccountStatus.ACTIVE);
        sender.setCurrencyCode(CurrencyCode.EUR);
        when(accountDatabaseService.findById(transaction.getDebitAccountUuid())).thenReturn(sender);

        Account recipient = Account.builder().build();
        recipient.setClientUuid(UUID.fromString("7e3dc741-7e9a-4b60-9f96-da9fc0924927"));
        recipient.setBalance(BigDecimal.ZERO);
        recipient.setStatus(AccountStatus.ACTIVE);
        recipient.setCurrencyCode(CurrencyCode.EUR);
        when(accountDatabaseService.findById(transaction.getCreditAccountUuid())).thenReturn(recipient);

        when(clientDatabaseService.isClientStatusActive(sender.getClientUuid())).thenReturn(true);
        when(clientDatabaseService.isClientStatusActive(recipient.getClientUuid())).thenReturn(true);

        // when, then
        assertThrows(IllegalArgumentException.class, () -> transactionDatabaseService.transferFunds(transaction));
        verify(accountDatabaseService, times(2)).findById(any(UUID.class));
        verify(clientDatabaseService, times(2)).isClientStatusActive(any(UUID.class));
        verifyNoInteractions(currencyConverter);
        verifyNoMoreInteractions(accountDatabaseService);
        verify(transactionRepository, never()).save(transaction);

    }

    @Test
    void transferFunds_validData_differentCurrencies_success() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDebitAccountUuid(UUID.fromString("ed3a5e5a-cd77-4052-91fc-b042f2aa4dbe"));
        transaction.setCreditAccountUuid(UUID.fromString("b0e642b4-d957-4cee-b4ca-13839ad16a20"));

        Account sender = Account.builder().build();
        sender.setClientUuid(UUID.fromString("1989d4da-0f91-46d3-96c6-2b4a72950c89"));
        sender.setBalance(BigDecimal.valueOf(200));
        sender.setStatus(AccountStatus.ACTIVE);
        sender.setCurrencyCode(CurrencyCode.GBP);
        when(accountDatabaseService.findById(transaction.getDebitAccountUuid())).thenReturn(sender);

        Account recipient = Account.builder().build();
        recipient.setClientUuid(UUID.fromString("7e3dc741-7e9a-4b60-9f96-da9fc0924927"));
        recipient.setBalance(BigDecimal.ZERO);
        recipient.setStatus(AccountStatus.ACTIVE);
        recipient.setCurrencyCode(CurrencyCode.AUD);
        when(accountDatabaseService.findById(transaction.getCreditAccountUuid())).thenReturn(recipient);

        when(clientDatabaseService.isClientStatusActive(sender.getClientUuid())).thenReturn(true);
        when(clientDatabaseService.isClientStatusActive(recipient.getClientUuid())).thenReturn(true);

        when(currencyConverter.performCurrencyConversion(amount, recipient, sender)).thenReturn(recipient);

        // when
        transactionDatabaseService.transferFunds(transaction);

        // then
        verify(accountDatabaseService).findById(transaction.getDebitAccountUuid());
        verify(accountDatabaseService).findById(transaction.getCreditAccountUuid());
        verify(clientDatabaseService).isClientStatusActive(sender.getClientUuid());
        verify(clientDatabaseService).isClientStatusActive(recipient.getClientUuid());
        verify(accountDatabaseService).update(sender.getUuid(), sender);
        verify(accountDatabaseService).update(recipient.getUuid(), recipient);
        verify(transactionRepository).save(transaction);
    }

    /*@Test
    void transferFunds_atLeastOneOfEntityFieldIsNull_throwsIllegalArgumentException() {
        // given
        when(accountDatabaseService.findDtoById(String.valueOf(transaction.getDebitAccountUuid()))).thenReturn(senderAccountDto);
        when(accountDatabaseService.findDtoById(String.valueOf(transaction.getCreditAccountUuid()))).thenReturn(recipientAccountDto);

        when(clientDatabaseService.isClientStatusActive(senderAccount.getClientUuid())).thenReturn(true);
        when(clientDatabaseService.isClientStatusActive(senderAccount.getClientUuid())).thenReturn(true);

        // when, then
        assertThrows(NullPointerException.class, () -> transactionDatabaseService.transferFunds(transaction));
        verifyNoInteractions(currencyConverter);
        verifyNoMoreInteractions(accountDtoMapper);
//        verifyNoMoreInteractions(accountDatabaseService);
        verifyNoInteractions(transactionRepository);
    }*/

    @Test
    void transferFunds_senderBalanceIsTooLow_throwsInsufficientFundsException() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDebitAccountUuid(UUID.fromString("ed3a5e5a-cd77-4052-91fc-b042f2aa4dbe"));
        transaction.setCreditAccountUuid(UUID.fromString("b0e642b4-d957-4cee-b4ca-13839ad16a20"));

        Account sender = Account.builder().build();
        sender.setClientUuid(UUID.fromString("1989d4da-0f91-46d3-96c6-2b4a72950c89"));
        sender.setBalance(BigDecimal.valueOf(50));
        sender.setStatus(AccountStatus.ACTIVE);
        sender.setCurrencyCode(CurrencyCode.EUR);
        when(accountDatabaseService.findById(transaction.getDebitAccountUuid())).thenReturn(sender);

        Account recipient = Account.builder().build();
        recipient.setClientUuid(UUID.fromString("7e3dc741-7e9a-4b60-9f96-da9fc0924927"));
        recipient.setBalance(BigDecimal.ZERO);
        recipient.setStatus(AccountStatus.ACTIVE);
        recipient.setCurrencyCode(CurrencyCode.EUR);
        when(accountDatabaseService.findById(transaction.getCreditAccountUuid())).thenReturn(recipient);

        when(clientDatabaseService.isClientStatusActive(sender.getClientUuid())).thenReturn(true);
        when(clientDatabaseService.isClientStatusActive(recipient.getClientUuid())).thenReturn(true);

        // when, then
        assertThrows(InsufficientFundsException.class, () -> transactionDatabaseService.transferFunds(transaction));
        verifyNoInteractions(currencyConverter);
        verifyNoMoreInteractions(accountDtoMapper);
        verifyNoMoreInteractions(accountDatabaseService);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transferFunds_senderAccountIsNotActive_throwsTransactionNotAllowedException() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDebitAccountUuid(UUID.fromString("ed3a5e5a-cd77-4052-91fc-b042f2aa4dbe"));
        transaction.setCreditAccountUuid(UUID.fromString("b0e642b4-d957-4cee-b4ca-13839ad16a20"));

        Account sender = Account.builder().build();
        sender.setClientUuid(UUID.fromString("1989d4da-0f91-46d3-96c6-2b4a72950c89"));
        sender.setBalance(BigDecimal.valueOf(200));
        sender.setStatus(AccountStatus.CLOSED);
        sender.setCurrencyCode(CurrencyCode.EUR);
        when(accountDatabaseService.findById(transaction.getDebitAccountUuid())).thenReturn(sender);

        Account recipient = Account.builder().build();
        recipient.setClientUuid(UUID.fromString("7e3dc741-7e9a-4b60-9f96-da9fc0924927"));
        recipient.setBalance(BigDecimal.ZERO);
        recipient.setStatus(AccountStatus.ACTIVE);
        recipient.setCurrencyCode(CurrencyCode.EUR);
        when(accountDatabaseService.findById(transaction.getCreditAccountUuid())).thenReturn(recipient);

        when(clientDatabaseService.isClientStatusActive(sender.getClientUuid())).thenReturn(true);
        when(clientDatabaseService.isClientStatusActive(recipient.getClientUuid())).thenReturn(true);


        // when, then
        assertThrows(TransactionNotAllowedException.class, () -> transactionDatabaseService.transferFunds(transaction));
        verifyNoInteractions(currencyConverter);
        verifyNoMoreInteractions(accountDtoMapper);
        verifyNoMoreInteractions(accountDatabaseService);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void transferFunds_recipientIsNotActive_throwsTransactionNotAllowedException() {
        // given
        BigDecimal amount = BigDecimal.valueOf(100);
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDebitAccountUuid(UUID.fromString("ed3a5e5a-cd77-4052-91fc-b042f2aa4dbe"));
        transaction.setCreditAccountUuid(UUID.fromString("b0e642b4-d957-4cee-b4ca-13839ad16a20"));

        Account sender = Account.builder().build();
        sender.setClientUuid(UUID.fromString("1989d4da-0f91-46d3-96c6-2b4a72950c89"));
        sender.setBalance(BigDecimal.valueOf(200));
        sender.setStatus(AccountStatus.ACTIVE);
        sender.setCurrencyCode(CurrencyCode.EUR);
        when(accountDatabaseService.findById(transaction.getDebitAccountUuid())).thenReturn(sender);

        Account recipient = Account.builder().build();
        recipient.setClientUuid(UUID.fromString("7e3dc741-7e9a-4b60-9f96-da9fc0924927"));
        recipient.setBalance(BigDecimal.ZERO);
        recipient.setStatus(AccountStatus.CLOSED);
        recipient.setCurrencyCode(CurrencyCode.EUR);
        when(accountDatabaseService.findById(transaction.getCreditAccountUuid())).thenReturn(recipient);

        when(clientDatabaseService.isClientStatusActive(sender.getClientUuid())).thenReturn(true);
        when(clientDatabaseService.isClientStatusActive(recipient.getClientUuid())).thenReturn(true);


        // when, then
        assertThrows(TransactionNotAllowedException.class, () -> transactionDatabaseService.transferFunds(transaction));
        verifyNoInteractions(currencyConverter);
        verifyNoMoreInteractions(accountDtoMapper);
        verifyNoMoreInteractions(accountDatabaseService);
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void findTransactionsByClientIdBetweenDates_success() {
        // given
        String from = "2023-07-15";
        String to = "2023-07-16";
        Timestamp start = Timestamp.valueOf(LocalDate.parse(from).atStartOfDay());
        Timestamp end = Timestamp.valueOf(LocalDate.parse(to).atStartOfDay());
        when(transactionRepository
                .findTransactionsByClientIdBetweenDates(uuid, start, end))
                .thenReturn(transactions);
        when(transactionDtoMapper.getDtoList(transactions)).thenReturn(expected);

        // when
        List<TransactionDto> actual = transactionDatabaseService
                .findTransactionsByClientIdBetweenDates(String.valueOf(uuid), "2023-07-15", "2023-07-16");

        // then
        assertEquals(expected, actual);
        verify(transactionRepository).findTransactionsByClientIdBetweenDates(uuid, start, end);
        verify(transactionDtoMapper).getDtoList(transactions);
    }

    @Test
    void findTransactionsByClientIdBetweenDates_invalidDate_throwsDateTimeParseException() {
        // given
        String from = "2023-07-15";
        String to = "0000-00-00";
        String strUuid = String.valueOf(uuid);

        // when
        assertThrows(DateTimeParseException.class, () -> transactionDatabaseService
                .findTransactionsByClientIdBetweenDates(strUuid, from, to));

        // then
        verifyNoInteractions(transactionRepository);
        verifyNoInteractions(transactionDtoMapper);
    }

    @Test
    void findTransactionsByClientIdBetweenDates_noTransactions_returnsEmptyList() {
        // given
        String from = "2023-07-15";
        String to = "2023-07-16";
        Timestamp start = Timestamp.valueOf(LocalDate.parse(from).atStartOfDay());
        Timestamp end = Timestamp.valueOf(LocalDate.parse(to).atStartOfDay());
        when(transactionRepository
                .findTransactionsByClientIdBetweenDates(uuid, start, end))
                .thenReturn(Collections.emptyList());

        // when
        List<TransactionDto> actual = transactionDatabaseService
                .findTransactionsByClientIdBetweenDates(String.valueOf(uuid), "2023-07-15", "2023-07-16");

        // then
        assertTrue(actual.isEmpty());
        verify(transactionRepository).findTransactionsByClientIdBetweenDates(uuid, start, end);
        verify(transactionDtoMapper).getDtoList(anyList());
    }

    @Test
    void findTransactionsBetweenDates_success() {
        // given
        String from = "2023-07-15";
        String to = "2023-07-16";
        Timestamp start = Timestamp.valueOf(LocalDate.parse(from).atStartOfDay());
        Timestamp end = Timestamp.valueOf(LocalDate.parse(to).atStartOfDay());
        when(transactionRepository.findTransactionsBetweenDates(start, end)).thenReturn(transactions);
        when(transactionDtoMapper.getDtoList(transactions)).thenReturn(expected);

        // when
        List<TransactionDto> actual = transactionDatabaseService.findTransactionsBetweenDates(from, to);

        // then
        assertEquals(expected, actual);
        verify(transactionRepository).findTransactionsBetweenDates(start, end);
        verify(transactionDtoMapper).getDtoList(transactions);
    }
}
