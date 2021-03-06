/*
 * Copyright © 2013-2016 The Nxt Core Developers.
 * Copyright © 2016-2017 Jelurida IP B.V.
 *
 * See the LICENSE.txt file at the top-level directory of this distribution
 * for licensing information.
 *
 * Unless otherwise agreed in a custom licensing agreement with Jelurida B.V.,
 * no part of the Nxt software, including this file, may be copied, modified,
 * propagated, or distributed except according to the terms contained in the
 * LICENSE.txt file.
 *
 * Removal or modification of this copyright notice is prohibited.
 *
 */

/*
 * Copyright © 2018 Apollo Foundation
 */

package com.apollocurrency.aplwallet.apl.env;

import org.slf4j.Logger;

import javax.swing.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

public class DesktopMode implements RuntimeMode {
    private static Logger LOG;

    private DesktopSystemTray desktopSystemTray;
    private Class desktopAppClass;

    @Override
    public void init() {
        LOG = getLogger(DesktopMode.class);
        try {
            LookAndFeel.init();
            desktopAppClass = Class.forName("com.apollocurrency.aplwallet.apldesktop.DesktopApplication");
            Method launchDesktopAppMethod = desktopAppClass.getMethod("launch");
            Thread desktopAppThread = new Thread(() -> {
                try {
                    launchDesktopAppMethod.invoke(null);
                }
                catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.error("Unable to launch desktop application", e);
                }
            });
            desktopAppThread.start();
            desktopSystemTray = new DesktopSystemTray();
            SwingUtilities.invokeLater(desktopSystemTray::createAndShowGUI);
        }
        catch (ClassNotFoundException e) {
            LOG.error("Cannot find desktop application class", e);
        }
        catch (NoSuchMethodException e) {
            LOG.error("Missing 'launch' method to start desktop application", e);
        }
    }

    @Override
    public void setServerStatus(ServerStatus status, URI wallet, File logFileDir) {
        desktopSystemTray.setToolTip(new SystemTrayDataProvider(status.getMessage(), wallet, logFileDir));
    }

    @Override
    public void launchDesktopApplication() {
        LOG.info("Launching desktop wallet");
        try {
            desktopAppClass.getMethod("startDesktopApplication").invoke(null);
        }
        catch (Exception e) {
            //rethrow
            throw new RuntimeException("Cannot start desktop application", e);
        }
    }

    @Override
    public void shutdown() {
        desktopSystemTray.shutdown();
        try {
            desktopAppClass.getMethod("shutdown").invoke(null);
        }
        catch (Exception e) {
            //rethrow
            throw new RuntimeException("Cannot shutdown desktop application", e);
        }
    }

    @Override
    public void alert(String message) {
        desktopSystemTray.alert(message);
    }

    @Override
    public void recoverDb() {
        try {
            desktopAppClass.getMethod("recoverDbUI").invoke(null);
        }
        catch (Exception e) {
            //rethrow
            throw new RuntimeException("Unable to show recover db dialog!", e);
        }
    }

    @Override
    public void updateAppStatus(String newStatus) {
        try {
            desktopAppClass.getMethod("updateSplashScreenStatus", String.class).invoke(null, newStatus);
        }
        catch (Exception e) {
            //rethrow
            throw new RuntimeException("Unable to update status on splash screen!", e);
        }
    }

    @Override
    public void displayError(String errorMessage) {
        try {
            desktopAppClass.getMethod("showError", String.class).invoke(null, errorMessage);
        }
        catch (Exception e) {
            //rethrow
            throw new RuntimeException("Unable to show gui error alert!", e);
        }
    }
}
