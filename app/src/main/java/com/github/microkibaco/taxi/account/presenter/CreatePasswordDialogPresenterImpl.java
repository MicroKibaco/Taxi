package com.github.microkibaco.taxi.account.presenter;


public class CreatePasswordDialogPresenterImpl implements ICreatePasswordDialogPresenter {


    @Override
    public boolean checkPw(String pw, String newPw) {
        return false;
    }

    @Override
    public void requestRegister(String phone, String pw) {

    }

    @Override
    public void requestLogin(String phone, String pw) {

    }
}
