package roboguice.inject;

import com.google.inject.Inject;
import com.google.inject.Provider;

import android.accounts.AccountManager;
import android.content.Context;

@ContextSingleton
public class AccountManagerProvider implements Provider<AccountManager> {
    @Inject protected Context context;

    public AccountManager get() {
        return AccountManager.get(context);
    }
}
