package roboguice.inject;

import android.accounts.AccountManager;
import android.content.Context;
import com.google.inject.Inject;
import com.google.inject.Provider;

@ContextScoped
public class AccountManagerProvider implements Provider<AccountManager> {
    @Inject protected Context context;

    public AccountManager get() {
        return AccountManager.get(context);
    }
}
