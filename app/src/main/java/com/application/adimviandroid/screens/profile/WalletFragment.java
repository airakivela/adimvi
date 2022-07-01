package com.application.adimviandroid.screens.profile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.application.adimviandroid.R;
import com.application.adimviandroid.models.InAppProductModel;
import com.application.adimviandroid.screens.CreditActivity;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.screens.sales.SalesFragment;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;
import com.application.adimviandroid.utils.SharedUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletFragment extends Fragment implements View.OnClickListener {

    private MainActivity mActivity;
    private boolean hasNew;
    private int tabIndex;

    private ImageView imgBack, imgPrev, imgNext;
    private TextView txtTitle, txtAccmulated, txtCredit;
    private Button btnRequest, btnPay;
    private LinearLayout lltTerms;
    private CardView crdQuestion1, crdQuestion2, crdRed;
    private RelativeLayout rtlVentas;

    private boolean isRequestable = false;
    private int isSelectedIndex = 0;
    private List<SkuDetails> skuProducts = new ArrayList<>();

    private AlertDialog alertDialog;

    private PurchasesUpdatedListener purchasesUpdatedListener;

    private BillingClient billingClient;

    public WalletFragment() {
        // Required empty public constructor
    }

    public WalletFragment(MainActivity mainActivity, boolean hasNew, int tabIndex) {
        this.mActivity = mainActivity;
        this.hasNew = hasNew;
        this.tabIndex = tabIndex;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (MainActivity) getActivity();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);
        initData();
        initView(view);
        initIAP();
        return view;
    }

    private void initData(){
        ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("userid", "" + SharedUtil.getSharedUserID());
        ApiUtil.onAPIConnectionResponse(ApiUtil.GET_USER_BALANCE, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                try {
                    progressDialog.dismiss();
                    JSONArray resArr = obj.getJSONArray("balance");
                    JSONObject result = resArr.getJSONObject(0);
                    txtCredit.setText(result.getString("credit"));
                    txtAccmulated.setText(result.getString("accumulated_money"));
                    if (Double.parseDouble(result.getString("accumulated_money").replace(" ", "")) > 10.0) {
                        btnRequest.setBackgroundResource(R.drawable.round_darggray16);
                        isRequestable = true;
                    } else {
                        btnRequest.setBackgroundResource(R.drawable.round_lightgray_6);
                        isRequestable = false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onEventInternetError(Exception e) {
                progressDialog.dismiss();
            }

            @Override
            public void onEventServerError(Exception e) {
                progressDialog.dismiss();
            }
        });
    }

    private void initView(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mActivity.onBackPressed());
        imgPrev = view.findViewById(R.id.imgPrev);
        imgNext = view.findViewById(R.id.imgNext);
        imgPrev.setOnClickListener(this);
        imgNext.setOnClickListener(this);
        imgPrev.setColorFilter(mActivity.getColor(R.color.lightGray));
        imgNext.setColorFilter(mActivity.getColor(R.color.white));
        txtTitle = view.findViewById(R.id.txtTitle);
        txtTitle.setText("Billetera");
        txtAccmulated = view.findViewById(R.id.txtAccmulation);
        txtCredit = view.findViewById(R.id.txtCredit);
        btnRequest = view.findViewById(R.id.btnRequest);
        btnRequest.setOnClickListener(this);
        btnPay = view.findViewById(R.id.btnBuy);
        btnPay.setOnClickListener(this);
        lltTerms = view.findViewById(R.id.lltTerms);
        lltTerms.setOnClickListener(this);
        crdQuestion1 = view.findViewById(R.id.crdQuestion1);
        crdQuestion1.setOnClickListener(v -> {
            alertDialog = AppUtil.showNormalDialog(mActivity, "Dinero acumulado", "Este balance muestra todo el dinero que han generado y acumulado tus posts por la venta de los mismos o por el numero de visualizaciones que han conseguido. Una vez llegues al mínimo establecido, podrás solicitar la cantidad acumulada.");
        });
        crdQuestion2 = view.findViewById(R.id.crdQuestion2);
        crdQuestion2.setOnClickListener(v -> {
            alertDialog = AppUtil.showNormalDialog(mActivity, "Créditos", "Este balance muestra la cantidad de créditos que has introducido en la plataforma para poder desbloquear los llamados posts PREMIUM, aquellos posts que son de pago.");
        });
        rtlVentas = view.findViewById(R.id.rltVentas);
        rtlVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.addFragment(new SalesFragment(mActivity, "Ventas", tabIndex), tabIndex);
            }
        });
        crdRed = view.findViewById(R.id.crdRed);
        crdRed.setVisibility(hasNew ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBuy:
                onPurchase();
                break;
            case R.id.btnRequest:
                if (isRequestable) {
                    alertDialog = AppUtil.showNormalDialog(mActivity, "Solicitud", "Puedes solicitar tu dinero acumulado o transferir el dinero a tu balance de créditos entrando en www.adimvi.com");
                } else {
                    alertDialog = AppUtil.showNormalDialog(mActivity, "Solicitud", "No puedes solicitar la cantidad acumulada hasta que no alcances la cantidad mínima de 10$");
                }
                break;
            case R.id.lltTerms:
                AppUtil.showOtherActivity(mActivity, CreditActivity.class, -1);
                break;
            case R.id.imgPrev:
                if (isSelectedIndex == 0) {
                    return;
                }
                onHandleInAppPurchase(true);
                break;
            case R.id.imgNext:
                if (isSelectedIndex == 3) {
                    return;
                }
                onHandleInAppPurchase(false);
                break;
        }
    }

    private void onHandleInAppPurchase(boolean isPrev) {
        if (isPrev) {
            if (isSelectedIndex == 1) {
                imgPrev.setColorFilter(mActivity.getColor(R.color.lightGray));
                imgNext.setColorFilter(mActivity.getColor(R.color.white));
            } else {
                imgPrev.setColorFilter(mActivity.getColor(R.color.white));
                imgNext.setColorFilter(mActivity.getColor(R.color.white));
            }
            isSelectedIndex--;
        } else {
            if (isSelectedIndex == 2) {
                imgNext.setColorFilter(mActivity.getColor(R.color.lightGray));
                imgPrev.setColorFilter(mActivity.getColor(R.color.white));
            } else {
                imgPrev.setColorFilter(mActivity.getColor(R.color.white));
                imgNext.setColorFilter(mActivity.getColor(R.color.white));
            }
            isSelectedIndex++;
        }
        btnPay.setText(AppConstant.INAPPPURCASE.get(isSelectedIndex).title);
    }

    private void initIAP() {
        purchasesUpdatedListener = (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                for (Purchase purchase: list) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                BannerUtil.onShowWaringAlert(mActivity.getContentView(), "User Cancelled", AppConstant.SHOW_BANNER_TIME);
            } else {
                BannerUtil.onShowWaringAlert(mActivity.getContentView(), "Error Occured", AppConstant.SHOW_BANNER_TIME);
            }
        };

        billingClient = BillingClient.newBuilder(mActivity)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingServiceDisconnected() {
                Log.d("Service ====> ", "Disconnected");
            }

            @Override
            public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    List<String> skuList = new ArrayList<>();
                    for (InAppProductModel model: AppConstant.INAPPPURCASE) {
                        skuList.add(model.id);
                    }
                    SkuDetailsParams.Builder params = new SkuDetailsParams().newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(), (billingResult1, list) -> {
                        skuProducts.clear();
                        skuProducts.addAll(list);
                        Collections.sort(skuProducts, (o1, o2) -> o1.getPriceAmountMicros() > o2.getPriceAmountMicros() ? 0 : -1);
                    });
                }
            }
        });
    }

    private void onPurchase() {
        if (skuProducts.size() > 0) {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuProducts.get(isSelectedIndex)).build();
            int reponseCode = billingClient.launchBillingFlow(mActivity, billingFlowParams).getResponseCode();
        }
    }

    private void handlePurchase(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
        ConsumeResponseListener listener = (billingResult, s) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                Map<String, String> param = new HashMap<>();
                param.put("userid", "" + SharedUtil.getSharedUserID());
                param.put("txnid", "" + purchase.getOrderId());
                param.put("usd", "" + AppConstant.INAPPPURCASE.get(isSelectedIndex).price);
                param.put("status", "done");
                ProgressDialog dialog = AppUtil.onShowProgressDialog(mActivity, AppConstant.LOADING, false);
                ApiUtil.onAPIConnectionResponse(ApiUtil.SET_USER_CREDIT, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback(){
                    @Override
                    public void onEventCallBack(JSONObject obj) {
                        AppUtil.onDismissProgressDialog(dialog);
                        try {
                            txtCredit.setText(obj.getString("now_credit"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onEventInternetError(Exception e) {
                        AppUtil.onDismissProgressDialog(dialog);
                    }

                    @Override
                    public void onEventServerError(Exception e) {
                        AppUtil.onDismissProgressDialog(dialog);
                    }
                });
            }
        };
        billingClient.consumeAsync(consumeParams, listener);
    }
}