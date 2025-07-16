package ru.netology.tests.test;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.*;
import org.openqa.selenium.chrome.ChromeOptions;
import ru.netology.tests.data.DataHelper;
import ru.netology.tests.page.DashboardPage;
import ru.netology.tests.page.LoginPage;

import java.util.HashMap;
import java.util.Map;

public class MoneyTransferTest {

    @BeforeEach
    void shouldOpen() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-save-password-bubble");
        options.addArguments("--disable-password-manager-reauthentication");
        options.setExperimentalOption("prefs", new java.util.HashMap<>() {{
            put("credentials_enable_service", false);
            put("profile.password_manager_enabled", false);
        }});

        Selenide.open("http://localhost:9999");
    }

    @Test
    void TransferMoneyFromSecondCardToFirstCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        int beforeBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int beforeBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        var transferPage = dashboardPage.replenishCard(DataHelper.getFirstCardInfo(authInfo));
        transferPage.transferMoney(
                DataHelper.getSecondCardInfo(authInfo).getCardNumber(), 100);
        int afterBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int afterBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        Assertions.assertEquals(beforeBalanceFirstCard + 100, afterBalanceFirstCard);
        Assertions.assertEquals(beforeBalanceSecondCard - 100, afterBalanceSecondCard);
    }

    @Test
    void TransferMoneyFromFirstCardToSecondCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        int beforeBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int beforeBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        var transferPage = dashboardPage.replenishCard(DataHelper.getSecondCardInfo(authInfo));
        transferPage.transferMoney(
                DataHelper.getFirstCardInfo(authInfo).getCardNumber(), 100);
        int afterBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int afterBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        Assertions.assertEquals(beforeBalanceFirstCard - 100, afterBalanceFirstCard);
        Assertions.assertEquals(beforeBalanceSecondCard + 100, afterBalanceSecondCard);
    }

    @Test
    void TransferZeroMoneyFromFirstCardToSecondCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        int beforeBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int beforeBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        var transferPage = dashboardPage.replenishCard(DataHelper.getSecondCardInfo(authInfo));
        transferPage.transferMoney(
                DataHelper.getFirstCardInfo(authInfo).getCardNumber(), 0);
        int afterBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int afterBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        Assertions.assertEquals(beforeBalanceFirstCard, afterBalanceFirstCard);
        Assertions.assertEquals(beforeBalanceSecondCard, afterBalanceSecondCard);
    }

    @AfterEach
    void returnToOriginal() {
        var authInfo = DataHelper.getAuthInfo();
        DashboardPage dashboardPage = new DashboardPage();
        int beforeBalanceFirstCard = dashboardPage.getIdAccountBalance(DataHelper.getFirstCardInfo(authInfo));
        int beforeBalanceSecondCard = dashboardPage.getIdAccountBalance(DataHelper.getSecondCardInfo(authInfo));
        int differenceBetween;
        if (beforeBalanceFirstCard == beforeBalanceSecondCard) {
            return;
        }
        if (beforeBalanceFirstCard > beforeBalanceSecondCard) {
            differenceBetween = beforeBalanceFirstCard - beforeBalanceSecondCard;
            differenceBetween = differenceBetween / 2;
            var transferPage = dashboardPage.replenishCard(DataHelper.getSecondCardInfo(authInfo));
            transferPage.transferMoney(
                    DataHelper.getFirstCardInfo(authInfo).getCardNumber(),
                    differenceBetween);
        } else {
            differenceBetween = beforeBalanceSecondCard - beforeBalanceFirstCard;
            differenceBetween = differenceBetween / 2;
            var transferPage = dashboardPage.replenishCard(DataHelper.getFirstCardInfo(authInfo));
            transferPage.transferMoney(
                    DataHelper.getSecondCardInfo(authInfo).getCardNumber(),
                    differenceBetween);
        }
    }

    @Test
    void transferFromFirstCardToSecondCardAmountMoreThanOnCard() {
        var loginPage = new LoginPage();
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var transferPage = dashboardPage.replenishCard(DataHelper.getSecondCardInfo(authInfo));
        transferPage.transferMoney(
                DataHelper.getFirstCardInfo(authInfo).getCardNumber(), 11000);
        transferPage.errorMessage();
    }

}