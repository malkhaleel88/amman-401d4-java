package com.d4coders.goodcitizen.ui.splash;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.pinpoint.AWSPinpointAnalyticsPlugin;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.AWSDataStorePlugin;
import com.amplifyframework.datastore.generated.model.Category;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;
import com.d4coders.goodcitizen.R;
import com.d4coders.goodcitizen.data.service.PushListenerService;
import com.d4coders.goodcitizen.ui.auth.AuthActivity;
import com.d4coders.goodcitizen.ui.dashboard.DashboardActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        configureAWS();

//        generateCats();

        createNotificationChannel();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //This method will be executed once the timer is over
            // Start your app main activity
            startActivity(new Intent(SplashActivity.this, AuthActivity.class));
            // close this activity
            finish();
        }, 500); // pause the launch of the dashboard for 3 seconds
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(PushListenerService.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void configureAWS() {
        try {
            Amplify.addPlugin(new AWSDataStorePlugin());
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.addPlugin(new AWSPinpointAnalyticsPlugin(getApplication()));
            Amplify.configure(getApplicationContext());

            Log.i("Tutorial", "Initialized Amplify");
        } catch (AmplifyException e) {
            Log.e("Tutorial", "Could not initialize Amplify", e);
        }
    }

    private void generateCats() {
        Category category1 = Category.builder().title("Fire").build();
        Category category2 = Category.builder().title("Car Accident").build();
        Category category3 = Category.builder().title("Fighting").build();
        Category category4 = Category.builder().title("Bio Hazard").build();
        Category category5 = Category.builder().title("Pot Hole").build();
        Category category6 = Category.builder().title("Power Lines").build();
        Category category7 = Category.builder().title("Explosion").build();

        Amplify.API.mutate(ModelMutation.create(category1), success -> {}, error -> {});
        Amplify.API.mutate(ModelMutation.create(category2), success -> {}, error -> {});
        Amplify.API.mutate(ModelMutation.create(category3), success -> {}, error -> {});
        Amplify.API.mutate(ModelMutation.create(category4), success -> {}, error -> {});
        Amplify.API.mutate(ModelMutation.create(category5), success -> {}, error -> {});
        Amplify.API.mutate(ModelMutation.create(category6), success -> {}, error -> {});
        Amplify.API.mutate(ModelMutation.create(category7), success -> {}, error -> {});
    }
}