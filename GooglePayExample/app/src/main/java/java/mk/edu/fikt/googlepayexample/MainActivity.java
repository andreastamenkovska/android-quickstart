package java.mk.edu.fikt.googlepayexample;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.TransactionInfo;

public class MainActivity extends AppCompatActivity {
    private PaymentsClient mPaymentsClient;
    private View mGooglePayButton;
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 991;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPaymentsClient = GooglePayUtils.createPaymentsClient(this);

        mGooglePayButton = findViewById(R.id.googlepay_button);
        mGooglePayButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        requestPayment(view);
                    }
                });
        mGooglePayButton.performClick();
    }

    private void requestPayment(View view) {
        mGooglePayButton.setClickable(false);

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        String price = GooglePayUtils.microsToString(399);

        TransactionInfo transaction = GooglePayUtils.createTransaction(price);
        PaymentDataRequest paymentDataRequest = GooglePayUtils.createPaymentDataRequest(transaction);
        Task<PaymentData> futurePaymentData = mPaymentsClient.loadPaymentData(paymentDataRequest);

        AutoResolveHelper.resolveTask(futurePaymentData, this, LOAD_PAYMENT_DATA_REQUEST_CODE);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE :
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        onPaymentSuccess(paymentData);
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();
                        // Nothing to here normally - the user simply cancelled without selecting a
                        // payment method.
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        onPaymentError(status.getStatusCode());
                        break;
                    default:
                        // Do nothing.
                }

                // Re-enables the Google Pay payment button.
                mGooglePayButton.setClickable(true);
                break;
        }
    }

    private void  onPaymentSuccess(PaymentData paymentData) {
        Toast.makeText(this, "Payment Success" + paymentData.getGoogleTransactionId(), Toast.LENGTH_SHORT).show();
    }

    private void  onPaymentError(int  status) {
        Log.w(MainActivity.class.getSimpleName(), "onPaymentError: " + status );
    }

}
