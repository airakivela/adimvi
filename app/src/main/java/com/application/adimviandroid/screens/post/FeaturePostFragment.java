package com.application.adimviandroid.screens.post;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.application.adimviandroid.adapter.InAppFeatureAdapter;
import com.application.adimviandroid.models.InAppFeatureModel;
import com.application.adimviandroid.screens.MainActivity;
import com.application.adimviandroid.ui.SetFeatureDialog;
import com.application.adimviandroid.utils.ApiUtil;
import com.application.adimviandroid.utils.AppConstant;
import com.application.adimviandroid.utils.AppUtil;
import com.application.adimviandroid.utils.BannerUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeaturePostFragment extends Fragment {

    private MainActivity mainActivity;
    private JSONObject postData;

    private ImageView imgBack;
    private TextView txtTitle, title;
    private CardView crdQuestion;
    private RecyclerView rclIAP;

    private InAppFeatureAdapter featureAdapter;

    private PurchasesUpdatedListener purchasesUpdatedListener;
    private BillingClient billingClient;
    private List<SkuDetails> skuProducts = new ArrayList<>();

    private int selectedIndex = 0;
    private int postID = 0;

    private SetFeatureDialog featureDialog;

    public FeaturePostFragment() {

    }

    public FeaturePostFragment(MainActivity mainActivity, JSONObject postModel) {
        this.mainActivity = mainActivity;
        this.postData = postModel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mainActivity == null) {
            mainActivity = (MainActivity) getActivity();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (featureDialog != null && featureDialog.isShowing()) {
            featureDialog.dismiss();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_feature_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initIAPItems();
        imgBack = view.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> mainActivity.onBackPressed());
        title = view.findViewById(R.id.txtTitle);
        title.setText("Destacar posts");
        txtTitle = view.findViewById(R.id.txtPostTitle);
        try {
            txtTitle.setText(postData.getString("post_title"));
            postID = postData.getInt("postid");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        rclIAP = view.findViewById(R.id.rclIAP);
        rclIAP.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));

        featureAdapter = new InAppFeatureAdapter(mainActivity, index -> {
            selectedIndex = index;
            onPurchase(index);
        });
        rclIAP.setAdapter(featureAdapter);

        crdQuestion = view.findViewById(R.id.crdQuestion);
        crdQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppUtil.showNormalDialog(mainActivity, "Información de posts destacados"
                        , "Los posts que destaques a partir de esta pantalla aparecerán en la sección de destacados de la página principal tanto de la app como de la web www.adimvi.com durante el tiempo seleccionado y el primero de la lista en este momento.");
            }
        });
    }

    private void initIAPItems() {
        purchasesUpdatedListener = (billingResult, list) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null) {
                for (Purchase purchase: list) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                BannerUtil.onShowWaringAlert(mainActivity.getContentView(), "User Cancelled", AppConstant.SHOW_BANNER_TIME);
            } else {
                BannerUtil.onShowWaringAlert(mainActivity.getContentView(), "Error Occured", AppConstant.SHOW_BANNER_TIME);
            }
        };

        billingClient = BillingClient.newBuilder(mainActivity)
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
                    for (InAppFeatureModel model: AppConstant.INAPPFEATURES) {
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

    private void onPurchase(int index) {
        if (skuProducts.size() > 0) {
            BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuProducts.get(index)).build();
            int reponseCode = billingClient.launchBillingFlow(mainActivity, billingFlowParams).getResponseCode();
        }
    }

    private void handlePurchase(Purchase purchase) {
        ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
        ConsumeResponseListener listener = (billingResult, s) -> {
            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                onCallSetFeatured();
            }
        };
        billingClient.consumeAsync(consumeParams, listener);
    }

    private void onCallSetFeatured() {
        ProgressDialog progressDialog = AppUtil.onShowProgressDialog(mainActivity, AppConstant.LOADING, false);
        Map<String, String> param = new HashMap<>();
        param.put("id", "" + postID);
        param.put("duration", AppConstant.INAPPFEATURES.get(selectedIndex).timeDay);
        ApiUtil.onAPIConnectionResponse(ApiUtil.SET_FEATURE_POST, param, ApiUtil.APIMethod.POST, new ApiUtil.APIManagerCallback() {
            @Override
            public void onEventCallBack(JSONObject obj) {
                AppUtil.onDismissProgressDialog(progressDialog);
                featureDialog = new SetFeatureDialog(mainActivity);
                featureDialog.show();
            }

            @Override
            public void onEventInternetError(Exception e) {
                AppUtil.onDismissProgressDialog(progressDialog);
            }

            @Override
            public void onEventServerError(Exception e) {
                AppUtil.onDismissProgressDialog(progressDialog);
            }
        });
    }
}