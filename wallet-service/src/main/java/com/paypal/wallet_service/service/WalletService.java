package com.paypal.wallet_service.service;

import com.paypal.wallet_service.dto.*;
import com.paypal.wallet_service.entity.Transaction;
import com.paypal.wallet_service.entity.Wallet;
import com.paypal.wallet_service.entity.WalletHold;
import com.paypal.wallet_service.exception.InsufficientFundsException;
import com.paypal.wallet_service.exception.NotFoundException;
import com.paypal.wallet_service.repository.TransactionRepository;
import com.paypal.wallet_service.repository.WalletHoldRepository;
import com.paypal.wallet_service.repository.WalletRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.server.NotAcceptableStatusException;

import java.util.Optional;

@Service
public class WalletService {

    private final WalletHoldRepository walletHoldRepository;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;


    public WalletService(WalletHoldRepository walletHoldRepository, TransactionRepository transactionRepository, WalletRepository walletRepository) {
        this.walletHoldRepository = walletHoldRepository;
        this.transactionRepository = transactionRepository;
        this.walletRepository = walletRepository;
    }

    @Transactional
    public WalletResponse createWallet(CreateWalletRequest walletRequest){
        Wallet wallet=new Wallet(walletRequest.getUserId(),walletRequest.getCurrency());
        Wallet saved=walletRepository.save(wallet);

        return  new WalletResponse(
                saved.getId(), saved.getUserId(), saved.getCurrency(),saved.getBalance(),saved.getAvailableBalance()
        );
    }

    @Transactional
    public WalletResponse credit(CreditRequest creditRequest){
        System.out.println("💰 CREDIT request received: userId=" + creditRequest.getUserId() +
                ", amount=" + creditRequest.getAmount() +", " +
                "currency=" + creditRequest.getCurrency());
        Wallet wallet=walletRepository.findByUserIdAndCurrency(creditRequest.getUserId(),creditRequest.getCurrency())
                .orElseThrow(()->new NotAcceptableStatusException("Wallet not found for user: "+creditRequest.getUserId()));

        wallet.setBalance(wallet.getBalance()+creditRequest.getAmount());
        wallet.setAvailableBalance(wallet.getAvailableBalance()+creditRequest.getAmount());

        Wallet saved=walletRepository.save(wallet);

        Long amount=creditRequest.getAmount();

        transactionRepository.save( new Transaction(wallet.getId(), "CREDIT", amount, "SUCCESS"));

        System.out.println("✅ CREDIT done: walletId=" + saved.getId() +
                ", newBalance=" + saved.getBalance() +
                ", availableBalance=" + saved.getAvailableBalance());

        return new WalletResponse(
                saved.getId(), saved.getUserId(), saved.getCurrency(),
                saved.getBalance(), saved.getAvailableBalance()

        );
    }

    @Transactional
    public WalletResponse debit(DebitRequest debitRequest){
        System.out.println("💸 DEBIT request received: userId=" + debitRequest.getUserId() +
                ", amount=" + debitRequest.getAmount() +
                ", currency=" + debitRequest.getCurrency());

        Wallet wallet=walletRepository.findByUserIdAndCurrency(debitRequest.getUserId(),"INR")
                .orElseThrow(()->new NotFoundException("Wallet not found for user : "+debitRequest.getUserId()));

        if(wallet.getAvailableBalance()<debitRequest.getAmount()){
            throw  new InsufficientFundsException("Not enough Balance");
        }

        wallet.setBalance(wallet.getBalance()-debitRequest.getAmount());
        wallet.setAvailableBalance(wallet.getAvailableBalance()-debitRequest.getAmount());
        Wallet saved=walletRepository.save(wallet);

        System.out.println("✅ DEBIT done: walletId=" + saved.getId() +
                ", newBalance=" + saved.getBalance() +
                ", availableBalance=" + saved.getAvailableBalance());

        return new WalletResponse(
                saved.getId(), saved.getUserId(), saved.getCurrency(),
                saved.getBalance(), saved.getAvailableBalance()
        );
    }

    public WalletResponse getWallet(int userId){

        CreateWalletRequest walletRequest=new CreateWalletRequest();
        walletRequest.setUserId(userId);
        walletRequest.setCurrency("INR");

        Optional<Wallet> wallet=walletRepository.findByUserId(userId);

        if(wallet.isEmpty()){
           return createWallet(walletRequest);
        }

        return new WalletResponse(
                wallet.get().getId(), wallet.get().getUserId(), wallet.get().getCurrency(),
                wallet.get().getBalance(), wallet.get().getAvailableBalance()
        );
    }

    @Transactional
    public HoldResponse  placeHold(HoldRequest holdRequest){
        Wallet wallet=walletRepository.findByUserIdAndCurrency(holdRequest.getUserId(), holdRequest.getCurrency())
                .orElseThrow(()->new NotAcceptableStatusException("Wallet not found for user: "+holdRequest.getUserId()));

        if (wallet.getAvailableBalance() < holdRequest.getAmount()) {
            throw new InsufficientFundsException("Not enough balance to hold");
        }
        wallet.setAvailableBalance(wallet.getAvailableBalance()-holdRequest.getAmount());

        WalletHold hold = new WalletHold();
        hold.setWallet(wallet);
        hold.setAmount(holdRequest.getAmount());
        hold.setHoldReference("HOLD-" + System.currentTimeMillis());
        hold.setStatus("ACTIVE");

        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        return new HoldResponse(hold.getHoldReference(), hold.getAmount(), hold.getStatus());

    }
    @Transactional
    public WalletResponse captureHold(CaptureRequest request) {
        WalletHold hold = walletHoldRepository.findByHoldReference(request.getHoldReference())
                .orElseThrow(() -> new NotFoundException("Hold not found"));

        if (!"ACTIVE".equals(hold.getStatus())) {
            throw new IllegalStateException("Hold is not active");
        }

        Wallet wallet = hold.getWallet();
        wallet.setBalance(wallet.getBalance() - hold.getAmount());

        hold.setStatus("CAPTURED");
        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        return new WalletResponse(wallet.getId(), wallet.getUserId(),
                wallet.getCurrency(), wallet.getBalance(), wallet.getAvailableBalance());
    }

    @Transactional
    public HoldResponse releaseHold(String holdReference) {
        WalletHold hold = walletHoldRepository.findByHoldReference(holdReference)
                .orElseThrow(() -> new NotFoundException("Hold not found"));

        if (!"ACTIVE".equals(hold.getStatus())) {
            throw new IllegalStateException("Hold is not active");
        }

        Wallet wallet = hold.getWallet();
        wallet.setAvailableBalance(wallet.getAvailableBalance() + hold.getAmount());

        hold.setStatus("RELEASED");
        walletRepository.save(wallet);
        walletHoldRepository.save(hold);

        return new HoldResponse(hold.getHoldReference(), hold.getAmount(), hold.getStatus());
    }

}
