package me.alek.serversecurity.network;

import me.alek.serversecurity.ServerSecurityPlugin;
import me.alek.serversecurity.lang.Lang;
import me.alek.serversecurity.logging.LogHolder;
import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.permissions.ServerOperator;

import java.net.SocketTimeoutException;
import java.security.Permission;

public class SecurityManagerInterceptor extends SecurityManager implements NetworkInterceptor {

    private boolean enabled;

    public SecurityManagerInterceptor() {
    }

    @Override
    public void enable() {
        this.enabled = true;
        System.setSecurityManager(this);
    }

    @Override
    public void disable() {
        this.enabled = false;
        System.setSecurityManager(null);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void checkConnect(final String host, final int port) {
        CommonNetworkInterceptor.check(host, port);
    }

    @Override
    public void checkConnect(final String host, final int port, final Object context) {
        this.checkConnect(host, port);
    }

    @Override
    public void checkPermission(final Permission perm) {
        final String name = perm.getName();
        if (name == null) {
            return;
        }
        if (this.enabled && name.equals("setSecurityManager")) {
            throw new SecurityException(Lang.getMessage(Lang.NETWORK_ERROR_REPLACE_SECURITY_MANAGER));
        }
    }

    @Override
    public void checkPermission(final Permission perm, final Object context) {
        this.checkPermission(perm);
    }

}
