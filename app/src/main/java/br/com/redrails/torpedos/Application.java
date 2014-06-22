package br.com.redrails.torpedos;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseInstallation;
import com.parse.PushService;

import br.com.redrails.torpedos.parse.SyncActivity;

public class Application extends android.app.Application {

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
            Parse.initialize(this, "IjHMioV35jvHn4LUpn4Xm6aTh51qNmUKPieVqdT3", "S5LWQJYulqwvanhDlhq1gXRAhUhhhKezmDQ5fZp9");
            PushService.setDefaultPushCallback(this, SyncActivity.class);
            //ParseAnalytics.trackAppOpened(this.getIntent());
            ParseInstallation.getCurrentInstallation().saveInBackground();

    }
}