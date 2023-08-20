package me.alek.serversecurity.network;

import me.alek.serversecurity.lang.Lang;

import java.security.Permission;

public class SecurityManagerInterceptorImpl extends SecurityManager implements NetworkInterceptor {

    private boolean enabled;

    public SecurityManagerInterceptorImpl() {
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
