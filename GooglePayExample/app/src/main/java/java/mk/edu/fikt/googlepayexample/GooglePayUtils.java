package java.mk.edu.fikt.googlepayexample;

import android.app.Activity;

import com.google.android.gms.wallet.CardRequirements;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentMethodTokenizationParameters;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.ShippingAddressRequirements;
import com.google.android.gms.wallet.TransactionInfo;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;


public class GooglePayUtils {
    private static final BigDecimal MICROS = new BigDecimal(1000000d);

    public static final List<Integer> SUPPORTED_METHODS =
            Arrays.asList(
                    WalletConstants.PAYMENT_METHOD_CARD,
                    WalletConstants.PAYMENT_METHOD_TOKENIZED_CARD);

    public static final List<Integer> SUPPORTED_NETWORKS = Arrays.asList(
            WalletConstants.CARD_NETWORK_VISA,
            WalletConstants.CARD_NETWORK_MASTERCARD
            );

    public static final String CURRENCY_CODE = "USD";
    public static final List<String> SHIPPING_SUPPORTED_COUNTRIES = Arrays.asList("US", "GB");

    public static PaymentsClient createPaymentsClient(Activity activity) {
        Wallet.WalletOptions walletOptions =
                new Wallet.WalletOptions.Builder().setEnvironment(WalletConstants.ENVIRONMENT_TEST).build();
        return Wallet.getPaymentsClient(activity, walletOptions);
    }

    public static String microsToString(int i) {
            return new BigDecimal(i).divide(MICROS).setScale(2, RoundingMode.HALF_EVEN).toString();
    }

    public static TransactionInfo createTransaction(String price) {
        return TransactionInfo.newBuilder()
                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                .setTotalPrice(price)
                .setCurrencyCode("USD")
                .build();
    }

    public static PaymentDataRequest createPaymentDataRequest(TransactionInfo transactionInfo){
        PaymentMethodTokenizationParameters paramsBuilder = PaymentMethodTokenizationParameters
                .newBuilder()
                .setPaymentMethodTokenizationType(WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                .addParameter("gateway", "http://www.example.com")
                .addParameter("gatewayMerchantId", "Example Merchant Name")
                .build();
        return  createPaymentDataRequest(transactionInfo, paramsBuilder);
    }

    public static PaymentDataRequest createPaymentDataRequest(TransactionInfo transactionInfo,
                                                            PaymentMethodTokenizationParameters paymentMethodTokenizationParameters){
        return PaymentDataRequest.newBuilder()
                .setPhoneNumberRequired(false)
                .setEmailRequired(true)
                .setShippingAddressRequired(true)
                .setShippingAddressRequirements(
                        ShippingAddressRequirements.newBuilder()
                                .addAllowedCountryCodes(SHIPPING_SUPPORTED_COUNTRIES)
                                .build()
                )
                .setTransactionInfo(transactionInfo)
                .addAllowedPaymentMethods(SUPPORTED_METHODS)
                .setCardRequirements(
                        CardRequirements.newBuilder()
                                .addAllowedCardNetworks(SUPPORTED_NETWORKS)
                                .setAllowPrepaidCards(true)
                                .setBillingAddressFormat(WalletConstants.BILLING_ADDRESS_FORMAT_FULL)
                                .build()
                )
                .setPaymentMethodTokenizationParameters(paymentMethodTokenizationParameters)
                .setUiRequired(true)
                .build();
    }

}
