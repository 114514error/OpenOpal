package wtf.opal.client.feature.helper.impl.server;

import wtf.opal.client.feature.helper.impl.LocalDataWatch;
import wtf.opal.client.feature.helper.impl.server.impl.HypixelServer;
import wtf.opal.event.impl.game.PreGameTickEvent;
import wtf.opal.event.subscriber.Subscribe;

import static wtf.opal.client.Constants.mc;

public class ProxyServer extends KnownServer {

    public ProxyServer(final String name) {
        super(name);
    }

    @Subscribe
    public void onPreGameTick(final PreGameTickEvent event) {
        if (mc.getNetworkHandler() == null) {
            return;
        }

        final String serverBrand = mc.getNetworkHandler().getBrand();
        if (serverBrand != null && HypixelServer.SERVER_BRAND_PATTERN.matcher(serverBrand).matches()) {
            final KnownServer realServer = new HypixelServer();
            realServer.setProxyServer(this);

            LocalDataWatch.get().getKnownServerManager().setServer(realServer);
        }
    }

}
