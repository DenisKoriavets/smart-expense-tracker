package com.github.deniskoriavets.smartexpensetracker.service;

import com.github.deniskoriavets.smartexpensetracker.dto.transaction.CreateTransactionDto;
import com.github.deniskoriavets.smartexpensetracker.dto.transaction.TransactionResponseDto;
import com.github.deniskoriavets.smartexpensetracker.dto.transaction.UpdateTransactionDto;
import com.github.deniskoriavets.smartexpensetracker.entity.User;
import com.github.deniskoriavets.smartexpensetracker.mapper.TransactionMapper;
import com.github.deniskoriavets.smartexpensetracker.repository.AccountRepository;
import com.github.deniskoriavets.smartexpensetracker.repository.CategoryRepository;
import com.github.deniskoriavets.smartexpensetracker.repository.TransactionRepository;
import com.github.deniskoriavets.smartexpensetracker.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionMapper transactionMapper;

    public TransactionResponseDto createTransaction(CreateTransactionDto dto) {
        var user = getCurrentUser();

        var account = accountRepository.findById(dto.accountId()).orElseThrow(EntityNotFoundException::new);
        if (!account.getUser().getId().equals(user.getId()))
            throw new EntityNotFoundException();
        var category = categoryRepository.findById(dto.categoryId()).orElseThrow(EntityNotFoundException::new);
        if (!category.getUser().getId().equals(user.getId()))
            throw new EntityNotFoundException();

        var transaction = transactionMapper.toEntity(dto);
        transaction.setAccount(account);
        transaction.setCategory(category);
        transaction.setCreatedAt(LocalDateTime.now());
        return transactionMapper.toDto(transactionRepository.save(transaction));
    }

    public List<TransactionResponseDto> getTransactionsByAccountId(UUID accountId) {
        var user = getCurrentUser();

        var account = accountRepository.findById(accountId).orElseThrow(EntityNotFoundException::new);
        if (!account.getUser().getId().equals(user.getId()))
            throw new EntityNotFoundException();

        return transactionRepository.findByAccountId(accountId).stream().map(transactionMapper::toDto).toList();
    }

    public TransactionResponseDto getTransactionById(UUID id) {
        var user = getCurrentUser();

        var transaction = transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!transaction.getAccount().getUser().getId().equals(user.getId()))
            throw new EntityNotFoundException();

        return transactionMapper.toDto(transaction);
    }

    public TransactionResponseDto updateTransaction(UUID id, UpdateTransactionDto dto) {
        var user = getCurrentUser();

        var transaction = transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!transaction.getAccount().getUser().getId().equals(user.getId()))
            throw new EntityNotFoundException();

        var category = categoryRepository.findById(dto.categoryId()).orElseThrow(EntityNotFoundException::new);
        if(!category.getUser().getId().equals(user.getId()))
            throw new EntityNotFoundException();

        transactionMapper.updateTransaction(transaction, dto);
        transaction.setCategory(category);

        transactionRepository.save(transaction);
        return transactionMapper.toDto(transaction);
    }

    public void deleteTransaction(UUID id) {
        var user = getCurrentUser();

        var transaction = transactionRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        if(!transaction.getAccount().getUser().getId().equals(user.getId()))
            throw new EntityNotFoundException();
        transactionRepository.delete(transaction);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}