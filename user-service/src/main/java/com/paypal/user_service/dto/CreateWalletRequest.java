package com.paypal.user_service.dto;

public class CreateWalletRequest {

    private int userId;

    private  String currency;


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
