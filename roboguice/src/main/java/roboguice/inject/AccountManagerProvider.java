package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

@ContextSingleton
@TargetApi(Build.VERSION_CODES.ECLAIR)
public class AccountManagerProvider implements Provider<AccountManager> {
    @Inject protected Context context;

    @Override
    public AccountManager get() {
        return AccountManager.get(context);
    }
}
