package com.thinkmobiles.easyerp.presentation.screens.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.thinkmobiles.easyerp.BuildConfig;
import com.thinkmobiles.easyerp.R;
import com.thinkmobiles.easyerp.data.model.user.UserInfo;
import com.thinkmobiles.easyerp.domain.LoginRepository;
import com.thinkmobiles.easyerp.domain.UserRepository;
import com.thinkmobiles.easyerp.presentation.managers.DateManager;
import com.thinkmobiles.easyerp.presentation.screens.home.HomeActivity_;
import com.thinkmobiles.easyerp.presentation.utils.Constants;
import com.thinkmobiles.easyerp.presentation.utils.CookieSharedPreferences_;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

import java.util.concurrent.TimeUnit;

@EActivity(R.layout.activity_login)
public class LoginActivity extends AppCompatActivity implements LoginContract.LoginView {

    private LoginContract.LoginPresenter presenter;

    private boolean isCookieExpired = true;
    private AnimatorSet animatorSet;

    @ViewById
    protected RelativeLayout rvContainer_AL;
    @ViewById
    protected ImageView ivAppIcon_AL;
    @ViewById
    protected LinearLayout llInput_AL;

    @ViewById
    protected EditText etLogin_AL;
    @ViewById
    protected EditText etPassword_AL;
    @ViewById
    protected EditText etDbId_AL;
    @ViewById
    protected Button btnLogin_AL;

    @Pref
    protected CookieSharedPreferences_ sharedPreferences;

    @Bean
    protected LoginRepository loginRepository;
    @Bean
    protected UserRepository userRepository;

    @AfterInject
    @Override
    public void initPresenter() {
        new LoginPresenter(this, loginRepository, userRepository, sharedPreferences);

        isCookieExpired = DateManager.isCookieExpired(sharedPreferences.getCookieExpireDate().get());
        if(isCookieExpired) presenter.clearCookies();
    }

    @AfterViews
    protected void initUI() {
        if (!BuildConfig.PRODUCTION) putDefaultDebugCredentials();

        ivAppIcon_AL.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                ivAppIcon_AL.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                runSplashAnimation();
            }
        });

        RxView.clicks(btnLogin_AL)
                .throttleFirst(Constants.DELAY_CLICK, TimeUnit.MILLISECONDS)
                .subscribe(aVoid -> presenter.login());
    }

    private void putDefaultDebugCredentials() {
        etLogin_AL.setText("testAdmin");
        etPassword_AL.setText("111111");
        etDbId_AL.setText("sergey");
    }

    @Override
    public void displayError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getLogin() {
        return etLogin_AL.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return etPassword_AL.getText().toString().trim();
    }

    @Override
    public String getDbID() {
        return etDbId_AL.getText().toString().trim();
    }

    @Override
    public void startHomeScreen(UserInfo userInfo) {
        HomeActivity_.intent(this)
                .flags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                .userInfo(userInfo)
                .start();
    }

    @Override
    public void setPresenter(LoginContract.LoginPresenter presenter) {
        this.presenter = presenter;
    }

    private void runSplashAnimation() {
        ObjectAnimator iconFade = ObjectAnimator.ofFloat(ivAppIcon_AL, View.ALPHA, 0.4f, 1f);
        ObjectAnimator iconScaleX = ObjectAnimator.ofFloat(ivAppIcon_AL, View.SCALE_X, 0.5f, 1f);
        ObjectAnimator iconScaleY = ObjectAnimator.ofFloat(ivAppIcon_AL, View.SCALE_Y, 0.5f, 1f);
        ObjectAnimator iconTranslateY = ObjectAnimator.ofFloat(ivAppIcon_AL, View.Y, getResources().getDisplayMetrics().heightPixels / 13);
        ObjectAnimator containerFade = ObjectAnimator.ofFloat(llInput_AL, View.ALPHA, 0f, 1f);

        containerFade.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                llInput_AL.setVisibility(View.VISIBLE);
            }
        });
        iconFade.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(!isCookieExpired) {
                    animatorSet.pause();
                    presenter.getCurrentUser();
                }
            }
        });

        iconScaleX.setInterpolator(new OvershootInterpolator());
        iconScaleY.setInterpolator(new OvershootInterpolator());
        iconFade.setInterpolator(new LinearInterpolator());
        iconTranslateY.setInterpolator(new DecelerateInterpolator());
        containerFade.setInterpolator(new AccelerateInterpolator());

        iconFade.setDuration(1500);
        iconScaleX.setDuration(1500);
        iconScaleY.setDuration(1500);

        iconTranslateY.setStartDelay(1500);
        iconTranslateY.setDuration(1000);

        containerFade.setStartDelay(2500);
        containerFade.setDuration(500);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(iconFade, iconScaleX, iconScaleY, iconTranslateY, containerFade);
        animatorSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(animatorSet != null) animatorSet.cancel();
    }
}
