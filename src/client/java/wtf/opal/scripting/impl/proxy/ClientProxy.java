package wtf.opal.scripting.impl.proxy;

import wtf.opal.client.OpalClient;
import wtf.opal.client.feature.module.Module;
import wtf.opal.client.feature.module.UnknownModuleException;
import wtf.opal.client.feature.module.repository.ModuleRepository;
import wtf.opal.utility.misc.chat.ChatUtility;

public class ClientProxy {

    public void print(final Object o) {
        ChatUtility.print(o);
    }

    public Module getModule(final String ID) {
        try {
            return OpalClient.getInstance().getModuleRepository().getModule(ID);
        } catch (UnknownModuleException e) {
            throw new RuntimeException(e);
        }
    }

}
