/*
 * Copyright (C) 2013-2014 Sony Computer Science Laboratories, Inc. All Rights Reserved.
 * Copyright (C) 2014 Sony Corporation. All Rights Reserved.
 */

package com.sonycsl.Kadecot.plugin.sample;

import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;

import com.sonycsl.Kadecot.plugin.PostReceiveCallback;
import com.sonycsl.Kadecot.plugin.ProviderAccessObject;
import com.sonycsl.Kadecot.provider.KadecotCoreStore.DeviceTypeData;
import com.sonycsl.Kadecot.provider.KadecotCoreStore.ProtocolData;
import com.sonycsl.wamp.WampError;
import com.sonycsl.wamp.WampPeer;
import com.sonycsl.wamp.message.WampMessage;
import com.sonycsl.wamp.message.WampMessageFactory;
import com.sonycsl.wamp.transport.ProxyPeer;
import com.sonycsl.wamp.transport.WampWebSocketTransport;
import com.sonycsl.wamp.transport.WampWebSocketTransport.OnWampMessageListener;

import org.json.JSONObject;

import java.util.HashSet;
import java.util.Set;

/**
 * This class will be started automatically when a Kadecot web socket server is
 * started. <br>
 */
public class SamplePluginService extends Service {

    private static final String LOCALHOST = "localhost";
    private static final int WEBSOCKET_PORT = 41314;

    private static final String EXTRA_ACCEPTED_ORIGIN = "acceptedOrigin";
    private static final String EXTRA_ACCEPTED_TOKEN = "acceptedToken";

    private ProviderAccessObject mPao;

    private SampleProtocolClient mClient;

    private WampWebSocketTransport mTransport;

    @Override
    public void onCreate() {
        super.onCreate();
        mClient = new SampleProtocolClient();
        mClient.setCallback(new PostReceiveCallback() {

            @Override
            public void postReceive(WampPeer transmitter, WampMessage msg) {
                if (msg.isWelcomeMessage()) {
                    mClient.onSearchEvent(null);
                }
            }
        });

        mPao = new ProviderAccessObject(getContentResolver());
        mTransport = new WampWebSocketTransport();

        final ProxyPeer proxyPeer = new ProxyPeer(mTransport);

        /**
         * Set a listener to transmit the message which is sent from web socket
         * server to client. <br>
         * Stop this service when the web socket is closed <br>
         */
        mTransport.setOnWampMessageListener(new OnWampMessageListener() {

            @Override
            public void onMessage(WampMessage msg) {
                proxyPeer.transmit(msg);
            }

            @Override
            public void onError(Exception e) {
                stopSelf();
            }

            @Override
            public void onClose() {
                stopSelf();
            }
        });

        mClient.connect(proxyPeer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        /**
         * Send GOODBYE message to close WAMP session. <br>
         */
        mClient.transmit(WampMessageFactory.createGoodbye(new JSONObject(),
                WampError.GOODBYE_AND_OUT));
        /**
         * Close Web socket transport.
         */
        mTransport.close();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        /**
         * Put Plug-in information into content provider of kadecot. <br>
         * The information includes protocol data with setting activity name
         * space and device type data with icons. <br>
         * Stop service when this plug-in can not access to the provider. <br>
         */
        try {
            mPao.putProtocolInfo(getProtocolData());
            mPao.putDeviceTypesInfo(getDeviceTypesData());
        } catch (IllegalArgumentException e) {
            stopSelf();
        }

        String origin = "";
        if (intent != null && intent.hasExtra(EXTRA_ACCEPTED_ORIGIN)) {
            origin = intent.getStringExtra(EXTRA_ACCEPTED_ORIGIN);
        }
        String token = "";
        if (intent.hasExtra(EXTRA_ACCEPTED_TOKEN)) {
            token = intent.getStringExtra(EXTRA_ACCEPTED_TOKEN);
        }
        /**
         * Open Web socket transport.
         */
        mTransport.open(LOCALHOST, WEBSOCKET_PORT, origin, token);
        /**
         * Send HELLO message to open WAMP session. <br>
         */
        mClient.transmit(WampMessageFactory.createHello("realm", new JSONObject()));
        return START_REDELIVER_INTENT;
    }

    private ProtocolData getProtocolData() {
        return new ProtocolData(SampleProtocolClient.PROTOCOL_NAME, getApplicationContext()
                .getPackageName(), SettingsActivity.class.getName());
    }

    private Set<DeviceTypeData> getDeviceTypesData() {
        Set<DeviceTypeData> set = new HashSet<DeviceTypeData>();
        set.add(new DeviceTypeData(SampleProtocolClient.DEVICE_TYPE_MEASURE,
                SampleProtocolClient.PROTOCOL_NAME, BitmapFactory.decodeResource(getResources(),
                        R.drawable.measure)));
        set.add(new DeviceTypeData(SampleProtocolClient.DEVICE_TYPE_LIGHTING,
                SampleProtocolClient.PROTOCOL_NAME, BitmapFactory.decodeResource(getResources(),
                        R.drawable.lighting)));
        return set;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

}
