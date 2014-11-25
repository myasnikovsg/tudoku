package tudoku.com.tudoku.service.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TudokuAuthenticationService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        TudokuAuthenticator authenticator = new TudokuAuthenticator(this);
        return authenticator.getIBinder();
    }
}
