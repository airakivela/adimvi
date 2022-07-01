package com.application.adimviandroid.utils;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.Element;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;

import com.application.adimviandroid.R;
import com.application.adimviandroid.models.CategoryModel;
import com.application.adimviandroid.models.MentionUserModel;
import com.application.adimviandroid.models.MuroModel;
import com.application.adimviandroid.screens.auth.LoginActivity;
import com.application.adimviandroid.types.FollowSegmentType;
import com.application.adimviandroid.types.HomeExploreSegmentType;
import com.application.adimviandroid.types.HomeSearchType;
import com.application.adimviandroid.types.ImagePlaceHolderType;
import com.application.adimviandroid.models.UserModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class AppUtil {

    public static UserModel gUser = new UserModel();
    public static HomeSearchType homeSearchType = HomeSearchType.POST;
    public static HomeExploreSegmentType homeExploreSegmentType = HomeExploreSegmentType.HOY;
    public static FollowSegmentType followSegmentType = FollowSegmentType.FOLLOWING_POST_TEST;
    public static List<CategoryModel> gCategories = new ArrayList<>();
    public static List<MentionUserModel> mMentionUsers = new ArrayList<>();
    public static List<MuroModel> gRecentWallUsers = new ArrayList<>();
    public static List<Boolean> gVisitedRecentWallUsers = new ArrayList<>();
    public static boolean isCameraOn = false;


    public static void showOtherActivity (Context context, Class<?> cls, int direction) {
        Intent myIntent = new Intent(context, cls);
        ActivityOptions options;
        switch (direction) {
            case 0:
                options = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.slide_out_left);
                context.startActivity(myIntent, options.toBundle());
                break;
            case 1:
                options = ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_left, R.anim.slide_out_right);
                context.startActivity(myIntent, options.toBundle());
                break;
            default:
                context.startActivity(myIntent);
                break;
        }
    }

    static public ProgressDialog onShowProgressDialog(final Context mActivity, final String message, boolean isCancelable) {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.show();
        progressDialog.setCancelable(isCancelable);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        return progressDialog;
    }

    static public void onDismissProgressDialog(ProgressDialog progressDialog) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    static public void setWebViewThemeMode(Context context, WebView view) {
        int version = Build.VERSION.SDK_INT;
        Log.d("SDK version", "" + version);
        if (Build.VERSION.SDK_INT > 27) {
            int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                WebSettingsCompat.setForceDark(view.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
            } else {
                WebSettingsCompat.setForceDark(view.getSettings(), WebSettingsCompat.FORCE_DARK_OFF);
            }
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK_STRATEGY)) {
                WebSettingsCompat.setForceDarkStrategy(view.getSettings(), WebSettingsCompat.DARK_STRATEGY_WEB_THEME_DARKENING_ONLY);
            }
        }
    }

    static public void loadImageByUrl(Context context, ImageView view, String url, ImagePlaceHolderType type) {
        Glide.with(context).asBitmap().fitCenter().load(url)
                .placeholder(type.resID)
                .error(type.resID)
                .into(view);
    }

    static public void loadImageByUrlWithBlur(Context context, ImageView view, String url, ImagePlaceHolderType type) {
        Glide.with(context).asBitmap().fitCenter().load(url)
                .apply(RequestOptions.bitmapTransform(new BlurTransformation(25, 3)))
                .placeholder(type.resID)
                .error(type.resID)
                .into(view);
    }

    static public void showShareLink(Context context, String content) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, content);
        Intent.createChooser(sendIntent, "Compartir via");
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    static public AlertDialog showNormalDialog(Context context, String strTitle, String strContent) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(strTitle)
                .setMessage(strContent)
                .setPositiveButton("Okay", (dialog, which) -> {
                    dialog.dismiss();
                }).create();
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(context.getResources().getColor(R.color.darkGray));
        positiveButton.setAllCaps(false);
        return alertDialog;
    }

    static public AlertDialog showNormalDialogWithCallBack(Context context, String strTitle, String strContent, OnShowNormalDialogListener listener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(strTitle)
                .setMessage(strContent)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    dialog.dismiss();
                    listener.onClickOKButton();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss()).create();
        alertDialog.show();
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(context.getResources().getColor(R.color.darkGray));
        positiveButton.setAllCaps(false);
        Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(context.getResources().getColor(R.color.darkGray));
        negativeButton.setAllCaps(false);
        return alertDialog;
    }

    public interface OnShowNormalDialogListener {
        void onClickOKButton();
    }

    static public AlertDialog showPostDialog(Context context, String strTitle, String strContent, OnShowPostDialogCallback callback) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(strTitle)
                .setMessage(strContent)
                .setNegativeButton("Okay", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton("Ver el post", (dialog, which) -> {
                    dialog.dismiss();
                    callback.onClickGoPostButton();
                }).create();
        alertDialog.show();
        Button negativButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativButton.setTextColor(context.getResources().getColor(R.color.darkGray));
        negativButton.setAllCaps(false);
        Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(context.getResources().getColor(R.color.darkGray));
        positiveButton.setAllCaps(false);
        return alertDialog;
    }

    public interface OnShowPostDialogCallback {
        void onClickGoPostButton();
    }

    static public void openURL(Context context, String url) {
        if (!url.startsWith("https://") && !url.startsWith("http://")) {
            url = "https://" + url;
        }
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    static public CropImageContractOptions options = new CropImageContractOptions(null, new CropImageOptions())
            .setScaleType(CropImageView.ScaleType.CENTER_CROP)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setMaxCropResultSize(AppConstant.IMAGE_WIDTH, AppConstant.IMAGE_HEIGHT);

    static public String saveBitmapToInternalStorage(Context context, Bitmap bitmap, String name) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath=new File(directory,name + ".jpg");
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    static public void logoutUser(Context context) {
        SharedUtil.userlogOut();
        showOtherActivity(context, LoginActivity.class, 0);
    }

    static public List<MentionUserModel> getMentionUsers(String htmlString) {
        Document document = Jsoup.parse(htmlString);
        Elements elementsID = document.getElementsByTag("b");
        Elements elementsMentions = document.getElementsByTag("font");
        List<MentionUserModel> mentions = new ArrayList<>();
        for (int i = 0; i < elementsID.size(); i++) {
            MentionUserModel mention = new MentionUserModel();
            mention.id = Integer.valueOf(elementsID.get(i).id().trim());
            mention.name = elementsMentions.get(i).text();
            mentions.add(mention);
        }
        return mentions;
    }

    static public SpannableString getSpannableString(Context context, String string, List<MentionUserModel> mentions, MentionListener listener) {
        int startIndex = -1;
        SpannableString spannableString = new SpannableString(string);
        for (MentionUserModel mention: mentions) {
            ClickableSpan span = new ClickableSpan() {
                @Override
                public void onClick(@NonNull View widget) {
                    listener.onClickMentionUser(mention);
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    ds.setColor(context.getColor(R.color.mainOrange));
                    ds.setUnderlineText(false);
                }
            };
            startIndex = string.indexOf(mention.name, startIndex + 1);
            spannableString.setSpan(span, startIndex, startIndex + mention.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannableString;
    }

    public interface MentionListener {
        void onClickMentionUser(MentionUserModel mentionUserModel);
    }

    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context,Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;
    }

    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }

    public static String getPrivateMessageChanelByCombinationUserIDS(int userID1, int userID2) {
        if (userID1 > userID2) {
            return userID1 + "_" + userID2;
        } else {
            return userID2 + "_" + userID1;
        }
    }

}
